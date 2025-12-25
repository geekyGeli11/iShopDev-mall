package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分销商状态更新参数
 */
@Schema(description = "分销商状态更新参数")
public class UmsDistributorStatusParam {
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "分销商状态：0-普通用户，1-分销商，2-暂停，3-禁用", required = true)
    private Byte status;
    
    @Schema(description = "状态变更原因")
    private String reason;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 