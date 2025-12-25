package com.macro.mall.selfcheck.service;

import com.macro.mall.selfcheck.dto.BundleMatchResult;
import com.macro.mall.selfcheck.dto.OrderItemParam;

import java.util.List;

/**
 * 自助收银组合优惠匹配服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckBundleMatchService {

    /**
     * 检测订单商品是否满足组合优惠条件
     * 
     * @param orderItems 订单商品列表
     * @param schoolId 学校ID（用于筛选适用的组合）
     * @return 组合优惠匹配结果
     */
    BundleMatchResult matchBundlePromotion(List<OrderItemParam> orderItems, Long schoolId);

    /**
     * 检测订单商品是否满足多个组合优惠条件（支持多组合叠加）
     * 
     * @param orderItems 订单商品列表
     * @param schoolId 学校ID
     * @return 所有匹配的组合优惠结果列表
     */
    List<BundleMatchResult> matchAllBundlePromotions(List<OrderItemParam> orderItems, Long schoolId);
}
