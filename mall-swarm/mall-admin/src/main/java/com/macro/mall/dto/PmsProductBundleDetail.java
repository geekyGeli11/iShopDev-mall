package com.macro.mall.dto;

import com.macro.mall.model.PmsProductBundle;
import com.macro.mall.model.PmsProductBundleItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 组合商品详情响应
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "PmsProductBundleDetail", description = "组合商品详情响应")
public class PmsProductBundleDetail extends PmsProductBundle {

    @Schema(title = "节省金额")
    private BigDecimal savedAmount;

    @Schema(title = "组合商品项列表")
    private List<BundleItemDetail> items;

    /**
     * 组合商品项详情（包含商品价格信息）
     */
    @Data
    @Schema(title = "BundleItemDetail", description = "组合商品项详情")
    public static class BundleItemDetail extends PmsProductBundleItem {

        @Schema(title = "商品单价")
        private BigDecimal productPrice;

        @Schema(title = "商品上架状态：0-下架 1-上架")
        private Integer productPublishStatus;
    }
}
