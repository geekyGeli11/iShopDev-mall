package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 首页统计数据DTO
 * Created by macro on 2023/10/10.
 */
@Data
public class HomeStatisticsDTO {
    
    @Schema(description = "今日订单数")
    private Integer todayOrderCount;
    
    @Schema(description = "今日销售总额")
    private BigDecimal todaySaleAmount;
    
    @Schema(description = "昨日销售总额")
    private BigDecimal yesterdaySaleAmount;
    
    @Schema(description = "近7天销售总额")
    private BigDecimal last7DaysSaleAmount;
    
    @Schema(description = "待付款订单")
    private Integer pendingPaymentCount;
    
    @Schema(description = "已完成订单")
    private Integer completedOrderCount;
    
    @Schema(description = "待确认收货订单")
    private Integer pendingConfirmCount;
    
    @Schema(description = "新增货源记录")
    private Integer newSourceCount;
    
    @Schema(description = "待处理退款申请")
    private Integer pendingRefundCount;
    
    @Schema(description = "待处理退货订单")
    private Integer pendingReturnCount;
    
    @Schema(description = "广告位即将到期")
    private Integer adExpiringSoonCount;
    
    @Schema(description = "商品总览")
    private ProductOverviewDTO productOverview;
    
    @Schema(description = "用户总览")
    private UserOverviewDTO userOverview;
    
    @Schema(description = "订单统计(日期-订单数)")
    private Map<String, Integer> orderCountStatistics;
    
    @Schema(description = "订单统计(日期-销售额)")
    private Map<String, BigDecimal> orderAmountStatistics;
    
    @Schema(description = "本月订单总数")
    private Integer monthOrderCount;
    
    @Schema(description = "本周订单总数")
    private Integer weekOrderCount;
    
    @Schema(description = "本月销售总额")
    private BigDecimal monthSaleAmount;
    
    @Schema(description = "本周销售总额")
    private BigDecimal weekSaleAmount;
    
    @Schema(description = "本月订单数趋势(%)")
    private Double monthOrderCountTrend;
    
    @Schema(description = "本周订单数趋势(%)")
    private Double weekOrderCountTrend;
    
    @Schema(description = "本月销售额趋势(%)")
    private Double monthSaleAmountTrend;
    
    @Schema(description = "本周销售额趋势(%)")
    private Double weekSaleAmountTrend;
    
    @Schema(description = "待发货订单数")
    private Integer pendingDeliveryCount;
    
    @Schema(description = "已发货订单数")
    private Integer deliveredCount;
    
    @Data
    public static class ProductOverviewDTO {
        @Schema(description = "已下架")
        private Integer offShelfCount;
        
        @Schema(description = "已上架")
        private Integer onShelfCount;
        
        @Schema(description = "库存紧张")
        private Integer lowStockCount;
        
        @Schema(description = "全部商品")
        private Integer totalCount;
    }
    
    @Data
    public static class UserOverviewDTO {
        @Schema(description = "今日新增")
        private Integer todayAddCount;
        
        @Schema(description = "昨日新增")
        private Integer yesterdayAddCount;
        
        @Schema(description = "本月新增")
        private Integer monthAddCount;
        
        @Schema(description = "会员总数")
        private Integer totalCount;
    }
} 