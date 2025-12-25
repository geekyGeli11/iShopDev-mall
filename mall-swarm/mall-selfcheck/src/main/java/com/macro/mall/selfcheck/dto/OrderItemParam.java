package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "订单商品参数")
public class OrderItemParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "商品ID", description = "商品ID", example = "1")
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @Schema(title = "SKU ID", description = "商品规格ID", example = "1")
    private Long skuId;

    @Schema(title = "商品数量", description = "购买数量", example = "2")
    @NotNull(message = "商品数量不能为空")
    @Min(value = 1, message = "商品数量必须大于0")
    @Max(value = 999, message = "单个商品数量不能超过999")
    private Integer quantity;

    @Schema(title = "商品单价", description = "商品实际单价（应用优惠后）", example = "99.90")
    private BigDecimal unitPrice;

    @Schema(title = "商品总价", description = "商品总价（单价 × 数量）", example = "199.80")
    private BigDecimal totalPrice;

    @Schema(title = "促销类型", description = "促销类型：0->无促销；1->促销价；2->会员价；3->阶梯价；4->满减价；5->限时购")
    private Integer promotionType;

    @Schema(title = "促销金额", description = "促销优惠金额", example = "10.00")
    private BigDecimal promotionAmount;

    @Schema(title = "备注", description = "商品备注", example = "扫码添加")
    @Size(max = 100, message = "商品备注长度不能超过100字符")
    private String remark;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getPromotionType() {
        return promotionType;
    }

    public void setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
    }

    public BigDecimal getPromotionAmount() {
        return promotionAmount;
    }

    public void setPromotionAmount(BigDecimal promotionAmount) {
        this.promotionAmount = promotionAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "OrderItemParam{" +
                "productId=" + productId +
                ", skuId=" + skuId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", promotionType=" + promotionType +
                ", promotionAmount=" + promotionAmount +
                ", remark='" + remark + '\'' +
                '}';
    }
} 