package com.macro.mall.selfcheck.component;

import com.macro.mall.selfcheck.domain.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 自助收银系统支付状态查询消息发送者
 * Created by macro on 2024/01/01.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "selfcheck.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class PayCheckSender {
    
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送延迟消息，用于检查支付状态
     * @param orderId 订单ID
     * @param delayTimes 延迟时间（毫秒）
     */
    public void sendDelayMessageCheckPayStatus(Long orderId, final long delayTimes) {
        try {
            //发送延迟消息
            amqpTemplate.convertAndSend(
                QueueEnum.QUEUE_TTL_PAY_CHECK.getExchange(), 
                QueueEnum.QUEUE_TTL_PAY_CHECK.getRouteKey(), 
                orderId, 
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //设置延迟时间，单位为毫秒
                        message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                        return message;
                    }
                }
            );
            log.info("发送支付状态查询延迟消息: orderId={}, delayTimes={}ms", orderId, delayTimes);
        } catch (Exception e) {
            log.error("发送支付状态查询延迟消息失败: orderId={}, delayTimes={}ms", orderId, delayTimes, e);
        }
    }

    /**
     * 发送延迟消息检查支付状态（默认5分钟）
     * @param orderId 订单ID
     */
    public void sendDelayMessageCheckPayStatus(Long orderId) {
        // 默认5分钟后检查支付状态
        long defaultDelayTime = 5 * 60 * 1000L;
        sendDelayMessageCheckPayStatus(orderId, defaultDelayTime);
    }
} 