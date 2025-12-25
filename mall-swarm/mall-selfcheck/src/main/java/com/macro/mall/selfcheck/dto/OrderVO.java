package com.macro.mall.selfcheck.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单信息视图对象
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(title = "订单信息")
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "订单ID", description = "订单唯一标识")
    private Long orderId;

    @Schema(title = "订单编号", description = "订单编号")
    private String orderSn;

    @Schema(title = "用户类型", description = "用户类型：MEMBER-会员，GUEST-游客")
    private String userType;

    @Schema(title = "用户ID", description = "会员ID或游客ID")
    private String userId;

    @Schema(title = "会员ID", description = "会员ID（会员订单）")
    private Long memberId;

    @Schema(title = "会员用户名", description = "会员用户名")
    private String memberUsername;

    @Schema(title = "订单状态", description = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Integer status;

    @Schema(title = "订单状态名称", description = "订单状态描述")
    private String statusName;

    @Schema(title = "订单类型", description = "订单类型：QUICK-快速下单，CART-购物车下单")
    private String orderType;

    @Schema(title = "订单来源", description = "订单来源：PC,APP,微信,自助收银")
    private Integer sourceType;

    @Schema(title = "商品总金额", description = "订单商品总金额")
    private BigDecimal totalAmount;

    @Schema(title = "运费金额", description = "运费金额")
    private BigDecimal freightAmount;

    @Schema(title = "促销优化金额", description = "促销优化金额（促销价、满减、阶梯价）")
    private BigDecimal promotionAmount;

    @Schema(title = "积分抵扣金额", description = "积分抵扣金额")
    private BigDecimal integrationAmount;

    @Schema(title = "优惠券抵扣金额", description = "优惠券抵扣金额")
    private BigDecimal couponAmount;

    @Schema(title = "管理员后台调整订单使用的折扣金额", description = "管理员后台调整订单使用的折扣金额")
    private BigDecimal discountAmount;

    @Schema(title = "应付金额", description = "应付金额（总金额+运费-促销优化金额-优惠券优化金额-积分抵扣金额）")
    private BigDecimal payAmount;

    @Schema(title = "支付方式", description = "支付方式：WECHAT-微信支付，ALIPAY-支付宝")
    private String payType;

    @Schema(title = "支付状态", description = "支付状态：0->未支付；1->已支付；2->支付失败")
    private Integer payStatus;

    @Schema(title = "支付时间", description = "支付时间")
    private Date payTime;

    @Schema(title = "配送方式", description = "配送方式：0-无需配送，1-快递配送，2-门店自提")
    private Integer deliveryType;

    @Schema(title = "收货人姓名", description = "收货人姓名")
    private String receiverName;

    @Schema(title = "收货人电话", description = "收货人电话")
    private String receiverPhone;

    @Schema(title = "收货人邮编", description = "收货人邮编")
    private String receiverPostCode;

    @Schema(title = "省份/直辖市", description = "省份/直辖市")
    private String receiverProvince;

    @Schema(title = "城市", description = "城市")
    private String receiverCity;

    @Schema(title = "区", description = "区")
    private String receiverRegion;

    @Schema(title = "详细地址", description = "详细地址")
    private String receiverDetailAddress;

    @Schema(title = "订单备注", description = "订单备注")
    private String note;

    @Schema(title = "确认收货状态", description = "确认收货状态：0->未确认；1->已确认")
    private Integer confirmStatus;

    @Schema(title = "删除状态", description = "删除状态：0->未删除；1->已删除")
    private Integer deleteStatus;

    @Schema(title = "下单时使用的积分", description = "下单时使用的积分")
    private Integer useIntegration;

    @Schema(title = "订单创建时间", description = "订单创建时间")
    private Date createTime;

    @Schema(title = "订单修改时间", description = "订单修改时间")
    private Date modifyTime;

    @Schema(title = "自动确认时间", description = "自动确认时间（天）")
    private Integer autoConfirmDay;

    @Schema(title = "可以获得的积分", description = "可以获得的积分")
    private Integer integration;

    @Schema(title = "可以活动的成长值", description = "可以活动的成长值")
    private Integer growth;

    @Schema(title = "终端编号", description = "自助收银终端编号")
    private String terminalCode;

    @Schema(title = "设备信息", description = "下单设备信息")
    private String deviceInfo;

    @Schema(title = "订单商品列表", description = "订单商品明细")
    private List<OrderItemVO> orderItems;

    @Schema(title = "使用的优惠券列表", description = "订单使用的优惠券")
    private List<MemberCouponVO> usedCoupons;

    @Schema(title = "订单操作历史", description = "订单状态变更历史")
    private List<OrderOperateHistoryVO> operateHistory;

    @Schema(title = "发货时间", description = "发货时间")
    private Date deliveryTime;

    @Schema(title = "确认收货时间", description = "确认收货时间")
    private Date receiveTime;

    @Schema(title = "评价时间", description = "评价时间")
    private Date commentTime;

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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Integer getUseIntegration() {
        return useIntegration;
    }

    public void setUseIntegration(Integer useIntegration) {
        this.useIntegration = useIntegration;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getAutoConfirmDay() {
        return autoConfirmDay;
    }

    public void setAutoConfirmDay(Integer autoConfirmDay) {
        this.autoConfirmDay = autoConfirmDay;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public List<OrderItemVO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemVO> orderItems) {
        this.orderItems = orderItems;
    }

    public List<MemberCouponVO> getUsedCoupons() {
        return usedCoupons;
    }

    public void setUsedCoupons(List<MemberCouponVO> usedCoupons) {
        this.usedCoupons = usedCoupons;
    }

    public List<OrderOperateHistoryVO> getOperateHistory() {
        return operateHistory;
    }

    public void setOperateHistory(List<OrderOperateHistoryVO> operateHistory) {
        this.operateHistory = operateHistory;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    @Override
    public String toString() {
        return "OrderVO{" +
                "orderId=" + orderId +
                ", orderSn='" + orderSn + '\'' +
                ", userType='" + userType + '\'' +
                ", userId='" + userId + '\'' +
                ", memberId=" + memberId +
                ", memberUsername='" + memberUsername + '\'' +
                ", status=" + status +
                ", statusName='" + statusName + '\'' +
                ", orderType='" + orderType + '\'' +
                ", sourceType=" + sourceType +
                ", totalAmount=" + totalAmount +
                ", freightAmount=" + freightAmount +
                ", promotionAmount=" + promotionAmount +
                ", integrationAmount=" + integrationAmount +
                ", couponAmount=" + couponAmount +
                ", discountAmount=" + discountAmount +
                ", payAmount=" + payAmount +
                ", payType='" + payType + '\'' +
                ", payStatus=" + payStatus +
                ", payTime=" + payTime +
                ", deliveryType=" + deliveryType +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", receiverPostCode='" + receiverPostCode + '\'' +
                ", receiverProvince='" + receiverProvince + '\'' +
                ", receiverCity='" + receiverCity + '\'' +
                ", receiverRegion='" + receiverRegion + '\'' +
                ", receiverDetailAddress='" + receiverDetailAddress + '\'' +
                ", note='" + note + '\'' +
                ", confirmStatus=" + confirmStatus +
                ", deleteStatus=" + deleteStatus +
                ", useIntegration=" + useIntegration +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", autoConfirmDay=" + autoConfirmDay +
                ", integration=" + integration +
                ", growth=" + growth +
                ", terminalCode='" + terminalCode + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", orderItems=" + orderItems +
                ", usedCoupons=" + usedCoupons +
                ", operateHistory=" + operateHistory +
                ", deliveryTime=" + deliveryTime +
                ", receiveTime=" + receiveTime +
                ", commentTime=" + commentTime +
                '}';
    }
} 