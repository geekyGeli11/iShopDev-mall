package com.macro.mall.portal.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新订单通知参数
 */
@Data
public class NewOrderNotificationParam {
    
    /**
     * 订单号
     */
    private String orderSn;
    
    /**
     * 订单类型
     */
    private String orderType;
    
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 下单时间
     */
    private Date orderTime;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 发货门店ID
     */
    private Long storeId;
}
