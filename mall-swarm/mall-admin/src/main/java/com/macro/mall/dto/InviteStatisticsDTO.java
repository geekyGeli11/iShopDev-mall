package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 邀请统计数据DTO
 * Created by guanghengzhou on 2024/01/20.
 */
@Data
public class InviteStatisticsDTO {
    
    @Schema(description = "概览数据")
    private OverviewData overviewData;
    
    @Schema(description = "奖励统计")
    private RewardStats rewardStats;
    
    @Schema(description = "提现统计")
    private WithdrawStats withdrawStats;
    
    @Schema(description = "转化分析数据")
    private ConversionData conversionData;
    
    @Schema(description = "用户排行榜")
    private List<UserRankingItem> userRanking;
    
    @Schema(description = "地域分布")
    private List<RegionItem> regionData;
    
    @Schema(description = "趋势数据")
    private List<TrendItem> trendData;
    
    @Data
    public static class OverviewData {
        @Schema(description = "总邀请数")
        private Long totalInvites;
        
        @Schema(description = "注册用户数")
        private Long registeredUsers;
        
        @Schema(description = "首单用户数")
        private Long firstOrderUsers;
        
        @Schema(description = "总奖励数")
        private Long totalRewards;
    }
    
    @Data
    public static class RewardStats {
        @Schema(description = "总奖励数")
        private Long totalRewards;
        
        @Schema(description = "成功发放数")
        private Long successRewards;
        
        @Schema(description = "发放失败数")
        private Long failRewards;
        
        @Schema(description = "待发放数")
        private Long pendingRewards;
        
        @Schema(description = "奖励类型统计")
        private Map<String, Object> rewardTypeCount;
    }
    
    @Data
    public static class WithdrawStats {
        @Schema(description = "总申请数")
        private Long totalApplies;
        
        @Schema(description = "待审核数")
        private Long pendingApplies;
        
        @Schema(description = "已通过数")
        private Long approvedApplies;
        
        @Schema(description = "成功率")
        private Double successRate;
    }
    
    @Data
    public static class ConversionData {
        @Schema(description = "邀请到注册转化率")
        private String inviteToRegisterRate;
        
        @Schema(description = "注册到首单转化率")
        private String registerToOrderRate;
        
        @Schema(description = "总邀请数")
        private Long totalInvites;
        
        @Schema(description = "总注册数")
        private Long totalRegistered;
        
        @Schema(description = "总首单数")
        private Long totalOrdered;
    }
    
    @Data
    public static class UserRankingItem {
        @Schema(description = "排名")
        private Integer rank;
        
        @Schema(description = "昵称")
        private String nickname;
        
        @Schema(description = "邀请数")
        private Integer inviteCount;
        
        @Schema(description = "用户ID")
        private Long userId;
        
        @Schema(description = "奖励金额")
        private String rewardAmount;
    }
    
    @Data
    public static class RegionItem {
        @Schema(description = "地区")
        private String region;
        
        @Schema(description = "数量")
        private Integer count;
    }
    
    @Data
    public static class TrendItem {
        @Schema(description = "日期")
        private String date;
        
        @Schema(description = "数量")
        private Integer count;
    }
} 