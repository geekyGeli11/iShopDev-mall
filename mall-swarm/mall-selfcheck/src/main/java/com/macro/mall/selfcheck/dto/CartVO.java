package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 购物车信息展示VO
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "购物车信息")
public class CartVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "用户类型", description = "用户类型：MEMBER-会员，GUEST-游客")
    private String userType;

    @Schema(title = "用户ID", description = "会员ID或游客ID")
    private String userId;

    @Schema(title = "购物车商品列表", description = "购物车中的商品项列表")
    private List<CartItemVO> items;

    @Schema(title = "商品总数量", description = "购物车中商品的总数量")
    private Integer totalQuantity;

    @Schema(title = "有效商品数量", description = "可购买商品的数量")
    private Integer availableQuantity;

    @Schema(title = "无效商品数量", description = "不可购买商品的数量")
    private Integer unavailableQuantity;

    @Schema(title = "商品总金额", description = "所有商品的原始总金额")
    private BigDecimal totalAmount;

    @Schema(title = "有效商品金额", description = "可购买商品的总金额")
    private BigDecimal availableAmount;

    @Schema(title = "优惠金额", description = "优惠券等优惠的总金额")
    private BigDecimal discountAmount;

    @Schema(title = "运费", description = "配送费用")
    private BigDecimal shippingFee;

    @Schema(title = "最终金额", description = "应付总金额（有效商品金额 - 优惠金额 + 运费）")
    private BigDecimal finalAmount;

    @Schema(title = "可用积分", description = "可使用的积分数量")
    private Integer availablePoints;

    @Schema(title = "使用积分", description = "本次购买使用的积分")
    private Integer usedPoints;

    @Schema(title = "积分抵扣金额", description = "积分抵扣的金额")
    private BigDecimal pointsAmount;

    @Schema(title = "已应用优惠券", description = "已应用的优惠券列表")
    private List<MemberCouponVO> appliedCoupons;

    @Schema(title = "可用优惠券数量", description = "当前订单可使用的优惠券数量")
    private Integer availableCouponCount;

    @Schema(title = "是否需要配送", description = "是否包含需要配送的商品")
    private Boolean needShipping;

    @Schema(title = "预计配送费", description = "预计配送费用")
    private BigDecimal estimatedShippingFee;

    @Schema(title = "购物车状态", description = "购物车状态：NORMAL-正常，EXPIRED-已过期，LOCKED-已锁定")
    private String cartStatus;

    @Schema(title = "创建时间", description = "购物车创建时间")
    private Date createTime;

    @Schema(title = "更新时间", description = "最后更新时间")
    private Date updateTime;

    @Schema(title = "过期时间", description = "购物车过期时间（游客模式）")
    private Date expireTime;

    @Schema(title = "会员等级", description = "会员等级，影响价格计算")
    private Integer memberLevel;

    @Schema(title = "会员折扣", description = "会员等级折扣")
    private BigDecimal memberDiscount;

    @Schema(title = "提示信息", description = "购物车相关提示信息")
    private List<String> messages;

    @Schema(title = "错误信息", description = "购物车错误信息")
    private List<String> errors;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemVO> getItems() {
        return items;
    }

    public void setItems(List<CartItemVO> items) {
        this.items = items;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getUnavailableQuantity() {
        return unavailableQuantity;
    }

    public void setUnavailableQuantity(Integer unavailableQuantity) {
        this.unavailableQuantity = unavailableQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Integer getAvailablePoints() {
        return availablePoints;
    }

    public void setAvailablePoints(Integer availablePoints) {
        this.availablePoints = availablePoints;
    }

    public Integer getUsedPoints() {
        return usedPoints;
    }

    public void setUsedPoints(Integer usedPoints) {
        this.usedPoints = usedPoints;
    }

    public BigDecimal getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(BigDecimal pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    public List<MemberCouponVO> getAppliedCoupons() {
        return appliedCoupons;
    }

    public void setAppliedCoupons(List<MemberCouponVO> appliedCoupons) {
        this.appliedCoupons = appliedCoupons;
    }

    public Integer getAvailableCouponCount() {
        return availableCouponCount;
    }

    public void setAvailableCouponCount(Integer availableCouponCount) {
        this.availableCouponCount = availableCouponCount;
    }

    public Boolean getNeedShipping() {
        return needShipping;
    }

    public void setNeedShipping(Boolean needShipping) {
        this.needShipping = needShipping;
    }

    public BigDecimal getEstimatedShippingFee() {
        return estimatedShippingFee;
    }

    public void setEstimatedShippingFee(BigDecimal estimatedShippingFee) {
        this.estimatedShippingFee = estimatedShippingFee;
    }

    public String getCartStatus() {
        return cartStatus;
    }

    public void setCartStatus(String cartStatus) {
        this.cartStatus = cartStatus;
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

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(Integer memberLevel) {
        this.memberLevel = memberLevel;
    }

    public BigDecimal getMemberDiscount() {
        return memberDiscount;
    }

    public void setMemberDiscount(BigDecimal memberDiscount) {
        this.memberDiscount = memberDiscount;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "CartVO{" +
                "userType='" + userType + '\'' +
                ", userId='" + userId + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", availableQuantity=" + availableQuantity +
                ", totalAmount=" + totalAmount +
                ", finalAmount=" + finalAmount +
                ", cartStatus='" + cartStatus + '\'' +
                '}';
    }
} 