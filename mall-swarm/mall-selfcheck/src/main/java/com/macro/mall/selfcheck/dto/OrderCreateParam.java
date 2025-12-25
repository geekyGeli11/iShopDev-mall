package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 自助收银订单创建参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "订单创建参数")
public class OrderCreateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "订单类型", description = "订单类型：QUICK-快速下单(单商品)，CART-购物车下单", example = "QUICK")
    @NotBlank(message = "订单类型不能为空")
    @Pattern(regexp = "^(QUICK|CART)$", message = "订单类型只能是QUICK或CART")
    private String orderType;

    @Schema(title = "游客ID", description = "游客ID（游客模式必传）", example = "guest_12345")
    private String guestId;

    @Schema(title = "订单商品列表", description = "订单商品明细")
    @Valid
    @NotEmpty(message = "订单商品不能为空")
    @Size(max = 50, message = "单次下单商品数量不能超过50")
    private List<OrderItemParam> orderItems;

    @Schema(title = "收货地址ID", description = "收货地址ID（如需配送）")
    private Long addressId;

    @Schema(title = "优惠券历史ID列表", description = "使用的优惠券历史ID列表")
    private List<Long> couponHistoryIds;

    @Schema(title = "使用积分", description = "使用的积分数量", example = "100")
    @Min(value = 0, message = "使用积分不能为负数")
    private Integer usePoints = 0;

    @Schema(title = "订单备注", description = "订单备注信息", example = "自助收银下单")
    @Size(max = 200, message = "订单备注长度不能超过200字符")
    private String note;

    @Schema(title = "支付方式", description = "支付方式：WECHAT-微信支付，ALIPAY-支付宝，BALANCE-余额支付", example = "WECHAT")
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(WECHAT|ALIPAY|BALANCE)$", message = "支付方式只能是WECHAT、ALIPAY或BALANCE")
    private String payType;

    @Schema(title = "配送方式", description = "配送方式：0-无需配送，1-快递配送，2-门店自提", example = "0")
    @NotNull(message = "配送方式不能为空")
    @Min(value = 0, message = "配送方式值错误")
    @Max(value = 2, message = "配送方式值错误")
    private Integer deliveryType = 0;

    @Schema(title = "预计支付金额", description = "前端计算的预计支付金额，用于校验", example = "99.90")
    private BigDecimal expectedAmount;

    @Schema(title = "设备信息", description = "下单设备信息")
    private String deviceInfo;

    @Schema(title = "终端编号", description = "自助收银终端编号", example = "SC001")
    private String terminalCode;

    @Schema(title = "强制下单", description = "是否强制下单（忽略库存检查等）", example = "false")
    private Boolean forceOrder = false;

    @Schema(title = "门店ID", description = "下单门店ID", example = "1")
    private Long storeId;

    @Schema(title = "学校ID", description = "下单学校ID", example = "1")
    private Long schoolId;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public List<OrderItemParam> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemParam> orderItems) {
        this.orderItems = orderItems;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public List<Long> getCouponHistoryIds() {
        return couponHistoryIds;
    }

    public void setCouponHistoryIds(List<Long> couponHistoryIds) {
        this.couponHistoryIds = couponHistoryIds;
    }

    public Integer getUsePoints() {
        return usePoints;
    }

    public void setUsePoints(Integer usePoints) {
        this.usePoints = usePoints;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public BigDecimal getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(BigDecimal expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public Boolean getForceOrder() {
        return forceOrder;
    }

    public void setForceOrder(Boolean forceOrder) {
        this.forceOrder = forceOrder;
    }

    @Override
    public String toString() {
        return "OrderCreateParam{" +
                "orderType='" + orderType + '\'' +
                ", guestId='" + guestId + '\'' +
                ", orderItems=" + orderItems +
                ", addressId=" + addressId +
                ", couponHistoryIds=" + couponHistoryIds +
                ", usePoints=" + usePoints +
                ", note='" + note + '\'' +
                ", payType='" + payType + '\'' +
                ", deliveryType=" + deliveryType +
                ", expectedAmount=" + expectedAmount +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", terminalCode='" + terminalCode + '\'' +
                ", forceOrder=" + forceOrder +
                '}';
    }
} 