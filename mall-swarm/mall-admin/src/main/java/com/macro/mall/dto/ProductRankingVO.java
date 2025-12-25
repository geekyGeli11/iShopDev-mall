package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品销售排行DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "商品销售排行")
public class ProductRankingVO {
    
    @Schema(description = "商品ID")
    private Long productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "SKU编码")
    private String skuCode;
    
    @Schema(description = "销售金额")
    private BigDecimal salesAmount;
    
    @Schema(description = "销售数量")
    private Integer salesQuantity;
    
    @Schema(description = "利润")
    private BigDecimal profit;
}
