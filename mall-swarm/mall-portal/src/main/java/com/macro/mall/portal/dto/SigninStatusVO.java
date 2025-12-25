package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 签到状态VO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到状态")
public class SigninStatusVO {
    
    @Schema(description = "今日是否已签到")
    private Boolean isSigninToday;
    
    @Schema(description = "连续签到天数")
    private Integer continuousDays;
    
    @Schema(description = "今日可获得积分")
    private Integer todayPoints;
    
    @Schema(description = "本月签到天数")
    private Integer monthSigninDays;
    
    @Schema(description = "是否可领取连签奖励")
    private Boolean canClaimReward;
    
    @Schema(description = "当前积分余额")
    private Integer currentPoints;
    
    @Schema(description = "近一周签到情况（日期列表）")
    private List<String> signedDates;

    // Getters and Setters
    public Boolean getIsSigninToday() {
        return isSigninToday;
    }

    public void setIsSigninToday(Boolean isSigninToday) {
        this.isSigninToday = isSigninToday;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }

    public Integer getTodayPoints() {
        return todayPoints;
    }

    public void setTodayPoints(Integer todayPoints) {
        this.todayPoints = todayPoints;
    }

    public Integer getMonthSigninDays() {
        return monthSigninDays;
    }

    public void setMonthSigninDays(Integer monthSigninDays) {
        this.monthSigninDays = monthSigninDays;
    }

    public Boolean getCanClaimReward() {
        return canClaimReward;
    }

    public void setCanClaimReward(Boolean canClaimReward) {
        this.canClaimReward = canClaimReward;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public List<String> getSignedDates() {
        return signedDates;
    }

    public void setSignedDates(List<String> signedDates) {
        this.signedDates = signedDates;
    }
} 