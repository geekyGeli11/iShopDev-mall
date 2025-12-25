package com.macro.mall.selfcheck.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 自助结算系统配置
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "selfcheck")
public class SelfcheckConfig implements WebMvcConfigurer {
    
    /**
     * 支付配置
     */
    private Payment payment = new Payment();
    
    /**
     * 会员配置
     */
    private Member member = new Member();
    
    /**
     * 扫码配置
     */
    private Scan scan = new Scan();
    
    /**
     * 安全配置
     */
    private Security security = new Security();
    
    @Data
    public static class Payment {
        /**
         * 支付超时时间（分钟）
         */
        private Integer timeout = 5;
        
        /**
         * 支付状态轮询间隔（秒）
         */
        private Integer pollInterval = 2;
        
        /**
         * 最大轮询次数
         */
        private Integer maxPollCount = 150;
        
        /**
         * 是否启用模拟支付
         */
        private Boolean mockEnabled = true;
        
        private Wechat wechat = new Wechat();
        private Alipay alipay = new Alipay();
        
        @Data
        public static class Wechat {
            /**
             * 微信支付应用ID
             */
            private String appId;
            
            /**
             * 微信支付应用密钥
             */
            private String appSecret;
            
            /**
             * 微信支付商户号
             */
            private String mchId;
            
            /**
             * 微信支付商户密钥
             */
            private String mchKey;
            
            /**
             * 支付回调通知地址
             */
            private String notifyUrl;
            
            /**
             * 证书路径（用于退款等操作）
             */
            private String certPath;
            
            /**
             * 证书密钥路径（PKCS12格式）
             */
            private String keyPath;
            
            /**
             * 支付超时时间（分钟）
             */
            private Integer expireMinutes = 5;
        }
        
        @Data
        public static class Alipay {
            /**
             * 支付宝应用ID
             */
            private String appId;
            
            /**
             * 支付宝私钥
             */
            private String privateKey;
            
            /**
             * 应用私钥（别名）
             */
            private String appPrivateKey;
            
            /**
             * 支付宝公钥
             */
            private String publicKey;
            
            /**
             * 支付宝公钥（别名）
             */
            private String alipayPublicKey;
            
            /**
             * 支付回调通知地址
             */
            private String notifyUrl;
            
            /**
             * 支付完成后跳转地址
             */
            private String returnUrl;
            
            /**
             * 支付宝网关地址
             */
            private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
            
            /**
             * 字符编码
             */
            private String charset = "UTF-8";
            
            /**
             * 签名类型
             */
            private String signType = "RSA2";
            
            /**
             * 支付超时时间（分钟）
             */
            private Integer expireMinutes = 5;
        }
    }
    
    @Data
    public static class Member {
        /**
         * 验证码有效期（分钟）
         */
        private Integer verifyCodeExpire = 5;
        
        /**
         * 会话超时时间（秒）
         */
        private Integer sessionTimeout = 3600;
        
        /**
         * 自动退出延迟时间（秒）
         */
        private Integer autoLogoutDelay = 3;
    }
    
    @Data
    public static class Scan {
        /**
         * 扫描超时时间（秒）
         */
        private Integer scanTimeout = 30;
        
        /**
         * 支持的条码格式
         */
        private List<String> supportedFormats = List.of("EAN_13", "EAN_8", "CODE_128", "QR_CODE");
    }
    
    @Data
    public static class Security {
        /**
         * 最大登录尝试次数
         */
        private Integer maxLoginAttempts = 5;
        
        /**
         * 登录锁定持续时间（秒）
         */
        private Long loginLockDuration = 900L;
        
        /**
         * IP封禁持续时间（秒）
         */
        private Long ipBlockDuration = 3600L;
        
        /**
         * 频率限制时间窗口（秒）
         */
        private Long rateLimitWindow = 300L;
        
        /**
         * 时间窗口内最大尝试次数
         */
        private Integer maxAttemptsPerWindow = 3;
    }
    
    /**
     * 配置CORS支持
     * 支持应用的跨域请求
     * 
     * 已禁用：由Gateway统一处理CORS，避免重复配置
     */
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    //     registry.addMapping("/**")
    //             .allowedOriginPatterns("*")
    //             .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //             .allowedHeaders("*")
    //             .allowCredentials(true)
    //             .maxAge(3600);
    // }
} 