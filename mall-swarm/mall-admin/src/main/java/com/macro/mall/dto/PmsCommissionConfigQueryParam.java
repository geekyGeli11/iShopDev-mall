package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 佣金配置查询参数
 */
@Schema(description = "佣金配置查询参数")
public class PmsCommissionConfigQueryParam {
    
    @Schema(description = "配置名称")
    private String configName;
    
    @Schema(description = "商品分类ID")
    private Long productCategoryId;
    
    @Schema(description = "佣金类型：1-按比例，2-固定金额，3-比例+固定")
    private Byte commissionType;
    
    @Schema(description = "是否启用：0-禁用，1-启用")
    private Byte isActive;
    
    @Schema(description = "生效开始时间")
    private String startTime;
    
    @Schema(description = "生效结束时间")
    private String endTime;
    
    @Schema(description = "创建开始时间")
    private String createStartTime;
    
    @Schema(description = "创建结束时间")
    private String createEndTime;

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

    public Byte getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Byte commissionType) {
        this.commissionType = commissionType;
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

    public String getCreateStartTime() {
        return createStartTime;
    }

    public void setCreateStartTime(String createStartTime) {
        this.createStartTime = createStartTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }
} 