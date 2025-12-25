package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品报损统计结果
 */
@Data
@Schema(description = "产品报损统计结果")
public class PmsProductDamageStatistics {
    
    @Schema(description = "统计维度值（门店名称/报损类型/时间等）")
    private String dimension;
    
    @Schema(description = "报损数量")
    private Integer damageCount;
    
    @Schema(description = "报损金额")
    private BigDecimal damageAmount;
    
    @Schema(description = "销售金额")
    private BigDecimal salesAmount;
    
    @Schema(description = "营业额占比（%）")
    private BigDecimal salesRatio;
    
    @Schema(description = "待处理数量")
    private Integer pendingCount;
    
    @Schema(description = "处理中数量")
    private Integer processingCount;
    
    @Schema(description = "已完成数量")
    private Integer completedCount;
}
