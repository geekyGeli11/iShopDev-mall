package com.macro.mall.selfcheck.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 * 
 * @author macro
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "selfcheck.payment.mockEnabled", havingValue = "false")
public class WechatPayConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatPayConfig.class);

    @Autowired
    private SelfcheckConfig selfcheckConfig;

    /**
     * 微信支付配置Bean
     */
    @Bean
    public WxPayConfig wxPayConfig() {
        WxPayConfig config = new WxPayConfig();
        SelfcheckConfig.Payment.Wechat wechatConfig = selfcheckConfig.getPayment().getWechat();
        
        // 基础配置
        config.setAppId(wechatConfig.getAppId());
        config.setMchId(wechatConfig.getMchId());
        config.setMchKey(wechatConfig.getMchKey());
        config.setNotifyUrl(wechatConfig.getNotifyUrl());
        
        // 证书配置（如果提供）
        if (StringUtils.isNotBlank(wechatConfig.getKeyPath())) {
            config.setKeyPath(wechatConfig.getKeyPath());
        }
        
        // 设置为使用沙箱环境（根据需要调整）
        config.setUseSandboxEnv(false);
        
        // 设置签名类型
        config.setSignType("MD5");
        
        LOGGER.info("微信支付配置初始化完成 - AppId: {}, MchId: {}", 
                wechatConfig.getAppId(), wechatConfig.getMchId());
        
        return config;
    }

    /**
     * 微信支付服务Bean
     */
    @Bean
    public WxPayService wxPayService(WxPayConfig wxPayConfig) {
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        
        LOGGER.info("微信支付服务初始化完成");
        return wxPayService;
    }
} 