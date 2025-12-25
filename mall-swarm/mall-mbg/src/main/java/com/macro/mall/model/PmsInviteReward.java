package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsInviteReward implements Serializable {
    private Long id;

    @Schema(title = "邀请关系ID")
    private Long relationId;

    @Schema(title = "获得奖励的用户ID")
    private Long userId;

    @Schema(title = "用户类型：1-邀请者，2-被邀请者")
    private Byte userType;

    @Schema(title = "奖励类型：1-积分，2-优惠券，3-现金红包，4-商品")
    private Byte rewardType;

    @Schema(title = "奖励数值")
    private BigDecimal rewardValue;

    @Schema(title = "已锁定金额")
    private BigDecimal lockedAmount;

    @Schema(title = "奖励描述")
    private String rewardDesc;

    @Schema(title = "触发类型：1-注册完成，2-首单完成，3-累计消费达标")
    private Byte triggerType;

    @Schema(title = "触发金额（如首单金额、累计消费金额）")
    private BigDecimal triggerAmount;

    @Schema(title = "发放状态：0-待发放，1-已发放，2-发放失败，3-已过期")
    private Byte status;

    @Schema(title = "优惠券ID（如果奖励是优惠券）")
    private Long couponId;

    @Schema(title = "奖励过期时间")
    private Date expireTime;

    @Schema(title = "发放时间")
    private Date sendTime;

    @Schema(title = "发放结果描述")
    private String sendResult;

    private Date createdAt;

    private Date updatedAt;

    @Schema(title = "佣金类型：1-邀请奖励，2-一级分销佣金，3-二级分销佣金")
    private Byte commissionType;

    @Schema(title = "关联订单ID")
    private Long orderId;

    @Schema(title = "订单金额")
    private BigDecimal orderAmount;

    @Schema(title = "佣金比例（如0.0500表示5%）")
    private BigDecimal commissionRate;

    @Schema(title = "商品分类")
    private String productCategory;

    @Schema(title = "结算状态：0-未结算，1-已结算，2-已提现")
    private Byte settlementStatus;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getUserType() {
        return userType;
    }

    public void setUserType(Byte userType) {
        this.userType = userType;
    }

    public Byte getRewardType() {
        return rewardType;
    }

    public void setRewardType(Byte rewardType) {
        this.rewardType = rewardType;
    }

    public BigDecimal getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(BigDecimal rewardValue) {
        this.rewardValue = rewardValue;
    }

    public BigDecimal getLockedAmount() {
        return lockedAmount;
    }

    public void setLockedAmount(BigDecimal lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public String getRewardDesc() {
        return rewardDesc;
    }

    public void setRewardDesc(String rewardDesc) {
        this.rewardDesc = rewardDesc;
    }

    public Byte getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Byte triggerType) {
        this.triggerType = triggerType;
    }

    public BigDecimal getTriggerAmount() {
        return triggerAmount;
    }

    public void setTriggerAmount(BigDecimal triggerAmount) {
        this.triggerAmount = triggerAmount;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendResult() {
        return sendResult;
    }

    public void setSendResult(String sendResult) {
        this.sendResult = sendResult;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Byte getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Byte commissionType) {
        this.commissionType = commissionType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public Byte getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(Byte settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", relationId=").append(relationId);
        sb.append(", userId=").append(userId);
        sb.append(", userType=").append(userType);
        sb.append(", rewardType=").append(rewardType);
        sb.append(", rewardValue=").append(rewardValue);
        sb.append(", lockedAmount=").append(lockedAmount);
        sb.append(", rewardDesc=").append(rewardDesc);
        sb.append(", triggerType=").append(triggerType);
        sb.append(", triggerAmount=").append(triggerAmount);
        sb.append(", status=").append(status);
        sb.append(", couponId=").append(couponId);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", sendTime=").append(sendTime);
        sb.append(", sendResult=").append(sendResult);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", commissionType=").append(commissionType);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderAmount=").append(orderAmount);
        sb.append(", commissionRate=").append(commissionRate);
        sb.append(", productCategory=").append(productCategory);
        sb.append(", settlementStatus=").append(settlementStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}