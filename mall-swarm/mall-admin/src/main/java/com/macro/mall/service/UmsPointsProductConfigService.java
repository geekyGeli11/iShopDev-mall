package com.macro.mall.service;

import com.macro.mall.dto.UmsPointsProductConfigParam;
import com.macro.mall.model.UmsPointsProductConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 积分换购商品配置Service
 * Created by macro on 2024/01/20.
 */
public interface UmsPointsProductConfigService {
    
    /**
     * 创建积分换购商品配置
     */
    @Transactional
    int create(UmsPointsProductConfigParam param);
    
    /**
     * 更新积分换购商品配置
     */
    @Transactional
    int update(Long id, UmsPointsProductConfigParam param);
    
    /**
     * 删除积分换购商品配置
     */
    @Transactional
    int delete(Long id);
    
    /**
     * 批量删除积分换购商品配置
     */
    @Transactional
    int delete(List<Long> ids);
    
    /**
     * 分页查询积分换购商品配置
     */
    List<UmsPointsProductConfig> list(String keyword, Byte status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取积分换购商品配置详情
     */
    UmsPointsProductConfig getItem(Long id);
    
    /**
     * 批量修改上架状态
     */
    @Transactional
    int updateStatus(List<Long> ids, Byte status);
    
    /**
     * 更新库存
     */
    @Transactional
    int updateStock(Long id, Integer totalStock);
    
    /**
     * 减少库存（兑换时调用）
     */
    @Transactional
    int reduceStock(Long id, Integer quantity);
    
    /**
     * 获取所有上架的换购商品
     */
    List<UmsPointsProductConfig> listAllAvailable();
    
    /**
     * 根据商品ID获取换购配置
     */
    UmsPointsProductConfig getByProductId(Long productId);
} 