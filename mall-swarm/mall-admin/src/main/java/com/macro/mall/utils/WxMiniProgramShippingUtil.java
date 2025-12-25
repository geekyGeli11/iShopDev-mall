package com.macro.mall.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.dto.OmsOrderDeliveryParam;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序发货信息录入工具类
 * Created by macro on 2024/12/20.
 */
@Component
public class WxMiniProgramShippingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMiniProgramShippingUtil.class);
    
    // Redis缓存中access_token的key，与其他服务保持一致
    private static final String WX_ACCESS_TOKEN_KEY = "wx:mini_program:access_token";
    // access_token默认过期时间，与其他服务保持一致
    private static final long ACCESS_TOKEN_EXPIRE = 7000;
    // 微信小程序发货信息录入接口URL
    private static final String SHIPPING_INFO_URL = "https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info";

    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;
    
    @Value("${wx.pay.mchId:}")
    private String wxPayMchId;
    
    @Autowired
    private RedisService redisService;

    /**
     * 调用微信小程序发货信息录入接口
     *
     * @param order 订单信息
     * @param member 用户信息
     * @param deliveryParam 发货参数
     * @param itemDesc 商品描述
     * @return 是否发货成功
     */
    public boolean uploadShippingInfo(OmsOrder order, UmsMember member, OmsOrderDeliveryParam deliveryParam, String itemDesc) {
        // 只有微信支付的订单才需要调用微信发货接口
        if (order.getPayType() == null || order.getPayType() != 2) {
            LOGGER.info("订单{}不是微信支付，跳过微信发货接口调用", order.getOrderSn());
            return true; // 非微信支付订单返回true，不影响发货流程
        }

        try {
            // 获取access_token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                LOGGER.error("无法获取access_token，跳过微信发货接口调用");
                return false;
            }

            // 构建请求URL
            String url = SHIPPING_INFO_URL + "?access_token=" + accessToken;

            // 构建请求参数
            Map<String, Object> requestData = buildShippingInfoRequest(order, member, deliveryParam, itemDesc);

            // 手动序列化JSON为String，避免框架自动处理导致的Content-Length问题
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestData);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

            // 创建专门的RestTemplate，避免chunked编码问题
            RestTemplate restTemplate = createWechatRestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(bodyBytes.length); // 明确设置Content-Length

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(bodyBytes, headers);

            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );
            } catch (HttpClientErrorException e) {
                LOGGER.error("微信发货API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());

                // 如果是412错误，尝试强制刷新access_token并重试
                if (e.getStatusCode().value() == 412) {
                    LOGGER.warn("收到412错误，强制刷新access_token并重试");

                    // 清除Redis缓存的token
                    redisService.del(WX_ACCESS_TOKEN_KEY);

                    // 获取新的access_token（强制刷新）
                    String newAccessToken = getAccessToken(true);
                    if (newAccessToken != null) {
                        String newUrl = SHIPPING_INFO_URL + "?access_token=" + newAccessToken;
                        try {
                            response = restTemplate.exchange(
                                    newUrl,
                                    HttpMethod.POST,
                                    requestEntity,
                                    String.class
                            );
                            LOGGER.info("使用新access_token重试成功");
                        } catch (HttpClientErrorException retryException) {
                            LOGGER.error("使用新access_token重试仍然失败: {}", retryException.getMessage());
                            return false;
                        }
                    } else {
                        LOGGER.error("无法获取新的access_token");
                        return false;
                    }
                } else {
                    return false;
                }
            }

            // 解析响应
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            int errcode = jsonNode.get("errcode").asInt();
            if (errcode == 0) {
                LOGGER.info("微信小程序发货信息录入成功，订单号：{}", order.getOrderSn());
                return true;
            } else {
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                LOGGER.error("微信小程序发货信息录入失败，订单号：{}，错误码：{}，错误信息：{}",
                           order.getOrderSn(), errcode, errmsg);

                // 如果是token过期等错误，清除缓存
                if (errcode == 41001 || errcode == 42001) {
                    LOGGER.info("access_token已过期，清除缓存");
                    redisService.del(WX_ACCESS_TOKEN_KEY);
                }
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("调用微信小程序发货信息录入接口异常，订单号：{}", order.getOrderSn(), e);
            return false;
        }
    }

    /**
     * 创建专门用于微信API的RestTemplate实例
     * 针对微信API的特殊要求进行优化
     */
    private RestTemplate createWechatRestTemplate() {
        // 配置连接超时和读取超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000); // 15秒连接超时
        factory.setReadTimeout(30000);    // 30秒读取超时

        RestTemplate restTemplate = new RestTemplate(factory);

        // 使用默认的消息转换器，确保正确处理JSON
        // 通过手动设置Content-Length避免chunked编码问题

        return restTemplate;
    }

    /**
     * 构建发货信息请求参数
     */
    private Map<String, Object> buildShippingInfoRequest(OmsOrder order, UmsMember member, 
                                                        OmsOrderDeliveryParam deliveryParam, String itemDesc) {
        Map<String, Object> requestData = new HashMap<>();
        
        // 订单信息
        Map<String, Object> orderKey = new HashMap<>();
        orderKey.put("order_number_type", 1); // 1-商户侧单号形式 2-微信支付单号形式
        
        // 只有微信支付(payType=2)且配置了商户号时才填入mchid
        String mchId = "";
        if (order.getPayType() != null && order.getPayType() == 2 && 
            wxPayMchId != null && !wxPayMchId.trim().isEmpty()) {
            mchId = wxPayMchId;
        }
        orderKey.put("mchid", mchId); // 商户号，微信支付时填写
        orderKey.put("out_trade_no", order.getOrderSn()); // 商户订单号
        
        requestData.put("order_key", orderKey);
        
        // 物流模式 1-实体物流配送 2-同城配送 3-虚拟商品 4-用户自提
        int logisticsType = 1; // 默认为实体物流配送
        if (deliveryParam.getDeliveryMethod() != null) {
            switch (deliveryParam.getDeliveryMethod()) {
                case 1: // 快递配送
                    logisticsType = 1; // 实体物流配送
                    break;
                case 2: // 门店自提
                    logisticsType = 4; // 用户自提
                    break;
                case 3: // 虚拟发货
                    logisticsType = 3; // 虚拟商品
                    break;
                default:
                    logisticsType = 1; // 默认实体物流配送
                    break;
            }
        }
        requestData.put("logistics_type", logisticsType);
        
        // 发货模式 1-统一发货 2-分拆发货
        requestData.put("delivery_mode", 1);
        
        // 物流信息列表
        Map<String, Object> shipping = new HashMap<>();
        shipping.put("item_desc", itemDesc); // 商品信息描述

        // 根据发货方式设置不同的物流信息
        if (deliveryParam.getDeliveryMethod() != null) {
            switch (deliveryParam.getDeliveryMethod()) {
                case 1: // 快递配送
                    shipping.put("tracking_no", deliveryParam.getDeliverySn()); // 物流单号
                    shipping.put("express_company", deliveryParam.getDeliveryCompany()); // 物流公司编码

                    // 联系方式（当物流公司为顺丰时必填）
                    if ("shunfeng".equals(deliveryParam.getDeliveryCompany()) || "SF".equals(deliveryParam.getDeliveryCompany())) {
                        Map<String, Object> contact = new HashMap<>();
                        contact.put("receiver_contact", maskPhoneNumber(order.getReceiverPhone()));
                        shipping.put("contact", contact);
                    }
                    break;
                case 2: // 门店自提
                    // 用户自提时，tracking_no填写门店名称
                    shipping.put("tracking_no", deliveryParam.getStoreName() != null ? deliveryParam.getStoreName() : "门店自提");
                    shipping.put("express_company", "门店自提");
                    break;
                case 3: // 虚拟发货
                    // 虚拟商品时，tracking_no可以填写激活码或下载链接等信息
                    shipping.put("tracking_no", "虚拟发货");
                    shipping.put("express_company", "虚拟商品");
                    break;
                default:
                    // 默认按快递配送处理
                    shipping.put("tracking_no", deliveryParam.getDeliverySn());
                    shipping.put("express_company", deliveryParam.getDeliveryCompany());
                    break;
            }
        } else {
            // 兼容旧版本，默认按快递配送处理
            shipping.put("tracking_no", deliveryParam.getDeliverySn());
            shipping.put("express_company", deliveryParam.getDeliveryCompany());
        }

        requestData.put("shipping_list", new Object[]{shipping});
        
        // 上传时间（RFC 3339格式）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        requestData.put("upload_time", sdf.format(new Date()));
        
        // 支付者信息
        Map<String, Object> payer = new HashMap<>();
        payer.put("openid", member.getOpenid());
        requestData.put("payer", payer);
        
        return requestData;
    }

    /**
     * 手机号掩码处理
     */
    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        // 保留最后4位，其余用*替换
        String last4 = phone.substring(phone.length() - 4);
        String prefix = phone.substring(0, phone.length() - 4).replaceAll("\\d", "*");
        return prefix + last4;
    }

    /**
     * 获取微信接口调用凭证，优先从Redis缓存获取
     */
    private String getAccessToken() {
        return getAccessToken(false);
    }

    /**
     * 获取微信接口调用凭证（支持强制刷新）
     */
    private String getAccessToken(boolean forceRefresh) {
        // 如果不是强制刷新，先从Redis缓存获取
        if (!forceRefresh) {
            Object cachedToken = redisService.get(WX_ACCESS_TOKEN_KEY);
            if (cachedToken != null) {
                String token = cachedToken.toString();
                LOGGER.debug("从Redis缓存获取到access_token");

                // 验证token有效性
                if (validateAccessToken(token)) {
                    LOGGER.debug("缓存的access_token验证有效");
                    return token;
                } else {
                    LOGGER.warn("缓存的access_token验证无效，清除缓存");
                    redisService.del(WX_ACCESS_TOKEN_KEY);
                }
            }
        }
        
        LOGGER.info("Redis中未找到access_token，从微信服务器获取");
        // 缓存中没有，重新请求
        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret
        );
        
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("access_token")) {
                String accessToken = jsonNode.get("access_token").asText();
                // 存入Redis，设置过期时间比微信官方的时间短一些，确保token在使用时有效
                redisService.set(WX_ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE);
                LOGGER.info("成功获取新的access_token并缓存到Redis");
                return accessToken;
            } else {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                throw new RuntimeException("获取access_token失败：" + errMsg);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析access_token响应失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证access_token有效性
     */
    private boolean validateAccessToken(String accessToken) {
        try {
            // 使用微信API验证token有效性
            String url = "https://api.weixin.qq.com/cgi-bin/get_api_domain_ip?access_token=" + accessToken;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            if (response != null && response.contains("\"errcode\":0")) {
                return true;
            }

            LOGGER.warn("access_token验证失败: {}", response);
            return false;
        } catch (Exception e) {
            LOGGER.warn("验证access_token时发生异常: {}", e.getMessage());
            return false;
        }
    }
}