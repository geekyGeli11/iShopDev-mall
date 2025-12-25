package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 邀请奖励管理自定义Dao
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteRewardDao {
    
    /**
     * 分页获取奖励记录列表（包含用户信息）
     */
    List<Map<String, Object>> getInviteRewardsList(@Param("userId") Long userId,
                                                   @Param("userType") Integer userType,
                                                   @Param("rewardType") Integer rewardType,
                                                   @Param("triggerType") Integer triggerType,
                                                   @Param("status") Integer status,
                                                   @Param("commissionType") Integer commissionType,
                                                   @Param("startTime") String startTime,
                                                   @Param("endTime") String endTime);

    /**
     * 统计奖励记录数量
     */
    long countInviteRewardsList(@Param("userId") Long userId,
                                @Param("userType") Integer userType,
                                @Param("rewardType") Integer rewardType,
                                @Param("triggerType") Integer triggerType,
                                @Param("status") Integer status,
                                @Param("commissionType") Integer commissionType,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime);
    
    /**
     * 获取奖励记录详情（包含用户信息）
     */
    Map<String, Object> getInviteRewardDetail(@Param("id") Long id);
    
    /**
     * 获取奖励发放汇总统计
     */
    Map<String, Object> getRewardSummaryStats();
    
    /**
     * 获取不同佣金类型的统计数据
     */
    Map<String, Object> getCommissionStatistics(@Param("startTime") String startTime,
                                                @Param("endTime") String endTime);
    
    /**
     * 获取佣金趋势数据
     */
    List<Map<String, Object>> getCommissionTrend(@Param("commissionType") Integer commissionType,
                                                 @Param("startTime") String startTime,
                                                 @Param("endTime") String endTime,
                                                 @Param("granularity") String granularity);
} 