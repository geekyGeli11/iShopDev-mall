package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsInviteWithdrawApply implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "申请用户ID")
    private Long userId;

    @Schema(title = "提现申请单号")
    private String withdrawSn;

    @Schema(title = "申请提现金额")
    private BigDecimal applyAmount;

    @Schema(title = "手续费金额")
    private BigDecimal feeAmount;

    @Schema(title = "实际到账金额")
    private BigDecimal actualAmount;

    @Schema(title = "提现方式：1-微信钱包，2-银行卡")
    private Byte withdrawType;

    @Schema(title = "提现账户信息（微信openid或银行卡号）")
    private String withdrawAccount;

    @Schema(title = "账户名称")
    private String accountName;

    @Schema(title = "申请状态：0-待审核，1-审核通过，2-提现成功，3-提现失败，4-已取消")
    private Byte status;

    @Schema(title = "申请时间")
    private Date applyTime;

    @Schema(title = "审核时间")
    private Date auditTime;

    @Schema(title = "处理时间")
    private Date processTime;

    @Schema(title = "预计到账时间")
    private Date expectedArrivalTime;

    @Schema(title = "实际到账时间")
    private Date actualArrivalTime;

    @Schema(title = "审核人员")
    private String auditUser;

    @Schema(title = "处理人员")
    private String processUser;

    @Schema(title = "申请备注")
    private String remark;

    @Schema(title = "审核备注")
    private String auditRemark;

    @Schema(title = "处理备注")
    private String processRemark;

    @Schema(title = "失败原因")
    private String failureReason;

    @Schema(title = "创建时间")
    private Date createdAt;

    @Schema(title = "更新时间")
    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWithdrawSn() {
        return withdrawSn;
    }

    public void setWithdrawSn(String withdrawSn) {
        this.withdrawSn = withdrawSn;
    }

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Byte getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(Byte withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String getWithdrawAccount() {
        return withdrawAccount;
    }

    public void setWithdrawAccount(String withdrawAccount) {
        this.withdrawAccount = withdrawAccount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public Date getExpectedArrivalTime() {
        return expectedArrivalTime;
    }

    public void setExpectedArrivalTime(Date expectedArrivalTime) {
        this.expectedArrivalTime = expectedArrivalTime;
    }

    public Date getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(Date actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public String getProcessUser() {
        return processUser;
    }

    public void setProcessUser(String processUser) {
        this.processUser = processUser;
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

    public String getProcessRemark() {
        return processRemark;
    }

    public void setProcessRemark(String processRemark) {
        this.processRemark = processRemark;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", withdrawSn=").append(withdrawSn);
        sb.append(", applyAmount=").append(applyAmount);
        sb.append(", feeAmount=").append(feeAmount);
        sb.append(", actualAmount=").append(actualAmount);
        sb.append(", withdrawType=").append(withdrawType);
        sb.append(", withdrawAccount=").append(withdrawAccount);
        sb.append(", accountName=").append(accountName);
        sb.append(", status=").append(status);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", auditTime=").append(auditTime);
        sb.append(", processTime=").append(processTime);
        sb.append(", expectedArrivalTime=").append(expectedArrivalTime);
        sb.append(", actualArrivalTime=").append(actualArrivalTime);
        sb.append(", auditUser=").append(auditUser);
        sb.append(", processUser=").append(processUser);
        sb.append(", remark=").append(remark);
        sb.append(", auditRemark=").append(auditRemark);
        sb.append(", processRemark=").append(processRemark);
        sb.append(", failureReason=").append(failureReason);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}