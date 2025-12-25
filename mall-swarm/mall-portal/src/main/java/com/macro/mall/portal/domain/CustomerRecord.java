package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户记录
 */
@Schema(description = "客户记录")
public class CustomerRecord {
    
    @Schema(description = "客户ID")
    private Long customerId;
    
    @Schema(description = "客户昵称")
    private String nickname;
    
    @Schema(description = "客户头像")
    private String avatar;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "分销层级：1-一级，2-二级")
    private Integer level;
    
    @Schema(description = "绑定时间")
    private Date bindTime;
    
    @Schema(description = "订单数量")
    private Integer orderCount;
    
    @Schema(description = "订单总金额")
    private BigDecimal orderAmount;
    
    @Schema(description = "贡献佣金")
    private BigDecimal commissionAmount;
    
    @Schema(description = "最后下单时间")
    private Date lastOrderTime;
    
    @Schema(description = "绑定状态：1-永久绑定，2-临时绑定")
    private Integer bindStatus;
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public Date getBindTime() {
        return bindTime;
    }
    
    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }
    
    public Integer getOrderCount() {
        return orderCount;
    }
    
    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
    
    public BigDecimal getOrderAmount() {
        return orderAmount;
    }
    
    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }
    
    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }
    
    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }
    
    public Date getLastOrderTime() {
        return lastOrderTime;
    }
    
    public void setLastOrderTime(Date lastOrderTime) {
        this.lastOrderTime = lastOrderTime;
    }
    
    public Integer getBindStatus() {
        return bindStatus;
    }
    
    public void setBindStatus(Integer bindStatus) {
        this.bindStatus = bindStatus;
    }
} 