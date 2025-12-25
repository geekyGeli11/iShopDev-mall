package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DIY素材参数
 * Created by macro on 2024/12/20.
 */
public class PmsDiyMaterialParam {
    
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

    @Schema(title = "字体文件URL")
    private String fontFileUrl;

    @Schema(title = "字体名称")
    private String fontFamily;

    @Schema(title = "标签，逗号分隔")
    private String tags;

    @Schema(title = "字体预览文字")
    private String previewText;

    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}