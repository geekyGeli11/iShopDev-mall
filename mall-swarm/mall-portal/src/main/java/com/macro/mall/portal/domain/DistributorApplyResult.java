package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * 分销商申请状态结果
 */
@Schema(description = "分销商申请状态结果")
public class DistributorApplyResult {
    
    @Schema(description = "是否已是分销商")
    private Boolean isDistributor;
    
    @Schema(description = "申请状态：none-未申请，pending-审核中，approved-已通过，rejected-已拒绝")
    private String applyStatus;
    
    @Schema(description = "申请时间")
    private Date applyTime;
    
    @Schema(description = "审核时间")
    private Date approvedTime;
    
    @Schema(description = "拒绝原因")
    private String rejectReason;
    
    @Schema(description = "分销商等级")
    private Integer distributorLevel;
    
    public Boolean getIsDistributor() {
        return isDistributor;
    }
    
    public void setIsDistributor(Boolean isDistributor) {
        this.isDistributor = isDistributor;
    }
    
    public String getApplyStatus() {
        return applyStatus;
    }
    
    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }
    
    public Date getApplyTime() {
        return applyTime;
    }
    
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
    
    public Date getApprovedTime() {
        return approvedTime;
    }
    
    public void setApprovedTime(Date approvedTime) {
        this.approvedTime = approvedTime;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    public Integer getDistributorLevel() {
        return distributorLevel;
    }
    
    public void setDistributorLevel(Integer distributorLevel) {
        this.distributorLevel = distributorLevel;
    }
} 