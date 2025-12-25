package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DIY模板面创建/更新参数
 * Created by macro on 2024/12/20.
 */
public class PmsDiyTemplateSurfaceParam {
    
    @Schema(title = "模板ID", required = true)
    @NotNull(message = "模板ID不能为空")
    private Long templateId;
    
    @Schema(title = "面名称", required = true)
    @NotBlank(message = "面名称不能为空")
    private String name;
    
    @Schema(title = "示例图URL", required = true)
    @NotBlank(message = "示例图不能为空")
    private String exampleImage;
    
    @Schema(title = "排序")
    private Integer sort;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExampleImage() {
        return exampleImage;
    }

    public void setExampleImage(String exampleImage) {
        this.exampleImage = exampleImage;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
