package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 组合商品列表项
 */
@Data
@Schema(title = "PortalBundleListItem", description = "组合商品列表项")
public class PortalBundleListItem {

    @Schema(title = "组合ID")
    private Long id;

    @Schema(title = "组合名称")
    private String name;

    @Schema(title = "组合主图")
    private String pic;

    @Schema(title = "组合描述")
    private String description;

    @Schema(title = "组合价格")
    private BigDecimal bundlePrice;

    @Schema(title = "原价总和")
    private BigDecimal originalPrice;

    @Schema(title = "节省金额")
    private BigDecimal savedAmount;

    @Schema(title = "包含商品数量")
    private Integer productCount;

    @Schema(title = "销售数量")
    private Integer saleCount;
}
