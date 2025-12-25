package com.macro.mall.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.macro.mall.service.AlipayRefundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * 支付宝退款服务实现
 * Created by macro on 2024/01/01.
 */
@Service
public class AlipayRefundServiceImpl implements AlipayRefundService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayRefundServiceImpl.class);
    
    @Value("${alipay.appId:}")
    private String appId;
    
    @Value("${alipay.appPrivateKey:}")
    private String appPrivateKey;
    
    @Value("${alipay.alipayPublicKey:}")
    private String alipayPublicKey;
    
    @Value("${alipay.gatewayUrl:https://openapi.alipay.com/gateway.do}")
    private String gatewayUrl;
    
    @Value("${alipay.charset:UTF-8}")
    private String charset;
    
    @Value("${alipay.signType:RSA2}")
    private String signType;
    
    private AlipayClient alipayClient;
    
    @PostConstruct
    public void init() {
        if (appId != null && !appId.isEmpty() && 
            appPrivateKey != null && !appPrivateKey.isEmpty()) {
            try {
                alipayClient = new DefaultAlipayClient(
                    gatewayUrl,
                    appId,
                    appPrivateKey,
                    "json",
                    charset,
                    alipayPublicKey,
                    signType
                );
                LOGGER.info("支付宝退款服务初始化成功，AppId: {}", appId);
            } catch (Exception e) {
                LOGGER.error("支付宝退款服务初始化失败", e);
            }
        } else {
            LOGGER.warn("支付宝配置不完整，退款服务未启用");
        }
    }

    @Override
    public boolean processRefund(String outTradeNo, String outRefundNo, BigDecimal refundAmount, String refundReason) {
        return doRefund(outTradeNo, null, outRefundNo, refundAmount, refundReason);
    }
    
    @Override
    public boolean processRefundByTradeNo(String tradeNo, String outRefundNo, BigDecimal refundAmount, String refundReason) {
        return doRefund(null, tradeNo, outRefundNo, refundAmount, refundReason);
    }
    
    /**
     * 执行退款操作
     * @param outTradeNo 商户订单号（与tradeNo二选一）
     * @param tradeNo 支付宝交易号（与outTradeNo二选一）
     */
    private boolean doRefund(String outTradeNo, String tradeNo, String outRefundNo, BigDecimal refundAmount, String refundReason) {
        if (alipayClient == null) {
            LOGGER.error("支付宝客户端未初始化，无法进行退款");
            return false;
        }
        
        try {
            LOGGER.info("开始处理支付宝退款，商户订单号：{}，支付宝交易号：{}，退款单号：{}，退款金额：{}", 
                outTradeNo, tradeNo, outRefundNo, refundAmount);
            
            // 构建退款请求
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            // 商户订单号和支付宝交易号二选一
            if (tradeNo != null && !tradeNo.isEmpty()) {
                model.setTradeNo(tradeNo);
            } else {
                model.setOutTradeNo(outTradeNo);
            }
            model.setOutRequestNo(outRefundNo);
            model.setRefundAmount(refundAmount.toString());
            model.setRefundReason(refundReason);
            
            request.setBizModel(model);
            
            // 调用支付宝退款接口
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                LOGGER.info("支付宝退款成功，商户订单号：{}，支付宝交易号：{}，退款金额：{}", 
                    outTradeNo, tradeNo, refundAmount);
                return true;
            } else {
                LOGGER.error("支付宝退款失败，商户订单号：{}，支付宝交易号：{}，错误码：{}，错误信息：{}", 
                    outTradeNo, tradeNo, response.getSubCode(), response.getSubMsg());
                return false;
            }
            
        } catch (AlipayApiException e) {
            LOGGER.error("支付宝退款异常，商户订单号：{}，支付宝交易号：{}", outTradeNo, tradeNo, e);
            return false;
        }
    }

    @Override
    public String queryRefundStatus(String outTradeNo, String outRefundNo) {
        // TODO: 实现退款状态查询
        return "SUCCESS";
    }
}
