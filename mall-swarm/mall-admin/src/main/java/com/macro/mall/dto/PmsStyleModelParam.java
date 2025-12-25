package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;

/**
 * 风格模型参数
 * Created by macro on 2024/8/25.
 */
public class PmsStyleModelParam {
    @Schema(title = "风格名称")
    @NotEmpty(message = "风格名称不能为空")
    private String name;

    @Schema(title = "风格介绍")
    private String description;

    @Schema(title = "API功能类型")
    private String functionType;

    @Schema(title = "封面图片URL")
    private String coverImage;

    @Schema(title = "横幅图片URL")
    private String bannerImage;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "状态：0-禁用，1-启用")
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
