package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsDiyArea implements Serializable {
    @Schema(title = "区域ID")
    private Long id;

    @Schema(title = "面ID")
    private Long surfaceId;

    @Schema(title = "区域名称")
    private String name;

    @Schema(title = "边界框：x,y,width,height")
    private String bounds;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "蒙版图片URL(画笔模式)")
    private String maskImageUrl;

    @Schema(title = "路径数据(SVG path格式)")
    private String pathData;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getBounds() {
        return bounds;
    }

    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMaskImageUrl() {
        return maskImageUrl;
    }

    public void setMaskImageUrl(String maskImageUrl) {
        this.maskImageUrl = maskImageUrl;
    }

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", surfaceId=").append(surfaceId);
        sb.append(", name=").append(name);
        sb.append(", bounds=").append(bounds);
        sb.append(", createTime=").append(createTime);
        sb.append(", maskImageUrl=").append(maskImageUrl);
        sb.append(", pathData=").append(pathData);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}