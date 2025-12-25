package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PmsInviteStatistics implements Serializable {
    private Long id;

    @Schema(title = "邀请者用户ID")
    private Long userId;

    @Schema(title = "统计日期")
    private Date statDate;

    @Schema(title = "当日邀请次数（生成参数次数）")
    private Integer inviteCount;

    @Schema(title = "当日成功注册人数")
    private Integer registerCount;

    @Schema(title = "当日首单完成人数")
    private Integer firstOrderCount;

    @Schema(title = "当日获得积分奖励总数")
    private Integer totalRewardPoints;

    @Schema(title = "当日获得现金奖励总额")
    private BigDecimal totalRewardAmount;

    private Date createdAt;

    private Date updatedAt;

    @Schema(title = "分销商状态：0-普通用户，1-分销商，2-暂停，3-禁用")
    private Byte distributorStatus;

    @Schema(title = "累计佣金收入")
    private BigDecimal totalCommission;

    @Schema(title = "已提现佣金")
    private BigDecimal withdrawCommission;

    @Schema(title = "待结算佣金")
    private BigDecimal pendingCommission;

    @Schema(title = "一级客户数量")
    private Integer level1CustomerCount;

    @Schema(title = "二级客户数量")
    private Integer level2CustomerCount;

    @Schema(title = "推广订单总金额")
    private BigDecimal totalOrderAmount;

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

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public Integer getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(Integer inviteCount) {
        this.inviteCount = inviteCount;
    }

    public Integer getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(Integer registerCount) {
        this.registerCount = registerCount;
    }

    public Integer getFirstOrderCount() {
        return firstOrderCount;
    }

    public void setFirstOrderCount(Integer firstOrderCount) {
        this.firstOrderCount = firstOrderCount;
    }

    public Integer getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(Integer totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    public BigDecimal getTotalRewardAmount() {
        return totalRewardAmount;
    }

    public void setTotalRewardAmount(BigDecimal totalRewardAmount) {
        this.totalRewardAmount = totalRewardAmount;
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

    public Byte getDistributorStatus() {
        return distributorStatus;
    }

    public void setDistributorStatus(Byte distributorStatus) {
        this.distributorStatus = distributorStatus;
    }

    public BigDecimal getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(BigDecimal totalCommission) {
        this.totalCommission = totalCommission;
    }

    public BigDecimal getWithdrawCommission() {
        return withdrawCommission;
    }

    public void setWithdrawCommission(BigDecimal withdrawCommission) {
        this.withdrawCommission = withdrawCommission;
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

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", statDate=").append(statDate);
        sb.append(", inviteCount=").append(inviteCount);
        sb.append(", registerCount=").append(registerCount);
        sb.append(", firstOrderCount=").append(firstOrderCount);
        sb.append(", totalRewardPoints=").append(totalRewardPoints);
        sb.append(", totalRewardAmount=").append(totalRewardAmount);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", distributorStatus=").append(distributorStatus);
        sb.append(", totalCommission=").append(totalCommission);
        sb.append(", withdrawCommission=").append(withdrawCommission);
        sb.append(", pendingCommission=").append(pendingCommission);
        sb.append(", level1CustomerCount=").append(level1CustomerCount);
        sb.append(", level2CustomerCount=").append(level2CustomerCount);
        sb.append(", totalOrderAmount=").append(totalOrderAmount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}