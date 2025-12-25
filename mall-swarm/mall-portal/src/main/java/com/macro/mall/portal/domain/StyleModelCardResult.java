package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 风格模型卡片结果
 * Created by macro on 2024/8/25.
 */
public class StyleModelCardResult {
    @Schema(title = "风格模型ID")
    private Long id;

    @Schema(title = "风格名称")
    private String name;

    @Schema(title = "风格介绍")
    private String description;

    @Schema(title = "封面图片URL")
    private String coverImage;

    @Schema(title = "横幅图片URL")
    private String bannerImage;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "关联商品数量")
    private Integer productCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
}
