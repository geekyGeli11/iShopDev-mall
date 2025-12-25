package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 库存操作参数
 * Created by macro on 2024-01-20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PmsStockOperationParam {
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "操作类型：in-入库，out-出库，adjust-调整，transfer-调货")
    private String operationType;
    
    @Schema(title = "操作子类型")
    private Integer operationSubtype;
    
    @Schema(title = "操作理由")
    private String reason;
    
    @Schema(title = "操作描述")
    private String description;
    
    @Schema(title = "源门店ID（调货操作时使用）")
    private Long fromStoreId;
    
    @Schema(title = "目标门店ID（调货操作时使用）")
    private Long toStoreId;
    
    @Schema(title = "操作明细")
    private List<PmsStockOperationItem> items;
    
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class PmsStockOperationItem {
        @Schema(title = "门店SKU库存ID（pms_store_sku_stock表的id）")
        private Long storeSkuStockId;
        
        @Schema(title = "SKU ID（pms_sku_stock表的id）")
        private Long skuId;
        
        @Schema(title = "SKU编码")
        private String skuCode;
        
        @Schema(title = "门店ID")
        private Long storeId;
        
        @Schema(title = "操作数量")
        private Integer operationQuantity;
        
        @Schema(title = "操作前库存")
        private Integer beforeStock;
        
        @Schema(title = "目标门店ID（调货操作时使用）")
        private Long targetStoreId;
        
        @Schema(title = "目标门店操作前库存（调货操作时使用）")
        private Integer targetBeforeStock;
    }
} 