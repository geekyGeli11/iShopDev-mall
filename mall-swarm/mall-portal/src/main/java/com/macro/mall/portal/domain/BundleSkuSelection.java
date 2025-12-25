package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 组合商品SKU选择
 */
@Data
@Schema(title = "BundleSkuSelection", description = "组合商品SKU选择")
public class BundleSkuSelection {

    @NotNull(message = "商品ID不能为空")
    @Schema(title = "商品ID")
    private Long productId;

    @NotNull(message = "SKU ID不能为空")
    @Schema(title = "SKU ID")
    private Long skuId;
}
