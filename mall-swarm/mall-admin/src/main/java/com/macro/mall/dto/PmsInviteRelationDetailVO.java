package com.macro.mall.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 邀请关系详情VO（包含被邀请人信息）
 */
public class PmsInviteRelationDetailVO {
    
    private Long id;
    private Long inviterId;
    private Long inviteeId;
    private String inviteParam;
    private Date inviteTime;
    private Date registerTime;
    private Date firstOrderTime;
    private BigDecimal firstOrderAmount;
    private Integer status;
    private Integer rewardStatus;
    private String deviceInfo;
    private String ipAddress;
    private Integer sceneType;
    private String sourcePage;
    private Date createdAt;
    private Date updatedAt;
    
    // 扩展字段 - 被邀请人信息
    private String inviteeNickname;
    private String inviteePhone;
    private Boolean isValid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public Long getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getInviteParam() {
        return inviteParam;
    }

    public void setInviteParam(String inviteParam) {
        this.inviteParam = inviteParam;
    }

    public Date getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(Date inviteTime) {
        this.inviteTime = inviteTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getFirstOrderTime() {
        return firstOrderTime;
    }

    public void setFirstOrderTime(Date firstOrderTime) {
        this.firstOrderTime = firstOrderTime;
    }

    public BigDecimal getFirstOrderAmount() {
        return firstOrderAmount;
    }

    public void setFirstOrderAmount(BigDecimal firstOrderAmount) {
        this.firstOrderAmount = firstOrderAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(Integer rewardStatus) {
        this.rewardStatus = rewardStatus;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getSceneType() {
        return sceneType;
    }

    public void setSceneType(Integer sceneType) {
        this.sceneType = sceneType;
    }

    public String getSourcePage() {
        return sourcePage;
    }

    public void setSourcePage(String sourcePage) {
        this.sourcePage = sourcePage;
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

    public String getInviteePhone() {
        return inviteePhone;
    }

    public void setInviteePhone(String inviteePhone) {
        this.inviteePhone = inviteePhone;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
} 