package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 产品报损明细项参数
 */
@Data
@Schema(description = "产品报损明细项参数")
public class PmsProductDamageItemParam {
    
    @Schema(description = "商品ID")
    private Long productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品图片")
    private String productPic;
    
    @Schema(description = "SKU ID")
    private Long skuId;
    
    @Schema(description = "SKU编码")
    private String skuCode;
    
    @Schema(description = "SKU规格")
    private String skuSpec;
    
    @Schema(description = "报损数量")
    private Integer damageQuantity;
    
    @Schema(description = "成本价")
    private BigDecimal costPrice;
    
    @Schema(description = "销售价")
    private BigDecimal salePrice;
    
    @Schema(description = "明细备注")
    private String itemRemark;
}
