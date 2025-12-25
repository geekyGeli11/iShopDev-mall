package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充值配置参数
 */
@Data
@Schema(description = "充值配置参数")
public class UmsRechargeConfigParam {

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

    @Schema(description = "是否启用充值功能（此字段在数据库中不存在，仅用于前端显示）")
    private Boolean enableRecharge;

    @Schema(description = "是否启用消费功能")
    private Boolean enableConsume;

    @Schema(description = "配置更新说明（此字段在数据库中不存在，仅用于前端显示）")
    private String updateRemark;

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

        @Schema(description = "是否启用（管理字段，不存储到数据库）")
        private Boolean enabled = true;

        @Schema(description = "排序（管理字段，不存储到数据库）")
        private Integer sort;
    }
}
