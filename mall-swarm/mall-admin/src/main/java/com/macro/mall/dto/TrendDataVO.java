package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 趋势数据DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "趋势数据")
public class TrendDataVO {
    
    @Schema(description = "日期")
    private String date;
    
    @Schema(description = "收入")
    private BigDecimal revenue;
    
    @Schema(description = "订单数")
    private Integer orderCount;
}
