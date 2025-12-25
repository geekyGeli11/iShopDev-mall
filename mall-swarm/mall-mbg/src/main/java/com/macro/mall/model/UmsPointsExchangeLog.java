package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UmsPointsExchangeLog implements Serializable {
    private Long id;

    @Schema(title = "用户ID")
    private Long memberId;

    @Schema(title = "用户名")
    private String memberUsername;

    @Schema(title = "兑换类型：1-商品，2-优惠券")
    private Byte exchangeType;

    @Schema(title = "目标ID（商品ID或优惠券ID）")
    private Long targetId;

    @Schema(title = "目标名称")
    private String targetName;

    @Schema(title = "使用积分")
    private Integer pointsUsed;

    @Schema(title = "支付现金金额")
    private BigDecimal cashAmount;

    @Schema(title = "兑换数量")
    private Integer quantity;

    @Schema(title = "关联订单ID（商品换购）")
    private Long orderId;

    @Schema(title = "关联优惠券领取记录ID")
    private Long couponHistoryId;

    @Schema(title = "兑换状态：1-成功，2-失败，3-已退回")
    private Byte exchangeStatus;

    @Schema(title = "兑换时间")
    private Date exchangeTime;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "客户端IP")
    private String clientIp;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public Byte getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(Byte exchangeType) {
        this.exchangeType = exchangeType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Integer getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(Integer pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCouponHistoryId() {
        return couponHistoryId;
    }

    public void setCouponHistoryId(Long couponHistoryId) {
        this.couponHistoryId = couponHistoryId;
    }

    public Byte getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(Byte exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public Date getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(Date exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", memberUsername=").append(memberUsername);
        sb.append(", exchangeType=").append(exchangeType);
        sb.append(", targetId=").append(targetId);
        sb.append(", targetName=").append(targetName);
        sb.append(", pointsUsed=").append(pointsUsed);
        sb.append(", cashAmount=").append(cashAmount);
        sb.append(", quantity=").append(quantity);
        sb.append(", orderId=").append(orderId);
        sb.append(", couponHistoryId=").append(couponHistoryId);
        sb.append(", exchangeStatus=").append(exchangeStatus);
        sb.append(", exchangeTime=").append(exchangeTime);
        sb.append(", remark=").append(remark);
        sb.append(", clientIp=").append(clientIp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}