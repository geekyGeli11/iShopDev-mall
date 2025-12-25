package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

/**
 * 优惠券兑换参数
 * Created by macro on 2024/01/20.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CouponExchangeParam {
    
    @Schema(title = "换购配置ID", required = true)
    @NotNull(message = "换购配置ID不能为空")
    private Long configId;
    
    @Schema(title = "兑换备注")
    private String remark;
} 