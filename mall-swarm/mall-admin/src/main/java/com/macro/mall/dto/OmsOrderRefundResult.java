package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单退款结果
 * Created by macro on 2024/01/01.
 */
@Data
@Schema(description = "订单退款结果")
public class OmsOrderRefundResult {
    
    @Schema(description = "是否成功")
    private boolean success;
    
    @Schema(description = "退款单号")
    private String refundSn;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;
    
    @Schema(description = "退款方式：微信退款、支付宝退款、余额退款")
    private String refundMethod;
    
    @Schema(description = "退款状态：PROCESSING-处理中、SUCCESS-成功、FAILED-失败")
    private String refundStatus;
    
    @Schema(description = "退款时间")
    private Date refundTime;
    
    @Schema(description = "错误信息")
    private String errorMsg;
    
    @Schema(description = "订单号")
    private String orderSn;
    
    @Schema(description = "订单ID")
    private Long orderId;
}
