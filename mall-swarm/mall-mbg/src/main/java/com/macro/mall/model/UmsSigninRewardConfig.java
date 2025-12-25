package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsSigninRewardConfig implements Serializable {
    private Long id;

    @Schema(title = "配置名称")
    private String configName;

    @Schema(title = "基础积分")
    private Integer basePoints;

    @Schema(title = "递增积分")
    private Integer incrementPoints;

    @Schema(title = "每日最高积分")
    private Integer maxDailyPoints;

    @Schema(title = "连签奖励天数要求")
    private Integer continuousDaysForReward;

    @Schema(title = "连签奖励优惠券ID")
    private Long rewardCouponId;

    @Schema(title = "签到周期天数")
    private Integer cycleDays;

    @Schema(title = "是否启用：0-禁用，1-启用")
    private Byte isActive;

    private Date createdAt;

    private Date updatedAt;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getRewardCouponId() {
        return rewardCouponId;
    }

    public void setRewardCouponId(Long rewardCouponId) {
        this.rewardCouponId = rewardCouponId;
    }

    public Integer getCycleDays() {
        return cycleDays;
    }

    public void setCycleDays(Integer cycleDays) {
        this.cycleDays = cycleDays;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
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
        sb.append(", configName=").append(configName);
        sb.append(", basePoints=").append(basePoints);
        sb.append(", incrementPoints=").append(incrementPoints);
        sb.append(", maxDailyPoints=").append(maxDailyPoints);
        sb.append(", continuousDaysForReward=").append(continuousDaysForReward);
        sb.append(", rewardCouponId=").append(rewardCouponId);
        sb.append(", cycleDays=").append(cycleDays);
        sb.append(", isActive=").append(isActive);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}