package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 定制区域配置
 * Created by macro on 2024/12/20.
 */
public class CustomizableArea {
    
    @Schema(title = "X坐标")
    private Integer x;
    
    @Schema(title = "Y坐标")
    private Integer y;
    
    @Schema(title = "宽度")
    private Integer width;
    
    @Schema(title = "高度")
    private Integer height;
    
    @Schema(title = "旋转角度")
    private Double rotation;
    
    @Schema(title = "缩放比例")
    private Double scale;
    
    @Schema(title = "是否保持宽高比")
    private Boolean keepAspectRatio;

    @Schema(title = "SVG路径数据")
    private String pathData;

    @Schema(title = "区域名称")
    private String name;

    @Schema(title = "蒙版图片URL(画笔模式)")
    private String maskImageUrl;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getRotation() {
        return rotation;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public Boolean getKeepAspectRatio() {
        return keepAspectRatio;
    }

    public void setKeepAspectRatio(Boolean keepAspectRatio) {
        this.keepAspectRatio = keepAspectRatio;
    }

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaskImageUrl() {
        return maskImageUrl;
    }

    public void setMaskImageUrl(String maskImageUrl) {
        this.maskImageUrl = maskImageUrl;
    }
}
