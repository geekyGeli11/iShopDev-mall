package com.macro.mall.portal.dto;

import com.macro.mall.model.CmsTraceabilityProductRelation;
import com.macro.mall.model.PmsProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 溯源与产品关系及商品信息封装
 */
@Getter
@Setter
public class TraceabilityProduct extends CmsTraceabilityProductRelation {

    @Schema(title = "关联商品")
    private PmsProduct product;  // 商品信息

}
