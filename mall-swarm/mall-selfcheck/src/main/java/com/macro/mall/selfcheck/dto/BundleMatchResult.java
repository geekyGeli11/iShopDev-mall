package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 组合优惠匹配结果
 *
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "组合优惠匹配结果")
public class BundleMatchResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "是否匹配到组合优惠")
    private Boolean matched = false;

    @Schema(title = "匹配到的组合商品ID")
    private Long bundleId;

    @Schema(title = "组合商品名称")
    private String bundleName;

    @Schema(title = "组合原价")
    private BigDecimal originalPrice;

    @Schema(title = "组合优惠价")
    private BigDecimal bundlePrice;

    @Schema(title = "组合优惠金额")
    private BigDecimal savedAmount;

    @Schema(title = "匹配的组合数量")
    private Integer matchedQuantity = 0;

    @Schema(title = "总优惠金额（组合优惠 * 匹配数量）")
    private BigDecimal totalSavedAmount;

    @Schema(title = "各商品的优惠分摊")
    private Map<Long, BigDecimal> discountDistribution;

    @Schema(title = "各SKU的优惠分摊")
    private Map<Long, BigDecimal> skuDiscountDistribution;

    @Schema(title = "匹配详情")
    private List<MatchedItem> matchedItems;

    @Schema(title = "未匹配的商品（剩余商品）")
    private List<OrderItemParam> unmatchedItems;

    @Data
    @Schema(title = "匹配的商品项")
    public static class MatchedItem implements Serializable {
        private static final long serialVersionUID = 1L;

        @Schema(title = "商品ID")
        private Long productId;

        @Schema(title = "SKU ID")
        private Long skuId;

        @Schema(title = "商品名称")
        private String productName;

        @Schema(title = "匹配数量")
        private Integer quantity;

        @Schema(title = "原单价")
        private BigDecimal originalPrice;

        @Schema(title = "优惠后单价")
        private BigDecimal discountedPrice;

        @Schema(title = "分摊的优惠金额")
        private BigDecimal discountAmount;
    }
}
