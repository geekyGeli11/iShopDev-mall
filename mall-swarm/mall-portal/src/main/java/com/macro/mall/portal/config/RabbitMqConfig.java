package com.macro.mall.portal.config;

import com.macro.mall.portal.domain.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 * Created by macro on 2018/9/14.
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 支付状态查询消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange payDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_PAY_CHECK.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 支付状态查询延迟队列所绑定的交换机
     */
    @Bean
    DirectExchange payTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_PAY_CHECK.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 支付状态查询实际消费队列
     */
    @Bean
    public Queue payCheckQueue() {
        return new Queue(QueueEnum.QUEUE_PAY_CHECK.getName());
    }

    /**
     * 支付状态查询延迟队列（死信队列）
     */
    @Bean
    public Queue payCheckTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_PAY_CHECK.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_PAY_CHECK.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_PAY_CHECK.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect, Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect, Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将支付状态查询队列绑定到交换机
     */
    @Bean
    Binding payCheckBinding(DirectExchange payDirect, Queue payCheckQueue){
        return BindingBuilder
                .bind(payCheckQueue)
                .to(payDirect)
                .with(QueueEnum.QUEUE_PAY_CHECK.getRouteKey());
    }

    /**
     * 将支付状态查询延迟队列绑定到交换机
     */
    @Bean
    Binding payCheckTtlBinding(DirectExchange payTtlDirect, Queue payCheckTtlQueue){
        return BindingBuilder
                .bind(payCheckTtlQueue)
                .to(payTtlDirect)
                .with(QueueEnum.QUEUE_TTL_PAY_CHECK.getRouteKey());
    }

    /**
     * 礼物过期提醒消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange giftExpireRemindDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_GIFT_EXPIRE_REMIND.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 礼物过期提醒延迟队列所绑定的交换机
     */
    @Bean
    DirectExchange giftExpireRemindTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_GIFT_EXPIRE_REMIND.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 礼物过期提醒实际消费队列
     */
    @Bean
    public Queue giftExpireRemindQueue() {
        return new Queue(QueueEnum.QUEUE_GIFT_EXPIRE_REMIND.getName());
    }

    /**
     * 礼物过期提醒延迟队列（死信队列）
     */
    @Bean
    public Queue giftExpireRemindTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_GIFT_EXPIRE_REMIND.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_GIFT_EXPIRE_REMIND.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_GIFT_EXPIRE_REMIND.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 将礼物过期提醒队列绑定到交换机
     */
    @Bean
    Binding giftExpireRemindBinding(DirectExchange giftExpireRemindDirect, Queue giftExpireRemindQueue){
        return BindingBuilder
                .bind(giftExpireRemindQueue)
                .to(giftExpireRemindDirect)
                .with(QueueEnum.QUEUE_GIFT_EXPIRE_REMIND.getRouteKey());
    }

    /**
     * 将礼物过期提醒延迟队列绑定到交换机
     */
    @Bean
    Binding giftExpireRemindTtlBinding(DirectExchange giftExpireRemindTtlDirect, Queue giftExpireRemindTtlQueue){
        return BindingBuilder
                .bind(giftExpireRemindTtlQueue)
                .to(giftExpireRemindTtlDirect)
                .with(QueueEnum.QUEUE_TTL_GIFT_EXPIRE_REMIND.getRouteKey());
    }

}
