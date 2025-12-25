package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsInviteRelation implements Serializable {
    private Long id;

    @Schema(title = "邀请者用户ID")
    private Long inviterId;

    @Schema(title = "被邀请者用户ID")
    private Long inviteeId;

    @Schema(title = "邀请参数：格式为{用户ID}_{时间戳}")
    private String inviteParam;

    @Schema(title = "邀请时间（邀请参数生成时间）")
    private Date inviteTime;

    @Schema(title = "被邀请者注册时间")
    private Date registerTime;

    @Schema(title = "被邀请者首单时间")
    private Date firstOrderTime;

    @Schema(title = "首单金额")
    private BigDecimal firstOrderAmount;

    @Schema(title = "邀请状态：0-已邀请未注册，1-已注册未首单，2-已完成首单")
    private Byte status;

    @Schema(title = "奖励发放状态：0-未发放，1-注册奖励已发放，2-首单奖励已发放，3-全部奖励已发放")
    private Byte rewardStatus;

    @Schema(title = "设备信息JSON")
    private String deviceInfo;

    @Schema(title = "IP地址")
    private String ipAddress;

    @Schema(title = "进入场景：1-小程序卡片分享，2-小程序码扫码")
    private Byte sceneType;

    @Schema(title = "分享来源页面")
    private String sourcePage;

    private Date createdAt;

    private Date updatedAt;

    @Schema(title = "关系类型：1-邀请关系，2-一级分销，3-二级分销")
    private Byte relationType;

    @Schema(title = "分销层级：0-非分销，1-一级，2-二级")
    private Byte distributorLevel;

    @Schema(title = "绑定来源：1-注册绑定，2-下单绑定")
    private Byte bindSource;

    @Schema(title = "关系过期时间（临时绑定用）")
    private Date relationExpireTime;

    @Schema(title = "是否永久关系：0-临时，1-永久")
    private Byte isPermanent;

    private static final long serialVersionUID = 1L;

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(Byte rewardStatus) {
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

    public Byte getSceneType() {
        return sceneType;
    }

    public void setSceneType(Byte sceneType) {
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

    public Byte getRelationType() {
        return relationType;
    }

    public void setRelationType(Byte relationType) {
        this.relationType = relationType;
    }

    public Byte getDistributorLevel() {
        return distributorLevel;
    }

    public void setDistributorLevel(Byte distributorLevel) {
        this.distributorLevel = distributorLevel;
    }

    public Byte getBindSource() {
        return bindSource;
    }

    public void setBindSource(Byte bindSource) {
        this.bindSource = bindSource;
    }

    public Date getRelationExpireTime() {
        return relationExpireTime;
    }

    public void setRelationExpireTime(Date relationExpireTime) {
        this.relationExpireTime = relationExpireTime;
    }

    public Byte getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(Byte isPermanent) {
        this.isPermanent = isPermanent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", inviterId=").append(inviterId);
        sb.append(", inviteeId=").append(inviteeId);
        sb.append(", inviteParam=").append(inviteParam);
        sb.append(", inviteTime=").append(inviteTime);
        sb.append(", registerTime=").append(registerTime);
        sb.append(", firstOrderTime=").append(firstOrderTime);
        sb.append(", firstOrderAmount=").append(firstOrderAmount);
        sb.append(", status=").append(status);
        sb.append(", rewardStatus=").append(rewardStatus);
        sb.append(", deviceInfo=").append(deviceInfo);
        sb.append(", ipAddress=").append(ipAddress);
        sb.append(", sceneType=").append(sceneType);
        sb.append(", sourcePage=").append(sourcePage);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", relationType=").append(relationType);
        sb.append(", distributorLevel=").append(distributorLevel);
        sb.append(", bindSource=").append(bindSource);
        sb.append(", relationExpireTime=").append(relationExpireTime);
        sb.append(", isPermanent=").append(isPermanent);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}