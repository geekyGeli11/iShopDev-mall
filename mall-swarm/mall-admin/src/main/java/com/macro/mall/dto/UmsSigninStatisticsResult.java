package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 签到统计结果DTO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到统计结果")
public class UmsSigninStatisticsResult {
    
    @Schema(description = "总签到人数")
    private Long totalSigninUsers;
    
    @Schema(description = "总签到次数")
    private Long totalSigninCount;
    
    @Schema(description = "今日签到人数")
    private Long todaySigninUsers;
    
    @Schema(description = "本月签到人数")
    private Long monthSigninUsers;
    
    @Schema(description = "连续签到7天以上人数")
    private Long continuous7DaysUsers;
    
    @Schema(description = "连续签到30天以上人数")
    private Long continuous30DaysUsers;
    
    @Schema(description = "累计发放积分")
    private Long totalPointsGiven;
    
    @Schema(description = "本月发放积分")
    private Long monthPointsGiven;
    
    @Schema(description = "连签奖励发放次数")
    private Long continuousRewardCount;

    // Getters and Setters
    public Long getTotalSigninUsers() {
        return totalSigninUsers;
    }

    public void setTotalSigninUsers(Long totalSigninUsers) {
        this.totalSigninUsers = totalSigninUsers;
    }

    public Long getTotalSigninCount() {
        return totalSigninCount;
    }

    public void setTotalSigninCount(Long totalSigninCount) {
        this.totalSigninCount = totalSigninCount;
    }

    public Long getTodaySigninUsers() {
        return todaySigninUsers;
    }

    public void setTodaySigninUsers(Long todaySigninUsers) {
        this.todaySigninUsers = todaySigninUsers;
    }

    public Long getMonthSigninUsers() {
        return monthSigninUsers;
    }

    public void setMonthSigninUsers(Long monthSigninUsers) {
        this.monthSigninUsers = monthSigninUsers;
    }

    public Long getContinuous7DaysUsers() {
        return continuous7DaysUsers;
    }

    public void setContinuous7DaysUsers(Long continuous7DaysUsers) {
        this.continuous7DaysUsers = continuous7DaysUsers;
    }

    public Long getContinuous30DaysUsers() {
        return continuous30DaysUsers;
    }

    public void setContinuous30DaysUsers(Long continuous30DaysUsers) {
        this.continuous30DaysUsers = continuous30DaysUsers;
    }

    public Long getTotalPointsGiven() {
        return totalPointsGiven;
    }

    public void setTotalPointsGiven(Long totalPointsGiven) {
        this.totalPointsGiven = totalPointsGiven;
    }

    public Long getMonthPointsGiven() {
        return monthPointsGiven;
    }

    public void setMonthPointsGiven(Long monthPointsGiven) {
        this.monthPointsGiven = monthPointsGiven;
    }

    public Long getContinuousRewardCount() {
        return continuousRewardCount;
    }

    public void setContinuousRewardCount(Long continuousRewardCount) {
        this.continuousRewardCount = continuousRewardCount;
    }
} 