package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class DecPageComponentRef implements Serializable {
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "页面 ID")
    private Long pageId;

    @Schema(title = "版本号，对应 dec_page_version.version")
    private Integer version;

    @Schema(title = "页面中组件唯一标识（前端生成）")
    private String blockKey;

    @Schema(title = "组件类型，如 product-grid")
    private String componentType;

    @Schema(title = "引用资源类型 product/coupon/video/page")
    private String refType;

    @Schema(title = "被引用资源主键 ID")
    private Long refId;

    private Date createTime;

    @Schema(title = "扩展信息 (如 dataSource.mode 等)")
    private String extra;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getBlockKey() {
        return blockKey;
    }

    public void setBlockKey(String blockKey) {
        this.blockKey = blockKey;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", pageId=").append(pageId);
        sb.append(", version=").append(version);
        sb.append(", blockKey=").append(blockKey);
        sb.append(", componentType=").append(componentType);
        sb.append(", refType=").append(refType);
        sb.append(", refId=").append(refId);
        sb.append(", createTime=").append(createTime);
        sb.append(", extra=").append(extra);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}