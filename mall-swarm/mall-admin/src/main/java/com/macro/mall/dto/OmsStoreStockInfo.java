package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 门店SKU库存信息
 */
@Data
@Schema(description = "门店SKU库存信息")
public class OmsStoreStockInfo {
    
    @Schema(title = "门店ID")
    private Long storeId;
    
    @Schema(title = "门店名称")
    private String storeName;
    
    @Schema(title = "门店类型：STORE-门店，WAREHOUSE-地库")
    private String storeType;
    
    @Schema(title = "是否为地库")
    private Boolean isWarehouse;
    
    @Schema(title = "当前库存")
    private Integer stock;
    
    @Schema(title = "SKU ID")
    private Long skuId;
    
    @Schema(title = "SKU编码")
    private String skuCode;
}
