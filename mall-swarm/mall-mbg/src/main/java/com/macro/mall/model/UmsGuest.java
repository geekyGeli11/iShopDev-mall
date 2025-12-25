package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsGuest implements Serializable {
    private Long id;

    @Schema(title = "游客ID")
    private String guestId;

    @Schema(title = "设备ID")
    private String deviceId;

    @Schema(title = "设备类型")
    private String deviceType;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "最后活跃时间")
    private Date lastActiveTime;

    @Schema(title = "最后访问IP")
    private String lastAccessIp;

    @Schema(title = "用户代理")
    private String userAgent;

    @Schema(title = "状态：0->无效；1->有效")
    private Integer status;

    @Schema(title = "是否有进行中的订单")
    private Boolean hasActiveOrder;

    @Schema(title = "绑定学校ID")
    private Long schoolId;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getLastAccessIp() {
        return lastAccessIp;
    }

    public void setLastAccessIp(String lastAccessIp) {
        this.lastAccessIp = lastAccessIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getHasActiveOrder() {
        return hasActiveOrder;
    }

    public void setHasActiveOrder(Boolean hasActiveOrder) {
        this.hasActiveOrder = hasActiveOrder;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", guestId=").append(guestId);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceType=").append(deviceType);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastActiveTime=").append(lastActiveTime);
        sb.append(", lastAccessIp=").append(lastAccessIp);
        sb.append(", userAgent=").append(userAgent);
        sb.append(", status=").append(status);
        sb.append(", hasActiveOrder=").append(hasActiveOrder);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}