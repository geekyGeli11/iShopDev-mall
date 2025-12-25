package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

/**
 * 海报生成参数
 */
@Schema(description = "海报生成参数")
public class PosterGenerateParam {
    
    @Schema(description = "海报类型：1-商品海报，2-店铺海报，3-活动海报")
    @NotNull(message = "海报类型不能为空")
    private Integer posterType;
    
    @Schema(description = "模板ID")
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    
    @Schema(description = "商品ID（商品海报时必填）")
    private Long productId;
    
    @Schema(description = "活动ID（活动海报时必填）")
    private Long activityId;
    
    @Schema(description = "自定义文案")
    private String customText;
    
    @Schema(description = "是否包含二维码")
    private Boolean includeQrCode = true;
    
    @Schema(description = "海报标题")
    private String title;
    
    public Integer getPosterType() {
        return posterType;
    }
    
    public void setPosterType(Integer posterType) {
        this.posterType = posterType;
    }
    
    public Long getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getActivityId() {
        return activityId;
    }
    
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
    public String getCustomText() {
        return customText;
    }
    
    public void setCustomText(String customText) {
        this.customText = customText;
    }
    
    public Boolean getIncludeQrCode() {
        return includeQrCode;
    }
    
    public void setIncludeQrCode(Boolean includeQrCode) {
        this.includeQrCode = includeQrCode;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
} 