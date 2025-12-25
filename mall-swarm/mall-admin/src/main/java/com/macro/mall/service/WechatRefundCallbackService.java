package com.macro.mall.service;

/**
 * 微信退款回调服务
 * Created by macro on 2024/01/01.
 */
public interface WechatRefundCallbackService {
    
    /**
     * 处理微信退款回调通知
     * @param xmlData 微信退款回调XML数据
     * @return 回调响应
     */
    String handleRefundNotify(String xmlData);
}
