package com.macro.mall.portal.domain;

import com.macro.mall.model.UmsIntegrationConsumeSetting;
import com.macro.mall.model.UmsMemberReceiveAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 组合商品确认订单结果
 */
@Data
@Schema(title = "BundleConfirmOrderResult", description = "组合商品确认订单结果")
public class BundleConfirmOrderResult {

    @Schema(title = "组合商品信息")
    private BundleInfo bundleInfo;

    @Schema(title = "组合内商品订单项列表")
    private List<BundleOrderItem> orderItems;

    @Schema(title = "用户收货地址列表")
    private List<UmsMemberReceiveAddress> memberReceiveAddressList;

    @Schema(title = "用户可用优惠券列表")
    private List<SmsCouponHistoryDetail> couponHistoryDetailList;

    @Schema(title = "积分使用规则")
    private UmsIntegrationConsumeSetting integrationConsumeSetting;

    @Schema(title = "会员持有的积分")
    private Integer memberIntegration;

    @Schema(title = "会员当前余额")
    private BigDecimal memberBalance;

    @Schema(title = "计算的金额")
    private CalcAmount calcAmount;

    /**
     * 组合商品信息
     */
    @Data
    @Schema(title = "BundleInfo", description = "组合商品信息")
    public static class BundleInfo {
        @Schema(title = "组合ID")
        private Long bundleId;

        @Schema(title = "组合名称")
        private String bundleName;

        @Schema(title = "组合图片")
        private String bundlePic;

        @Schema(title = "组合价格")
        private BigDecimal bundlePrice;

        @Schema(title = "原价总和")
        private BigDecimal originalPrice;

        @Schema(title = "节省金额")
        private BigDecimal savedAmount;

        @Schema(title = "购买数量")
        private Integer quantity;
    }

    /**
     * 组合内商品订单项
     */
    @Data
    @Schema(title = "BundleOrderItem", description = "组合内商品订单项")
    public static class BundleOrderItem {
        @Schema(title = "商品ID")
        private Long productId;

        @Schema(title = "商品名称")
        private String productName;

        @Schema(title = "商品图片")
        private String productPic;

        @Schema(title = "SKU ID")
        private Long skuId;

        @Schema(title = "SKU编码")
        private String skuCode;

        @Schema(title = "商品规格")
        private String spData;

        @Schema(title = "商品单价")
        private BigDecimal price;

        @Schema(title = "商品数量")
        private Integer quantity;

        @Schema(title = "组合优惠分摊金额")
        private BigDecimal bundleDiscount;
    }

    /**
     * 计算金额
     */
    @Data
    @Schema(title = "CalcAmount", description = "计算金额")
    public static class CalcAmount {
        @Schema(title = "订单商品总金额（原价）")
        private BigDecimal totalAmount;

        @Schema(title = "组合优惠金额")
        private BigDecimal bundleDiscountAmount;

        @Schema(title = "运费")
        private BigDecimal freightAmount;

        @Schema(title = "优惠券优惠金额")
        private BigDecimal couponAmount;

        @Schema(title = "积分抵扣金额")
        private BigDecimal integrationAmount;

        @Schema(title = "应付金额")
        private BigDecimal payAmount;
    }
}
