package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 积分兑换支付成功参数
 * Created by macro on 2024/01/20.
 */
@Schema(description = "积分兑换支付成功参数")
public class ExchangePaySuccessParam {
    
    @Schema(description = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderSn;
    
    @Schema(description = "支付方式：1-支付宝，2-微信支付")
    @NotNull(message = "支付方式不能为空")
    private Integer payType;
    
    @Schema(description = "第三方支付流水号")
    @NotBlank(message = "第三方支付流水号不能为空")
    private String paySn;
    
    // Getters and Setters
    public String getOrderSn() {
        return orderSn;
    }
    
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }
    
    public Integer getPayType() {
        return payType;
    }
    
    public void setPayType(Integer payType) {
        this.payType = payType;
    }
    
    public String getPaySn() {
        return paySn;
    }
    
    public void setPaySn(String paySn) {
        this.paySn = paySn;
    }
}