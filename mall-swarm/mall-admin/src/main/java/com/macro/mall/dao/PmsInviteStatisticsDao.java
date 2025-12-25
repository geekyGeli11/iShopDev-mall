package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 邀请数据统计自定义Dao
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteStatisticsDao {
    
    /**
     * 获取邀请趋势数据
     */
    List<Map<String, Object>> getInviteTrendData(@Param("startDate") String startDate,
                                                 @Param("endDate") String endDate,
                                                 @Param("type") String type);
    
    /**
     * 获取用户邀请排行榜
     */
    List<Map<String, Object>> getUserInviteRanking(@Param("pageSize") Integer pageSize,
                                                   @Param("pageNum") Integer pageNum);
    
    /**
     * 获取转化率分析数据
     */
    Map<String, Object> getConversionAnalysisData();
    
    /**
     * 获取地域分布统计数据
     */
    List<Map<String, Object>> getRegionDistributionData();
    
    /**
     * 获取提现金额统计数据
     */
    Map<String, Object> getWithdrawAmountStatistics();
} 