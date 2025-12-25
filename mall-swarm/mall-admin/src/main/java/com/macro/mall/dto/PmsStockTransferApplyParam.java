package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 调货申请参数
 */
@Data
public class PmsStockTransferApplyParam {
    
    @Schema(title = "调货源门店ID（供货门店）")
    private Long fromStoreId;
    
    @Schema(title = "调货目标门店ID（收货门店）")
    private Long toStoreId;
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "操作子类型")
    private Integer operationSubtype;
    
    @Schema(title = "操作理由")
    private String reason;
    
    @Schema(title = "操作描述")
    private String description;
    
    @Schema(title = "调货明细列表")
    private List<TransferItem> items;
    
    @Data
    public static class TransferItem {
        @Schema(title = "SKU ID")
        private Long skuId;
        
        @Schema(title = "SKU编码")
        private String skuCode;
        
        @Schema(title = "商品名称")
        private String productName;
        
        @Schema(title = "商品货号")
        private String productSn;
        
        @Schema(title = "申请调货数量")
        private Integer operationQuantity;
        
        @Schema(title = "供货门店当前库存")
        private Integer fromStoreStock;
        
        @Schema(title = "收货门店当前库存")
        private Integer toStoreStock;
    }
}
