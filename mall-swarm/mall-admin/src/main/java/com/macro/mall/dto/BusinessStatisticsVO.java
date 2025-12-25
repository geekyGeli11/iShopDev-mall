package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营业数据统计响应DTO
 * Created by macro on 2025/11/28.
 */
@Data
@Schema(description = "营业数据统计响应")
public class BusinessStatisticsVO {
    
    @Schema(description = "总收入")
    private BigDecimal totalRevenue;
    
    @Schema(description = "订单数量")
    private Integer orderCount;
    
    @Schema(description = "平均订单金额")
    private BigDecimal avgOrderValue;
    
    @Schema(description = "销售渠道细分数据")
    private List<ChannelBreakdownVO> channelBreakdown;
    
    @Schema(description = "趋势数据（用于图表）")
    private List<TrendDataVO> trendData;
    
    @Schema(description = "学校营业数据统计")
    private List<SchoolStatisticsVO> schoolStatistics;
    
    @Schema(description = "门店营业数据统计")
    private List<StoreStatisticsVO> storeStatistics;
}
