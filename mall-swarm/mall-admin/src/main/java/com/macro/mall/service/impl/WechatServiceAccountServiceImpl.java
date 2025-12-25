package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.UmsAdminMapper;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.WechatServiceAccountService;
import com.macro.mall.utils.WechatMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信服务号服务实现类
 */
@Service
public class WechatServiceAccountServiceImpl implements WechatServiceAccountService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatServiceAccountServiceImpl.class);
    
    // Redis缓存key
    private static final String WX_SERVICE_ACCESS_TOKEN_KEY = "wx:service_account:access_token";
    // access_token过期时间（秒），设置为110分钟，比微信官方的120分钟短一些
    private static final long ACCESS_TOKEN_EXPIRE = 110 * 60;
    
    @Value("${wechat.service-account.app-id}")
    private String appId;
    
    @Value("${wechat.service-account.app-secret}")
    private String appSecret;
    
    @Value("${wechat.service-account.token}")
    private String token;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private UmsAdminMapper adminMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 创建专门用于微信API的RestTemplate实例
     * 针对微信API的特殊要求进行优化，避免412错误
     */
    private RestTemplate createWechatRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        return new RestTemplate(factory);
    }
    
    @Override
    public String getAccessToken() {
        // 先从缓存获取
        Object cachedToken = redisService.get(WX_SERVICE_ACCESS_TOKEN_KEY);
        if (cachedToken != null) {
            String tokenStr = cachedToken.toString();
            LOGGER.debug("从Redis缓存获取到服务号access_token");
            return tokenStr;
        }
        
        // 缓存中没有，重新获取
        return refreshAccessToken();
    }
    
    @Override
    public String refreshAccessToken() {
        LOGGER.info("获取新的服务号access_token");
        
        String url = String.format(
            "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
            appId, appSecret
        );
        
        try {
            RestTemplate restTemplate = createWechatRestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("access_token")) {
                String accessToken = jsonNode.get("access_token").asText();
                // 存入Redis
                redisService.set(WX_SERVICE_ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE);
                LOGGER.info("成功获取新的服务号access_token并缓存到Redis");
                return accessToken;
            } else {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;
                LOGGER.error("获取服务号access_token失败: errcode={}, errmsg={}", errCode, errMsg);
                throw new RuntimeException("获取access_token失败：" + errMsg);
            }
        } catch (Exception e) {
            LOGGER.error("获取服务号access_token异常", e);
            throw new RuntimeException("获取access_token失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateQRCode(Long adminId, int expireSeconds) {
        if (adminId == null) {
            Asserts.fail("管理员ID不能为空");
        }
        
        // 限制过期时间最大30天
        if (expireSeconds > 2592000) {
            expireSeconds = 2592000;
        }
        
        String accessToken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;
        
        // 构建请求体 - 临时二维码
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("expire_seconds", expireSeconds);
        requestBody.put("action_name", "QR_STR_SCENE");
        
        Map<String, Object> actionInfo = new HashMap<>();
        Map<String, Object> scene = new HashMap<>();
        scene.put("scene_str", String.valueOf(adminId));
        actionInfo.put("scene", scene);
        requestBody.put("action_info", actionInfo);
        
        try {
            RestTemplate restTemplate = createWechatRestTemplate();
            
            // 将请求体转为JSON字符串，手动设置Content-Length避免chunked编码
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(bodyBytes.length);
            
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.has("ticket")) {
                String ticket = jsonNode.get("ticket").asText();
                // 返回二维码图片URL
                return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
            } else {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;
                
                // 如果是token过期，刷新后重试
                if (errCode == 40001 || errCode == 42001) {
                    LOGGER.warn("access_token过期，刷新后重试");
                    redisService.del(WX_SERVICE_ACCESS_TOKEN_KEY);
                    return generateQRCode(adminId, expireSeconds);
                }
                
                LOGGER.error("生成二维码失败: errcode={}, errmsg={}", errCode, errMsg);
                throw new RuntimeException("生成二维码失败：" + errMsg);
            }
        } catch (Exception e) {
            LOGGER.error("生成二维码异常", e);
            throw new RuntimeException("生成二维码失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> getUserInfo(String openId) {
        if (StrUtil.isEmpty(openId)) {
            return null;
        }
        
        String accessToken = getAccessToken();
        String url = String.format(
            "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN",
            accessToken, openId
        );
        
        try {
            RestTemplate restTemplate = createWechatRestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                LOGGER.error("获取用户信息失败: {}", errMsg);
                return null;
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("openid", jsonNode.has("openid") ? jsonNode.get("openid").asText() : null);
            userInfo.put("nickname", jsonNode.has("nickname") ? jsonNode.get("nickname").asText() : null);
            userInfo.put("headimgurl", jsonNode.has("headimgurl") ? jsonNode.get("headimgurl").asText() : null);
            userInfo.put("subscribe", jsonNode.has("subscribe") ? jsonNode.get("subscribe").asInt() : 0);
            
            return userInfo;
        } catch (Exception e) {
            LOGGER.error("获取用户信息异常", e);
            return null;
        }
    }

    
    @Override
    public void bindWechat(Long adminId, String openId) {
        if (adminId == null || StrUtil.isEmpty(openId)) {
            Asserts.fail("参数不能为空");
        }
        
        // 获取用户信息
        Map<String, Object> userInfo = getUserInfo(openId);
        
        UmsAdmin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin == null) {
            LOGGER.error("绑定微信失败：管理员不存在，adminId={}", adminId);
            Asserts.fail("管理员不存在");
        }
        
        // 更新管理员的微信信息
        UmsAdmin updateAdmin = new UmsAdmin();
        updateAdmin.setId(adminId);
        updateAdmin.setWxServiceOpenid(openId);
        updateAdmin.setWxServiceBindtime(new Date());
        
        if (userInfo != null) {
            String nickname = (String) userInfo.get("nickname");
            String headimgurl = (String) userInfo.get("headimgurl");
            updateAdmin.setWxServiceNickname(nickname);
            updateAdmin.setWxServiceHeadimg(headimgurl);
        }
        
        adminMapper.updateByPrimaryKeySelective(updateAdmin);
        LOGGER.info("管理员微信绑定成功：adminId={}, openId={}", adminId, openId);
    }
    
    @Override
    public void unbindWechat(Long adminId) {
        if (adminId == null) {
            Asserts.fail("管理员ID不能为空");
        }
        
        UmsAdmin admin = adminMapper.selectByPrimaryKey(adminId);
        if (admin == null) {
            Asserts.fail("管理员不存在");
        }
        
        // 清除微信绑定信息
        UmsAdmin updateAdmin = new UmsAdmin();
        updateAdmin.setId(adminId);
        updateAdmin.setWxServiceOpenid(null);
        updateAdmin.setWxServiceNickname(null);
        updateAdmin.setWxServiceHeadimg(null);
        updateAdmin.setWxServiceBindtime(null);
        
        adminMapper.updateByPrimaryKeySelective(updateAdmin);
        LOGGER.info("管理员微信解绑成功：adminId={}", adminId);
    }
    
    @Override
    public Long sendTemplateMessage(String openId, String templateId, Map<String, String> data, String url) {
        if (StrUtil.isEmpty(openId) || StrUtil.isEmpty(templateId)) {
            LOGGER.warn("发送模板消息参数不完整");
            return null;
        }
        
        String accessToken = getAccessToken();
        String apiUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("touser", openId);
        requestBody.put("template_id", templateId);
        
        if (StrUtil.isNotEmpty(url)) {
            requestBody.put("url", url);
        }
        
        // 构建模板数据
        Map<String, Object> templateData = new HashMap<>();
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                Map<String, String> valueMap = new HashMap<>();
                valueMap.put("value", entry.getValue());
                templateData.put(entry.getKey(), valueMap);
            }
        }
        requestBody.put("data", templateData);
        
        try {
            RestTemplate restTemplate = createWechatRestTemplate();
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(bodyBytes.length);
            
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;
            if (errCode == 0) {
                Long msgId = jsonNode.has("msgid") ? jsonNode.get("msgid").asLong() : null;
                LOGGER.info("模板消息发送成功：openId={}, msgId={}", openId, msgId);
                return msgId;
            } else {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                LOGGER.error("模板消息发送失败：errcode={}, errmsg={}", errCode, errMsg);
                
                // token过期重试
                if (errCode == 40001 || errCode == 42001) {
                    redisService.del(WX_SERVICE_ACCESS_TOKEN_KEY);
                    return sendTemplateMessage(openId, templateId, data, url);
                }
                
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("发送模板消息异常", e);
            return null;
        }
    }
    
    @Override
    public void sendTextMessage(String openId, String content) {
        if (StrUtil.isEmpty(openId) || StrUtil.isEmpty(content)) {
            return;
        }
        
        String accessToken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("touser", openId);
        requestBody.put("msgtype", "text");
        
        Map<String, String> text = new HashMap<>();
        text.put("content", content);
        requestBody.put("text", text);
        
        try {
            RestTemplate restTemplate = createWechatRestTemplate();
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentLength(bodyBytes.length);
            
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            int errCode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;
            if (errCode != 0) {
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                LOGGER.warn("发送客服消息失败：errcode={}, errmsg={}", errCode, errMsg);
            }
        } catch (Exception e) {
            LOGGER.error("发送客服消息异常", e);
        }
    }
    
    @Override
    public boolean verifySignature(String signature, String timestamp, String nonce) {
        return WechatMessageUtil.verifySignature(signature, timestamp, nonce, token);
    }
}
