package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 签到配置VO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到配置")
public class SigninConfigVO {
    
    @Schema(description = "配置名称")
    private String configName;
    
    @Schema(description = "基础积分")
    private Integer basePoints;
    
    @Schema(description = "递增积分")
    private Integer incrementPoints;
    
    @Schema(description = "每日最高积分")
    private Integer maxDailyPoints;
    
    @Schema(description = "连签奖励天数要求")
    private Integer continuousDaysForReward;
    
    @Schema(description = "签到周期天数")
    private Integer cycleDays;
    
    @Schema(description = "奖励优惠券ID")
    private Long rewardCouponId;
    
    @Schema(description = "奖励优惠券名称")
    private String rewardCouponName;
    
    @Schema(description = "奖励优惠券金额")
    private Integer rewardCouponAmount;
    
    @Schema(description = "奖励优惠券使用门槛")
    private Integer rewardCouponMinPoint;
    
    @Schema(description = "奖励优惠券有效期开始时间")
    private String rewardCouponStartTime;
    
    @Schema(description = "奖励优惠券有效期结束时间")
    private String rewardCouponEndTime;

    // Getters and Setters
    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Integer getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(Integer basePoints) {
        this.basePoints = basePoints;
    }

    public Integer getIncrementPoints() {
        return incrementPoints;
    }

    public void setIncrementPoints(Integer incrementPoints) {
        this.incrementPoints = incrementPoints;
    }

    public Integer getMaxDailyPoints() {
        return maxDailyPoints;
    }

    public void setMaxDailyPoints(Integer maxDailyPoints) {
        this.maxDailyPoints = maxDailyPoints;
    }

    public Integer getContinuousDaysForReward() {
        return continuousDaysForReward;
    }

    public void setContinuousDaysForReward(Integer continuousDaysForReward) {
        this.continuousDaysForReward = continuousDaysForReward;
    }

    public Integer getCycleDays() {
        return cycleDays;
    }

    public void setCycleDays(Integer cycleDays) {
        this.cycleDays = cycleDays;
    }

    public Long getRewardCouponId() {
        return rewardCouponId;
    }

    public void setRewardCouponId(Long rewardCouponId) {
        this.rewardCouponId = rewardCouponId;
    }

    public String getRewardCouponName() {
        return rewardCouponName;
    }

    public void setRewardCouponName(String rewardCouponName) {
        this.rewardCouponName = rewardCouponName;
    }

    public Integer getRewardCouponAmount() {
        return rewardCouponAmount;
    }

    public void setRewardCouponAmount(Integer rewardCouponAmount) {
        this.rewardCouponAmount = rewardCouponAmount;
    }

    public Integer getRewardCouponMinPoint() {
        return rewardCouponMinPoint;
    }

    public void setRewardCouponMinPoint(Integer rewardCouponMinPoint) {
        this.rewardCouponMinPoint = rewardCouponMinPoint;
    }

    public String getRewardCouponStartTime() {
        return rewardCouponStartTime;
    }

    public void setRewardCouponStartTime(String rewardCouponStartTime) {
        this.rewardCouponStartTime = rewardCouponStartTime;
    }

    public String getRewardCouponEndTime() {
        return rewardCouponEndTime;
    }

    public void setRewardCouponEndTime(String rewardCouponEndTime) {
        this.rewardCouponEndTime = rewardCouponEndTime;
    }
}
