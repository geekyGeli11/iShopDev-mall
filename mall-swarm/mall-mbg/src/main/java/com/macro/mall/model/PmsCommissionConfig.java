package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsCommissionConfig implements Serializable {
    private Long id;

    @Schema(title = "配置名称")
    private String configName;

    @Schema(title = "商品分类ID，NULL表示全局配置")
    private Long productCategoryId;

    @Schema(title = "一级分销佣金比例")
    private BigDecimal level1Rate;

    @Schema(title = "二级分销佣金比例")
    private BigDecimal level2Rate;

    @Schema(title = "一级分销固定佣金金额")
    private BigDecimal level1Amount;

    @Schema(title = "二级分销固定佣金金额")
    private BigDecimal level2Amount;

    @Schema(title = "佣金类型：1-按比例，2-固定金额，3-比例+固定")
    private Byte commissionType;

    @Schema(title = "最小订单金额")
    private BigDecimal minOrderAmount;

    @Schema(title = "单笔最大佣金")
    private BigDecimal maxCommission;

    @Schema(title = "是否启用：0-禁用，1-启用")
    private Byte isActive;

    @Schema(title = "生效开始时间")
    private Date startTime;

    @Schema(title = "生效结束时间")
    private Date endTime;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public BigDecimal getLevel1Rate() {
        return level1Rate;
    }

    public void setLevel1Rate(BigDecimal level1Rate) {
        this.level1Rate = level1Rate;
    }

    public BigDecimal getLevel2Rate() {
        return level2Rate;
    }

    public void setLevel2Rate(BigDecimal level2Rate) {
        this.level2Rate = level2Rate;
    }

    public BigDecimal getLevel1Amount() {
        return level1Amount;
    }

    public void setLevel1Amount(BigDecimal level1Amount) {
        this.level1Amount = level1Amount;
    }

    public BigDecimal getLevel2Amount() {
        return level2Amount;
    }

    public void setLevel2Amount(BigDecimal level2Amount) {
        this.level2Amount = level2Amount;
    }

    public Byte getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Byte commissionType) {
        this.commissionType = commissionType;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getMaxCommission() {
        return maxCommission;
    }

    public void setMaxCommission(BigDecimal maxCommission) {
        this.maxCommission = maxCommission;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", configName=").append(configName);
        sb.append(", productCategoryId=").append(productCategoryId);
        sb.append(", level1Rate=").append(level1Rate);
        sb.append(", level2Rate=").append(level2Rate);
        sb.append(", level1Amount=").append(level1Amount);
        sb.append(", level2Amount=").append(level2Amount);
        sb.append(", commissionType=").append(commissionType);
        sb.append(", minOrderAmount=").append(minOrderAmount);
        sb.append(", maxCommission=").append(maxCommission);
        sb.append(", isActive=").append(isActive);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}