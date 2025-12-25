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
 * 自助收银系统取消订单消息的发送者
 * Created by macro on 2024/01/01.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "selfcheck.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
public class CancelOrderSender {
    
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送延迟取消订单消息
     * @param orderId 订单ID
     * @param delayTimes 延迟时间（毫秒）
     */
    public void sendMessage(Long orderId, final long delayTimes) {
        try {
            //给延迟队列发送消息
            amqpTemplate.convertAndSend(
                QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange(), 
                QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey(), 
                orderId, 
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //给消息设置延迟毫秒值
                        message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                        return message;
                    }
                }
            );
            log.info("发送延迟取消订单消息: orderId={}, delayTimes={}ms", orderId, delayTimes);
        } catch (Exception e) {
            log.error("发送延迟取消订单消息失败: orderId={}, delayTimes={}ms", orderId, delayTimes, e);
        }
    }

    /**
     * 发送延迟取消订单消息（默认30分钟）
     * @param orderId 订单ID
     */
    public void sendMessage(Long orderId) {
        // 默认30分钟后取消订单
        long defaultDelayTime = 30 * 60 * 1000L;
        sendMessage(orderId, defaultDelayTime);
    }
} 