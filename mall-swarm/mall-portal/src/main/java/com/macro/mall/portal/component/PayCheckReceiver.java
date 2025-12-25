package com.macro.mall.portal.component;

import com.macro.mall.portal.service.OmsPortalOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 支付状态查询消息监听者
 * Created by macro on 2023/12/8.
 */
@Component
@RabbitListener(queues = "mall.pay.check")
public class PayCheckReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayCheckReceiver.class);
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @RabbitHandler
    public void handle(String orderSn){
        LOGGER.info("接收到支付状态查询消息：{}",orderSn);
        portalOrderService.checkPayStatus(orderSn);
    }
} 