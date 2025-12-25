package com.macro.mall.selfcheck.component;

import com.macro.mall.selfcheck.service.SelfcheckOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 自助收银系统取消订单消息的接收者
 * Created by macro on 2024/01/01.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "selfcheck.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RabbitListener(queues = "selfcheck.order.cancel")
public class CancelOrderReceiver {
    
    @Autowired
    private SelfcheckOrderService orderService;
    
    /**
     * 处理延迟取消订单消息
     * @param orderId 订单ID
     */
    @RabbitHandler
    public void handle(Long orderId) {
        try {
            log.info("接收到延迟取消订单消息: orderId={}", orderId);
            
            // 调用订单服务处理超时取消逻辑
            boolean success = orderService.cancelTimeoutOrder(orderId);
            
            if (success) {
                log.info("延迟取消订单成功: orderId={}", orderId);
            } else {
                log.warn("延迟取消订单失败或订单已处理: orderId={}", orderId);
            }
            
        } catch (Exception e) {
            log.error("处理延迟取消订单消息失败: orderId={}", orderId, e);
        }
    }
} 