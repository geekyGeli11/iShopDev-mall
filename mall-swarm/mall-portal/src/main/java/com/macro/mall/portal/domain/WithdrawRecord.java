package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现记录
 */
@Schema(description = "提现记录")
public class WithdrawRecord {
    
    @Schema(description = "提现申请ID")
    private Long id;
    
    @Schema(description = "提现金额")
    private BigDecimal amount;
    
    @Schema(description = "提现方式：alipay-支付宝，wechat-微信，bank-银行卡")
    private String withdrawMethod;
    
    @Schema(description = "提现账户信息")
    private String accountInfo;
    
    @Schema(description = "账户姓名")
    private String accountName;
    
    @Schema(description = "申请状态：0-待审核，1-审核通过，2-提现成功，3-提现失败，4-已取消，5-审核拒绝")
    private Integer status;
    
    @Schema(description = "申请时间")
    private Date applyTime;
    
    @Schema(description = "处理时间")
    private Date processTime;
    
    @Schema(description = "到账时间")
    private Date arrivalTime;
    
    @Schema(description = "交易单号")
    private String transactionNo;
    
    @Schema(description = "手续费")
    private BigDecimal fee;
    
    @Schema(description = "实际到账金额")
    private BigDecimal actualAmount;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "审核备注")
    private String auditRemark;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getWithdrawMethod() {
        return withdrawMethod;
    }
    
    public void setWithdrawMethod(String withdrawMethod) {
        this.withdrawMethod = withdrawMethod;
    }
    
    public String getAccountInfo() {
        return accountInfo;
    }
    
    public void setAccountInfo(String accountInfo) {
        this.accountInfo = accountInfo;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Date getApplyTime() {
        return applyTime;
    }
    
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
    
    public Date getProcessTime() {
        return processTime;
    }
    
    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }
    
    public Date getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public String getTransactionNo() {
        return transactionNo;
    }
    
    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }
    
    public BigDecimal getFee() {
        return fee;
    }
    
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
    
    public BigDecimal getActualAmount() {
        return actualAmount;
    }
    
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getAuditRemark() {
        return auditRemark;
    }
    
    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
} 