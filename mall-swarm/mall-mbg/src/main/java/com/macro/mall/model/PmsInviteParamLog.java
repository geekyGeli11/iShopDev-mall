package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsInviteParamLog implements Serializable {
    private Long id;

    @Schema(title = "邀请者用户ID")
    private Long userId;

    @Schema(title = "邀请参数：格式为{用户ID}_{时间戳}")
    private String inviteParam;

    @Schema(title = "参数中的时间戳")
    private Long paramTimestamp;

    @Schema(title = "过期时间（生成时间+3天）")
    private Date expireTime;

    @Schema(title = "使用次数（成功注册的次数）")
    private Integer useCount;

    @Schema(title = "最大使用次数，0表示无限制")
    private Integer maxUseCount;

    @Schema(title = "小程序码图片URL（如果生成了海报）")
    private String qrCodeUrl;

    @Schema(title = "参数状态：0-已过期，1-有效")
    private Byte status;

    @Schema(title = "最后使用时间")
    private Date lastUsedTime;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getInviteParam() {
        return inviteParam;
    }

    public void setInviteParam(String inviteParam) {
        this.inviteParam = inviteParam;
    }

    public Long getParamTimestamp() {
        return paramTimestamp;
    }

    public void setParamTimestamp(Long paramTimestamp) {
        this.paramTimestamp = paramTimestamp;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Integer getMaxUseCount() {
        return maxUseCount;
    }

    public void setMaxUseCount(Integer maxUseCount) {
        this.maxUseCount = maxUseCount;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(Date lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", inviteParam=").append(inviteParam);
        sb.append(", paramTimestamp=").append(paramTimestamp);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", useCount=").append(useCount);
        sb.append(", maxUseCount=").append(maxUseCount);
        sb.append(", qrCodeUrl=").append(qrCodeUrl);
        sb.append(", status=").append(status);
        sb.append(", lastUsedTime=").append(lastUsedTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}