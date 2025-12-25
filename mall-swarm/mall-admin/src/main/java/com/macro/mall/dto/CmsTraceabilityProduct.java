package com.macro.mall.dto;

import com.macro.mall.model.CmsTraceabilityProductRelation;
import com.macro.mall.model.PmsProduct;  // 假设有 Product 类代表商品信息
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 溯源与产品关系及商品信息封装
 */
@Getter
@Setter
public class CmsTraceabilityProduct extends CmsTraceabilityProductRelation {

    @Schema(title = "关联商品")
    private PmsProduct product;  // 商品信息

}
