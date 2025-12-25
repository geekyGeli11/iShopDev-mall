package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 订单商品库存扣除详情
 */
@Data
@Schema(description = "订单商品库存扣除详情")
public class OmsStockDeductionDetail {
    
    @Schema(title = "订单商品ID")
    private Long orderItemId;
    
    @Schema(title = "商品ID")
    private Long productId;
    
    @Schema(title = "SKU ID")
    private Long skuId;
    
    @Schema(title = "SKU编码")
    private String skuCode;
    
    @Schema(title = "商品名称")
    private String productName;
    
    @Schema(title = "扣除门店ID")
    private Long storeId;
    
    @Schema(title = "扣除门店名称")
    private String storeName;
    
    @Schema(title = "门店类型：STORE-门店，WAREHOUSE-地库")
    private String storeType;
    
    @Schema(title = "扣除数量")
    private Integer deductQuantity;
    
    @Schema(title = "扣除前库存")
    private Integer beforeStock;
    
    @Schema(title = "扣除后库存")
    private Integer afterStock;
    
    @Schema(title = "操作单号")
    private String operationNo;
    
    @Schema(title = "操作时间")
    private Date operationTime;
    
    @Schema(title = "操作原因")
    private String operationReason;
}
