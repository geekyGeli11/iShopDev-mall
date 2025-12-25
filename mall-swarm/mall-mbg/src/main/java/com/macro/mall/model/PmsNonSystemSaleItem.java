package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsNonSystemSaleItem implements Serializable {
    @Schema(title = "销售明细ID")
    private Long id;

    @Schema(title = "销售单ID")
    private Long saleId;

    @Schema(title = "商品ID")
    private Long productId;

    @Schema(title = "商品名称")
    private String productName;

    @Schema(title = "SKU ID")
    private Long skuId;

    @Schema(title = "SKU编码")
    private String skuCode;

    @Schema(title = "门店ID")
    private Long storeId;

    @Schema(title = "门店名称")
    private String storeName;

    @Schema(title = "销售数量")
    private Integer quantity;

    @Schema(title = "从门店库存扣除的数量")
    private Integer storeStockDeducted;

    @Schema(title = "从总库存扣除的数量")
    private Integer totalStockDeducted;

    @Schema(title = "系统价格")
    private BigDecimal systemPrice;

    @Schema(title = "销售价格")
    private BigDecimal salePrice;

    @Schema(title = "行金额（销售价格*数量）")
    private BigDecimal lineAmount;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "库存扣减详情（JSON格式）")
    private String stockDeductionDetail;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getStoreStockDeducted() {
        return storeStockDeducted;
    }

    public void setStoreStockDeducted(Integer storeStockDeducted) {
        this.storeStockDeducted = storeStockDeducted;
    }

    public Integer getTotalStockDeducted() {
        return totalStockDeducted;
    }

    public void setTotalStockDeducted(Integer totalStockDeducted) {
        this.totalStockDeducted = totalStockDeducted;
    }

    public BigDecimal getSystemPrice() {
        return systemPrice;
    }

    public void setSystemPrice(BigDecimal systemPrice) {
        this.systemPrice = systemPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getLineAmount() {
        return lineAmount;
    }

    public void setLineAmount(BigDecimal lineAmount) {
        this.lineAmount = lineAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStockDeductionDetail() {
        return stockDeductionDetail;
    }

    public void setStockDeductionDetail(String stockDeductionDetail) {
        this.stockDeductionDetail = stockDeductionDetail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", saleId=").append(saleId);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", skuId=").append(skuId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", storeId=").append(storeId);
        sb.append(", storeName=").append(storeName);
        sb.append(", quantity=").append(quantity);
        sb.append(", storeStockDeducted=").append(storeStockDeducted);
        sb.append(", totalStockDeducted=").append(totalStockDeducted);
        sb.append(", systemPrice=").append(systemPrice);
        sb.append(", salePrice=").append(salePrice);
        sb.append(", lineAmount=").append(lineAmount);
        sb.append(", createTime=").append(createTime);
        sb.append(", stockDeductionDetail=").append(stockDeductionDetail);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}