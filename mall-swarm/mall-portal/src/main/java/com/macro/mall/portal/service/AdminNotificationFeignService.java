package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dto.NewOrderNotificationParam;
import com.macro.mall.portal.dto.RefundNotificationParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 管理员通知Feign服务
 * 用于调用mall-admin的通知接口
 * 
 * 注意：通知发送失败不应影响主业务流程，调用方需要用 try-catch 包裹
 */
@FeignClient(value = "mall-admin")
public interface AdminNotificationFeignService {
    
    /**
     * 发送新订单通知
     */
    @PostMapping("/notification/newOrder")
    CommonResult<Void> notifyNewOrder(@RequestBody NewOrderNotificationParam param);
    
    /**
     * 发送退款申请通知
     */
    @PostMapping("/notification/refundApplication")
    CommonResult<Void> notifyRefundApplication(@RequestBody RefundNotificationParam param);
}
