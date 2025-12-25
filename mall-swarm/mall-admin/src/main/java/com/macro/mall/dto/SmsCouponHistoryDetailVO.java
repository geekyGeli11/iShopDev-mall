package com.macro.mall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户优惠券使用历史详情VO
 * 包含优惠券历史记录和优惠券详细信息
 * 
 * @author macro
 * @since 1.0.0
 */
@Schema(description = "用户优惠券使用历史详情")
public class SmsCouponHistoryDetailVO {

    @Schema(description = "优惠券历史记录ID")
    private Long id;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "优惠券码")
    private String couponCode;

    @Schema(description = "会员昵称")
    private String memberNickname;

    @Schema(description = "获取类型：0->后台赠送；1->主动获取")
    private Integer getType;

    @Schema(description = "使用状态：0->未使用；1->已使用；2->已过期")
    private Integer useStatus;

    @Schema(description = "获取时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    @Schema(description = "使用订单号")
    private String orderSn;

    // 优惠券详细信息
    @Schema(description = "优惠券名称")
    private String couponName;

    @Schema(description = "优惠券类型：0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券")
    private Integer couponType;

    @Schema(description = "优惠金额")
    private BigDecimal amount;

    @Schema(description = "使用门槛；0表示无门槛")
    private BigDecimal minPoint;

    @Schema(description = "使用类型：0->全场通用；1->指定分类；2->指定商品")
    private Integer useType;

    @Schema(description = "优惠券开始使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @Schema(description = "优惠券结束使用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @Schema(description = "备注")
    private String note;

    @Schema(description = "是否即将过期（3天内过期）")
    private Boolean nearExpiry;

    @Schema(description = "剩余有效天数(-1表示已过期)")
    private Integer remainingDays;

    // 构造函数
    public SmsCouponHistoryDetailVO() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getMemberNickname() {
        return memberNickname;
    }

    public void setMemberNickname(String memberNickname) {
        this.memberNickname = memberNickname;
    }

    public Integer getGetType() {
        return getType;
    }

    public void setGetType(Integer getType) {
        this.getType = getType;
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

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public Integer getCouponType() {
        return couponType;
    }

    public void setCouponType(Integer couponType) {
        this.couponType = couponType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getNearExpiry() {
        return nearExpiry;
    }

    public void setNearExpiry(Boolean nearExpiry) {
        this.nearExpiry = nearExpiry;
    }

    public Integer getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(Integer remainingDays) {
        this.remainingDays = remainingDays;
    }
} 