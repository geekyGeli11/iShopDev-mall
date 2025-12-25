package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分销商等级更新参数
 */
@Schema(description = "分销商等级更新参数")
public class UmsDistributorLevelParam {
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "分销商等级：0-普通，1-初级，2-中级，3-高级", required = true)
    private Byte distributorLevel;
    
    @Schema(description = "等级变更原因")
    private String reason;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getDistributorLevel() {
        return distributorLevel;
    }

    public void setDistributorLevel(Byte distributorLevel) {
        this.distributorLevel = distributorLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 