package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车商品项展示VO
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "购物车商品项信息")
public class CartItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "购物车项ID", description = "购物车项唯一标识")
    private Long itemId;

    @Schema(title = "商品ID", description = "商品基础信息ID")
    private Long productId;

    @Schema(title = "SKU ID", description = "商品规格ID")
    private Long skuId;

    @Schema(title = "商品名称", description = "商品名称")
    private String productName;

    @Schema(title = "商品图片", description = "商品主图URL")
    private String productPic;

    @Schema(title = "品牌名称", description = "品牌名称")
    private String brandName;

    @Schema(title = "分类名称", description = "商品分类名称")
    private String categoryName;

    @Schema(title = "商品货号", description = "商品货号")
    private String productSn;

    @Schema(title = "SKU编码", description = "SKU编码，如果有规格")
    private String skuCode;

    @Schema(title = "规格信息", description = "商品规格描述")
    private String skuSpec;

    @Schema(title = "单位", description = "计量单位")
    private String unit;

    @Schema(title = "原价", description = "商品原价")
    private BigDecimal originalPrice;

    @Schema(title = "现价", description = "当前销售价格")
    private BigDecimal currentPrice;

    @Schema(title = "促销价", description = "促销价格，如果有促销")
    private BigDecimal promotionPrice;

    @Schema(title = "会员价", description = "会员专享价格")
    private BigDecimal memberPrice;

    @Schema(title = "实际单价", description = "实际购买单价（应用优惠后）")
    private BigDecimal actualPrice;

    @Schema(title = "数量", description = "购买数量")
    private Integer quantity;

    @Schema(title = "小计", description = "商品小计金额（实际单价 × 数量）")
    private BigDecimal subtotal;

    @Schema(title = "库存数量", description = "当前库存数量")
    private Integer stock;

    @Schema(title = "库存状态", description = "库存状态：SUFFICIENT-充足，LOW-库存不足，OUT_OF_STOCK-缺货")
    private String stockStatus;

    @Schema(title = "是否有效", description = "购物车项是否有效（库存足够、商品未下架等）")
    private Boolean available;

    @Schema(title = "是否可售", description = "商品是否可以销售")
    private Boolean saleable;

    @Schema(title = "商品状态", description = "商品状态：NORMAL-正常，OFF_SHELF-下架，DELETED-已删除")
    private String productStatus;

    @Schema(title = "促销类型", description = "促销类型：0->无促销；1->促销价；2->会员价；3->阶梯价；4->满减价；5->限时购")
    private Integer promotionType;

    @Schema(title = "限购数量", description = "单次购买限制数量，0表示无限制")
    private Integer limitQuantity;

    @Schema(title = "加入时间", description = "加入购物车时间")
    private Date createTime;

    @Schema(title = "更新时间", description = "最后更新时间")
    private Date updateTime;

    @Schema(title = "备注", description = "购物车项备注")
    private String remark;

    @Schema(title = "错误信息", description = "如果商品不可用，显示错误原因")
    private String errorMessage;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(BigDecimal promotionPrice) {
        this.promotionPrice = promotionPrice;
    }

    public BigDecimal getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(BigDecimal memberPrice) {
        this.memberPrice = memberPrice;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getSaleable() {
        return saleable;
    }

    public void setSaleable(Boolean saleable) {
        this.saleable = saleable;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public Integer getLimitQuantity() {
        return limitQuantity;
    }

    public void setLimitQuantity(Integer limitQuantity) {
        this.limitQuantity = limitQuantity;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "CartItemVO{" +
                "itemId=" + itemId +
                ", productId=" + productId +
                ", skuId=" + skuId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", actualPrice=" + actualPrice +
                ", subtotal=" + subtotal +
                ", available=" + available +
                '}';
    }
} 