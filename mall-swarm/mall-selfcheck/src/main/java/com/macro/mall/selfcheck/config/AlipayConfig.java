package com.macro.mall.selfcheck.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝支付配置
 * 
 * @author macro
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "selfcheck.payment.mockEnabled", havingValue = "false")
public class AlipayConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayConfig.class);

    @Autowired
    private SelfcheckConfig selfcheckConfig;

    /**
     * 支付宝客户端Bean
     */
    @Bean
    public AlipayClient alipayClient() {
        SelfcheckConfig.Payment.Alipay alipayConfig = selfcheckConfig.getPayment().getAlipay();
        
        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getAppPrivateKey(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
            );
            
            LOGGER.info("支付宝客户端初始化完成 - AppId: {}, GatewayUrl: {}", 
                    alipayConfig.getAppId(), alipayConfig.getGatewayUrl());
            
            return alipayClient;
            
        } catch (Exception e) {
            LOGGER.error("支付宝客户端初始化失败", e);
            throw new RuntimeException("支付宝客户端初始化失败", e);
        }
    }
} 