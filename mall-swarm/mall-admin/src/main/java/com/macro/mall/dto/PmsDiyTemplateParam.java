package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DIY模板创建/更新参数
 * Created by macro on 2024/12/20.
 */
public class PmsDiyTemplateParam {
    
    @Schema(title = "模板名称", required = true)
    @NotBlank(message = "模板名称不能为空")
    private String name;
    
    @Schema(title = "适用商品分类ID")
    private Long productCategoryId;
    
    @Schema(title = "模板描述")
    private String description;
    
    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
