package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 模板面配置
 * Created by macro on 2024/12/20.
 */
public class TemplateFaceConfig {

    @Schema(title = "面ID")
    private Long faceId;

    @Schema(title = "面名称")
    private String faceName;
    
    @Schema(title = "底图URL")
    private String baseImageUrl;
    
    @Schema(title = "定制区域配置")
    private CustomizableArea customizableArea;
    
    @Schema(title = "面描述")
    private String description;
    
    @Schema(title = "排序")
    private Integer sortOrder;

    public Long getFaceId() {
        return faceId;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
    }

    public String getFaceName() {
        return faceName;
    }

    public void setFaceName(String faceName) {
        this.faceName = faceName;
    }

    public String getBaseImageUrl() {
        return baseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        this.baseImageUrl = baseImageUrl;
    }

    public CustomizableArea getCustomizableArea() {
        return customizableArea;
    }

    public void setCustomizableArea(CustomizableArea customizableArea) {
        this.customizableArea = customizableArea;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
