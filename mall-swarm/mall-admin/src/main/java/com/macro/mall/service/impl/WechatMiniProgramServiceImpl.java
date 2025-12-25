package com.macro.mall.service.impl;

import com.macro.mall.service.WechatMiniProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信小程序API服务实现
 * Created by macro on 2024/12/30.
 */
@Service
public class WechatMiniProgramServiceImpl implements WechatMiniProgramService {
    
    private static final Logger logger = LoggerFactory.getLogger(WechatMiniProgramServiceImpl.class);
    
    @Value("${wechat.app-id:}")
    private String appId;

    @Value("${wechat.app-secret:}")
    private String appSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    // 使用与mall-portal相同的Redis key，确保token共享
    private static final String WX_ACCESS_TOKEN_KEY = "wx:mini_program:access_token";
    private static final long ACCESS_TOKEN_EXPIRE = 7000; // 7000秒，比微信官方的7200秒短一些
    
    @Override
    public String getAccessToken() {
        return getAccessToken(false);
    }

    /**
     * 获取微信接口调用凭证（支持强制刷新）
     */
    private String getAccessToken(boolean forceRefresh) {
        if (!isConfigAvailable()) {
            logger.warn("微信小程序配置不完整，无法获取access_token");
            return null;
        }

        // 如果不是强制刷新，先从Redis缓存获取
        if (!forceRefresh) {
            Object cachedToken = redisService.get(WX_ACCESS_TOKEN_KEY);
            if (cachedToken != null) {
                String token = cachedToken.toString();
                logger.debug("从Redis缓存获取到access_token");

                // 验证token有效性
                if (validateAccessToken(token)) {
                    logger.debug("缓存的access_token验证有效");
                    return token;
                } else {
                    logger.warn("缓存的access_token验证无效，清除缓存");
                    redisService.del(WX_ACCESS_TOKEN_KEY);
                }
            }
        }

        logger.info("获取新的access_token");
        
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" 
                        + appId + "&secret=" + appSecret;
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("access_token")) {
                String accessToken = (String) response.get("access_token");
                Integer expiresIn = (Integer) response.get("expires_in");
                
                // 存入Redis，设置过期时间比微信官方的时间短一些，确保token在使用时有效
                redisService.set(WX_ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE);

                logger.info("获取微信小程序access_token成功并缓存到Redis");
                return accessToken;
            } else {
                logger.error("获取微信小程序access_token失败: {}", response);
                return null;
            }
        } catch (Exception e) {
            logger.error("获取微信小程序access_token异常", e);
            return null;
        }
    }
    
    @Override
    public String generateUrlLink(String path, String query) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            logger.warn("无法获取access_token，生成URL Link失败");
            return null;
        }
        
        try {
            String url = "https://api.weixin.qq.com/wxa/genwxashortlink?access_token=" + accessToken;

            // 准备请求参数 - 使用正确的短链接API参数格式
            Map<String, Object> requestBody = new HashMap<>();
            String pageUrl = path + (query != null && !query.isEmpty() ? "?" + query : "");
            requestBody.put("page_url", pageUrl); // 完整的页面URL，包含查询参数

            // 根据页面路径设置合适的标题
            String pageTitle = "商品详情";
            if (path.contains("coupon")) {
                pageTitle = "优惠券详情";
            } else if (path.contains("sale") || path.contains("Sale")) {
                pageTitle = "销售单详情";
            } else if (path.contains("product")) {
                pageTitle = "商品详情";
            }
            requestBody.put("page_title", pageTitle); // 页面标题
            requestBody.put("is_permanent", false); // 永久有效

            logger.info("生成短链接参数: page_url={}", pageUrl);

            // 手动序列化JSON为String，避免框架自动处理导致的Content-Length问题
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

            // 创建专门的RestTemplate，避免chunked编码问题
            RestTemplate wechatRestTemplate = createWechatRestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(bodyBytes.length);

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(bodyBytes, headers);

            ResponseEntity<String> responseEntity;
            try {
                responseEntity = wechatRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
                );
            } catch (HttpClientErrorException e) {
                logger.error("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());

                // 如果是412错误，尝试强制刷新access_token并重试
                if (e.getStatusCode().value() == 412) {
                    logger.warn("收到412错误，强制刷新access_token并重试");

                    // 清除Redis缓存的token
                    redisService.del(WX_ACCESS_TOKEN_KEY);

                    // 获取新的access_token（强制刷新）
                    String newAccessToken = getAccessToken(true);
                    if (newAccessToken != null) {
                        String newUrl = "https://api.weixin.qq.com/wxa/genwxashortlink?access_token=" + newAccessToken;
                        try {
                            responseEntity = wechatRestTemplate.exchange(
                                newUrl,
                                HttpMethod.POST,
                                requestEntity,
                                String.class
                            );
                            logger.info("使用新access_token重试成功");
                        } catch (HttpClientErrorException retryException) {
                            logger.error("使用新access_token重试仍然失败: {}", retryException.getMessage());
                            return null;
                        }
                    } else {
                        logger.error("无法获取新的access_token");
                        return null;
                    }
                } else {
                    logger.error("微信API调用失败: {}", e.getMessage());
                    return null;
                }
            }

            String responseBody = responseEntity.getBody();
            if (responseBody != null) {
                ObjectMapper responseMapper = new ObjectMapper();
                Map<String, Object> response = responseMapper.readValue(responseBody, Map.class);

                if (response.containsKey("link")) {
                    String urlLink = (String) response.get("link");
                    logger.info("生成小程序短链接成功: {}", urlLink);
                    return urlLink;
                } else {
                    logger.error("生成小程序短链接失败: {}", response);
                    return null;
                }
            } else {
                logger.error("生成小程序URL Link失败，返回数据为空");
                return null;
            }
        } catch (Exception e) {
            logger.error("生成小程序URL Link异常", e);
            return null;
        }
    }
    
    @Override
    public String generateMiniProgramCode(String scene, String page, Integer width) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            logger.warn("无法获取access_token，生成小程序码失败");
            return null;
        }

        try {
            // 验证scene参数长度（微信限制32个可见字符）
            if (scene != null && scene.length() > 32) {
                logger.warn("scene参数长度超过32字符，当前长度: {}, 参数: {}", scene.length(), scene);
                scene = scene.substring(0, 32);
                logger.info("截断scene参数为: {}", scene);
            }

            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;

            // 准备请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("scene", scene);
            requestBody.put("page", page);
            requestBody.put("width", width != null ? width : 430);
            requestBody.put("auto_color", false);
            requestBody.put("line_color", Map.of("r", 0, "g", 0, "b", 0));
            requestBody.put("is_hyaline", false);

            logger.info("小程序码生成参数: scene={}, page={}, width={}", scene, page, width);

            // 手动序列化JSON为String，避免框架自动处理导致的Content-Length问题
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

            // 创建专门的RestTemplate，避免chunked编码问题
            RestTemplate wechatRestTemplate = createWechatRestTemplate();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(bodyBytes.length);

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(bodyBytes, headers);

            ResponseEntity<byte[]> responseEntity;
            try {
                responseEntity = wechatRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    byte[].class
                );
            } catch (HttpClientErrorException e) {
                logger.error("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());

                // 如果是412错误，尝试强制刷新access_token并重试
                if (e.getStatusCode().value() == 412) {
                    logger.warn("收到412错误，强制刷新access_token并重试");

                    // 清除Redis缓存的token
                    redisService.del(WX_ACCESS_TOKEN_KEY);

                    // 获取新的access_token（强制刷新）
                    String newAccessToken = getAccessToken(true);
                    if (newAccessToken != null) {
                        String newUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + newAccessToken;
                        try {
                            responseEntity = wechatRestTemplate.exchange(
                                newUrl,
                                HttpMethod.POST,
                                requestEntity,
                                byte[].class
                            );
                            logger.info("使用新access_token重试成功");
                        } catch (HttpClientErrorException retryException) {
                            logger.error("使用新access_token重试仍然失败: {}", retryException.getMessage());
                            return null;
                        }
                    } else {
                        logger.error("无法获取新的access_token");
                        return null;
                    }
                } else {
                    logger.error("微信API调用失败: {}", e.getMessage());
                    return null;
                }
            }

            byte[] imageBytes = responseEntity.getBody();
            if (imageBytes != null && imageBytes.length > 0) {
                logger.info("微信API返回数据长度: {} bytes", imageBytes.length);

                // 检查返回的是否是图片数据（而不是错误JSON）
                if (isPngImage(imageBytes)) {
                    // PNG文件头检查，确认是图片
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    String result = "data:image/png;base64," + base64Image;
                    logger.info("生成小程序码成功，base64长度: {}", result.length());
                    return result;
                } else if (isJsonResponse(imageBytes)) {
                    // 可能是错误响应，尝试解析JSON
                    String errorResponse = new String(imageBytes, StandardCharsets.UTF_8);
                    logger.error("生成小程序码失败，微信API返回错误: {}", errorResponse);
                    return null;
                } else {
                    // 未知格式，直接当作图片处理
                    logger.warn("返回数据格式未知，尝试当作图片处理，数据长度: {} bytes", imageBytes.length);
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    String result = "data:image/png;base64," + base64Image;
                    logger.info("生成小程序码成功（未知格式），base64长度: {}", result.length());
                    return result;
                }
            }

            logger.error("生成小程序码失败，返回数据为空");
            return null;
        } catch (Exception e) {
            logger.error("生成小程序码异常", e);
            return null;
        }
    }
    
    @Override
    public boolean isConfigAvailable() {
        return StringUtils.hasText(appId) && StringUtils.hasText(appSecret);
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

            logger.warn("access_token验证失败: {}", response);
            return false;
        } catch (Exception e) {
            logger.warn("验证access_token时发生异常: {}", e.getMessage());
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
     * 检查字节数组是否为PNG图片
     */
    private boolean isPngImage(byte[] data) {
        if (data == null || data.length < 8) {
            return false;
        }
        // PNG文件头：89 50 4E 47 0D 0A 1A 0A
        return data[0] == (byte) 0x89 &&
               data[1] == (byte) 0x50 &&
               data[2] == (byte) 0x4E &&
               data[3] == (byte) 0x47;
    }

    /**
     * 检查字节数组是否为JSON响应
     */
    private boolean isJsonResponse(byte[] data) {
        if (data == null || data.length < 2) {
            return false;
        }
        // 检查是否以 { 开头（JSON对象）
        return data[0] == (byte) '{';
    }
    
    @Override
    public void refreshAccessToken() {
        logger.info("强制刷新access_token");
        redisService.del(WX_ACCESS_TOKEN_KEY);
        getAccessToken(true);
    }

}
