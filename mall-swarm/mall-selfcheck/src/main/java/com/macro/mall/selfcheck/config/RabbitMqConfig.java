package com.macro.mall.selfcheck.config;

import com.macro.mall.selfcheck.domain.QueueEnum;
import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自助收银系统消息队列配置
 * Created by macro on 2024/01/01.
 */
@Configuration
@ConditionalOnProperty(value = "selfcheck.rabbitmq.enabled", havingValue = "true", matchIfMissing = true)
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
} 