package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsStockOperationLog implements Serializable {
    private Long id;

    @Schema(title = "操作单号")
    private String operationNo;

    @Schema(title = "操作类型：1-入库，2-出库，3-调整")
    private Byte operationType;

    @Schema(title = "入库 (operationType: 1) | value | label | |-------|-------| | 1 | 采购 | | 2 | 退换货 | | 3 | 盘点 | | 4 | 调拨 | | 5 | 外借 | | 6 | 赠送 | | 7 | 其它 |")
    private Byte operationSubtype;

    @Schema(title = "商品ID")
    private Long productId;

    @Schema(title = "商品名称")
    private String productName;

    @Schema(title = "商品货号")
    private String productSn;

    @Schema(title = "SKU ID")
    private Long skuId;

    @Schema(title = "SKU编码")
    private String skuCode;

    @Schema(title = "门店ID（为空表示总库存操作）")
    private Long storeId;

    @Schema(title = "关联订单ID")
    private Long orderId;

    @Schema(title = "关联订单编号")
    private String orderSn;

    @Schema(title = "操作前库存")
    private Integer beforeStock;

    @Schema(title = "操作数量（正数增加，负数减少）")
    private Integer operationQuantity;

    @Schema(title = "操作后库存")
    private Integer afterStock;

    @Schema(title = "操作理由")
    private String operationReason;

    @Schema(title = "操作人ID")
    private Long operatorId;

    @Schema(title = "操作人姓名")
    private String operatorName;

    @Schema(title = "操作IP")
    private String operatorIp;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperationNo() {
        return operationNo;
    }

    public void setOperationNo(String operationNo) {
        this.operationNo = operationNo;
    }

    public Byte getOperationType() {
        return operationType;
    }

    public void setOperationType(Byte operationType) {
        this.operationType = operationType;
    }

    public Byte getOperationSubtype() {
        return operationSubtype;
    }

    public void setOperationSubtype(Byte operationSubtype) {
        this.operationSubtype = operationSubtype;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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

    public Integer getBeforeStock() {
        return beforeStock;
    }

    public void setBeforeStock(Integer beforeStock) {
        this.beforeStock = beforeStock;
    }

    public Integer getOperationQuantity() {
        return operationQuantity;
    }

    public void setOperationQuantity(Integer operationQuantity) {
        this.operationQuantity = operationQuantity;
    }

    public Integer getAfterStock() {
        return afterStock;
    }

    public void setAfterStock(Integer afterStock) {
        this.afterStock = afterStock;
    }

    public String getOperationReason() {
        return operationReason;
    }

    public void setOperationReason(String operationReason) {
        this.operationReason = operationReason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", operationNo=").append(operationNo);
        sb.append(", operationType=").append(operationType);
        sb.append(", operationSubtype=").append(operationSubtype);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", productSn=").append(productSn);
        sb.append(", skuId=").append(skuId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", storeId=").append(storeId);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderSn=").append(orderSn);
        sb.append(", beforeStock=").append(beforeStock);
        sb.append(", operationQuantity=").append(operationQuantity);
        sb.append(", afterStock=").append(afterStock);
        sb.append(", operationReason=").append(operationReason);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", operatorIp=").append(operatorIp);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}