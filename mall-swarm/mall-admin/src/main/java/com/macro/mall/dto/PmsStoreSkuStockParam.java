package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 门店SKU库存参数
 * Created by macro on 2024-01-20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PmsStoreSkuStockParam {
    
    @Schema(title = "SKU ID")
    private Long skuId;
    
    @Schema(title = "SKU编码")
    private String skuCode;
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "SKU总库存")
    private Integer totalStock;
    
    @Schema(title = "门店库存分配列表")
    private List<StoreStockAllocation> storeAllocations;
    
    /**
     * 门店库存分配明细
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class StoreStockAllocation {
        
        @Schema(title = "门店ID")
        private Long storeId;
        
        @Schema(title = "门店名称")
        private String storeName;
        
        @Schema(title = "分配库存数量")
        private Integer stock;
        
        @Schema(title = "门店库存预警值")
        private Integer lowStock;
        
        @Schema(title = "状态：0->禁用；1->启用")
        private Integer status;
    }
} 