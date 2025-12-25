package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 库存扣减方案
 */
@Data
public class PmsStockDeductionPlan {
    
    @Schema(title = "SKU ID")
    private Long skuId;
    
    @Schema(title = "SKU编码")
    private String skuCode;
    
    @Schema(title = "商品名称")
    private String productName;
    
    @Schema(title = "需要扣减的总数量")
    private Integer totalQuantity;
    
    @Schema(title = "从各门店扣减的详情")
    private List<StoreDeductionDetail> storeDeductions;
    
    /**
     * 门店扣减详情
     */
    @Data
    public static class StoreDeductionDetail {
        
        @Schema(title = "门店ID")
        private Long storeId;
        
        @Schema(title = "门店名称")
        private String storeName;
        
        @Schema(title = "该门店的库存")
        private Integer storeStock;
        
        @Schema(title = "从该门店扣减的数量")
        private Integer deductionQuantity;
        
        @Schema(title = "是否为地库")
        private Boolean isWarehouse;
    }
}
