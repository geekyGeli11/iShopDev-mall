package com.macro.mall.portal.config;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Binary Wang
 */
@Configuration
@ConditionalOnClass(WxPayService.class)
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayConfiguration {
    @Autowired
    private WxPayProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public WxPayService wxService() {
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(this.properties.getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(this.properties.getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(this.properties.getMchKey()));
        payConfig.setSubAppId(StringUtils.trimToNull(this.properties.getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(this.properties.getSubMchId()));
        payConfig.setKeyPath(StringUtils.trimToNull(this.properties.getKeyPath()));

        // 设置私钥文件路径（API v3需要）
        String privateKeyPath = StringUtils.trimToNull(this.properties.getPrivateKeyPath());
        if (privateKeyPath != null) {
            payConfig.setPrivateKeyPath(privateKeyPath);
        }

        // 设置商户证书文件路径（API v3需要）
        String privateCertPath = StringUtils.trimToNull(this.properties.getPrivateCertPath());
        if (privateCertPath != null) {
            payConfig.setPrivateCertPath(privateCertPath);
        }

        // 设置API v3密钥
        String apiV3Key = StringUtils.trimToNull(this.properties.getApiV3Key());
        if (apiV3Key != null) {
            payConfig.setApiV3Key(apiV3Key);
        }

        // 设置商户证书序列号（可选）
        String certSerialNo = StringUtils.trimToNull(this.properties.getMerchantSerialNumber());
        if (certSerialNo != null) {
            payConfig.setCertSerialNo(certSerialNo);
        }

        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);

        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

}
