package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 盈利汇总数据DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "盈利汇总数据")
public class ProfitSummaryVO {
    
    @Schema(description = "总利润")
    private BigDecimal totalProfit;
    
    @Schema(description = "利润率（百分比）")
    private BigDecimal profitMargin;
    
    @Schema(description = "利润最高的商品")
    private List<ProductRankingVO> topProfitProducts;
}
