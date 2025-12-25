package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class AppUpdateLog implements Serializable {
    @Schema(title = "日志ID")
    private Long id;

    @Schema(title = "设备ID")
    private String deviceId;

    @Schema(title = "更新前版本号")
    private Integer fromVersionCode;

    @Schema(title = "更新后版本号")
    private Integer toVersionCode;

    @Schema(title = "更新类型(0:可选更新 1:强制更新)")
    private Boolean updateType;

    @Schema(title = "更新状态(0:开始 1:下载中 2:下载完成 3:安装中 4:成功 5:失败)")
    private Boolean updateStatus;

    @Schema(title = "下载进度(0-100)")
    private Integer downloadProgress;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "耗时(秒)")
    private Integer duration;

    @Schema(title = "创建时间")
    private Date createdTime;

    @Schema(title = "错误信息")
    private String errorMessage;

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

    public Integer getFromVersionCode() {
        return fromVersionCode;
    }

    public void setFromVersionCode(Integer fromVersionCode) {
        this.fromVersionCode = fromVersionCode;
    }

    public Integer getToVersionCode() {
        return toVersionCode;
    }

    public void setToVersionCode(Integer toVersionCode) {
        this.toVersionCode = toVersionCode;
    }

    public Boolean getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Boolean updateType) {
        this.updateType = updateType;
    }

    public Boolean getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(Boolean updateStatus) {
        this.updateStatus = updateStatus;
    }

    public Integer getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(Integer downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", fromVersionCode=").append(fromVersionCode);
        sb.append(", toVersionCode=").append(toVersionCode);
        sb.append(", updateType=").append(updateType);
        sb.append(", updateStatus=").append(updateStatus);
        sb.append(", downloadProgress=").append(downloadProgress);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", duration=").append(duration);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", errorMessage=").append(errorMessage);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}