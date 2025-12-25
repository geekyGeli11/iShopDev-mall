package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsDiyMaterial implements Serializable {
    @Schema(title = "素材ID")
    private Long id;

    @Schema(title = "分类ID")
    private Long categoryId;

    @Schema(title = "素材名称")
    private String name;

    @Schema(title = "文件URL")
    private String fileUrl;

    @Schema(title = "文件类型：png,jpg,ttf,otf等")
    private String fileType;

    @Schema(title = "文件大小(字节)")
    private Long fileSize;

    @Schema(title = "字体文件URL（用于文字素材）")
    private String fontFileUrl;

    @Schema(title = "字体名称（用于小程序端加载字体）")
    private String fontFamily;

    @Schema(title = "标签，逗号分隔")
    private String tags;

    @Schema(title = "字体预览文字")
    private String previewText;

    @Schema(title = "使用次数")
    private Integer usageCount;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFontFileUrl() {
        return fontFileUrl;
    }

    public void setFontFileUrl(String fontFileUrl) {
        this.fontFileUrl = fontFileUrl;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getPreviewText() {
        return previewText;
    }

    public void setPreviewText(String previewText) {
        this.previewText = previewText;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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
        sb.append(", categoryId=").append(categoryId);
        sb.append(", name=").append(name);
        sb.append(", fileUrl=").append(fileUrl);
        sb.append(", fileType=").append(fileType);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", fontFileUrl=").append(fontFileUrl);
        sb.append(", fontFamily=").append(fontFamily);
        sb.append(", tags=").append(tags);
        sb.append(", previewText=").append(previewText);
        sb.append(", usageCount=").append(usageCount);
        sb.append(", sort=").append(sort);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}