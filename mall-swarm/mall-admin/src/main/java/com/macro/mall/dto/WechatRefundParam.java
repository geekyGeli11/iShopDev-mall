package com.macro.mall.dto;

import java.math.BigDecimal;

/**
 * 微信退款参数
 * Created by macro on 2024/01/01.
 */
public class WechatRefundParam {
    
    /**
     * 商户订单号
     */
    private String outTradeNo;
    
    /**
     * 商户退款单号
     */
    private String outRefundNo;
    
    /**
     * 退款金额（分）
     */
    private Integer refundFee;
    
    /**
     * 订单总金额（分）
     */
    private Integer totalFee;
    
    /**
     * 退款原因
     */
    private String refundDesc;
    
    /**
     * 退款资金来源
     */
    private String refundAccount;

    // Getters and Setters
    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOutRefundNo() {
        return outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }

    public String getRefundAccount() {
        return refundAccount;
    }

    public void setRefundAccount(String refundAccount) {
        this.refundAccount = refundAccount;
    }
} 