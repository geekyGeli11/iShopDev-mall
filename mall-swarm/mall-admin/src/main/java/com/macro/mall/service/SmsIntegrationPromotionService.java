package com.macro.mall.service;

import com.macro.mall.model.SmsIntegrationPromotion;

import java.util.List;

/**
 * 积分营销活动管理Service
 * Created by macro on 2024/12/20.
 */
public interface SmsIntegrationPromotionService {
    
    /**
     * 创建积分营销活动
     */
    int create(SmsIntegrationPromotion integrationPromotion);

    /**
     * 修改积分营销活动
     */
    int update(Long id, SmsIntegrationPromotion integrationPromotion);

    /**
     * 删除积分营销活动
     */
    int delete(Long id);

    /**
     * 批量删除积分营销活动
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID获取积分营销活动详情
     */
    SmsIntegrationPromotion getItem(Long id);

    /**
     * 分页查询积分营销活动
     */
    List<SmsIntegrationPromotion> list(String name, Boolean status, Integer pageNum, Integer pageSize);

    /**
     * 修改积分营销活动状态
     */
    int updateStatus(Long id, Boolean status);

    /**
     * 获取所有启用的积分营销活动
     */
    List<SmsIntegrationPromotion> listEnabled();

    /**
     * 获取当前生效的积分营销活动（按优先级排序）
     */
    List<SmsIntegrationPromotion> getCurrentActive();

    /**
     * 根据订单信息获取适用的积分营销活动
     * @param orderAmount 订单金额
     * @param productIds 商品ID列表
     * @param categoryIds 分类ID列表
     * @return 适用的积分营销活动（按优先级排序，取第一个）
     */
    SmsIntegrationPromotion getApplicablePromotion(java.math.BigDecimal orderAmount, List<Long> productIds, List<Long> categoryIds);
}
