package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 积分兑换支付订单结果
 * Created by macro on 2024/01/20.
 */
@Schema(description = "积分兑换支付订单结果")
public class ExchangePayOrderResult {
    
    @Schema(description = "订单号")
    private String orderSn;
    
    @Schema(description = "支付金额")
    private BigDecimal payAmount;
    
    @Schema(description = "积分数量")
    private Integer pointsAmount;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "兑换数量")
    private Integer quantity;
    
    // Getters and Setters
    public String getOrderSn() {
        return orderSn;
    }
    
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
    
    public BigDecimal getPayAmount() {
        return payAmount;
    }
    
    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
    
    public Integer getPointsAmount() {
        return pointsAmount;
    }
    
    public void setPointsAmount(Integer pointsAmount) {
        this.pointsAmount = pointsAmount;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}