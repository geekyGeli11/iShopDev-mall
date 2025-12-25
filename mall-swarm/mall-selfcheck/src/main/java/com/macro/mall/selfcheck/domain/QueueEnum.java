package com.macro.mall.selfcheck.domain;

import lombok.Getter;

/**
 * 自助收银系统消息队列枚举配置
 * Created by macro on 2024/01/01.
 */
@Getter
public enum QueueEnum {
    /**
     * 订单取消队列
     */
    QUEUE_ORDER_CANCEL("selfcheck.order.direct", "selfcheck.order.cancel", "selfcheck.order.cancel"),
    /**
     * 订单取消TTL队列
     */
    QUEUE_TTL_ORDER_CANCEL("selfcheck.order.direct.ttl", "selfcheck.order.cancel.ttl", "selfcheck.order.cancel.ttl"),
    /**
     * 支付状态查询队列
     */
    QUEUE_PAY_CHECK("selfcheck.pay.direct", "selfcheck.pay.check", "selfcheck.pay.check"),
    /**
     * 支付状态查询TTL队列
     */
    QUEUE_TTL_PAY_CHECK("selfcheck.pay.direct.ttl", "selfcheck.pay.check.ttl", "selfcheck.pay.check.ttl");

    /**
     * 交换机名称
     */
    private final String exchange;
    /**
     * 队列名称
     */
    private final String name;
    /**
     * 路由键
     */
    private final String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
} 