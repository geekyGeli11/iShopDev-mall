package com.macro.mall.service;

import com.macro.mall.dto.UmsPointsExchangeLogQueryParam;
import com.macro.mall.model.UmsPointsExchangeLog;

import java.util.List;
import java.util.Map;

/**
 * 积分兑换记录Service
 * Created by macro on 2024/01/20.
 */
public interface UmsPointsExchangeLogService {
    
    /**
     * 分页查询兑换记录
     */
    List<UmsPointsExchangeLog> list(UmsPointsExchangeLogQueryParam queryParam, Integer pageNum, Integer pageSize);
    
    /**
     * 获取兑换记录详情
     */
    UmsPointsExchangeLog getItem(Long id);
    
    /**
     * 获取兑换统计数据
     */
    Map<String, Object> getStatistics(UmsPointsExchangeLogQueryParam queryParam);
    
    /**
     * 获取用户兑换记录
     */
    List<UmsPointsExchangeLog> getUserExchangeList(Long memberId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户在指定配置下的兑换次数
     */
    int getUserExchangedCount(Long memberId, Long configId, Byte exchangeType);
    
    /**
     * 导出兑换记录
     */
    List<UmsPointsExchangeLog> exportList(UmsPointsExchangeLogQueryParam queryParam);
} 