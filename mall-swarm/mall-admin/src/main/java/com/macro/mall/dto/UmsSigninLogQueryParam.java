package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 签到记录查询参数DTO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到记录查询参数")
public class UmsSigninLogQueryParam {
    
    @Schema(description = "用户名")
    private String memberUsername;
    
    @Schema(description = "签到月份 (YYYY-MM)")
    private String signinMonth;
    
    @Schema(description = "开始日期 (YYYY-MM-DD)")
    private String startDate;
    
    @Schema(description = "结束日期 (YYYY-MM-DD)")
    private String endDate;
    
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum;
    
    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize;

    // Getters and Setters
    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public String getSigninMonth() {
        return signinMonth;
    }

    public void setSigninMonth(String signinMonth) {
        this.signinMonth = signinMonth;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
} 