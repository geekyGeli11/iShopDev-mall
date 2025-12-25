package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

/**
 * DIY设计参数
 * Created by macro on 2024/12/20.
 */
public class DiyDesignParam {
    
    @Schema(title = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @Schema(title = "模板ID", required = true)
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @Schema(title = "设计ID（更新时传递）")
    private Long designId;

    @Schema(title = "设计名称")
    private String designName;

    @Schema(title = "设计数据(JSON格式)", required = true)
    @NotNull(message = "设计数据不能为空")
    private String designData;

    @Schema(title = "预览图URL")
    private String previewImage;

    @Schema(title = "预览图数组(JSON格式)")
    private String previewImages;

    @Schema(title = "是否为草稿")
    private Boolean isDraft;

    @Schema(title = "设计描述")
    private String description;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getDesignData() {
        return designData;
    }

    public void setDesignData(String designData) {
        this.designData = designData;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getPreviewImages() {
        return previewImages;
    }

    public void setPreviewImages(String previewImages) {
        this.previewImages = previewImages;
    }

    public Boolean getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(Boolean isDraft) {
        this.isDraft = isDraft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
