package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.CommissionRecord;
import com.macro.mall.portal.domain.CommissionSummary;
import com.macro.mall.portal.domain.WithdrawApplyResult;
import com.macro.mall.portal.domain.WithdrawRecord;
import com.macro.mall.portal.dto.WithdrawApplyParam;

import java.util.List;
import java.util.Map;

/**
 * 佣金服务接口
 */
public interface CommissionService {
    
    /**
     * 获取我的佣金记录
     */
    List<CommissionRecord> getMyCommissionRecords(Long userId, Integer page, Integer size, 
                                                 String type, String startDate, String endDate);
    
    /**
     * 获取我的佣金汇总
     */
    CommissionSummary getMyCommissionSummary(Long userId);
    
    /**
     * 申请提现
     */
    WithdrawApplyResult applyWithdraw(Long userId, WithdrawApplyParam param);
    
    /**
     * 获取提现历史
     */
    List<WithdrawRecord> getWithdrawHistory(Long userId, Integer page, Integer size, 
                                           Integer status, String timeRange);
    
    /**
     * 获取提现统计
     */
    Map<String, Object> getWithdrawStats(Long userId);
    
    /**
     * 取消提现申请
     */
    Boolean cancelWithdraw(Long userId, Long withdrawId);
    
    /**
     * 计算并发放佣金
     */
    void calculateAndGrantCommission(Long orderId, Long customerId, Long distributorId, Integer level);
    
    /**
     * 更新佣金结算状态
     */
    void updateCommissionSettlement(List<Long> commissionIds, Integer status);
} 