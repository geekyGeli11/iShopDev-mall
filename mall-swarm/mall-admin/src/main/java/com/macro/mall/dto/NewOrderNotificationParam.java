package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新订单通知参数
 */
@Data
@Schema(description = "新订单通知参数")
public class NewOrderNotificationParam {
    
    @Schema(description = "订单号")
    private String orderSn;
    
    @Schema(description = "订单类型")
    private String orderType;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmount;
    
    @Schema(description = "下单时间")
    private Date orderTime;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "发货门店ID")
    private Long storeId;
}
