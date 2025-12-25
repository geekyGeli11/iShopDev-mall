package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 品类销售分布DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "品类销售分布")
public class CategoryDistributionVO {
    
    @Schema(description = "品类名称")
    private String category;
    
    @Schema(description = "销售金额")
    private BigDecimal salesAmount;
    
    @Schema(description = "占比百分比")
    private BigDecimal percentage;
}
