package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsGiftRecord;
import com.macro.mall.portal.domain.GiftOrderDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 送礼业务Service
 */
public interface OmsGiftService {
    /**
     * 处理送礼订单支付成功
     * @param orderId 订单ID
     * @param giftMessage 送礼消息
     * @param giftPic 送礼图片
     */
    @Transactional
    void handleGiftOrderPaySuccess(Long orderId, String giftMessage, String giftPic);

    /**
     * 接收礼物
     */
    @Transactional
    CommonResult receiveGift(Long giftRecordId);

    /**
     * 更新送礼订单收货地址
     */
    @Transactional
    CommonResult updateGiftOrderAddress(Long giftRecordId, Long memberReceiveAddressId);

    /**
     * 获取用户发送的礼物列表
     */
    CommonResult listSentGifts(Integer pageNum, Integer pageSize);

    /**
     * 获取用户收到的礼物列表
     */
    CommonResult listReceivedGifts(Integer pageNum, Integer pageSize);

    /**
     * 获取礼物详情
     */
    CommonResult getGiftDetail(Long giftRecordId);
    
    /**
     * 获取送礼订单详情
     */
    CommonResult<GiftOrderDetail> getGiftOrderDetail(Long orderId);

    /**
     * 处理礼物过期提醒
     * @param giftRecordId 礼物记录ID
     */
    void handleGiftExpireRemind(Long giftRecordId);
} 