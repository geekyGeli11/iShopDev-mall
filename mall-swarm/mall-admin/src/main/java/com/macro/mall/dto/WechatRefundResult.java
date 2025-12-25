package com.macro.mall.dto;

import java.util.Date;

/**
 * 微信退款结果
 * Created by macro on 2024/01/01.
 */
public class WechatRefundResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String errorMsg;
    
    /**
     * 微信退款单号
     */
    private String refundId;
    
    /**
     * 商户退款单号
     */
    private String outRefundNo;
    
    /**
     * 退款金额（分）
     */
    private Integer refundFee;
    
    /**
     * 退款状态
     */
    private String refundStatus;
    
    /**
     * 退款时间
     */
    private Date refundTime;
    
    /**
     * 退款渠道
     */
    private String refundChannel;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
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

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundChannel() {
        return refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel;
    }
} 