package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值订单结果
 */
@Data
@Schema(description = "充值订单结果")
public class RechargeOrderResult {

    @Schema(description = "充值订单ID")
    private Long orderId;

    @Schema(description = "充值订单号")
    private String orderSn;

    @Schema(description = "充值金额")
    private BigDecimal amount;

    @Schema(description = "支付方式：1-支付宝，2-微信")
    private Integer payType;

    @Schema(description = "订单状态：0-待支付，1-支付成功，2-支付失败，3-已取消")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "支付参数（用于调用支付接口）")
    private Object payParams;

    @Schema(description = "支付跳转URL（支付宝等需要）")
    private String payUrl;
} 