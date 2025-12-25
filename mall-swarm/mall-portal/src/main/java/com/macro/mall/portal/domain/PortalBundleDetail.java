package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsSkuStock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 组合商品详情响应
 */
@Data
@Schema(title = "PortalBundleDetail", description = "组合商品详情响应")
public class PortalBundleDetail {

    @Schema(title = "组合ID")
    private Long id;

    @Schema(title = "组合名称")
    private String name;

    @Schema(title = "组合主图")
    private String pic;

    @Schema(title = "组合宣传图片列表")
    private List<String> albumPics;

    @Schema(title = "组合描述")
    private String description;

    @Schema(title = "组合详情描述(富文本)")
    private String detailDesc;

    @Schema(title = "定价方式：1-固定价格 2-折扣比例")
    private Integer priceType;

    @Schema(title = "组合价格")
    private BigDecimal bundlePrice;

    @Schema(title = "折扣比例")
    private BigDecimal discountRate;

    @Schema(title = "原价总和")
    private BigDecimal originalPrice;

    @Schema(title = "节省金额")
    private BigDecimal savedAmount;

    @Schema(title = "销售数量")
    private Integer saleCount;

    @Schema(title = "组合内商品列表")
    private List<BundleProductItem> products;

    /**
     * 组合内商品项
     */
    @Data
    @Schema(title = "BundleProductItem", description = "组合内商品项")
    public static class BundleProductItem {

        @Schema(title = "商品ID")
        private Long productId;

        @Schema(title = "商品名称")
        private String productName;

        @Schema(title = "商品图片")
        private String productPic;

        @Schema(title = "商品数量")
        private Integer quantity;

        @Schema(title = "商品单价")
        private BigDecimal price;

        @Schema(title = "商品副标题")
        private String subTitle;

        @Schema(title = "SKU列表")
        private List<PmsSkuStock> skuList;
    }
}
