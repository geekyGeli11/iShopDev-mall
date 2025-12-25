package com.macro.mall.selfcheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自助结算短信服务配置
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "selfcheck.sms")
public class SelfcheckSmsConfig {

    /**
     * 短信服务提供商
     */
    private String provider = "mock";

    /**
     * 模拟模式配置
     */
    private MockConfig mock = new MockConfig();

    /**
     * 阿里云SMS配置
     */
    private AliyunConfig aliyun = new AliyunConfig();

    /**
     * 腾讯云SMS配置
     */
    private TencentConfig tencent = new TencentConfig();

    /**
     * 华为云SMS配置
     */
    private HuaweiConfig huawei = new HuaweiConfig();

    @Data
    public static class MockConfig {
        /**
         * 是否启用模拟模式
         */
        private Boolean enabled = true;

        /**
         * 固定验证码（用于测试）
         */
        private String fixedCode = "123456";
    }

    @Data
    public static class AliyunConfig {
        /**
         * Access Key ID
         */
        private String accessKeyId;

        /**
         * Access Key Secret
         */
        private String accessKeySecret;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 短信模板代码
         */
        private String templateCode;

        /**
         * 区域
         */
        private String region = "cn-hangzhou";
        
        /**
         * API端点
         */
        private String endpoint = "dysmsapi.aliyuncs.com";
    }

    @Data
    public static class TencentConfig {
        /**
         * Secret ID
         */
        private String secretId;

        /**
         * Secret Key
         */
        private String secretKey;

        /**
         * SDK App ID
         */
        private String sdkAppId;

        /**
         * 短信签名
         */
        private String signName;

        /**
         * 短信模板ID
         */
        private String templateId;
    }

    @Data
    public static class HuaweiConfig {
        /**
         * App Key
         */
        private String appKey;

        /**
         * App Secret
         */
        private String appSecret;

        /**
         * 发送方
         */
        private String sender;

        /**
         * 短信模板ID
         */
        private String templateId;

        /**
         * 签名
         */
        private String signature;

        /**
         * 服务地址
         */
        private String url = "https://smsapi.cn-north-4.myhuaweicloud.com:443";
    }
} 