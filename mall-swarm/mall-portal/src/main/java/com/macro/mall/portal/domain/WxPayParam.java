package com.macro.mall.portal.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 微信支付请求参数
 */
@Getter
@Setter
public class WxPayParam {
    private String body;        // 商品描述
    private String outTradeNo;  // 商户订单号
    private long totalFee;    // 总金额（单位：分）
    private String spbillCreateIp;  // 用户端IP
    private String openid;      // 用户的openid
    private String notifyUrl;   // 支付成功回调地址
}
