package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsProductDamageItem implements Serializable {
    @Schema(title = "涓婚敭ID")
    private Long id;

    @Schema(title = "鎶ユ崯鍗旾D")
    private Long damageReportId;

    @Schema(title = "鍟嗗搧ID")
    private Long productId;

    @Schema(title = "鍟嗗搧鍚嶇О")
    private String productName;

    @Schema(title = "鍟嗗搧鍥剧墖")
    private String productPic;

    @Schema(title = "SKU ID")
    private Long skuId;

    @Schema(title = "SKU缂栫爜")
    private String skuCode;

    @Schema(title = "SKU瑙勬牸锛圝SON鏍煎紡锛")
    private String skuSpec;

    @Schema(title = "鎶ユ崯鏁伴噺")
    private Integer damageQuantity;

    @Schema(title = "鎴愭湰浠")
    private BigDecimal costPrice;

    @Schema(title = "閿?敭浠")
    private BigDecimal salePrice;

    @Schema(title = "鎶ユ崯閲戦?锛堟暟閲?鎴愭湰浠凤級")
    private BigDecimal damageAmount;

    @Schema(title = "閿?敭閲戦?锛堟暟閲?閿?敭浠凤級")
    private BigDecimal salesAmount;

    @Schema(title = "鏄庣粏澶囨敞")
    private String itemRemark;

    @Schema(title = "鍒涘缓鏃堕棿")
    private Date createTime;

    @Schema(title = "鏇存柊鏃堕棿")
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDamageReportId() {
        return damageReportId;
    }

    public void setDamageReportId(Long damageReportId) {
        this.damageReportId = damageReportId;
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

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
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

    public String getSkuSpec() {
        return skuSpec;
    }

    public void setSkuSpec(String skuSpec) {
        this.skuSpec = skuSpec;
    }

    public Integer getDamageQuantity() {
        return damageQuantity;
    }

    public void setDamageQuantity(Integer damageQuantity) {
        this.damageQuantity = damageQuantity;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getDamageAmount() {
        return damageAmount;
    }

    public void setDamageAmount(BigDecimal damageAmount) {
        this.damageAmount = damageAmount;
    }

    public BigDecimal getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(BigDecimal salesAmount) {
        this.salesAmount = salesAmount;
    }

    public String getItemRemark() {
        return itemRemark;
    }

    public void setItemRemark(String itemRemark) {
        this.itemRemark = itemRemark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", damageReportId=").append(damageReportId);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", productPic=").append(productPic);
        sb.append(", skuId=").append(skuId);
        sb.append(", skuCode=").append(skuCode);
        sb.append(", skuSpec=").append(skuSpec);
        sb.append(", damageQuantity=").append(damageQuantity);
        sb.append(", costPrice=").append(costPrice);
        sb.append(", salePrice=").append(salePrice);
        sb.append(", damageAmount=").append(damageAmount);
        sb.append(", salesAmount=").append(salesAmount);
        sb.append(", itemRemark=").append(itemRemark);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}