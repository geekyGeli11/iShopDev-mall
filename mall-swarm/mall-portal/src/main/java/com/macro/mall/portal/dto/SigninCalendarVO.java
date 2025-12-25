package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 签到日历VO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到日历")
public class SigninCalendarVO {
    
    @Schema(description = "月份 (YYYY-MM)")
    private String month;
    
    @Schema(description = "签到日期列表")
    private List<String> signinDates;
    
    @Schema(description = "本月签到天数")
    private Integer monthSigninDays;
    
    @Schema(description = "连续签到天数")
    private Integer continuousDays;

    // Getters and Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<String> getSigninDates() {
        return signinDates;
    }

    public void setSigninDates(List<String> signinDates) {
        this.signinDates = signinDates;
    }

    public Integer getMonthSigninDays() {
        return monthSigninDays;
    }

    public void setMonthSigninDays(Integer monthSigninDays) {
        this.monthSigninDays = monthSigninDays;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }
} 