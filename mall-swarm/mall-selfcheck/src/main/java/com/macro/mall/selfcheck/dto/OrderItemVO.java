package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品明细视图对象
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "订单商品明细")
public class OrderItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "订单商品ID", description = "订单商品明细ID")
    private Long id;

    @Schema(title = "订单ID", description = "所属订单ID")
    private Long orderId;

    @Schema(title = "订单编号", description = "订单编号")
    private String orderSn;

    @Schema(title = "商品ID", description = "商品ID")
    private Long productId;

    @Schema(title = "商品图片", description = "商品图片")
    private String productPic;

    @Schema(title = "商品名称", description = "商品名称")
    private String productName;

    @Schema(title = "商品品牌", description = "商品品牌")
    private String productBrand;

    @Schema(title = "商品分类", description = "商品分类名称")
    private String productCategoryName;

    @Schema(title = "商品货号", description = "商品货号")
    private String productSn;

    @Schema(title = "商品单价", description = "商品销售价格")
    private BigDecimal productPrice;

    @Schema(title = "商品购买数量", description = "商品购买数量")
    private Integer productQuantity;

    @Schema(title = "SKU ID", description = "商品sku编号")
    private Long productSkuId;

    @Schema(title = "SKU编码", description = "商品sku编码")
    private String productSkuCode;

    @Schema(title = "商品规格", description = "商品销售属性")
    private String productAttr;

    @Schema(title = "商品促销名称", description = "商品促销名称")
    private String promotionName;

    @Schema(title = "商品促销分解金额", description = "商品促销分解金额")
    private BigDecimal promotionAmount;

    @Schema(title = "优惠券优惠分解金额", description = "优惠券优惠分解金额")
    private BigDecimal couponAmount;

    @Schema(title = "积分优惠分解金额", description = "积分优惠分解金额")
    private BigDecimal integrationAmount;

    @Schema(title = "该商品经过优惠后的分解金额", description = "该商品经过优惠后的分解金额")
    private BigDecimal realAmount;

    @Schema(title = "商品赠送积分", description = "商品赠送积分")
    private Integer giftIntegration;

    @Schema(title = "商品赠送成长值", description = "商品赠送成长值")
    private Integer giftGrowth;

    @Schema(title = "商品销售属性", description = "商品销售属性:[{'key':'颜色','value':'颜色'},{'key':'容量','value':'4G'}]")
    private String productAttrJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getProductSn() {
        return productSn;
    }

    public void setProductSn(String productSn) {
        this.productSn = productSn;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Long getProductSkuId() {
        return productSkuId;
    }

    public void setProductSkuId(Long productSkuId) {
        this.productSkuId = productSkuId;
    }

    public String getProductSkuCode() {
        return productSkuCode;
    }

    public void setProductSkuCode(String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    public String getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public BigDecimal getPromotionAmount() {
        return promotionAmount;
    }

    public void setPromotionAmount(BigDecimal promotionAmount) {
        this.promotionAmount = promotionAmount;
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

    public BigDecimal getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(BigDecimal realAmount) {
        this.realAmount = realAmount;
    }

    public Integer getGiftIntegration() {
        return giftIntegration;
    }

    public void setGiftIntegration(Integer giftIntegration) {
        this.giftIntegration = giftIntegration;
    }

    public Integer getGiftGrowth() {
        return giftGrowth;
    }

    public void setGiftGrowth(Integer giftGrowth) {
        this.giftGrowth = giftGrowth;
    }

    public String getProductAttrJson() {
        return productAttrJson;
    }

    public void setProductAttrJson(String productAttrJson) {
        this.productAttrJson = productAttrJson;
    }

    @Override
    public String toString() {
        return "OrderItemVO{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", orderSn='" + orderSn + '\'' +
                ", productId=" + productId +
                ", productPic='" + productPic + '\'' +
                ", productName='" + productName + '\'' +
                ", productBrand='" + productBrand + '\'' +
                ", productCategoryName='" + productCategoryName + '\'' +
                ", productSn='" + productSn + '\'' +
                ", productPrice=" + productPrice +
                ", productQuantity=" + productQuantity +
                ", productSkuId=" + productSkuId +
                ", productSkuCode='" + productSkuCode + '\'' +
                ", productAttr='" + productAttr + '\'' +
                ", promotionName='" + promotionName + '\'' +
                ", promotionAmount=" + promotionAmount +
                ", couponAmount=" + couponAmount +
                ", integrationAmount=" + integrationAmount +
                ", realAmount=" + realAmount +
                ", giftIntegration=" + giftIntegration +
                ", giftGrowth=" + giftGrowth +
                ", productAttrJson='" + productAttrJson + '\'' +
                '}';
    }
} 