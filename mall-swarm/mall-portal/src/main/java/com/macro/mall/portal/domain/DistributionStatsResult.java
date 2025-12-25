package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 分销统计结果
 */
@Schema(description = "分销统计结果")
public class DistributionStatsResult {
    
    @Schema(description = "累计佣金收入")
    private BigDecimal totalCommission;
    
    @Schema(description = "可提现金额")
    private BigDecimal availableAmount;
    
    @Schema(description = "已提现金额")
    private BigDecimal withdrawAmount;
    
    @Schema(description = "待结算佣金")
    private BigDecimal pendingCommission;
    
    @Schema(description = "一级客户数量")
    private Integer level1CustomerCount;
    
    @Schema(description = "二级客户数量")
    private Integer level2CustomerCount;
    
    @Schema(description = "总订单数")
    private Integer totalOrderCount;
    
    @Schema(description = "推广订单总金额")
    private BigDecimal totalOrderAmount;
    
    @Schema(description = "本月佣金收入")
    private BigDecimal monthCommission;
    
    @Schema(description = "昨日佣金收入")
    private BigDecimal yesterdayCommission;
    
    @Schema(description = "分销码")
    private String distributionCode;
    
    public BigDecimal getTotalCommission() {
        return totalCommission;
    }
    
    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }
    
    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }
    
    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }
    
    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }
    
    public void setWithdrawAmount(BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }
    
    public BigDecimal getPendingCommission() {
        return pendingCommission;
    }
    
    public void setPendingCommission(BigDecimal pendingCommission) {
        this.pendingCommission = pendingCommission;
    }
    
    public Integer getLevel1CustomerCount() {
        return level1CustomerCount;
    }
    
    public void setLevel1CustomerCount(Integer level1CustomerCount) {
        this.level1CustomerCount = level1CustomerCount;
    }
    
    public Integer getLevel2CustomerCount() {
        return level2CustomerCount;
    }
    
    public void setLevel2CustomerCount(Integer level2CustomerCount) {
        this.level2CustomerCount = level2CustomerCount;
    }
    
    public Integer getTotalOrderCount() {
        return totalOrderCount;
    }
    
    public void setTotalOrderCount(Integer totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }
    
    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }
    
    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }
    
    public BigDecimal getMonthCommission() {
        return monthCommission;
    }
    
    public void setMonthCommission(BigDecimal monthCommission) {
        this.monthCommission = monthCommission;
    }
    
    public BigDecimal getYesterdayCommission() {
        return yesterdayCommission;
    }
    
    public void setYesterdayCommission(BigDecimal yesterdayCommission) {
        this.yesterdayCommission = yesterdayCommission;
    }
    
    public String getDistributionCode() {
        return distributionCode;
    }
    
    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode;
    }
} 