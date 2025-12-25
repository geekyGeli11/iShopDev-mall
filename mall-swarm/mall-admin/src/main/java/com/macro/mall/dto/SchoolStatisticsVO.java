package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 学校营业数据统计VO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "学校营业数据统计")
public class SchoolStatisticsVO {
    
    @Schema(description = "学校ID")
    private Long schoolId;
    
    @Schema(description = "学校名称")
    private String schoolName;
    
    @Schema(description = "营业金额")
    private BigDecimal totalAmount;
    
    @Schema(description = "订单数")
    private Integer orderCount;
    
    @Schema(description = "平均订单金额")
    private BigDecimal avgOrderAmount;
}
