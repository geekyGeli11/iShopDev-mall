package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 邀请提现申请管理自定义Dao
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteWithdrawDao {
    
    /**
     * 分页获取提现申请列表（包含用户信息）
     */
    List<Map<String, Object>> getInviteWithdrawsList(@Param("userId") Long userId,
                                                     @Param("status") Integer status,
                                                     @Param("sourceType") Integer sourceType,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime);
    
    /**
     * 获取提现申请详情（包含用户信息）
     */
    Map<String, Object> getInviteWithdrawDetail(@Param("id") Long id);
    
    /**
     * 获取提现申请的资金构成明细
     */
    Map<String, Object> getWithdrawComposition(@Param("id") Long id);
    
    /**
     * 获取提现申请汇总统计
     */
    Map<String, Object> getWithdrawSummaryStats();
    
    /**
     * 获取提现资金来源统计
     */
    Map<String, Object> getSourceStatistics(@Param("startTime") String startTime,
                                           @Param("endTime") String endTime);
    
    /**
     * 获取用户可提现金额详情
     */
    Map<String, Object> getUserAvailableAmount(@Param("userId") Long userId);
    
    /**
     * 获取提现趋势分析数据
     */
    List<Map<String, Object>> getWithdrawTrend(@Param("sourceType") Integer sourceType,
                                              @Param("startTime") String startTime,
                                              @Param("endTime") String endTime,
                                              @Param("granularity") String granularity);

    /**
     * 统计提现申请记录数量
     */
    long countInviteWithdrawList(@Param("userId") Long userId,
                                @Param("status") Integer status,
                                @Param("sourceType") Integer sourceType,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime);
} 