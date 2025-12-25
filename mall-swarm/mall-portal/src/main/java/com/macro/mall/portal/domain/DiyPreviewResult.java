package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * DIY预览结果
 * Created by macro on 2024/12/20.
 */
public class DiyPreviewResult {

    @Schema(title = "预览图URL（兼容旧版本）")
    private String previewImageUrl;

    @Schema(title = "高清预览图URL（兼容旧版本）")
    private String hdPreviewImageUrl;

    @Schema(title = "多面预览图列表")
    private List<FacePreviewResult> previewImages;

    @Schema(title = "生成状态：0-生成中，1-生成成功，2-生成失败")
    private Integer status;

    @Schema(title = "错误信息")
    private String errorMessage;

    @Schema(title = "生成时间戳")
    private Long timestamp;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    public List<FacePreviewResult> getPreviewImages() {
        return previewImages;
    }

    public void setPreviewImages(List<FacePreviewResult> previewImages) {
        this.previewImages = previewImages;

        // 为了兼容旧版本，设置第一个面的预览图为主预览图
        if (previewImages != null && !previewImages.isEmpty()) {
            FacePreviewResult firstFace = previewImages.get(0);
            this.previewImageUrl = firstFace.getPreviewImageUrl();
            this.hdPreviewImageUrl = firstFace.getHdPreviewImageUrl();
        }
    }
}
