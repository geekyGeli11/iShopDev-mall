package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款申请通知参数
 */
@Data
@Schema(description = "退款申请通知参数")
public class RefundNotificationParam {
    
    @Schema(description = "订单号")
    private String orderSn;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;
    
    @Schema(description = "申请时间")
    private Date applyTime;
    
    @Schema(description = "联系电话")
    private String phoneNumber;
}
