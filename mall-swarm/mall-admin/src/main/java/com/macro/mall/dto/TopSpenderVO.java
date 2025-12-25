package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员消费排行DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "会员消费排行")
public class TopSpenderVO {
    
    @Schema(description = "会员ID")
    private Long memberId;
    
    @Schema(description = "会员昵称")
    private String memberName;
    
    @Schema(description = "会员编号")
    private String memberCode;
    
    @Schema(description = "总消费金额")
    private BigDecimal totalSpending;
    
    @Schema(description = "订单数量")
    private Integer orderCount;
}
