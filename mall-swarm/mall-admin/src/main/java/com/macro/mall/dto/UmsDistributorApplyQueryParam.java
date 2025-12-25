package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分销商申请查询参数
 */
@Schema(description = "分销商申请查询参数")
public class UmsDistributorApplyQueryParam {
    
    @Schema(description = "申请人姓名")
    private String realName;
    
    @Schema(description = "申请人手机号")
    private String phone;
    
    @Schema(description = "申请状态：0-待审核，1-已通过，2-已拒绝")
    private Byte status;
    
    @Schema(description = "申请开始时间")
    private String startTime;
    
    @Schema(description = "申请结束时间") 
    private String endTime;
    
    @Schema(description = "审核人ID")
    private Long reviewerId;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }
} 