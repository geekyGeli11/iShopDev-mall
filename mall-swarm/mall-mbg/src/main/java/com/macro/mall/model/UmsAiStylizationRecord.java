package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsAiStylizationRecord implements Serializable {
    @Schema(title = "记录ID")
    private Long id;

    @Schema(title = "用户ID")
    private Long userId;

    @Schema(title = "原始图片URL")
    private String originalImage;

    @Schema(title = "风格化后图片URL")
    private String stylizedImage;

    @Schema(title = "风格提示词")
    private String stylePrompt;

    @Schema(title = "阿里云任务ID")
    private String aliyunTaskId;

    @Schema(title = "状态：0-处理中，1-成功，2-失败")
    private Byte status;

    @Schema(title = "错误信息")
    private String errorMessage;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

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

    public String getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(String originalImage) {
        this.originalImage = originalImage;
    }

    public String getStylizedImage() {
        return stylizedImage;
    }

    public void setStylizedImage(String stylizedImage) {
        this.stylizedImage = stylizedImage;
    }

    public String getStylePrompt() {
        return stylePrompt;
    }

    public void setStylePrompt(String stylePrompt) {
        this.stylePrompt = stylePrompt;
    }

    public String getAliyunTaskId() {
        return aliyunTaskId;
    }

    public void setAliyunTaskId(String aliyunTaskId) {
        this.aliyunTaskId = aliyunTaskId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", originalImage=").append(originalImage);
        sb.append(", stylizedImage=").append(stylizedImage);
        sb.append(", stylePrompt=").append(stylePrompt);
        sb.append(", aliyunTaskId=").append(aliyunTaskId);
        sb.append(", status=").append(status);
        sb.append(", errorMessage=").append(errorMessage);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}