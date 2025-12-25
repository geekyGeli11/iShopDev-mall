package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DIY订单创建参数
 * Created by macro on 2024/12/20.
 */
public class DiyOrderParam {
    
    @Schema(title = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @Schema(title = "DIY设计ID", required = true)
    @NotNull(message = "DIY设计ID不能为空")
    private Long designId;
    
    @Schema(title = "购买数量", required = true)
    @NotNull(message = "购买数量不能为空")
    private Integer quantity;
    
    @Schema(title = "收货地址ID", required = true)
    @NotNull(message = "收货地址ID不能为空")
    private Long memberReceiveAddressId;
    
    @Schema(title = "优惠券ID")
    private Long couponId;
    
    @Schema(title = "使用积分数")
    private Integer useIntegration;
    
    @Schema(title = "支付方式：0->未支付；1->支付宝；2->微信")
    private Integer payType;
    
    @Schema(title = "订单来源：0->PC订单；1->app订单；2->小程序订单")
    private Integer sourceType;
    
    @Schema(title = "订单备注")
    private String note;

    @Schema(title = "配送方式：1->标准快递；2->顺丰快递；3->同城配送")
    private Integer deliveryType;

    @Schema(title = "设计数据JSON")
    private String designData;

    @Schema(title = "定制面数据JSON")
    private String customizeFaces;

    @Schema(title = "总价格")
    private BigDecimal totalPrice;

    @Schema(title = "运费")
    private BigDecimal freightAmount;

    @Schema(title = "学校ID")
    private Long schoolId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getDesignId() {
        return designId;
    }

    public void setDesignId(Long designId) {
        this.designId = designId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getMemberReceiveAddressId() {
        return memberReceiveAddressId;
    }

    public void setMemberReceiveAddressId(Long memberReceiveAddressId) {
        this.memberReceiveAddressId = memberReceiveAddressId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Integer getUseIntegration() {
        return useIntegration;
    }

    public void setUseIntegration(Integer useIntegration) {
        this.useIntegration = useIntegration;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDesignData() {
        return designData;
    }

    public void setDesignData(String designData) {
        this.designData = designData;
    }

    public String getCustomizeFaces() {
        return customizeFaces;
    }

    public void setCustomizeFaces(String customizeFaces) {
        this.customizeFaces = customizeFaces;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
}
