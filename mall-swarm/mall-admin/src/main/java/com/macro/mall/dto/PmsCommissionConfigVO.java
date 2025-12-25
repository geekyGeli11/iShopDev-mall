package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 佣金配置展示VO
 */
@Schema(description = "佣金配置展示对象")
public class PmsCommissionConfigVO {
    
    @Schema(description = "配置ID")
    private Long id;
    
    @Schema(description = "配置名称")
    private String configName;
    
    @Schema(description = "商品分类ID")
    private Long productCategoryId;
    
    @Schema(description = "商品分类名称")
    private String productCategoryName;
    
    @Schema(description = "一级分销佣金比例")
    private BigDecimal level1Rate;
    
    @Schema(description = "二级分销佣金比例")
    private BigDecimal level2Rate;
    
    @Schema(description = "一级分销固定佣金金额")
    private BigDecimal level1Amount;
    
    @Schema(description = "二级分销固定佣金金额")
    private BigDecimal level2Amount;
    
    @Schema(description = "佣金类型：1-按比例，2-固定金额，3-比例+固定")
    private Byte commissionType;
    
    @Schema(description = "佣金类型文本")
    private String commissionTypeText;
    
    @Schema(description = "最小订单金额")
    private BigDecimal minOrderAmount;
    
    @Schema(description = "单笔最大佣金")
    private BigDecimal maxCommission;
    
    @Schema(description = "是否启用：0-禁用，1-启用")
    private Byte isActive;
    
    @Schema(description = "是否启用文本")
    private String isActiveText;
    
    @Schema(description = "生效开始时间")
    private Date startTime;
    
    @Schema(description = "生效结束时间")
    private Date endTime;
    
    @Schema(description = "创建时间")
    private Date createdAt;
    
    @Schema(description = "更新时间")
    private Date updatedAt;
    
    @Schema(description = "应用产品数量")
    private Integer productCount;
    
    @Schema(description = "累计发放佣金")
    private BigDecimal totalCommission;

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

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
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
        // 设置类型文本
        if (commissionType != null) {
            switch (commissionType) {
                case 1:
                    this.commissionTypeText = "按比例";
                    break;
                case 2:
                    this.commissionTypeText = "固定金额";
                    break;
                case 3:
                    this.commissionTypeText = "比例+固定";
                    break;
                default:
                    this.commissionTypeText = "未知类型";
            }
        }
    }

    public String getCommissionTypeText() {
        return commissionTypeText;
    }

    public void setCommissionTypeText(String commissionTypeText) {
        this.commissionTypeText = commissionTypeText;
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
        // 设置启用状态文本
        if (isActive != null) {
            this.isActiveText = isActive == 1 ? "启用" : "禁用";
        }
    }

    public String getIsActiveText() {
        return isActiveText;
    }

    public void setIsActiveText(String isActiveText) {
        this.isActiveText = isActiveText;
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

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
} 