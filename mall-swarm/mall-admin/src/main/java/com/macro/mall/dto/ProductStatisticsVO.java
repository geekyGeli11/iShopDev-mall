package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 商品销售数据统计响应DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "商品销售数据统计响应")
public class ProductStatisticsVO {
    
    @Schema(description = "总商品数")
    private Integer totalProducts;
    
    @Schema(description = "销售总额")
    private Double totalSalesAmount;
    
    @Schema(description = "销售总数量")
    private Integer totalSalesQuantity;
    
    @Schema(description = "热销商品数")
    private Integer hotProducts;
    
    @Schema(description = "按销售金额排行")
    private List<ProductRankingVO> topSellingByAmount;
    
    @Schema(description = "按销售数量排行")
    private List<ProductRankingVO> topSellingByQuantity;
    
    @Schema(description = "盈利汇总数据")
    private ProfitSummaryVO profitSummary;
    
    @Schema(description = "品类销售分布")
    private List<CategoryDistributionVO> categoryDistribution;
}
