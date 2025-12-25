package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.DistributionStatsResult;
import com.macro.mall.portal.domain.DistributorApplyResult;
import com.macro.mall.portal.domain.CommissionRecord;
import com.macro.mall.portal.domain.CustomerRecord;
import com.macro.mall.portal.dto.DistributorApplyParam;

import java.util.List;
import java.util.Map;

/**
 * 分销功能业务Service
 */
public interface DistributionService {
    
    /**
     * 获取分销商申请状态
     */
    DistributorApplyResult getDistributorApplyStatus(Long memberId);
    
    /**
     * 提交分销商申请
     */
    Boolean submitDistributorApply(Long memberId, DistributorApplyParam param);
    
    /**
     * 获取分销商申请详情
     */
    Map<String, Object> getDistributorApplyDetail(Long memberId);
    
    /**
     * 检查申请资格
     */
    Map<String, Object> checkApplyEligibility(Long memberId);

    /**
     * 补充申请信息
     */
    Boolean supplementApplyInfo(Long applyId, String workScreenshots);

    /**
     * 获取分销商申请要求
     */
    Map<String, Object> getDistributorRequirements();
    
    /**
     * 检查是否为分销商
     */
    Boolean isDistributor(Long memberId);
    
    /**
     * 生成分销码
     */
    String generateDistributionCode(Long memberId);
    
    /**
     * 获取我的分销码
     */
    String getMyDistributionCode(Long memberId);
    
    /**
     * 获取我的分销统计
     */
    DistributionStatsResult getMyDistributionStats(Long memberId);
    
    /**
     * 获取我的客户列表
     */
    Map<String, Object> getMyCustomers(Long memberId, Integer page, Integer size, String level);
    
    /**
     * 获取我的客户统计
     */
    Map<String, Object> getMyCustomerStats(Long memberId);
    
    /**
     * 获取我的佣金记录
     */
    Map<String, Object> getMyCommissionRecords(Long memberId, Integer page, Integer size, String filter);
    
    /**
     * 获取我的佣金汇总
     */
    Map<String, Object> getMyCommissionSummary(Long memberId, String period);
    
    /**
     * 绑定分销关系
     */
    Boolean bindDistributionRelation(String distributionCode, Long memberId);
    
    /**
     * 处理订单完成后的佣金计算
     */
    void processOrderCommission(Long orderId);
    
    /**
     * 更新分销统计数据
     */
    void updateDistributionStats(Long userId);
} 