package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单退款参数
 * Created by macro on 2024/01/01.
 */
@Data
@Schema(description = "订单退款参数")
public class OmsOrderRefundParam {
    
    @Schema(description = "订单ID", required = true)
    private Long orderId;
    
    @Schema(description = "退款金额", required = true)
    private BigDecimal refundAmount;
    
    @Schema(description = "退款原因")
    private String refundReason;
    
    @Schema(description = "操作人")
    private String operator;
}
