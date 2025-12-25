package com.macro.mall.selfcheck.component;

import com.macro.mall.selfcheck.service.SelfcheckOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 自助收银系统支付状态查询消息接收者
 * Created by macro on 2024/01/01.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "selfcheck.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
@RabbitListener(queues = "selfcheck.pay.check")
public class PayCheckReceiver {
    
    @Autowired
    private SelfcheckOrderService orderService;

    /**
     * 处理支付状态查询消息
     * @param orderId 订单ID
     */
    @RabbitHandler
    public void handle(Long orderId) {
        try {
            log.info("接收到支付状态查询消息: orderId={}", orderId);
            
            // 调用订单服务检查支付状态
            orderService.checkPaymentStatus(orderId);
            
        } catch (Exception e) {
            log.error("处理支付状态查询消息失败: orderId={}", orderId, e);
        }
    }
} 