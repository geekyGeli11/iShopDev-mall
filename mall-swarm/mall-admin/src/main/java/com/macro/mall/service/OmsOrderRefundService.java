package com.macro.mall.service;

import com.macro.mall.dto.OmsOrderRefundParam;
import com.macro.mall.dto.OmsOrderRefundResult;

/**
 * 订单退款服务接口
 * 支持自助订单的微信支付、支付宝支付、余额支付退款
 * Created by macro on 2024/01/01.
 */
public interface OmsOrderRefundService {
    
    /**
     * 处理订单退款
     * 根据订单的支付方式自动选择退款渠道
     * 
     * @param refundParam 退款参数
     * @return 退款结果
     */
    OmsOrderRefundResult processRefund(OmsOrderRefundParam refundParam);
    
    /**
     * 查询退款状态
     * 
     * @param orderId 订单ID
     * @return 退款结果
     */
    OmsOrderRefundResult queryRefundStatus(Long orderId);
    
    /**
     * 检查订单是否可以退款
     * 
     * @param orderId 订单ID
     * @return 是否可以退款
     */
    boolean canRefund(Long orderId);
}
