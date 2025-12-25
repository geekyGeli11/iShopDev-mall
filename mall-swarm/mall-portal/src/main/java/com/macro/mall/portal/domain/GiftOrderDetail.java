package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsGiftRecord;
import com.macro.mall.model.OmsOrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 送礼订单详情
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GiftOrderDetail extends OmsOrderDetail {

    /**
     * 送礼记录信息
     */
    private OmsGiftRecord giftRecord;

    /**
     * 礼物是否已被接收
     */
    private Boolean isReceived;

    /**
     * 礼物是否已过期
     */
    private Boolean isExpired;
    
    /**
     * 礼物过期时间
     */
    private Date expireTime;
} 