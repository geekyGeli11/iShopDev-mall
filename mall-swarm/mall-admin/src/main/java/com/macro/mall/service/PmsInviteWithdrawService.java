package com.macro.mall.service;

import java.util.List;
import java.util.Map;

/**
 * 邀请提现申请管理Service
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteWithdrawService {
    
    /**
     * 分页获取提现申请列表
     */
    List<Map<String, Object>> list(Long userId, Integer status, Integer sourceType,
                                   String startTime, String endTime,
                                   Integer pageSize, Integer pageNum);
    
    /**
     * 获取提现申请详情
     */
    Map<String, Object> getDetail(Long id);
    
    /**
     * 获取提现申请的资金构成明细
     */
    Map<String, Object> getWithdrawComposition(Long id);
    
    /**
     * 审批提现申请
     */
    int approve(Long id, Integer status, String remark);
    
    /**
     * 批量审批提现申请
     */
    int batchApprove(List<Long> ids, Integer status, String remark);
    
    /**
     * 导出提现申请记录（分页）
     */
    List<Map<String, Object>> exportWithdrawRecords(Long userId, Integer status, Integer sourceType,
                                                     String startTime, String endTime, Integer pageSize, Integer pageNum);

    /**
     * 统计提现申请记录数量
     */
    long countWithdrawRecords(Long userId, Integer status, Integer sourceType,
                             String startTime, String endTime);
    
    /**
     * 获取提现申请汇总
     */
    Map<String, Object> getWithdrawSummary();
    
    /**
     * 获取提现资金来源统计
     */
    Map<String, Object> getSourceStatistics(String startTime, String endTime);
    
    /**
     * 获取用户可提现金额详情
     */
    Map<String, Object> getUserAvailableAmount(Long userId);
    
    /**
     * 获取提现趋势分析
     */
    List<Map<String, Object>> getWithdrawTrend(Integer sourceType, String startTime, 
                                               String endTime, String granularity);
    
    /**
     * 获取提现配置
     */
    Map<String, Object> getWithdrawConfig();
    
    /**
     * 更新提现配置
     */
    int updateWithdrawConfig(Map<String, Object> configData);
} 