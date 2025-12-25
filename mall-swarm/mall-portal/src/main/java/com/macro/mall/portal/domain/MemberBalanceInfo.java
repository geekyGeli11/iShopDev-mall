package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户余额信息
 */
@Data
@Schema(description = "用户余额信息")
public class MemberBalanceInfo {

    @Schema(description = "用户ID")
    private Long memberId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "当前余额")
    private BigDecimal balance;

    @Schema(description = "累计充值金额")
    private BigDecimal totalRechargeAmount;

    @Schema(description = "累计消费金额")
    private BigDecimal totalConsumeAmount;

    @Schema(description = "今日充值金额")
    private BigDecimal todayRechargeAmount;

    @Schema(description = "今日消费金额")
    private BigDecimal todayConsumeAmount;

    @Schema(description = "是否可以充值")
    private Boolean canRecharge = true;

    @Schema(description = "是否可以消费")
    private Boolean canConsume = true;
} 