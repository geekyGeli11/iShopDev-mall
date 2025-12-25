package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class PmsStockOperationApprovalItem implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "审核记录ID")
    private Long approvalId;

    @Schema(title = "操作单号")
    private String operationNo;

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

    @Schema(title = "操作数量")
    private Integer operationQuantity;

    @Schema(title = "实际收货数量")
    private Integer actualQuantity;

    @Schema(title = "数量差异（实际-申请，正数表示多收，负数表示少收）")
    private Integer quantityDiff;

    @Schema(title = "差异原因说明")
    private String diffReason;

    @Schema(title = "操作前库存")
    private Integer beforeStock;

    @Schema(title = "操作后库存（预期）")
    private Integer afterStock;

    @Schema(title = "实际操作后库存")
    private Integer actualAfterStock;

    @Schema(title = "门店ID（为空表示总库存操作）")
    private Long storeId;

    @Schema(title = "创建时间")
    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    public String getOperationNo() {
        return operationNo;
    }

    public void setOperationNo(String operationNo) {
        this.operationNo = operationNo;
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

    public Integer getOperationQuantity() {
        return operationQuantity;
    }

    public void setOperationQuantity(Integer operationQuantity) {
        this.operationQuantity = operationQuantity;
    }

    public Integer getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public Integer getQuantityDiff() {
        return quantityDiff;
    }

    public void setQuantityDiff(Integer quantityDiff) {
        this.quantityDiff = quantityDiff;
    }

    public String getDiffReason() {
        return diffReason;
    }

    public void setDiffReason(String diffReason) {
        this.diffReason = diffReason;
    }

    public Integer getBeforeStock() {
        return beforeStock;
    }

    public void setBeforeStock(Integer beforeStock) {
        this.beforeStock = beforeStock;
    }

    public Integer getAfterStock() {
        return afterStock;
    }

    public void setAfterStock(Integer afterStock) {
        this.afterStock = afterStock;
    }

    public Integer getActualAfterStock() {
        return actualAfterStock;
    }

    public void setActualAfterStock(Integer actualAfterStock) {
        this.actualAfterStock = actualAfterStock;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
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
        sb.append(", approvalId=").append(approvalId);
        sb.append(", operationNo=").append(operationNo);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", productSn=").append(productSn);
        sb.append(", skuId=").append(skuId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", operationQuantity=").append(operationQuantity);
        sb.append(", actualQuantity=").append(actualQuantity);
        sb.append(", quantityDiff=").append(quantityDiff);
        sb.append(", diffReason=").append(diffReason);
        sb.append(", beforeStock=").append(beforeStock);
        sb.append(", afterStock=").append(afterStock);
        sb.append(", actualAfterStock=").append(actualAfterStock);
        sb.append(", storeId=").append(storeId);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}