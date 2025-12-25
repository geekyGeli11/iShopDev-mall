package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class AppVersion implements Serializable {
    @Schema(title = "版本ID")
    private Long id;

    @Schema(title = "版本名称(如1.2.3)")
    private String versionName;

    @Schema(title = "版本号(递增数字)")
    private Integer versionCode;

    @Schema(title = "APK文件路径")
    private String apkFilePath;

    @Schema(title = "APK文件大小(字节)")
    private Long apkFileSize;

    @Schema(title = "APK文件MD5校验值")
    private String apkFileMd5;

    @Schema(title = "更新类型(0:可选更新 1:强制更新)")
    private Boolean updateType;

    @Schema(title = "最低支持版本")
    private String minSupportVersion;

    @Schema(title = "目标平台")
    private String targetPlatform;

    @Schema(title = "发布时间")
    private Date releaseTime;

    @Schema(title = "是否激活(0:否 1:是)")
    private Boolean isActive;

    @Schema(title = "下载地址")
    private String downloadUrl;

    @Schema(title = "创建时间")
    private Date createdTime;

    @Schema(title = "更新时间")
    private Date updatedTime;

    @Schema(title = "更新内容描述")
    private String updateContent;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public Long getApkFileSize() {
        return apkFileSize;
    }

    public void setApkFileSize(Long apkFileSize) {
        this.apkFileSize = apkFileSize;
    }

    public String getApkFileMd5() {
        return apkFileMd5;
    }

    public void setApkFileMd5(String apkFileMd5) {
        this.apkFileMd5 = apkFileMd5;
    }

    public Boolean getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Boolean updateType) {
        this.updateType = updateType;
    }

    public String getMinSupportVersion() {
        return minSupportVersion;
    }

    public void setMinSupportVersion(String minSupportVersion) {
        this.minSupportVersion = minSupportVersion;
    }

    public String getTargetPlatform() {
        return targetPlatform;
    }

    public void setTargetPlatform(String targetPlatform) {
        this.targetPlatform = targetPlatform;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", versionName=").append(versionName);
        sb.append(", versionCode=").append(versionCode);
        sb.append(", apkFilePath=").append(apkFilePath);
        sb.append(", apkFileSize=").append(apkFileSize);
        sb.append(", apkFileMd5=").append(apkFileMd5);
        sb.append(", updateType=").append(updateType);
        sb.append(", minSupportVersion=").append(minSupportVersion);
        sb.append(", targetPlatform=").append(targetPlatform);
        sb.append(", releaseTime=").append(releaseTime);
        sb.append(", isActive=").append(isActive);
        sb.append(", downloadUrl=").append(downloadUrl);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", updateContent=").append(updateContent);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}