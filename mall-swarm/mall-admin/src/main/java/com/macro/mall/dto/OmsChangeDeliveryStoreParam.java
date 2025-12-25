package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 改选发货门店参数
 */
@Data
@Schema(description = "改选发货门店参数")
public class OmsChangeDeliveryStoreParam {
    
    @Schema(title = "订单ID", required = true)
    private Long orderId;
    
    @Schema(title = "订单编号", required = true)
    private String orderSn;
    
    @Schema(title = "商品ID", required = true)
    private Long productId;
    
    @Schema(title = "SKU ID", required = true)
    private Long skuId;
    
    @Schema(title = "原发货门店ID", required = true)
    private Long originalStoreId;
    
    @Schema(title = "新发货门店ID", required = true)
    private Long newStoreId;
    
    @Schema(title = "数量", required = true)
    private Integer quantity;
}
