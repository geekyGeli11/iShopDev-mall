package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DIY素材分类参数
 * Created by macro on 2024/12/20.
 */
public class PmsDiyMaterialCategoryParam {
    
    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "分类类型：1-图片素材，2-文字字体")
    private Byte type;

    @Schema(title = "分类图标")
    private String icon;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "状态：0-禁用，1-启用")
    private Byte status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}