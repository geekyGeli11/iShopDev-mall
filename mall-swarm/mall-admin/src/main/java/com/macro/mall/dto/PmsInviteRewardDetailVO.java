package com.macro.mall.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 邀请奖励记录详情VO（包含被邀请人信息）
 */
public class PmsInviteRewardDetailVO {
    
    private Long id;
    private Long relationId;
    private Long userId;
    private Integer userType;
    private Integer rewardType;
    private BigDecimal rewardValue;
    private String rewardDesc;
    private Integer triggerType;
    private BigDecimal triggerAmount;
    private Integer status;
    private Long couponId;
    private Date expireTime;
    private Date sendTime;
    private String sendResult;
    private Date createdAt;
    private Date updatedAt;
    
    // 扩展字段
    private String inviteeNickname;
    private BigDecimal amount; // 别名，为了兼容前端
    private Date createTime; // 别名，为了兼容前端
    private String remark; // 备注信息

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

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getRewardType() {
        return rewardType;
    }

    public void setRewardType(Integer rewardType) {
        this.rewardType = rewardType;
    }

    public BigDecimal getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(BigDecimal rewardValue) {
        this.rewardValue = rewardValue;
    }

    public String getRewardDesc() {
        return rewardDesc;
    }

    public void setRewardDesc(String rewardDesc) {
        this.rewardDesc = rewardDesc;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public BigDecimal getTriggerAmount() {
        return triggerAmount;
    }

    public void setTriggerAmount(BigDecimal triggerAmount) {
        this.triggerAmount = triggerAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    public String getInviteeNickname() {
        return inviteeNickname;
    }

    public void setInviteeNickname(String inviteeNickname) {
        this.inviteeNickname = inviteeNickname;
    }

    // 别名方法，为了兼容前端字段映射
    public BigDecimal getAmount() {
        return rewardValue;
    }

    public void setAmount(BigDecimal amount) {
        this.rewardValue = amount;
    }

    public Date getCreateTime() {
        return sendTime;
    }

    public void setCreateTime(Date createTime) {
        this.sendTime = createTime;
    }

    public String getRemark() {
        return sendResult;
    }

    public void setRemark(String remark) {
        this.sendResult = remark;
    }
} 