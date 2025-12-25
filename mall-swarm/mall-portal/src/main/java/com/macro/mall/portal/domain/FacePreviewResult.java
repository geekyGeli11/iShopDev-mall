package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 面预览结果
 * Created by macro on 2024/12/20.
 */
public class FacePreviewResult {
    
    @Schema(title = "面索引")
    private Integer faceIndex;
    
    @Schema(title = "面名称")
    private String faceName;
    
    @Schema(title = "预览图URL")
    private String previewImageUrl;
    
    @Schema(title = "高清预览图URL")
    private String hdPreviewImageUrl;
    
    @Schema(title = "生成状态")
    private String status;
    
    @Schema(title = "错误信息")
    private String errorMessage;
    
    @Schema(title = "生成时间戳")
    private Long timestamp;

    public Integer getFaceIndex() {
        return faceIndex;
    }

    public void setFaceIndex(Integer faceIndex) {
        this.faceIndex = faceIndex;
    }

    public String getFaceName() {
        return faceName;
    }

    public void setFaceName(String faceName) {
        this.faceName = faceName;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public String getHdPreviewImageUrl() {
        return hdPreviewImageUrl;
    }

    public void setHdPreviewImageUrl(String hdPreviewImageUrl) {
        this.hdPreviewImageUrl = hdPreviewImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
