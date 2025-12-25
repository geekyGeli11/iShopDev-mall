package com.macro.mall.portal.service;

/**
 * 商品回本分析服务（Portal模块版本）
 * 专注于处理订单支付成功和退款时的回本分析数据更新
 * Created by guanghengzhou on 2024/01/01.
 */
public interface PmsProductPaybackService {

    /**
     * 订单支付成功时更新回本分析数据
     * 在订单支付完成后调用，更新商品的销售数据和回本进度
     * 
     * @param orderId 订单ID
     * @param orderSn 订单编号
     * @return 操作结果：1-成功，0-失败
     */
    int updateOnPaymentSuccess(Long orderId, String orderSn);

    /**
     * 订单退款时更新回本分析数据
     * 在订单退款完成后调用，减少商品的销售数据并重新计算回本进度
     * 
     * @param orderId 订单ID
     * @param orderSn 订单编号
     * @return 操作结果：1-成功，0-失败
     */
    int updateOnRefund(Long orderId, String orderSn);
} 