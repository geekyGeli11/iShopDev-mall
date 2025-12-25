package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.UmsPointsExchangeLogQueryParam;
import com.macro.mall.mapper.UmsPointsExchangeLogMapper;
import com.macro.mall.model.UmsPointsExchangeLog;
import com.macro.mall.model.UmsPointsExchangeLogExample;
import com.macro.mall.service.UmsPointsExchangeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分兑换记录Service实现类
 * Created by macro on 2024/01/20.
 */
@Service
public class UmsPointsExchangeLogServiceImpl implements UmsPointsExchangeLogService {
    
    @Autowired
    private UmsPointsExchangeLogMapper pointsExchangeLogMapper;
    
    @Override
    public List<UmsPointsExchangeLog> list(UmsPointsExchangeLogQueryParam queryParam, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsPointsExchangeLogExample example = createExample(queryParam);
        example.setOrderByClause("exchange_time desc");
        return pointsExchangeLogMapper.selectByExample(example);
    }
    
    @Override
    public UmsPointsExchangeLog getItem(Long id) {
        return pointsExchangeLogMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public Map<String, Object> getStatistics(UmsPointsExchangeLogQueryParam queryParam) {
        UmsPointsExchangeLogExample example = createExample(queryParam);
        List<UmsPointsExchangeLog> logs = pointsExchangeLogMapper.selectByExample(example);
        
        Map<String, Object> statistics = new HashMap<>();
        
        int totalCount = logs.size();
        int successCount = 0;
        int productExchangeCount = 0;
        int couponExchangeCount = 0;
        int totalPointsUsed = 0;
        BigDecimal totalCashAmount = BigDecimal.ZERO;
        
        for (UmsPointsExchangeLog log : logs) {
            if (log.getExchangeStatus() == 1) { // 成功状态
                successCount++;
            }
            if (log.getExchangeType() == 1) { // 商品换购
                productExchangeCount++;
            } else if (log.getExchangeType() == 2) { // 优惠券兑换
                couponExchangeCount++;
            }
            
            totalPointsUsed += log.getPointsUsed() != null ? log.getPointsUsed() : 0;
            if (log.getCashAmount() != null) {
                totalCashAmount = totalCashAmount.add(log.getCashAmount());
            }
        }
        
        statistics.put("totalCount", totalCount);
        statistics.put("successCount", successCount);
        statistics.put("successRate", totalCount > 0 ? (double) successCount / totalCount : 0.0);
        statistics.put("productExchangeCount", productExchangeCount);
        statistics.put("couponExchangeCount", couponExchangeCount);
        statistics.put("totalPointsUsed", totalPointsUsed);
        statistics.put("totalCashAmount", totalCashAmount);
        
        return statistics;
    }
    
    @Override
    public List<UmsPointsExchangeLog> getUserExchangeList(Long memberId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        UmsPointsExchangeLogExample example = new UmsPointsExchangeLogExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("exchange_time desc");
        return pointsExchangeLogMapper.selectByExample(example);
    }
    
    @Override
    public int getUserExchangedCount(Long memberId, Long configId, Byte exchangeType) {
        UmsPointsExchangeLogExample example = new UmsPointsExchangeLogExample();
        UmsPointsExchangeLogExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        criteria.andTargetIdEqualTo(configId);
        criteria.andExchangeTypeEqualTo(exchangeType);
        criteria.andExchangeStatusEqualTo((byte) 1); // 只统计成功的兑换
        
        List<UmsPointsExchangeLog> logs = pointsExchangeLogMapper.selectByExample(example);
        int totalCount = 0;
        for (UmsPointsExchangeLog log : logs) {
            totalCount += log.getQuantity() != null ? log.getQuantity() : 1;
        }
        return totalCount;
    }
    
    @Override
    public List<UmsPointsExchangeLog> exportList(UmsPointsExchangeLogQueryParam queryParam) {
        UmsPointsExchangeLogExample example = createExample(queryParam);
        example.setOrderByClause("exchange_time desc");
        return pointsExchangeLogMapper.selectByExample(example);
    }
    
    /**
     * 根据查询参数创建Example
     */
    private UmsPointsExchangeLogExample createExample(UmsPointsExchangeLogQueryParam queryParam) {
        UmsPointsExchangeLogExample example = new UmsPointsExchangeLogExample();
        UmsPointsExchangeLogExample.Criteria criteria = example.createCriteria();
        
        if (queryParam.getMemberId() != null) {
            criteria.andMemberIdEqualTo(queryParam.getMemberId());
        }
        if (!StringUtils.isEmpty(queryParam.getMemberUsername())) {
            criteria.andMemberUsernameLike("%" + queryParam.getMemberUsername() + "%");
        }
        if (queryParam.getExchangeType() != null) {
            criteria.andExchangeTypeEqualTo(queryParam.getExchangeType());
        }
        if (!StringUtils.isEmpty(queryParam.getTargetName())) {
            criteria.andTargetNameLike("%" + queryParam.getTargetName() + "%");
        }
        if (queryParam.getExchangeStatus() != null) {
            criteria.andExchangeStatusEqualTo(queryParam.getExchangeStatus());
        }
        if (queryParam.getStartDate() != null) {
            criteria.andExchangeTimeGreaterThanOrEqualTo(queryParam.getStartDate());
        }
        if (queryParam.getEndDate() != null) {
            criteria.andExchangeTimeLessThanOrEqualTo(queryParam.getEndDate());
        }
        if (queryParam.getMinPointsUsed() != null) {
            criteria.andPointsUsedGreaterThanOrEqualTo(queryParam.getMinPointsUsed());
        }
        if (queryParam.getMaxPointsUsed() != null) {
            criteria.andPointsUsedLessThanOrEqualTo(queryParam.getMaxPointsUsed());
        }
        
        return example;
    }
} 