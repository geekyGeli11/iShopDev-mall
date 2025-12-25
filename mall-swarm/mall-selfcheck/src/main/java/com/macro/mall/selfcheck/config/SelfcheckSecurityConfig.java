package com.macro.mall.selfcheck.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自助结算安全配置
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "selfcheck.security")
public class SelfcheckSecurityConfig {

    /**
     * 最大登录尝试次数
     */
    private Integer maxLoginAttempts = 5;

    /**
     * 登录锁定时长（秒）
     */
    private Long loginLockDuration = 900L;

    /**
     * IP锁定时长（秒）
     */
    private Long ipBlockDuration = 3600L;

    /**
     * 频率限制窗口期（秒）
     */
    private Long rateLimitWindow = 300L;

    /**
     * 窗口期内最大尝试次数
     */
    private Integer maxAttemptsPerWindow = 3;

    /**
     * 设备信任期（天）
     */
    private Integer deviceTrustDays = 30;

    /**
     * 异常登录检测开关
     */
    private Boolean abnormalLoginDetection = true;

    /**
     * 地理位置检测开关
     */
    private Boolean geoLocationDetection = false;
} 