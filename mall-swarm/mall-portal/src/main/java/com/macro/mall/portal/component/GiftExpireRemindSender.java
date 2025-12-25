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
 * 礼物过期提醒消息发送者
 */
@Component
public class GiftExpireRemindSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiftExpireRemindSender.class);
    
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送礼物过期提醒延迟消息
     * @param giftRecordId 礼物记录ID
     * @param delayTimes 延迟时间（毫秒）
     */
    public void sendGiftExpireRemindMessage(Long giftRecordId, final long delayTimes) {
        try {
            //发送延迟消息到TTL队列
            amqpTemplate.convertAndSend(
                QueueEnum.QUEUE_TTL_GIFT_EXPIRE_REMIND.getExchange(), 
                QueueEnum.QUEUE_TTL_GIFT_EXPIRE_REMIND.getRouteKey(), 
                giftRecordId, 
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //设置延迟时间，单位为毫秒
                        message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                        return message;
                    }
                }
            );
            LOGGER.info("发送礼物过期提醒延迟消息成功，礼物记录ID：{}，延迟时间：{}ms", giftRecordId, delayTimes);
        } catch (Exception e) {
            LOGGER.error("发送礼物过期提醒延迟消息失败，礼物记录ID：{}，延迟时间：{}ms", giftRecordId, delayTimes, e);
        }
    }

    /**
     * 发送礼物过期提醒延迟消息（默认24小时）
     * @param giftRecordId 礼物记录ID
     */
    public void sendGiftExpireRemindMessage(Long giftRecordId) {
        // 默认24小时后发送提醒
        long defaultDelayTime = 24 * 60 * 60 * 1000L;
        sendGiftExpireRemindMessage(giftRecordId, defaultDelayTime);
    }
} 