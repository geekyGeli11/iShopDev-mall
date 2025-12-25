package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DIY区域创建/更新参数
 * Created by macro on 2024/12/20.
 */
public class PmsDiyAreaParam {
    
    @Schema(title = "面ID", required = true)
    @NotNull(message = "面ID不能为空")
    private Long surfaceId;
    
    @Schema(title = "区域名称")
    private String name;

    @Schema(title = "路径数据(SVG path格式)")
    private String pathData;

    @Schema(title = "边界框：x,y,width,height", required = true)
    @NotBlank(message = "边界框不能为空")
    private String bounds;

    @Schema(title = "蒙版图片URL(画笔模式)")
    private String maskImageUrl;

    public Long getSurfaceId() {
        return surfaceId;
    }

    public void setSurfaceId(Long surfaceId) {
        this.surfaceId = surfaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public String getMaskImageUrl() {
        return maskImageUrl;
    }

    public void setMaskImageUrl(String maskImageUrl) {
        this.maskImageUrl = maskImageUrl;
    }
}
