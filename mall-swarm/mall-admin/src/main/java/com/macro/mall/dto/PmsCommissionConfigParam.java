package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * 佣金配置参数
 */
@Schema(description = "佣金配置参数")
public class PmsCommissionConfigParam {
    
    @Schema(description = "配置ID（更新时必填）")
    private Long id;
    
    @Schema(description = "配置名称", required = true)
    private String configName;
    
    @Schema(description = "商品分类ID（NULL表示全局配置）")
    private Long productCategoryId;
    
    @Schema(description = "一级分销佣金比例", required = true)
    private BigDecimal level1Rate;
    
    @Schema(description = "二级分销佣金比例", required = true)
    private BigDecimal level2Rate;
    
    @Schema(description = "一级分销固定佣金金额")
    private BigDecimal level1Amount;
    
    @Schema(description = "二级分销固定佣金金额")
    private BigDecimal level2Amount;
    
    @Schema(description = "佣金类型：1-按比例，2-固定金额，3-比例+固定", required = true)
    private Byte commissionType;
    
    @Schema(description = "最小订单金额")
    private BigDecimal minOrderAmount;
    
    @Schema(description = "单笔最大佣金")
    private BigDecimal maxCommission;
    
    @Schema(description = "是否启用：0-禁用，1-启用", required = true)
    private Byte isActive;
    
    @Schema(description = "生效开始时间")
    private String startTime;
    
    @Schema(description = "生效结束时间")
    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
} 