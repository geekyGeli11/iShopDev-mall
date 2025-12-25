package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 积分兑换支付订单参数
 * Created by macro on 2024/01/20.
 */
@Schema(description = "积分兑换支付订单参数")
public class ExchangePayOrderParam {
    
    @Schema(description = "兑换配置ID")
    @NotNull(message = "兑换配置ID不能为空")
    private Long configId;
    
    @Schema(description = "商品SKU ID")
    private Long productSkuId;
    
    @Schema(description = "兑换数量")
    @NotNull(message = "兑换数量不能为空")
    @Positive(message = "兑换数量必须大于0")
    private Integer quantity;
    
    @Schema(description = "收货地址ID")
    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;
    
    @Schema(description = "现金支付金额")
    @NotNull(message = "现金支付金额不能为空")
    private BigDecimal cashAmount;
    
    @Schema(description = "积分支付数量")
    @NotNull(message = "积分支付数量不能为空")
    private Integer pointsAmount;
    
    @Schema(description = "备注")
    private String remark;
    
    // Getters and Setters
    public Long getConfigId() {
        return configId;
    }
    
    public void setConfigId(Long configId) {
        this.configId = configId;
    }
    
    public Long getProductSkuId() {
        return productSkuId;
    }
    
    public void setProductSkuId(Long productSkuId) {
        this.productSkuId = productSkuId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Long getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
    
    public BigDecimal getCashAmount() {
        return cashAmount;
    }
    
    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }
    
    public Integer getPointsAmount() {
        return pointsAmount;
    }
    
    public void setPointsAmount(Integer pointsAmount) {
        this.pointsAmount = pointsAmount;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}