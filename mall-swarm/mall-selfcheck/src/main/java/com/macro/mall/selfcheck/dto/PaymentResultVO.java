package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付结果信息
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "支付结果信息")
public class PaymentResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "支付ID", description = "支付唯一标识")
    private String paymentId;

    @Schema(title = "订单ID", description = "订单ID")
    private Long orderId;

    @Schema(title = "订单编号", description = "订单编号")
    private String orderSn;

    @Schema(title = "支付方式", description = "支付方式：WECHAT-微信支付，ALIPAY-支付宝")
    private String payType;

    @Schema(title = "支付状态", description = "支付状态：PENDING-待支付，SUCCESS-支付成功，FAILED-支付失败，TIMEOUT-支付超时")
    private String payStatus;

    @Schema(title = "支付金额", description = "支付金额，单位：元")
    private BigDecimal amount;

    @Schema(title = "实际支付金额", description = "实际支付金额，单位：元")
    private BigDecimal paidAmount;

    @Schema(title = "第三方支付流水号", description = "微信/支付宝支付流水号")
    private String transactionId;

    @Schema(title = "支付时间", description = "支付完成时间")
    private Date payTime;

    @Schema(title = "创建时间", description = "支付创建时间")
    private Date createTime;

    @Schema(title = "支付二维码", description = "支付二维码URL（如果是二维码支付）")
    private String qrCodeUrl;

    @Schema(title = "二维码内容", description = "二维码内容文本")
    private String qrCodeText;

    @Schema(title = "失败原因", description = "支付失败原因")
    private String failureReason;

    @Schema(title = "终端编号", description = "支付终端编号")
    private String terminalCode;

    @Schema(title = "备注", description = "支付备注")
    private String remark;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getQrCodeText() {
        return qrCodeText;
    }

    public void setQrCodeText(String qrCodeText) {
        this.qrCodeText = qrCodeText;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PaymentResultVO{" +
                "paymentId='" + paymentId + '\'' +
                ", orderId=" + orderId +
                ", orderSn='" + orderSn + '\'' +
                ", payType='" + payType + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", amount=" + amount +
                ", paidAmount=" + paidAmount +
                ", transactionId='" + transactionId + '\'' +
                ", payTime=" + payTime +
                ", createTime=" + createTime +
                ", qrCodeUrl='" + qrCodeUrl + '\'' +
                ", qrCodeText='" + qrCodeText + '\'' +
                ", failureReason='" + failureReason + '\'' +
                ", terminalCode='" + terminalCode + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
} 