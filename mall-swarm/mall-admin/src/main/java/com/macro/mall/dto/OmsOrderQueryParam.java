package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单查询参数
 * Created by macro on 2018/10/11.
 */
@Getter
@Setter
public class OmsOrderQueryParam {
    @Schema(title = "订单编号")
    private String orderSn;
    @Schema(title = "收货人姓名/号码")
    private String receiverKeyword;
    @Schema(title = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Integer status;
    @Schema(title = "订单类型：0->正常订单；1->秒杀订单")
    private Integer orderType;
    @Schema(title = "订单来源：0->PC订单；1->app订单")
    private Integer sourceType;
    @Schema(title = "订单提交时间")
    private String createTime;
    @Schema(title = "开始时间")
    private String startTime;
    @Schema(title = "结束时间")
    private String endTime;
    @Schema(title = "订单取货方式：0->快递送达；1->上门自取")
    private Integer deliveryType;
    @Schema(title = "支付方式：0->未支付；1->支付宝；2->微信；3->余额支付")
    private Integer payType;
    @Schema(title = "是否为礼品订单：true->是；false->否")
    private Boolean isGift;
    @Schema(title = "商品名称")
    private String productName;
    @Schema(title = "是否有售后申请：true->有售后申请；false->无售后申请")
    private Boolean hasReturnApply;
    @Schema(title = "学校ID（用于筛选特定学校的订单）")
    private Long schoolId;
    @Schema(title = "发货门店ID（通过库存操作记录筛选）")
    private Long deliveryStoreId;
}
