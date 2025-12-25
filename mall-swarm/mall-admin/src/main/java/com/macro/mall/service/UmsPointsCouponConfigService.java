package com.macro.mall.service;

import com.macro.mall.dto.UmsPointsCouponConfigParam;
import com.macro.mall.model.UmsPointsCouponConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 积分换购优惠券配置Service
 * Created by macro on 2024/01/20.
 */
public interface UmsPointsCouponConfigService {
    
    /**
     * 创建积分换购优惠券配置
     */
    @Transactional
    int create(UmsPointsCouponConfigParam param);
    
    /**
     * 更新积分换购优惠券配置
     */
    @Transactional
    int update(Long id, UmsPointsCouponConfigParam param);
    
    /**
     * 删除积分换购优惠券配置
     */
    @Transactional
    int delete(Long id);
    
    /**
     * 批量删除积分换购优惠券配置
     */
    @Transactional
    int delete(List<Long> ids);
    
    /**
     * 分页查询积分换购优惠券配置
     */
    List<UmsPointsCouponConfig> list(String keyword, Byte status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取积分换购优惠券配置详情
     */
    UmsPointsCouponConfig getItem(Long id);
    
    /**
     * 批量修改启用状态
     */
    @Transactional
    int updateStatus(List<Long> ids, Byte status);
    
    /**
     * 更新发放数量
     */
    @Transactional
    int updateCount(Long id, Integer totalCount);
    
    /**
     * 减少可领数量（兑换时调用）
     */
    @Transactional
    int reduceCount(Long id, Integer quantity);
    
    /**
     * 获取所有启用的换购优惠券
     */
    List<UmsPointsCouponConfig> listAllAvailable();
    
    /**
     * 根据优惠券ID获取换购配置
     */
    UmsPointsCouponConfig getByCouponId(Long couponId);
} 