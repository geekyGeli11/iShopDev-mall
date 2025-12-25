package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 组合商品下单请求参数
 */
@Data
@Schema(title = "BundleOrderParam", description = "组合商品下单请求参数")
public class BundleOrderParam {

    @NotNull(message = "组合商品ID不能为空")
    @Schema(title = "组合商品ID")
    private Long bundleId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    @Schema(title = "组合购买数量")
    private Integer quantity;

    @NotNull(message = "SKU选择不能为空")
    @Size(min = 1, message = "请为所有商品选择规格")
    @Schema(title = "SKU选择列表")
    private List<BundleSkuSelection> skuSelections;

    @Schema(title = "收货地址ID")
    private Long addressId;

    @Schema(title = "优惠券ID")
    private Long couponId;

    @Schema(title = "使用积分数量")
    private Integer useIntegration;

    @Schema(title = "支付方式：0-未支付 1-支付宝 2-微信")
    private Integer payType;

    @Schema(title = "门店ID（自提时使用）")
    private Long storeId;

    @Schema(title = "配送方式：0-快递配送 1-门店自取")
    private Integer deliveryType;

    @Schema(title = "订单备注")
    private String note;

    @Schema(title = "学校ID")
    private Long schoolId;
}
