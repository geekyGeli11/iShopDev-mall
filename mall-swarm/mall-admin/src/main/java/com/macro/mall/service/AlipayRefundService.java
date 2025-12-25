package com.macro.mall.service;

import java.math.BigDecimal;

/**
 * 支付宝退款服务接口
 * Created by macro on 2024/01/01.
 */
public interface AlipayRefundService {
    
    /**
     * 处理支付宝退款（使用商户订单号）
     * 
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 是否成功
     */
    boolean processRefund(String outTradeNo, String outRefundNo, BigDecimal refundAmount, String refundReason);
    
    /**
     * 处理支付宝退款（使用支付宝交易号，用于兼容旧订单）
     * 
     * @param tradeNo 支付宝交易号
     * @param outRefundNo 商户退款单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 是否成功
     */
    boolean processRefundByTradeNo(String tradeNo, String outRefundNo, BigDecimal refundAmount, String refundReason);
    
    /**
     * 查询退款状态
     * 
     * @param outTradeNo 商户订单号
     * @param outRefundNo 商户退款单号
     * @return 退款状态
     */
    String queryRefundStatus(String outTradeNo, String outRefundNo);
}
