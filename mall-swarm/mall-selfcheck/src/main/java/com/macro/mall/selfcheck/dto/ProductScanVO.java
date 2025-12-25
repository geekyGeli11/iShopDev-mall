package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品扫码查询结果
 * 
 * @author macro
 * @since 1.0.0
 */
@Schema(title = "商品扫码查询结果")
public class ProductScanVO {

    @Schema(title = "商品ID", description = "商品基础信息ID")
    private Long productId;

    @Schema(title = "SKU ID", description = "具体规格ID，如果是SKU扫码则返回")
    private Long skuId;

    @Schema(title = "商品名称", description = "商品名称")
    private String productName;

    @Schema(title = "商品图片", description = "商品主图")
    private String productPic;

    @Schema(title = "品牌名称", description = "品牌名称")
    private String brandName;

    @Schema(title = "分类名称", description = "商品分类名称")
    private String categoryName;

    @Schema(title = "货号", description = "商品货号")
    private String productSn;

    @Schema(title = "SKU编码", description = "SKU编码，如果有规格")
    private String skuCode;

    @Schema(title = "SKU规格信息", description = "SKU规格信息，解析自spData")
    private String skuSpecInfo;

    @Schema(title = "SKU规格数据", description = "SKU规格原始JSON数据，用于解析", hidden = true)
    private String spData;

    @Schema(title = "单位", description = "计量单位")
    private String unit;

    @Schema(title = "重量", description = "商品重量(克)")
    private BigDecimal weight;

    @Schema(title = "原价", description = "商品原价")
    private BigDecimal originalPrice;

    @Schema(title = "现价", description = "当前销售价格")
    private BigDecimal currentPrice;

    @Schema(title = "促销价", description = "促销价格，如果有促销")
    private BigDecimal promotionPrice;

    @Schema(title = "促销类型", description = "促销类型：0->无促销；1->促销价；2->会员价；3->阶梯价；4->满减价；5->限时购")
    private Integer promotionType;

    @Schema(title = "促销开始时间", description = "促销开始时间")
    private Date promotionStartTime;

    @Schema(title = "促销结束时间", description = "促销结束时间")
    private Date promotionEndTime;

    @Schema(title = "库存数量", description = "当前库存数量")
    private Integer stock;

    @Schema(title = "库存状态", description = "库存状态：SUFFICIENT-充足，LOW-库存不足，OUT_OF_STOCK-缺货")
    private String stockStatus;

    @Schema(title = "库存预警值", description = "库存预警阈值")
    private Integer lowStock;

    @Schema(title = "销量", description = "商品销量")
    private Integer sale;

    @Schema(title = "商品状态", description = "商品状态：NORMAL-正常，OFF_SHELF-下架，DELETED-已删除")
    private String productStatus;

    @Schema(title = "上架状态", description = "上架状态：0->下架；1->上架")
    private Integer publishStatus;

    @Schema(title = "是否可售", description = "当前是否可以销售")
    private Boolean saleable;

    @Schema(title = "积分赠送", description = "购买可获得的积分")
    private Integer giftPoint;

    @Schema(title = "成长值赠送", description = "购买可获得的成长值")
    private Integer giftGrowth;

    @Schema(title = "副标题", description = "商品副标题")
    private String subTitle;

    @Schema(title = "扫码类型", description = "扫码识别类型")
    private String scanType;

    @Schema(title = "扫码时间", description = "扫码时间戳")
    private Long scanTime;

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

    public String getSkuSpecInfo() {
        return skuSpecInfo;
    }

    public void setSkuSpecInfo(String skuSpecInfo) {
        this.skuSpecInfo = skuSpecInfo;
    }

    public String getSpData() {
        return spData;
    }

    public void setSpData(String spData) {
        this.spData = spData;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
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

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public Date getPromotionStartTime() {
        return promotionStartTime;
    }

    public void setPromotionStartTime(Date promotionStartTime) {
        this.promotionStartTime = promotionStartTime;
    }

    public Date getPromotionEndTime() {
        return promotionEndTime;
    }

    public void setPromotionEndTime(Date promotionEndTime) {
        this.promotionEndTime = promotionEndTime;
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

    public Integer getLowStock() {
        return lowStock;
    }

    public void setLowStock(Integer lowStock) {
        this.lowStock = lowStock;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Boolean getSaleable() {
        return saleable;
    }

    public void setSaleable(Boolean saleable) {
        this.saleable = saleable;
    }

    public Integer getGiftPoint() {
        return giftPoint;
    }

    public void setGiftPoint(Integer giftPoint) {
        this.giftPoint = giftPoint;
    }

    public Integer getGiftGrowth() {
        return giftGrowth;
    }

    public void setGiftGrowth(Integer giftGrowth) {
        this.giftGrowth = giftGrowth;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public Long getScanTime() {
        return scanTime;
    }

    public void setScanTime(Long scanTime) {
        this.scanTime = scanTime;
    }

    @Override
    public String toString() {
        return "ProductScanVO{" +
                "productId=" + productId +
                ", skuId=" + skuId +
                ", productName='" + productName + '\'' +
                ", productPic='" + productPic + '\'' +
                ", brandName='" + brandName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", productSn='" + productSn + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", skuSpecInfo='" + skuSpecInfo + '\'' +
                ", unit='" + unit + '\'' +
                ", weight=" + weight +
                ", originalPrice=" + originalPrice +
                ", currentPrice=" + currentPrice +
                ", promotionPrice=" + promotionPrice +
                ", promotionType=" + promotionType +
                ", promotionStartTime=" + promotionStartTime +
                ", promotionEndTime=" + promotionEndTime +
                ", stock=" + stock +
                ", stockStatus='" + stockStatus + '\'' +
                ", lowStock=" + lowStock +
                ", sale=" + sale +
                ", productStatus='" + productStatus + '\'' +
                ", publishStatus=" + publishStatus +
                ", saleable=" + saleable +
                ", giftPoint=" + giftPoint +
                ", giftGrowth=" + giftGrowth +
                ", subTitle='" + subTitle + '\'' +
                ", scanType='" + scanType + '\'' +
                ", scanTime=" + scanTime +
                '}';
    }
} 