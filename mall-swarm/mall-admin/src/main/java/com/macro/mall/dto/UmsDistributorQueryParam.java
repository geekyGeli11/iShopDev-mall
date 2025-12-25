package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分销商查询参数
 */
@Schema(description = "分销商查询参数")
public class UmsDistributorQueryParam {
    
    @Schema(description = "用户昵称或手机号")
    private String keyword;
    
    @Schema(description = "分销商状态：0-普通用户，1-分销商，2-暂停，3-禁用")
    private Byte distributorStatus;
    
    @Schema(description = "分销商等级：0-普通，1-初级，2-中级，3-高级")
    private Byte distributorLevel;
    
    @Schema(description = "注册开始时间")
    private String startTime;
    
    @Schema(description = "注册结束时间") 
    private String endTime;
    
    @Schema(description = "审核通过开始时间")
    private String approvedStartTime;
    
    @Schema(description = "审核通过结束时间")
    private String approvedEndTime;
    
    @Schema(description = "最小累计佣金")
    private String minCommission;
    
    @Schema(description = "最大累计佣金")
    private String maxCommission;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Byte getDistributorStatus() {
        return distributorStatus;
    }

    public void setDistributorStatus(Byte distributorStatus) {
        this.distributorStatus = distributorStatus;
    }

    public Byte getDistributorLevel() {
        return distributorLevel;
    }

    public void setDistributorLevel(Byte distributorLevel) {
        this.distributorLevel = distributorLevel;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getApprovedStartTime() {
        return approvedStartTime;
    }

    public void setApprovedStartTime(String approvedStartTime) {
        this.approvedStartTime = approvedStartTime;
    }

    public String getApprovedEndTime() {
        return approvedEndTime;
    }

    public void setApprovedEndTime(String approvedEndTime) {
        this.approvedEndTime = approvedEndTime;
    }

    public String getMinCommission() {
        return minCommission;
    }

    public void setMinCommission(String minCommission) {
        this.minCommission = minCommission;
    }

    public String getMaxCommission() {
        return maxCommission;
    }

    public void setMaxCommission(String maxCommission) {
        this.maxCommission = maxCommission;
    }
} 