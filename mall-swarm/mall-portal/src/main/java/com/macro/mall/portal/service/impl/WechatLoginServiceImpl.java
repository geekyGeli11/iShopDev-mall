package com.macro.mall.portal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.portal.service.WechatLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

@Service
public class WechatLoginServiceImpl implements WechatLoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatLoginServiceImpl.class);

    // 使用与其他服务相同的Redis key，确保token共享
    private static final String WX_ACCESS_TOKEN_KEY = "wx:mini_program:access_token";
    private static final long ACCESS_TOKEN_EXPIRE = 7000; // 7000秒，比微信官方的7200秒短一些

    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

    @Autowired
    private RedisService redisService;

    @Override
    public String getOpenId(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code
        );
        RestTemplate restTemplate = new RestTemplate();
        try {
            // 调用微信接口
            String response = restTemplate.getForObject(url, String.class);

            // 解析响应内容
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            // 提取 openid
            if (jsonNode.has("openid")) {
                return jsonNode.get("openid").asText();
            } else {
                // 处理微信返回的错误信息
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                throw new RuntimeException("获取OpenID失败：" + errMsg);
            }
        } catch (Exception e) {
            throw new RuntimeException("调用微信接口失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public String getPhoneNumber(String code) {
        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber";

        RestTemplate restTemplate = new RestTemplate();
        try {
            // 1. 获取接口调用凭证access_token（使用Redis缓存）
            String accessToken = getAccessToken();
            if (accessToken == null) {
                throw new RuntimeException("无法获取access_token");
            }

            // 2. 构建请求体
            String requestBody = String.format("{\"code\": \"%s\"}", code);

            // 3. 发起获取手机号请求
            String phoneUrl = url + "?access_token=" + accessToken;
            String phoneResponse = restTemplate.postForObject(phoneUrl, requestBody, String.class);

            // 4. 解析响应结果
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode phoneNode = objectMapper.readTree(phoneResponse);
            
            if (phoneNode.has("errcode") && phoneNode.get("errcode").asInt() != 0) {
                String errMsg = phoneNode.has("errmsg") ? phoneNode.get("errmsg").asText() : "未知错误";
                throw new RuntimeException("获取手机号失败：" + errMsg);
            }
            
            // 5. 从响应中提取手机号
            if (phoneNode.has("phone_info") && phoneNode.get("phone_info").has("phoneNumber")) {
                return phoneNode.get("phone_info").get("phoneNumber").asText();
            } else {
                throw new RuntimeException("响应中未包含手机号信息");
            }
        } catch (Exception e) {
            throw new RuntimeException("获取微信手机号失败：" + e.getMessage(), e);
        }
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

        LOGGER.info("获取新的access_token");

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