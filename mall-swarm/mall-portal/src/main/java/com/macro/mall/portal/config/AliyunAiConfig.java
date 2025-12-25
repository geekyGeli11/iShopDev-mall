package com.macro.mall.portal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云万相API配置
 * Created by macro on 2024/12/20.
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun")
public class AliyunAiConfig {
    
    /**
     * 阿里云万相API密钥
     */
    private String apiKey;
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
