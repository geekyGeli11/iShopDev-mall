package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存不一致记录DTO
 */
@Data
@Schema(description = "库存不一致记录")
public class StockInconsistencyDto {
    
    @Schema(description = "SKU ID")
    private Long skuId;
    
    @Schema(description = "SKU编码")
    private String skuCode;
    
    @Schema(description = "记录的总库存")
    private Integer totalStock;
    
    @Schema(description = "计算得出的总库存")
    private Integer calculatedStock;
    
    @Schema(description = "差异数量")
    private Integer difference;
}
