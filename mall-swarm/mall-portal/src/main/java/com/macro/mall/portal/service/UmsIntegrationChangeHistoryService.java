package com.macro.mall.portal.service;

import com.macro.mall.model.UmsIntegrationChangeHistory;

import java.util.List;

/**
 * 用户积分变动历史记录Service
 */
public interface UmsIntegrationChangeHistoryService {
    
    /**
     * 获取当前用户的积分变动历史记录
     * @param type 变动类型：0->全部；1->获取；2->消耗；3->近三月
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @return 积分变动历史记录
     */
    List<UmsIntegrationChangeHistory> list(Integer type, Integer pageNum, Integer pageSize);
    
    /**
     * 根据ID获取积分变动历史详情
     */
    UmsIntegrationChangeHistory getItem(Long id);
    
    /**
     * 获取指定会员的积分变动记录
     */
    List<UmsIntegrationChangeHistory> listByMemberId(Long memberId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取当前用户积分总数
     * @return 积分总数
     */
    Integer getCurrentIntegration();
} 