package com.macro.mall.service;

import com.macro.mall.dto.InviteStatisticsDTO;

import java.util.List;
import java.util.Map;

/**
 * 邀请数据统计Service
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteStatisticsService {
    
    /**
     * 获取完整的邀请统计数据（统一接口）
     */
    InviteStatisticsDTO getAllStatistics(String startDate, String endDate, String trendType, Integer pageSize, Integer pageNum);
    
    /**
     * 刷新统计数据缓存
     */
    void refreshStatisticsCache();
    
    /**
     * 获取邀请数据总览
     */
    Map<String, Object> getOverviewData();
    
    /**
     * 获取邀请趋势数据
     */
    List<Map<String, Object>> getTrendData(String startDate, String endDate, String type);
    
    /**
     * 获取用户邀请排行榜
     */
    List<Map<String, Object>> getUserRanking(Integer pageSize, Integer pageNum);
    
    /**
     * 获取奖励发放统计
     */
    Map<String, Object> getRewardStatistics();
    
    /**
     * 获取提现申请统计
     */
    Map<String, Object> getWithdrawStatistics();
    
    /**
     * 获取转化率分析
     */
    Map<String, Object> getConversionAnalysis();
    
    /**
     * 获取地域分布统计
     */
    List<Map<String, Object>> getRegionDistribution();
} 