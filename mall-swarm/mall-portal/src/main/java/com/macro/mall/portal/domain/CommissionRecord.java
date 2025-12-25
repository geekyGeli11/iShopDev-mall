package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 佣金记录
 */
@Schema(description = "佣金记录")
public class CommissionRecord {
    
    @Schema(description = "记录ID")
    private Long id;
    
    @Schema(description = "佣金类型：1-邀请奖励，2-一级分销佣金，3-二级分销佣金")
    private Integer commissionType;
    
    @Schema(description = "佣金金额")
    private BigDecimal commissionAmount;
    
    @Schema(description = "订单ID")
    private Long orderId;
    
    @Schema(description = "订单金额")
    private BigDecimal orderAmount;
    
    @Schema(description = "佣金比例")
    private BigDecimal commissionRate;
    
    @Schema(description = "客户ID")
    private Long customerId;
    
    @Schema(description = "客户昵称")
    private String customerNickname;
    
    @Schema(description = "商品分类")
    private String productCategory;
    
    @Schema(description = "结算状态：0-未结算，1-已结算，2-已提现")
    private Integer settlementStatus;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "结算时间")
    private Date settlementTime;
    
    @Schema(description = "备注")
    private String remark;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getCommissionType() {
        return commissionType;
    }
    
    public void setCommissionType(Integer commissionType) {
        this.commissionType = commissionType;
    }
    
    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }
    
    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public BigDecimal getOrderAmount() {
        return orderAmount;
    }
    
    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerNickname() {
        return customerNickname;
    }
    
    public void setCustomerNickname(String customerNickname) {
        this.customerNickname = customerNickname;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public Integer getSettlementStatus() {
        return settlementStatus;
    }
    
    public void setSettlementStatus(Integer settlementStatus) {
        this.settlementStatus = settlementStatus;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getSettlementTime() {
        return settlementTime;
    }
    
    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
} 