package com.macro.mall.portal.domain;

import lombok.Getter;

/**
 * 消息队列枚举配置
 * Created by macro on 2018/9/14.
 */
@Getter
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.order.cancel", "mall.order.cancel"),
    /**
     * 消息通知ttl队列
     */
    QUEUE_TTL_ORDER_CANCEL("mall.order.direct.ttl", "mall.order.cancel.ttl", "mall.order.cancel.ttl"),
    /**
     * 支付状态查询队列
     */
    QUEUE_PAY_CHECK("mall.pay.direct", "mall.pay.check", "mall.pay.check"),
    /**
     * 支付状态查询ttl队列
     */
    QUEUE_TTL_PAY_CHECK("mall.pay.direct.ttl", "mall.pay.check.ttl", "mall.pay.check.ttl"),
    /**
     * 礼物过期提醒队列
     */
    QUEUE_GIFT_EXPIRE_REMIND("mall.gift.direct", "mall.gift.expire.remind", "mall.gift.expire.remind"),
    /**
     * 礼物过期提醒ttl队列
     */
    QUEUE_TTL_GIFT_EXPIRE_REMIND("mall.gift.direct.ttl", "mall.gift.expire.remind.ttl", "mall.gift.expire.remind.ttl");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
