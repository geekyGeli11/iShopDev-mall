package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 付款码支付参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "付款码支付参数")
public class PaymentCodeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "订单ID", description = "订单ID", example = "1001")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(title = "付款码", description = "用户付款码（扫码获取）", example = "134567890123456789")
    @NotBlank(message = "付款码不能为空")
    @Size(min = 16, max = 20, message = "付款码长度不正确")
    private String paymentCode;

    @Schema(title = "支付金额", description = "支付金额，单位：元", example = "99.90")
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额不能小于0.01元")
    @DecimalMax(value = "99999.99", message = "支付金额不能超过99999.99元")
    private BigDecimal amount;

    @Schema(title = "支付方式", description = "支付方式：WECHAT-微信支付，ALIPAY-支付宝", example = "WECHAT")
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(WECHAT|ALIPAY)$", message = "支付方式只能是WECHAT或ALIPAY")
    private String payType;

    @Schema(title = "终端编号", description = "自助收银终端编号", example = "SC001")
    private String terminalCode;

    @Schema(title = "设备信息", description = "支付设备信息")
    private String deviceInfo;

    @Schema(title = "游客ID", description = "游客ID（游客模式必传）", example = "guest_12345")
    private String guestId;

    @Schema(title = "备注", description = "支付备注", example = "自助收银付款")
    @Size(max = 100, message = "备注长度不能超过100字符")
    private String remark;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "PaymentCodeParam{" +
                "orderId=" + orderId +
                ", paymentCode='" + paymentCode + '\'' +
                ", amount=" + amount +
                ", payType='" + payType + '\'' +
                ", terminalCode='" + terminalCode + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", guestId='" + guestId + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
} 