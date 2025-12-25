package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 库存分布信息DTO
 */
@Data
@Schema(description = "库存分布信息")
public class StockDistributionDto {
    
    @Schema(description = "SKU ID")
    private Long skuId;
    
    @Schema(description = "SKU编码")
    private String skuCode;
    
    @Schema(description = "总库存")
    private Integer totalStock;
    
    @Schema(description = "地库库存")
    private Integer warehouseStock;
    
    @Schema(description = "门店库存总和")
    private Integer storesStockSum;
    
    @Schema(description = "各门店库存明细")
    private List<StoreStockDetail> storeStocks;
    
    @Data
    @Schema(description = "门店库存明细")
    public static class StoreStockDetail {
        @Schema(description = "门店ID")
        private Long storeId;
        
        @Schema(description = "门店名称")
        private String storeName;
        
        @Schema(description = "门店类型：STORE-门店，WAREHOUSE-地库")
        private String storeType;
        
        @Schema(description = "库存数量")
        private Integer stock;
        
        @Schema(description = "预警库存")
        private Integer lowStock;
        
        @Schema(description = "库存状态：充足/不足")
        private String status;
    }
}
