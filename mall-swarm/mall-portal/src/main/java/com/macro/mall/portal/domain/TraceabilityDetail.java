package com.macro.mall.portal.domain;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.portal.dto.TraceabilityProduct;
import com.macro.mall.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 前台溯源详情
 */
@Getter
@Setter
public class TraceabilityDetail {
    @Schema(title = "溯源信息")
    private CmsTraceabilityList traceability;

    @Schema(title = "溯源关联商品列表")
    private List<TraceabilityProduct> productRelations;

    @Schema(title = "溯源关联商品分页信息")
    private CommonPage<TraceabilityProduct> productRelationsPageInfo;
}