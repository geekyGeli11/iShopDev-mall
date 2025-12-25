package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class AppDevice implements Serializable {
    @Schema(title = "设备ID")
    private Long id;

    @Schema(title = "设备唯一标识")
    private String deviceId;

    @Schema(title = "设备名称")
    private String deviceName;

    @Schema(title = "设备型号")
    private String deviceModel;

    @Schema(title = "Android版本")
    private String androidVersion;

    @Schema(title = "当前应用版本名称")
    private String appVersionName;

    @Schema(title = "当前应用版本号")
    private Integer appVersionCode;

    @Schema(title = "门店ID")
    private Long storeId;

    @Schema(title = "门店名称")
    private String storeName;

    @Schema(title = "最后检查更新时间")
    private Date lastCheckTime;

    @Schema(title = "最后更新时间")
    private Date lastUpdateTime;

    @Schema(title = "更新状态(0:正常 1:更新中 2:更新失败)")
    private Boolean updateStatus;

    @Schema(title = "是否在线(0:离线 1:在线)")
    private Boolean isOnline;

    @Schema(title = "创建时间")
    private Date createdTime;

    @Schema(title = "更新时间")
    private Date updatedTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(Integer appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Boolean getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Boolean updateStatus) {
        this.updateStatus = updateStatus;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", deviceModel=").append(deviceModel);
        sb.append(", androidVersion=").append(androidVersion);
        sb.append(", appVersionName=").append(appVersionName);
        sb.append(", appVersionCode=").append(appVersionCode);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", lastCheckTime=").append(lastCheckTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append(", updateStatus=").append(updateStatus);
        sb.append(", isOnline=").append(isOnline);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}