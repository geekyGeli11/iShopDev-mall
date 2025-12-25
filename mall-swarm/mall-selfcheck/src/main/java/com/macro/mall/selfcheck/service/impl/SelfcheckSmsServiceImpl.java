package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.selfcheck.config.SelfcheckSmsConfig;
import com.macro.mall.selfcheck.exception.SmsException;
import com.macro.mall.selfcheck.service.SelfcheckSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 自助结算短信服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckSmsServiceImpl implements SelfcheckSmsService {

    private static final String REDIS_KEY_CODE = "selfcheck:sms:code:";
    private static final String REDIS_KEY_RATE_LIMIT = "selfcheck:sms:limit:";

    @Value("${selfcheck.member.verifyCodeExpire:5}")
    private long codeExpireMinutes;

    @Value("${selfcheck.member.sendRateLimit:60}")
    private long rateLimitSeconds;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SelfcheckSmsConfig smsConfig;

    @Override
    public String sendVerifyCode(String phone) {
        // 检查发送频率限制
        if (!canSendCode(phone)) {
            long remainingTime = getRateLimitTtl(phone);
            throw new RuntimeException("发送过于频繁，请等待 " + remainingTime + " 秒后重试");
        }

        // 生成6位数字验证码
        String code = generateVerifyCode();

        // 存储验证码（将分钟转换为秒）
        String codeKey = REDIS_KEY_CODE + phone;
        long codeExpireSeconds = codeExpireMinutes * 60;
        redisTemplate.opsForValue().set(codeKey, code, codeExpireSeconds, TimeUnit.SECONDS);

        // 设置发送频率限制
        String rateLimitKey = REDIS_KEY_RATE_LIMIT + phone;
        redisTemplate.opsForValue().set(rateLimitKey, "1", rateLimitSeconds, TimeUnit.SECONDS);

        // TODO: 这里应该调用实际的短信服务提供商API发送短信
        // 暂时只打印日志，实际环境需要集成阿里云SMS、腾讯云SMS等
        log.info("发送验证码：{}，手机号：{}，有效期：{}秒", code, phone, codeExpireSeconds);

        // 模拟短信发送
        sendSmsMessage(phone, code);

        return code;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        if (phone == null || code == null) {
            return false;
        }

        String codeKey = REDIS_KEY_CODE + phone;
        
        // 安全地获取存储的验证码，避免类型转换异常
        String storedCode = null;
        try {
            Object storedObj = redisTemplate.opsForValue().get(codeKey);
            if (storedObj != null) {
                storedCode = String.valueOf(storedObj);
            }
        } catch (Exception e) {
            log.warn("获取存储验证码时出现异常，手机号：{}，错误：{}", phone, e.getMessage());
            // 清除可能损坏的缓存
            redisTemplate.delete(codeKey);
        }

        boolean isValid = code.equals(storedCode);
        
        // 如果正常验证码验证失败，且启用了模拟模式，则检查固定验证码
        if (!isValid && smsConfig.getMock().getEnabled() && smsConfig.getMock().getFixedCode() != null) {
            String fixedCodeKey = REDIS_KEY_CODE + phone + ":fixed";
            
            // 安全地获取固定验证码
            String fixedCode = null;
            try {
                Object fixedCodeObj = redisTemplate.opsForValue().get(fixedCodeKey);
                if (fixedCodeObj != null) {
                    fixedCode = String.valueOf(fixedCodeObj);
                }
            } catch (Exception e) {
                log.warn("获取固定验证码时出现异常，手机号：{}，错误：{}", phone, e.getMessage());
                // 清除可能损坏的缓存
                redisTemplate.delete(fixedCodeKey);
            }
            
            // 安全地获取配置中的固定验证码
            String configFixedCode = String.valueOf(smsConfig.getMock().getFixedCode());
            
            isValid = code.equals(fixedCode) || code.equals(configFixedCode);
            
            if (isValid) {
                log.info("使用固定验证码验证成功，手机号：{}", phone);
            }
        }
        
        if (isValid) {
            log.info("验证码验证成功，手机号：{}", phone);
        } else {
            log.warn("验证码验证失败，手机号：{}，输入验证码：{}，存储验证码：{}", phone, code, storedCode);
        }

        return isValid;
    }

    @Override
    public long getCodeTtl(String phone) {
        String codeKey = REDIS_KEY_CODE + phone;
        Long ttl = redisTemplate.getExpire(codeKey, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }

    @Override
    public boolean canSendCode(String phone) {
        String rateLimitKey = REDIS_KEY_RATE_LIMIT + phone;
        return !redisTemplate.hasKey(rateLimitKey);
    }

    @Override
    public void clearCode(String phone) {
        String codeKey = REDIS_KEY_CODE + phone;
        redisTemplate.delete(codeKey);
        log.info("清除验证码成功，手机号：{}", phone);
    }

    @Override
    public long getRateLimitTtl(String phone) {
        String rateLimitKey = REDIS_KEY_RATE_LIMIT + phone;
        Long ttl = redisTemplate.getExpire(rateLimitKey, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }

    /**
     * 生成6位数字验证码
     */
    private String generateVerifyCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送短信消息
     */
    private void sendSmsMessage(String phone, String code) {
        String provider = smsConfig.getProvider();
        
        try {
            switch (provider.toLowerCase()) {
                case "mock":
                    sendMockSms(phone, code);
                    break;
                case "aliyun":
                    sendAliyunSms(phone, code);
                    break;
                case "tencent":
                    sendTencentSms(phone, code);
                    break;
                case "huawei":
                    sendHuaweiSms(phone, code);
                    break;
                default:
                    log.warn("未知的短信服务提供商：{}，使用模拟模式", provider);
                    sendMockSms(phone, code);
            }
            
        } catch (SmsException e) {
            // 如果是流控限制且不是模拟模式，尝试降级到模拟模式
            if (e.getErrorType() == SmsException.SmsErrorType.RATE_LIMIT && 
                !"mock".equalsIgnoreCase(provider)) {
                
                log.warn("短信服务商 {} 流控限制，降级到模拟模式，手机号：{}", provider, phone);
                try {
                    sendMockSms(phone, code);
                    return;
                } catch (Exception fallbackException) {
                    log.error("模拟短信发送也失败，手机号：{}", phone, fallbackException);
                }
            }
            
            // 重新抛出原始异常
            throw e;
            
        } catch (Exception e) {
            log.error("发送短信失败，手机号：{}，验证码：{}，服务商：{}", phone, code, provider, e);
            
            // 如果不是模拟模式，尝试降级到模拟模式
            if (!"mock".equalsIgnoreCase(provider)) {
                log.warn("短信服务异常，降级到模拟模式，手机号：{}", phone);
                try {
                    sendMockSms(phone, code);
                    return;
                } catch (Exception fallbackException) {
                    log.error("模拟短信发送也失败，手机号：{}", phone, fallbackException);
                }
            }
            
            throw new SmsException("短信发送失败，请重试", "SERVICE_ERROR", e);
        }
    }

    /**
     * 模拟发送短信
     */
    private void sendMockSms(String phone, String code) {
        String message = String.format("您的验证码是：%s，有效期%d分钟，请勿泄露给他人。", 
                code, codeExpireMinutes);
        
        log.info("模拟发送短信成功 - 手机号：{}，内容：{}", phone, message);
        
        // 在模拟模式下，如果设置了固定验证码，则验证时允许使用固定验证码
        if (smsConfig.getMock().getEnabled() && smsConfig.getMock().getFixedCode() != null) {
            String fixedCodeKey = REDIS_KEY_CODE + phone + ":fixed";
            long codeExpireSeconds = codeExpireMinutes * 60;
            // 确保存储为String类型
            String fixedCodeToStore = String.valueOf(smsConfig.getMock().getFixedCode());
            redisTemplate.opsForValue().set(fixedCodeKey, fixedCodeToStore, 
                    codeExpireSeconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 阿里云短信发送
     */
    private void sendAliyunSms(String phone, String code) {
        try {
            log.info("使用阿里云发送短信 - 手机号：{}，验证码：{}", phone, code);
            
            // 创建阿里云客户端
            com.aliyun.dysmsapi20170525.Client client = createAliyunSmsClient();
            
            // 构建短信发送请求
            com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = 
                new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsConfig.getAliyun().getSignName())
                    .setTemplateCode(smsConfig.getAliyun().getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
            
            // 发送短信
            com.aliyun.dysmsapi20170525.models.SendSmsResponse response = client.sendSms(sendSmsRequest);
            
            // 检查发送结果
            if ("OK".equals(response.getBody().getCode())) {
                log.info("阿里云短信发送成功 - 手机号：{}，验证码：{}，RequestId：{}", 
                        phone, code, response.getBody().getRequestId());
            } else {
                String errorCode = response.getBody().getCode();
                String errorMessage = response.getBody().getMessage();
                
                log.error("阿里云短信发送失败 - 手机号：{}，错误代码：{}，错误信息：{}", 
                        phone, errorCode, errorMessage);
                
                // 根据错误类型给出更友好的提示
                String userMessage = generateUserFriendlyMessage(errorCode, errorMessage);
                throw new SmsException(userMessage, errorCode + ":" + errorMessage);
            }
            
        } catch (SmsException e) {
            // 重新抛出SMS异常
            throw e;
        } catch (Exception e) {
            log.error("阿里云短信发送异常 - 手机号：{}，验证码：{}", phone, code, e);
            throw new SmsException("短信服务暂时不可用，请稍后重试", "ALIYUN_ERROR", e);
        }
    }
    
    /**
     * 根据阿里云错误码生成用户友好的错误信息
     */
    private String generateUserFriendlyMessage(String errorCode, String errorMessage) {
        if (errorMessage == null) {
            errorMessage = "";
        }
        
        // 流控相关错误
        if (errorMessage.contains("流控") || errorMessage.contains("Permits") || 
            errorMessage.contains("限流") || errorMessage.contains("限制")) {
            if (errorMessage.contains("天级")) {
                return "今日验证码发送次数已达上限，请明天再试或联系客服";
            } else if (errorMessage.contains("小时")) {
                return "验证码发送过于频繁，请1小时后重试";
            } else {
                return "验证码发送过于频繁，请稍后重试";
            }
        }
        
        // 余额相关错误
        if (errorMessage.contains("余额") || errorMessage.contains("欠费")) {
            return "短信服务暂时不可用，请联系客服";
        }
        
        // 模板相关错误
        if (errorMessage.contains("模板") || errorMessage.contains("签名")) {
            return "短信服务配置异常，请联系客服";
        }
        
        // 手机号相关错误
        if (errorMessage.contains("手机号") || errorMessage.contains("号码")) {
            return "手机号格式不正确，请检查后重试";
        }
        
        // 默认错误信息
        return "验证码发送失败，请稍后重试";
    }
    
    /**
     * 创建阿里云短信客户端
     */
    private com.aliyun.dysmsapi20170525.Client createAliyunSmsClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(smsConfig.getAliyun().getAccessKeyId())
                .setAccessKeySecret(smsConfig.getAliyun().getAccessKeySecret())
                .setRegionId(smsConfig.getAliyun().getRegion())
                .setEndpoint("dysmsapi.aliyuncs.com");
        
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 腾讯云短信发送
     * TODO: 集成腾讯云SMS SDK
     */
    private void sendTencentSms(String phone, String code) {
        log.info("使用腾讯云发送短信 - 手机号：{}，验证码：{}", phone, code);
        // 这里集成腾讯云SMS SDK
    }

    /**
     * 华为云短信发送
     * TODO: 集成华为云SMS SDK
     */
    private void sendHuaweiSms(String phone, String code) {
        log.info("使用华为云发送短信 - 手机号：{}，验证码：{}", phone, code);
        // 这里集成华为云SMS SDK
    }
} 