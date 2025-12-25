package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 商品兑换参数
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductExchangeParam {
    
    @Schema(title = "换购配置ID", required = true)
    @NotNull(message = "换购配置ID不能为空")
    private Long configId;
    
    @Schema(title = "商品SKU ID")
    private Long productSkuId;
    
    @Schema(title = "兑换数量", required = true)
    @NotNull(message = "兑换数量不能为空")
    @Min(value = 1, message = "兑换数量必须大于0")
    private Integer quantity;
    
    @Schema(title = "收货地址ID", required = true)
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
    
    @Schema(title = "兑换备注")
    private String remark;
} 