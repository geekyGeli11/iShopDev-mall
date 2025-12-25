package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充值配置响应实体
 */
@Data
@Schema(description = "充值配置响应")
public class RechargeConfigResult {

    @Schema(description = "最小充值金额")
    private BigDecimal minAmount;

    @Schema(description = "最大充值金额")
    private BigDecimal maxAmount;

    @Schema(description = "快速充值选项")
    private List<QuickAmountOption> quickAmounts;

    @Schema(description = "是否启用赠送功能")
    private Boolean enableBonus;

    @Schema(description = "赠送余额比例")
    private BigDecimal bonusBalanceRate;

    @Schema(description = "赠送积分比例")
    private BigDecimal bonusIntegrationRate;

    @Schema(description = "享受赠送的最小充值金额")
    private BigDecimal bonusMinAmount;

    /**
     * 快速充值选项
     */
    @Data
    @Schema(description = "快速充值选项")
    public static class QuickAmountOption {
        
        @Schema(description = "充值金额")
        private BigDecimal amount;

        @Schema(description = "赠送余额")
        private BigDecimal bonusBalance;

        @Schema(description = "赠送积分")
        private Integer bonusIntegration;

        @Schema(description = "描述信息")
        private String description;
    }
} 