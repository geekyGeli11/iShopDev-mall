package com.macro.mall.service;

import java.util.List;
import java.util.Map;

/**
 * 邀请奖励记录管理Service
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteRewardService {
    
    /**
     * 分页获取奖励记录列表
     */
    List<Map<String, Object>> list(Long userId, Integer userType, Integer rewardType,
                                   Integer triggerType, Integer status, Integer commissionType, 
                                   String startTime, String endTime,
                                   Integer pageSize, Integer pageNum);
    
    /**
     * 获取奖励记录详情
     */
    Map<String, Object> getDetail(Long id);
    
    /**
     * 更新奖励状态
     */
    int updateStatus(Long id, Integer status, String remark);
    
    /**
     * 重试发放失败的奖励
     */
    int retrySendReward(Long id);
    
    /**
     * 批量发放奖励
     */
    int batchSendReward(List<Long> ids);
    
    /**
     * 导出奖励记录（不分页）
     */
    List<Map<String, Object>> exportRewardRecords(Long userId, Integer userType, Integer rewardType,
                                                   Integer triggerType, Integer status, Integer commissionType,
                                                   String startTime, String endTime);

    /**
     * 导出奖励记录（分页）
     */
    List<Map<String, Object>> exportRewardRecords(Long userId, Integer userType, Integer rewardType,
                                                   Integer triggerType, Integer status, Integer commissionType,
                                                   String startTime, String endTime, Integer pageSize, Integer pageNum);

    /**
     * 统计奖励记录数量
     */
    long countRewardRecords(Long userId, Integer userType, Integer rewardType,
                           Integer triggerType, Integer status, Integer commissionType,
                           String startTime, String endTime);
    
    /**
     * 获取奖励发放汇总
     */
    Map<String, Object> getRewardSummary();
    
    /**
     * 获取不同佣金类型的统计数据
     */
    Map<String, Object> getCommissionStatistics(String startTime, String endTime);
    
    /**
     * 获取佣金趋势数据
     */
    List<Map<String, Object>> getCommissionTrend(Integer commissionType, String startTime, 
                                                String endTime, String granularity);
}