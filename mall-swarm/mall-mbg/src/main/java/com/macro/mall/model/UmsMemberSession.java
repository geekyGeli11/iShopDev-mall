package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsMemberSession implements Serializable {
    private Long id;

    @Schema(title = "会话ID")
    private String sessionId;

    @Schema(title = "会员ID")
    private Long memberId;

    @Schema(title = "手机号")
    private String phone;

    @Schema(title = "登录时间")
    private Date loginTime;

    @Schema(title = "最后活跃时间")
    private Date lastActiveTime;

    @Schema(title = "登录IP")
    private String loginIp;

    @Schema(title = "设备信息")
    private String deviceInfo;

    @Schema(title = "用户代理")
    private String userAgent;

    @Schema(title = "是否有进行中的订单")
    private Boolean orderInProgress;

    @Schema(title = "会话状态：0->无效；1->有效")
    private Integer status;

    @Schema(title = "过期时间")
    private Date expireTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Boolean getOrderInProgress() {
        return orderInProgress;
    }

    public void setOrderInProgress(Boolean orderInProgress) {
        this.orderInProgress = orderInProgress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sessionId=").append(sessionId);
        sb.append(", memberId=").append(memberId);
        sb.append(", phone=").append(phone);
        sb.append(", loginTime=").append(loginTime);
        sb.append(", lastActiveTime=").append(lastActiveTime);
        sb.append(", loginIp=").append(loginIp);
        sb.append(", deviceInfo=").append(deviceInfo);
        sb.append(", userAgent=").append(userAgent);
        sb.append(", orderInProgress=").append(orderInProgress);
        sb.append(", status=").append(status);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}