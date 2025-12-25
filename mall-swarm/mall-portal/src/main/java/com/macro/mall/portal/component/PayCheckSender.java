package com.macro.mall.portal.component;

import com.macro.mall.portal.domain.QueueEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 支付状态查询消息发送者
 */
@Component
public class PayCheckSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayCheckSender.class);
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送延迟消息，用于检查支付状态
     * @param orderSn 订单编号
     * @param delayTimes 延迟时间（毫秒）
     */
    public void sendDelayMessageCheckPayStatus(String orderSn, final long delayTimes) {
        //发送延迟消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_TTL_PAY_CHECK.getExchange(), QueueEnum.QUEUE_TTL_PAY_CHECK.getRouteKey(), orderSn, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //设置延迟时间，单位为毫秒
                message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                return message;
            }
        });
        LOGGER.info("发送支付状态查询延迟消息 orderSn:{}", orderSn);
    }
} 