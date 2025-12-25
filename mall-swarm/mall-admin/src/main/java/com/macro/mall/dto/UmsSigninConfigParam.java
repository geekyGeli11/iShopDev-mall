package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 签到配置参数DTO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到配置参数")
public class UmsSigninConfigParam {
    
    @Schema(description = "配置ID")
    private Long id;
    
    @NotBlank(message = "配置名称不能为空")
    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configName;
    
    @NotNull(message = "基础积分不能为空")
    @Min(value = 1, message = "基础积分不能小于1")
    @Max(value = 100, message = "基础积分不能大于100")
    @Schema(description = "基础积分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer basePoints;
    
    @NotNull(message = "递增积分不能为空")
    @Min(value = 1, message = "递增积分不能小于1")
    @Max(value = 50, message = "递增积分不能大于50")
    @Schema(description = "递增积分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer incrementPoints;
    
    @NotNull(message = "每日上限不能为空")
    @Min(value = 1, message = "每日上限不能小于1")
    @Max(value = 200, message = "每日上限不能大于200")
    @Schema(description = "每日最高积分", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer maxDailyPoints;
    
    @NotNull(message = "连签天数不能为空")
    @Min(value = 1, message = "连签天数不能小于1")
    @Max(value = 365, message = "连签天数不能大于365")
    @Schema(description = "连签奖励天数要求", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer continuousDaysForReward;
    
    @Schema(description = "奖励优惠券ID")
    private Long rewardCouponId;
    
    @NotNull(message = "签到周期不能为空")
    @Min(value = 1, message = "签到周期不能小于1")
    @Max(value = 365, message = "签到周期不能大于365")
    @Schema(description = "签到周期天数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cycleDays;
    
    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用：0-禁用，1-启用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isActive;

    // Getters and Setters
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
} 