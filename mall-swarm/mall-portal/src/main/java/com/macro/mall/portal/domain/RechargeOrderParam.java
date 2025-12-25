package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 充值订单参数
 */
@Data
@Schema(description = "充值订单参数")
public class RechargeOrderParam {

    @Schema(description = "充值金额", required = true)
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "支付方式：1-支付宝，2-微信", required = true)
    @NotNull(message = "支付方式不能为空")
    private Integer payType;

    @Schema(description = "订单备注")
    private String note;
} 