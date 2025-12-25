package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单发货参数
 * Created by macro on 2018/10/12.
 */
@Getter
@Setter
public class OmsOrderDeliveryParam {
    @Schema(title = "订单id")
    private Long orderId;
    @Schema(title = "物流公司")
    private String deliveryCompany;
    @Schema(title = "物流单号")
    private String deliverySn;
    @Schema(title = "发货方式：1-快递配送，2-门店自提，3-虚拟发货")
    private Integer deliveryMethod;
    @Schema(title = "虚拟发货信息（JSON格式，包含激活码、下载链接等）")
    private String virtualDeliveryInfo;
    @Schema(title = "门店名称（门店自提时使用）")
    private String storeName;
    @Schema(title = "联系电话（门店自提时使用）")
    private String contactPhone;
}
