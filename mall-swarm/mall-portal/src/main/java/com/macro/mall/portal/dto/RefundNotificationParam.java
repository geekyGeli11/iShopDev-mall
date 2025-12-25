package com.macro.mall.portal.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退款申请通知参数
 */
@Data
public class RefundNotificationParam {
    
    /**
     * 订单号
     */
    private String orderSn;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
    
    /**
     * 申请时间
     */
    private Date applyTime;
    
    /**
     * 联系电话
     */
    private String phoneNumber;
}
