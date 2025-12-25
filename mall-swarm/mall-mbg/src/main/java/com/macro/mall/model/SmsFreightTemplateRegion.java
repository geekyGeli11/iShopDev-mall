package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;

public class SmsFreightTemplateRegion implements Serializable {
    private Long id;

    @Schema(title = "运费模板ID")
    private Long templateId;

    @Schema(title = "区域类型：1-省份,2-城市,3-距离范围")
    private Byte regionType;

    @Schema(title = "起始距离(km)")
    private BigDecimal distanceStart;

    @Schema(title = "结束距离(km)")
    private BigDecimal distanceEnd;

    @Schema(title = "首件/首重运费")
    private BigDecimal firstAmount;

    @Schema(title = "首件数量/首重重量")
    private BigDecimal firstCount;

    @Schema(title = "续件/续重运费")
    private BigDecimal additionalAmount;

    @Schema(title = "续件数量/续重重量")
    private BigDecimal additionalCount;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "区域ID列表（JSON格式）")
    private String regionIds;

    @Schema(title = "区域名称列表")
    private String regionNames;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Byte getRegionType() {
        return regionType;
    }

    public void setRegionType(Byte regionType) {
        this.regionType = regionType;
    }

    public BigDecimal getDistanceStart() {
        return distanceStart;
    }

    public void setDistanceStart(BigDecimal distanceStart) {
        this.distanceStart = distanceStart;
    }

    public BigDecimal getDistanceEnd() {
        return distanceEnd;
    }

    public void setDistanceEnd(BigDecimal distanceEnd) {
        this.distanceEnd = distanceEnd;
    }

    public BigDecimal getFirstAmount() {
        return firstAmount;
    }

    public void setFirstAmount(BigDecimal firstAmount) {
        this.firstAmount = firstAmount;
    }

    public BigDecimal getFirstCount() {
        return firstCount;
    }

    public void setFirstCount(BigDecimal firstCount) {
        this.firstCount = firstCount;
    }

    public BigDecimal getAdditionalAmount() {
        return additionalAmount;
    }

    public void setAdditionalAmount(BigDecimal additionalAmount) {
        this.additionalAmount = additionalAmount;
    }

    public BigDecimal getAdditionalCount() {
        return additionalCount;
    }

    public void setAdditionalCount(BigDecimal additionalCount) {
        this.additionalCount = additionalCount;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(String regionIds) {
        this.regionIds = regionIds;
    }

    public String getRegionNames() {
        return regionNames;
    }

    public void setRegionNames(String regionNames) {
        this.regionNames = regionNames;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", templateId=").append(templateId);
        sb.append(", regionType=").append(regionType);
        sb.append(", distanceStart=").append(distanceStart);
        sb.append(", distanceEnd=").append(distanceEnd);
        sb.append(", firstAmount=").append(firstAmount);
        sb.append(", firstCount=").append(firstCount);
        sb.append(", additionalAmount=").append(additionalAmount);
        sb.append(", additionalCount=").append(additionalCount);
        sb.append(", sort=").append(sort);
        sb.append(", regionIds=").append(regionIds);
        sb.append(", regionNames=").append(regionNames);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}