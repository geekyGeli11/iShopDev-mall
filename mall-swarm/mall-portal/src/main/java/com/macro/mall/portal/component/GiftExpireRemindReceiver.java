package com.macro.mall.portal.component;

import com.macro.mall.portal.service.OmsGiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 礼物过期提醒消息接收者
 */
@Component
@RabbitListener(queues = "mall.gift.expire.remind")
public class GiftExpireRemindReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GiftExpireRemindReceiver.class);
    
    @Autowired
    private OmsGiftService giftService;
    
    /**
     * 处理礼物过期提醒消息
     * @param giftRecordId 礼物记录ID
     */
    @RabbitHandler
    public void handle(Long giftRecordId) {
        try {
            LOGGER.info("接收到礼物过期提醒消息，礼物记录ID：{}", giftRecordId);
            
            // 调用礼物服务处理过期提醒
            giftService.handleGiftExpireRemind(giftRecordId);
            
            LOGGER.info("礼物过期提醒处理成功，礼物记录ID：{}", giftRecordId);
        } catch (Exception e) {
            LOGGER.error("处理礼物过期提醒消息失败，礼物记录ID：{}", giftRecordId, e);
        }
    }
} 