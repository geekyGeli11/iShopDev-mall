package com.macro.mall.service;

import com.macro.mall.dto.WechatRefundParam;
import com.macro.mall.dto.WechatRefundResult;

/**
 * 微信支付退款服务
 * Created by macro on 2024/01/01.
 */
public interface WechatRefundService {
    
    /**
     * 处理微信支付退款
     * @param refundParam 退款参数
     * @return 退款结果
     */
    WechatRefundResult processRefund(WechatRefundParam refundParam);
    
    /**
     * 查询退款状态
     * @param refundSn 退款单号
     * @return 退款结果
     */
    WechatRefundResult queryRefund(String refundSn);
} 