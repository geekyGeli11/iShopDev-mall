package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderDiyInfo;
import com.macro.mall.model.UmsDiyDesign;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DIY订单结果
 * Created by macro on 2024/12/20.
 */
public class DiyOrderResult {
    
    @Schema(title = "订单信息")
    private OmsOrder order;
    
    @Schema(title = "DIY设计信息")
    private UmsDiyDesign diyDesign;
    
    @Schema(title = "订单DIY信息")
    private OmsOrderDiyInfo orderDiyInfo;
    
    @Schema(title = "订单状态：0-创建成功，1-创建失败")
    private Integer status;
    
    @Schema(title = "错误信息")
    private String errorMessage;

    @Schema(title = "实际支付金额")
    private BigDecimal payAmount;

    @Schema(title = "DIY价格")
    private BigDecimal diyPrice;

    @Schema(title = "原始价格")
    private BigDecimal originalPrice;

    @Schema(title = "运费金额")
    private BigDecimal freightAmount;

    @Schema(title = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    @Schema(title = "积分抵扣金额")
    private BigDecimal integrationAmount;

    public OmsOrder getOrder() {
        return order;
    }

    public void setOrder(OmsOrder order) {
        this.order = order;
    }

    public UmsDiyDesign getDiyDesign() {
        return diyDesign;
    }

    public void setDiyDesign(UmsDiyDesign diyDesign) {
        this.diyDesign = diyDesign;
    }

    public OmsOrderDiyInfo getOrderDiyInfo() {
        return orderDiyInfo;
    }

    public void setOrderDiyInfo(OmsOrderDiyInfo orderDiyInfo) {
        this.orderDiyInfo = orderDiyInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getDiyPrice() {
        return diyPrice;
    }

    public void setDiyPrice(BigDecimal diyPrice) {
        this.diyPrice = diyPrice;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getIntegrationAmount() {
        return integrationAmount;
    }

    public void setIntegrationAmount(BigDecimal integrationAmount) {
        this.integrationAmount = integrationAmount;
    }
}
