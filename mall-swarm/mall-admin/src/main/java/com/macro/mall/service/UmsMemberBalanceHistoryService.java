package com.macro.mall.service;

import com.macro.mall.model.UmsMemberBalanceHistory;

import java.util.List;

/**
 * 用户余额记录管理Service
 */
public interface UmsMemberBalanceHistoryService {

    /**
     * 获取用户充值记录
     */
    List<UmsMemberBalanceHistory> getRechargeHistory(Long memberId, Integer status, String startDate, String endDate, Integer pageSize, Integer pageNum);

    /**
     * 获取用户消费记录
     */
    List<UmsMemberBalanceHistory> getConsumptionHistory(Long memberId, String businessType, Integer changeType, String startDate, String endDate, Integer pageSize, Integer pageNum);
} 