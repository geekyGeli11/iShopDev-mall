package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 佣金汇总
 */
@Schema(description = "佣金汇总")
public class CommissionSummary {
    
    @Schema(description = "总佣金")
    private BigDecimal totalCommission;
    
    @Schema(description = "可提现金额")
    private BigDecimal availableAmount;
    
    @Schema(description = "已提现金额")
    private BigDecimal withdrawnAmount;
    
    @Schema(description = "待结算金额")
    private BigDecimal pendingAmount;
    
    @Schema(description = "今日佣金")
    private BigDecimal todayCommission;
    
    @Schema(description = "昨日佣金")
    private BigDecimal yesterdayCommission;
    
    @Schema(description = "本周佣金")
    private BigDecimal weekCommission;
    
    @Schema(description = "本月佣金")
    private BigDecimal monthCommission;
    
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
    
    public BigDecimal getWithdrawnAmount() {
        return withdrawnAmount;
    }
    
    public void setWithdrawnAmount(BigDecimal withdrawnAmount) {
        this.withdrawnAmount = withdrawnAmount;
    }
    
    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }
    
    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }
    
    public BigDecimal getTodayCommission() {
        return todayCommission;
    }
    
    public void setTodayCommission(BigDecimal todayCommission) {
        this.todayCommission = todayCommission;
    }
    
    public BigDecimal getYesterdayCommission() {
        return yesterdayCommission;
    }
    
    public void setYesterdayCommission(BigDecimal yesterdayCommission) {
        this.yesterdayCommission = yesterdayCommission;
    }
    
    public BigDecimal getWeekCommission() {
        return weekCommission;
    }
    
    public void setWeekCommission(BigDecimal weekCommission) {
        this.weekCommission = weekCommission;
    }
    
    public BigDecimal getMonthCommission() {
        return monthCommission;
    }
    
    public void setMonthCommission(BigDecimal monthCommission) {
        this.monthCommission = monthCommission;
    }
} 