package com.macro.mall.portal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.portal.service.WxMiniProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序服务实现类
 * @author macro
 * @since 1.0.0
 */
@Service
public class WxMiniProgramServiceImpl implements WxMiniProgramService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WxMiniProgramServiceImpl.class);
    
    // Redis中存储access_token的key
    private static final String WX_ACCESS_TOKEN_KEY = "wx:access_token";
    // access_token过期时间（秒），设置为110分钟，比微信官方的120分钟短一些
    private static final long ACCESS_TOKEN_EXPIRE = 110 * 60;
    
    @Value("${wechat.app-id}")
    private String appId;
    
    @Value("${wechat.app-secret}")
    private String appSecret;
    
    @Autowired
    private RedisService redisService;
    
    @Override
    public InputStream generateMiniProgramCode(String page, String scene) {
        try {
            LOGGER.info("开始生成小程序码 - 页面: {}, 场景参数: {}", page, scene);
            
            // 验证scene参数长度（微信限制32个可见字符）
            if (scene != null && scene.length() > 32) {
                LOGGER.warn("scene参数长度超过32字符，当前长度: {}, 参数: {}", scene.length(), scene);
                scene = scene.substring(0, 32);
                LOGGER.info("截断scene参数为: {}", scene);
            }
            
            // 验证配置
            if (appId == null || appId.isEmpty()) {
                throw new RuntimeException("微信小程序AppId未配置");
            }
            if (appSecret == null || appSecret.isEmpty()) {
                throw new RuntimeException("微信小程序AppSecret未配置");
            }
            
            LOGGER.info("使用的AppId: {}", appId);
            
            // 获取微信小程序接口调用凭证
            String accessToken = getAccessToken();
            LOGGER.info("获取到AccessToken: {}", accessToken.substring(0, Math.min(accessToken.length(), 20)) + "...");
            
            // 调用微信接口生成小程序码
            String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
            
            // 准备请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("scene", scene); // 场景参数，最大32个可见字符
            
            // 如果页面路径为空，设置为默认首页
            if (page != null && !page.isEmpty()) {
                params.put("page", page); // 小程序页面路径
            } else {
                // 尝试使用不同的页面路径
                params.put("page", "pages/new_index/index"); // 默认首页
            }
            
            params.put("width", 280); // 二维码宽度，单位 px，默认430
            params.put("auto_color", false); // 自动配置线条颜色
            
            Map<String, Object> lineColor = new HashMap<>();
            lineColor.put("r", 0);
            lineColor.put("g", 0);
            lineColor.put("b", 0);
            params.put("line_color", lineColor);
            
            params.put("is_hyaline", false); // 是否需要透明底色
            
            LOGGER.info("小程序码生成参数: scene={}, page={}", scene, page);
            
            // 手动序列化JSON为String，避免框架自动处理导致的Content-Length问题
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(params);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
            
            // 创建RestTemplate，明确禁用chunked编码
            RestTemplate restTemplate = createWechatRestTemplate();
            
            // 设置必要的请求头，特别是Content-Length
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            // 关键修复：手动设置Content-Length，避免Spring Framework 6.1不自动设置的问题
            headers.setContentLength(bodyBytes.length);
            // 确保不使用chunked编码
            headers.set("Transfer-Encoding", "identity");
            
            // 创建HttpEntity，使用String作为请求体而不是Map
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
            
            LOGGER.debug("请求体长度: {} bytes", bodyBytes.length);
            
            ResponseEntity<byte[]> responseEntity;
            
            try {
                responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        byte[].class
                );
            } catch (HttpClientErrorException e) {
                LOGGER.error("微信API调用失败 - HTTP状态码: {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
                
                // 如果是412错误，尝试不同的处理方案
                if (e.getStatusCode().value() == 412) {
                    LOGGER.warn("收到412错误，可能原因：1.access_token无效 2.小程序未发布 3.页面路径不存在 4.AppId权限不足");
                    
                    // 第一步：强制刷新access_token并重试
                    LOGGER.info("强制刷新access_token并重试");
                    String newAccessToken = getAccessToken(true);
                    String newUrl = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + newAccessToken;
                    
                    try {
                        responseEntity = restTemplate.exchange(
                                newUrl,
                                HttpMethod.POST,
                                requestEntity,
                                byte[].class
                        );
                        LOGGER.info("使用新access_token重试成功");
                    } catch (HttpClientErrorException retryException) {
                        LOGGER.error("使用新access_token重试仍然失败: {}", retryException.getMessage());
                        
                        // 第二步：如果仍然是412错误，尝试使用空页面路径
                        if (retryException.getStatusCode().value() == 412 && params.containsKey("page")) {
                            LOGGER.info("尝试使用空页面路径重新生成");
                            params.remove("page"); // 不指定页面，让微信使用默认页面
                            
                            // 重新序列化JSON
                            String retryJsonBody = objectMapper.writeValueAsString(params);
                            byte[] retryBodyBytes = retryJsonBody.getBytes(StandardCharsets.UTF_8);
                            headers.setContentLength(retryBodyBytes.length);
                            
                            HttpEntity<String> finalRetryRequestEntity = new HttpEntity<>(retryJsonBody, headers);
                            try {
                                responseEntity = restTemplate.exchange(
                                        newUrl,
                                        HttpMethod.POST,
                                        finalRetryRequestEntity,
                                        byte[].class
                                );
                                LOGGER.info("使用空页面路径重试成功");
                            } catch (Exception finalException) {
                                LOGGER.error("最终重试仍然失败: {}", finalException.getMessage());
                                
                                // 提供详细的错误信息和解决建议
                                throw new RuntimeException(
                                    "生成小程序码失败 (HTTP 412)，可能原因：\n" +
                                    "1. 小程序未发布体验版或正式版，请在微信开发者工具中发布\n" +
                                    "2. 页面路径 '" + page + "' 不存在，请检查页面路径\n" +
                                    "3. AppId '" + appId + "' 权限不足，请检查小程序配置\n" +
                                    "4. 已尝试刷新access_token和使用默认页面，仍然失败"
                                );
                            }
                        } else {
                            throw new RuntimeException("生成小程序码失败: " + retryException.getMessage(), retryException);
                        }
                    }
                } else {
                    throw new RuntimeException("生成小程序码失败: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                LOGGER.error("调用微信API时发生网络错误: {}", e.getMessage());
                throw new RuntimeException("生成小程序码失败: " + e.getMessage(), e);
            }
            
            byte[] responseBody = responseEntity.getBody();
            
            if (responseBody == null || responseBody.length == 0) {
                throw new RuntimeException("微信API返回空响应");
            }
            
            LOGGER.debug("微信API响应长度: {} bytes", responseBody.length);
            
            // 微信返回错误信息时会返回JSON，检查返回内容是否为JSON
            if (isJsonResponse(responseBody)) {
                String errorMessage = new String(responseBody, StandardCharsets.UTF_8);
                LOGGER.error("微信API返回错误JSON: {}", errorMessage);
                
                // 解析错误码
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(errorMessage);
                    int errcode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;
                    String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                    
                    LOGGER.error("微信API错误码: {}, 错误信息: {}", errcode, errmsg);
                    
                    // 根据错误码提供具体的错误信息
                    String detailedError = getDetailedErrorMessage(errcode, errmsg);
                    
                    // 如果是token相关错误，强制刷新token并重试
                    if (errcode == 40001 || errcode == 41001 || errcode == 42001) {
                        LOGGER.info("access_token相关错误，强制刷新token并重试");
                        redisService.del(WX_ACCESS_TOKEN_KEY);
                        String newAccessToken = getAccessToken(true);
                        LOGGER.info("已获取新的access_token，递归重试");
                        return generateMiniProgramCode(page, scene); // 递归重试
                    }
                    
                    throw new RuntimeException(detailedError);
                } catch (Exception e) {
                    throw new RuntimeException("生成小程序码失败: " + errorMessage);
                }
            }
            
            LOGGER.info("小程序码生成成功");
            // 返回图片数据流
            return new ByteArrayInputStream(responseBody);
        } catch (Exception e) {
            LOGGER.error("生成小程序码时发生异常", e);
            throw new RuntimeException("生成小程序码失败: " + e.getMessage(), e);
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
     * 获取微信接口调用凭证，优先从Redis缓存获取
     */
    private String getAccessToken() {
        return getAccessToken(false);
    }
    
    /**
     * 获取微信接口调用凭证（支持强制刷新）
     */
    private String getAccessToken(boolean forceRefresh) {
        // 如果不是强制刷新，先从缓存获取
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
        // 缓存中没有或token无效，重新请求
        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret
        );
        
        LOGGER.debug("请求微信access_token接口");
        
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        
        LOGGER.debug("微信access_token接口响应: {}", response);
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("access_token")) {
                String accessToken = jsonNode.get("access_token").asText();
                // 存入Redis，设置过期时间比微信官方的时间短一些，确保token在使用时有效
                redisService.set(WX_ACCESS_TOKEN_KEY, accessToken, ACCESS_TOKEN_EXPIRE);
                LOGGER.info("成功获取新的access_token");
                return accessToken;
            } else {
                int errcode = jsonNode.has("errcode") ? jsonNode.get("errcode").asInt() : -1;
                String errMsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                throw new RuntimeException(String.format("获取access_token失败 - 错误码: %d, 错误信息: %s", errcode, errMsg));
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
            String validateUrl = String.format(
                    "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=%s",
                    accessToken
            );
            
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(validateUrl, String.class);
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // 如果返回成功（有ip_list字段）或者错误码为0，说明token有效
            boolean isValid = jsonNode.has("ip_list") || 
                            (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() == 0);
            
            if (!isValid && jsonNode.has("errcode")) {
                int errcode = jsonNode.get("errcode").asInt();
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                LOGGER.warn("access_token验证失败 - 错误码: {}, 错误信息: {}", errcode, errmsg);
            }
            
            return isValid;
        } catch (Exception e) {
            LOGGER.warn("验证access_token时发生异常: {}", e.getMessage());
            return false; // 验证异常时认为token无效
        }
    }
    
    /**
     * 判断返回内容是否为JSON格式（微信返回错误信息时会是JSON格式）
     */
    private boolean isJsonResponse(byte[] data) {
        try {
            String content = new String(data);
            if (content.startsWith("{") && content.endsWith("}")) {
                new ObjectMapper().readTree(content);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成小程序码并返回字节数组
     * 
     * @param page 小程序页面路径
     * @param scene 场景参数
     * @return 小程序码字节数组
     */
    @Override
    public byte[] generateMiniProgramCodeBytes(String page, String scene) {
        try {
            InputStream inputStream = generateMiniProgramCode(page, scene);
            return inputStream.readAllBytes();
        } catch (Exception e) {
            LOGGER.error("生成小程序码字节数组失败", e);
            throw new RuntimeException("生成小程序码字节数组失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据微信错误码返回详细的错误信息
     */
    private String getDetailedErrorMessage(int errcode, String errmsg) {
        switch (errcode) {
            case 40001:
                return "access_token无效或过期，请重试";
            case 40013:
                return "AppId无效，请检查微信小程序配置";
            case 40097:
                return "参数错误，可能页面路径不存在或格式不正确";
            case 41002:
                return "AppSecret缺失，请检查微信小程序配置";
            case 41003:
                return "AppId缺失，请检查微信小程序配置";
            case 45009:
                return "接口调用超过限额，请稍后再试";
            case 85088:
                return "小程序未发布或页面不存在，请确保小程序已发布到体验版或正式版";
            default:
                return String.format("生成小程序码失败 - 错误码: %d, 错误信息: %s", errcode, errmsg);
        }
    }
} 