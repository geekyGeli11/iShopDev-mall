package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员优惠券展示对象
 * 
 * @author macro
 * @since 1.0.0
 */
@Schema(title = "会员优惠券信息")
public class MemberCouponVO {

    @Schema(title = "优惠券历史ID", description = "优惠券领取记录ID")
    private Long historyId;

    @Schema(title = "优惠券ID", description = "优惠券基础信息ID")
    private Long couponId;

    @Schema(title = "优惠券名称", description = "优惠券名称")
    private String name;

    @Schema(title = "优惠券类型", description = "优惠券类型：0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券")
    private Integer type;

    @Schema(title = "优惠券码", description = "优惠券唯一编码")
    private String couponCode;

    @Schema(title = "优惠金额", description = "优惠券面额")
    private BigDecimal amount;

    @Schema(title = "优惠券类型", description = "优惠券类型：0->满减券；1->打折券")
    private Integer couponType;

    @Schema(title = "打折率", description = "打折率（0.1-0.99），仅打折券使用")
    private BigDecimal discountRate;

    @Schema(title = "使用门槛", description = "使用门槛金额，0表示无门槛")
    private BigDecimal minPoint;

    @Schema(title = "使用类型", description = "使用类型：0->全场通用；1->指定分类；2->指定商品")
    private Integer useType;

    @Schema(title = "开始时间", description = "优惠券使用开始时间")
    private Date startTime;

    @Schema(title = "结束时间", description = "优惠券使用结束时间")
    private Date endTime;

    @Schema(title = "使用状态", description = "使用状态：0->未使用；1->已使用；2->已过期")
    private Integer useStatus;

    @Schema(title = "获取时间", description = "会员获取优惠券的时间")
    private Date createTime;

    @Schema(title = "使用时间", description = "优惠券使用时间")
    private Date useTime;

    @Schema(title = "备注", description = "优惠券使用说明")
    private String note;

    @Schema(title = "剩余天数", description = "距离过期的剩余天数，-1表示已过期")
    private Integer remainingDays;

    @Schema(title = "是否即将过期", description = "是否在3天内过期")
    private Boolean nearExpiry;

    @Schema(title = "是否可用", description = "当前是否可以使用")
    private Boolean available;

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(BigDecimal minPoint) {
        this.minPoint = minPoint;
    }

    public Integer getUseType() {
        return useType;
    }

    public void setUseType(Integer useType) {
        this.useType = useType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(Integer useStatus) {
        this.useStatus = useStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUseTime() {
        return useTime;
    }

    public void setUseTime(Date useTime) {
        this.useTime = useTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }

    public Boolean getNearExpiry() {
        return nearExpiry;
    }

    public void setNearExpiry(Boolean nearExpiry) {
        this.nearExpiry = nearExpiry;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "MemberCouponVO{" +
                "historyId=" + historyId +
                ", couponId=" + couponId +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", couponCode='" + couponCode + '\'' +
                ", amount=" + amount +
                ", couponType=" + couponType +
                ", discountRate=" + discountRate +
                ", minPoint=" + minPoint +
                ", useType=" + useType +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", useStatus=" + useStatus +
                ", createTime=" + createTime +
                ", useTime=" + useTime +
                ", note='" + note + '\'' +
                ", remainingDays=" + remainingDays +
                ", nearExpiry=" + nearExpiry +
                ", available=" + available +
                '}';
    }
} 