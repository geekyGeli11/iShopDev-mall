package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 组合商品项参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(title = "BundleItemParam", description = "组合商品项参数")
public class BundleItemParam {

    @NotNull(message = "商品ID不能为空")
    @Schema(title = "商品ID")
    private Long productId;

    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量至少为1")
    @Schema(title = "商品数量")
    private Integer quantity;

    @Schema(title = "排序")
    private Integer sort;
}
