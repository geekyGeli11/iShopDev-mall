package com.macro.mall.dto;

import com.macro.mall.model.OmsOrderDiyInfo;
import com.macro.mall.model.PmsDiyTemplate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * 订单DIY信息VO，包含关联的模板信息和订单信息
 * Created by macro on 2024/12/20.
 */
public class OmsOrderDiyInfoVO extends OmsOrderDiyInfo {

    @Schema(title = "关联的DIY模板信息")
    private PmsDiyTemplate template;

    @Schema(title = "生产状态描述")
    private String productionStatusDesc;

    @Schema(title = "商品名称")
    private String productName;

    // 订单基本信息
    @Schema(title = "用户账号")
    private String memberUsername;

    @Schema(title = "收货人姓名")
    private String receiverName;

    @Schema(title = "收货人电话")
    private String receiverPhone;

    @Schema(title = "收货人邮编")
    private String receiverPostCode;

    @Schema(title = "省份/直辖市")
    private String receiverProvince;

    @Schema(title = "城市")
    private String receiverCity;

    @Schema(title = "区")
    private String receiverRegion;

    @Schema(title = "详细地址")
    private String receiverDetailAddress;

    // 订单金额信息
    @Schema(title = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(title = "应付金额（实际支付金额）")
    private BigDecimal payAmount;

    @Schema(title = "运费金额")
    private BigDecimal freightAmount;

    @Schema(title = "促销优化金额")
    private BigDecimal promotionAmount;

    @Schema(title = "积分抵扣金额")
    private BigDecimal integrationAmount;

    @Schema(title = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    @Schema(title = "管理员后台调整订单使用的折扣金额")
    private BigDecimal discountAmount;

    public PmsDiyTemplate getTemplate() {
        return template;
    }

    public void setTemplate(PmsDiyTemplate template) {
        this.template = template;
    }

    public String getProductionStatusDesc() {
        return productionStatusDesc;
    }

    public void setProductionStatusDesc(String productionStatusDesc) {
        this.productionStatusDesc = productionStatusDesc;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverPostCode() {
        return receiverPostCode;
    }

    public void setReceiverPostCode(String receiverPostCode) {
        this.receiverPostCode = receiverPostCode;
    }

    public String getReceiverProvince() {
        return receiverProvince;
    }

    public void setReceiverProvince(String receiverProvince) {
        this.receiverProvince = receiverProvince;
    }

    public String getReceiverCity() {
        return receiverCity;
    }

    public void setReceiverCity(String receiverCity) {
        this.receiverCity = receiverCity;
    }

    public String getReceiverRegion() {
        return receiverRegion;
    }

    public void setReceiverRegion(String receiverRegion) {
        this.receiverRegion = receiverRegion;
    }

    public String getReceiverDetailAddress() {
        return receiverDetailAddress;
    }

    public void setReceiverDetailAddress(String receiverDetailAddress) {
        this.receiverDetailAddress = receiverDetailAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getPromotionAmount() {
        return promotionAmount;
    }

    public void setPromotionAmount(BigDecimal promotionAmount) {
        this.promotionAmount = promotionAmount;
    }

    public BigDecimal getIntegrationAmount() {
        return integrationAmount;
    }

    public void setIntegrationAmount(BigDecimal integrationAmount) {
        this.integrationAmount = integrationAmount;
    }

    public BigDecimal getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    /**
     * 根据生产状态获取描述
     */
    public static String getProductionStatusDesc(Byte status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待生产";
            case 1:
                return "生产中";
            case 2:
                return "已完成";
            default:
                return "未知";
        }
    }
}
