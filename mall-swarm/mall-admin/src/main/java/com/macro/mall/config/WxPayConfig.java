package com.macro.mall.config;

import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 * Created by macro on 2024/01/01.
 */
@Configuration
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayConfig {
    
    /**
     * 微信小程序AppID
     */
    private String appId;
    
    /**
     * 微信支付商户号
     */
    private String mchId;
    
    /**
     * 微信支付API密钥
     */
    private String mchKey;
    
    /**
     * 支付回调地址
     */
    private String notifyUrl;
    
    /**
     * 证书路径（退款需要）
     */
    private String keyPath;

    // Getters and Setters
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    /**
     * 创建微信支付服务Bean
     */
    @Bean
    public WxPayService wxPayService() {
        com.github.binarywang.wxpay.config.WxPayConfig payConfig = new com.github.binarywang.wxpay.config.WxPayConfig();
        payConfig.setAppId(this.appId);
        payConfig.setMchId(this.mchId);
        payConfig.setMchKey(this.mchKey);
        payConfig.setKeyPath(this.keyPath);
        payConfig.setNotifyUrl(this.notifyUrl);
        payConfig.setTradeType("NATIVE");
        payConfig.setSignType("MD5");

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

    @Override
    public String toString() {
        return "WxPayConfig{" +
                "appId='" + appId + '\'' +
                ", mchId='" + mchId + '\'' +
                ", mchKey='***'" + // 不输出敏感信息
                ", notifyUrl='" + notifyUrl + '\'' +
                ", keyPath='" + keyPath + '\'' +
                '}';
    }
}