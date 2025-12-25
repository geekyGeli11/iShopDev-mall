package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 奖励汇总结果
 */
@Data
public class RewardSummaryResult {
    
    @Schema(title = "年度总奖励金额")
    private BigDecimal yearTotalAmount;
    
    @Schema(title = "年度总奖励积分")
    private Integer yearTotalPoints;
    
    @Schema(title = "年度奖励笔数")
    private Integer yearRewardCount;
    
    @Schema(title = "月度奖励列表")
    private List<MonthlyReward> monthlyRewards;
    
    /**
     * 月度奖励数据
     */
    @Data
    public static class MonthlyReward {
        
        @Schema(title = "月份(1-12)")
        private Integer month;
        
        @Schema(title = "月份名称")
        private String monthName;
        
        @Schema(title = "月度奖励金额")
        private BigDecimal amount;
        
        @Schema(title = "月度奖励积分")
        private Integer points;
        
        @Schema(title = "月度奖励笔数")
        private Integer rewardCount;
        
        @Schema(title = "是否当前月份")
        private Boolean isCurrent;
    }
} 