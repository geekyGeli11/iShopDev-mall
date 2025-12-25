package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 送礼订单参数
 */
@Data
public class GiftOrderParam {
    @Schema(title = "送礼商品列表")
    private List<DirectBuyParam> directBuyParamList;

    @Schema(title = "支付方式")
    private Integer payType;

    @Schema(title = "送礼祝福文字")
    private String giftMessage;

    @Schema(title = "送礼祝福图片")
    private String giftPic;
} 