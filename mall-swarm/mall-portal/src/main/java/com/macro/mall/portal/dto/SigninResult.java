package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 签到结果DTO
 * Created by guanghengzhou on 2024/12/23.
 */
@Schema(description = "签到结果")
public class SigninResult {
    
    @Schema(description = "获得的积分")
    private Integer points;
    
    @Schema(description = "连续签到天数")
    private Integer continuousDays;
    
    @Schema(description = "是否可领取连签奖励")
    private Boolean canClaimReward;
    
    @Schema(description = "签到消息")
    private String message;

    // Getters and Setters
    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }

    public Boolean getCanClaimReward() {
        return canClaimReward;
    }

    public void setCanClaimReward(Boolean canClaimReward) {
        this.canClaimReward = canClaimReward;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 