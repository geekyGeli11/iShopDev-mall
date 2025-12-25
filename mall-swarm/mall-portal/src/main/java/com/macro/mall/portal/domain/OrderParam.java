package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 生成订单时传入的参数
 * Created by macro on 2018/8/30.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderParam {
    @Schema(title = "收货地址ID")
    private Long memberReceiveAddressId;
    @Schema(title = "优惠券ID")
    private Long couponId;
    @Schema(title = "使用的积分数")
    private Integer useIntegration;
    @Schema(title = "支付方式")
    private Integer payType;
    @Schema(title = "被选中的购物车商品ID")
    private List<Long> cartIds;
    @Schema(title = "商品促销信息（直接购买时使用）")
    private List<CartPromotionItem> cartPromotionItemList;
    @Schema(title = "配送方式：0->快递配送；1->门店自取")
    private Integer deliveryType;
    @Schema(title = "是否为送礼订单：0->正常订单；1->送礼订单")
    private Integer isGift;
    @Schema(title = "送礼赠言")
    private String giftMessage;
    @Schema(title = "自提门店ID")
    private Long storeId;
    @Schema(title = "订单备注")
    private String note;
    @Schema(title = "学校ID")
    private Long schoolId;
    @Schema(title = "订单类型：0->正常订单；1->秒杀订单；2->积分兑换订单")
    private Integer orderType;
}
