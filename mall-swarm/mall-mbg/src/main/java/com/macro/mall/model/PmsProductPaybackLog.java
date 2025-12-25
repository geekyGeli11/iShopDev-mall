package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsProductPaybackLog implements Serializable {
    private Long id;

    @Schema(title = "商品ID")
    private Long productId;

    @Schema(title = "批次ID")
    private Long batchId;

    @Schema(title = "订单ID")
    private Long orderId;

    @Schema(title = "订单编号")
    private String orderSn;

    @Schema(title = "销售数量")
    private Integer soldQuantity;

    @Schema(title = "销售金额")
    private BigDecimal soldAmount;

    @Schema(title = "销售日期")
    private Date orderDate;

    @Schema(title = "累计销量")
    private Integer cumulativeQuantity;

    @Schema(title = "累计销售金额")
    private BigDecimal cumulativeAmount;

    @Schema(title = "更新前回本进度")
    private BigDecimal paybackProgressBefore;

    @Schema(title = "更新后回本进度")
    private BigDecimal paybackProgressAfter;

    private Date createdAt;

    @Schema(title = "销售渠道：selfcheck-自助售卖机，miniprogram-小程序，non_system-非系统销售")
    private String salesChannel;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public BigDecimal getSoldAmount() {
        return soldAmount;
    }

    public void setSoldAmount(BigDecimal soldAmount) {
        this.soldAmount = soldAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getCumulativeQuantity() {
        return cumulativeQuantity;
    }

    public void setCumulativeQuantity(Integer cumulativeQuantity) {
        this.cumulativeQuantity = cumulativeQuantity;
    }

    public BigDecimal getCumulativeAmount() {
        return cumulativeAmount;
    }

    public void setCumulativeAmount(BigDecimal cumulativeAmount) {
        this.cumulativeAmount = cumulativeAmount;
    }

    public BigDecimal getPaybackProgressBefore() {
        return paybackProgressBefore;
    }

    public void setPaybackProgressBefore(BigDecimal paybackProgressBefore) {
        this.paybackProgressBefore = paybackProgressBefore;
    }

    public BigDecimal getPaybackProgressAfter() {
        return paybackProgressAfter;
    }

    public void setPaybackProgressAfter(BigDecimal paybackProgressAfter) {
        this.paybackProgressAfter = paybackProgressAfter;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productId=").append(productId);
        sb.append(", batchId=").append(batchId);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", soldQuantity=").append(soldQuantity);
        sb.append(", soldAmount=").append(soldAmount);
        sb.append(", orderDate=").append(orderDate);
        sb.append(", cumulativeQuantity=").append(cumulativeQuantity);
        sb.append(", cumulativeAmount=").append(cumulativeAmount);
        sb.append(", paybackProgressBefore=").append(paybackProgressBefore);
        sb.append(", paybackProgressAfter=").append(paybackProgressAfter);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", salesChannel=").append(salesChannel);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}