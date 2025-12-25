package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 兑换结果
 * Created by macro on 2024/01/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "兑换结果")
public class ExchangeResult {
    
    @Schema(description = "关联订单ID（商品换购）")
    private Long orderId;

    @Schema(description = "关联订单号（商品换购）")
    private String orderSn;

    @Schema(description = "关联优惠券领取记录ID（优惠券兑换）")
    private Long couponHistoryId;
    
    @Schema(description = "使用积分")
    private Integer pointsUsed;
    
    @Schema(description = "支付现金金额")
    private BigDecimal cashAmount;
    
    @Schema(description = "兑换数量")
    private Integer quantity;
    
    @Schema(description = "兑换类型：1-商品，2-优惠券")
    private Byte exchangeType;
    
    @Schema(description = "兑换状态：1-成功，2-失败，3-已退回")
    private Byte exchangeStatus;
    
    @Schema(description = "目标名称")
    private String targetName;
    
    @Schema(description = "提示信息")
    private String message;
} 