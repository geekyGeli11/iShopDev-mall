package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售渠道细分数据DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "销售渠道细分数据")
public class ChannelBreakdownVO {
    
    @Schema(description = "销售渠道：miniprogram-小程序，non-system-非系统销售，self-checkout-自助结算")
    private String channel;
    
    @Schema(description = "渠道收入")
    private BigDecimal revenue;
    
    @Schema(description = "渠道订单数")
    private Integer orderCount;
    
    @Schema(description = "占比百分比")
    private BigDecimal percentage;
}
