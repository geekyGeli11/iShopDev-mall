package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 生成收款二维码参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "生成收款二维码参数")
public class PaymentQRParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "订单ID", description = "订单ID", example = "1001")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(title = "支付金额", description = "支付金额，单位：元", example = "99.90")
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额不能小于0.01元")
    @DecimalMax(value = "99999.99", message = "支付金额不能超过99999.99元")
    private BigDecimal amount;

    @Schema(title = "支付方式", description = "支付方式：WECHAT-微信支付，ALIPAY-支付宝", example = "WECHAT")
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(WECHAT|ALIPAY)$", message = "支付方式只能是WECHAT或ALIPAY")
    private String payType;

    @Schema(title = "订单标题", description = "订单标题", example = "自助收银付款")
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 50, message = "订单标题长度不能超过50字符")
    private String title;

    @Schema(title = "订单描述", description = "订单描述", example = "自助收银系统付款")
    @Size(max = 200, message = "订单描述长度不能超过200字符")
    private String description;

    @Schema(title = "终端编号", description = "自助收银终端编号", example = "SC001")
    private String terminalCode;

    @Schema(title = "设备信息", description = "支付设备信息")
    private String deviceInfo;

    @Schema(title = "游客ID", description = "游客ID（游客模式必传）", example = "guest_12345")
    private String guestId;

    @Schema(title = "二维码有效期", description = "二维码有效期（分钟），默认5分钟", example = "5")
    @Min(value = 1, message = "二维码有效期不能小于1分钟")
    @Max(value = 30, message = "二维码有效期不能超过30分钟")
    private Integer expireMinutes = 5;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getExpireMinutes() {
        return expireMinutes;
    }

    public void setExpireMinutes(Integer expireMinutes) {
        this.expireMinutes = expireMinutes;
    }

    @Override
    public String toString() {
        return "PaymentQRParam{" +
                "orderId=" + orderId +
                ", amount=" + amount +
                ", payType='" + payType + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", terminalCode='" + terminalCode + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", guestId='" + guestId + '\'' +
                ", expireMinutes=" + expireMinutes +
                '}';
    }
} 