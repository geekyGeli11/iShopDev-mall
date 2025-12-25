package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.portal.domain.ConfirmOrderResult;
import com.macro.mall.portal.domain.OmsOrderDetail;
import com.macro.mall.portal.domain.OrderParam;
import com.macro.mall.portal.dto.DirectBuyParam;
import com.macro.mall.portal.dto.GiftOrderParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
public interface OmsPortalOrderService {
    /**
     * 根据用户购物车信息生成确认单信息
     */
    ConfirmOrderResult generateConfirmOrder(List<Long> cartIds);

    /**
     * 根据用户购物车信息生成确认单信息（支持门店库存校验）
     * @param cartIds 购物车ID列表
     * @param storeId 门店ID，用于校验门店库存
     */
    ConfirmOrderResult generateConfirmOrder(List<Long> cartIds, Long storeId);

    /**
     * 直接购买生成确认单信息
     */
    ConfirmOrderResult generateDirectConfirmOrder(DirectBuyParam directBuyParam);

    /**
     * 直接购买生成确认单信息（支持门店库存校验）
     * @param directBuyParam 直接购买参数
     * @param storeId 门店ID，用于校验门店库存
     */
    ConfirmOrderResult generateDirectConfirmOrder(DirectBuyParam directBuyParam, Long storeId);

    /**
     * 根据提交信息生成订单
     */
    @Transactional
    Map<String, Object> generateOrder(OrderParam orderParam);

    /**
     * 生成送礼订单
     */
    @Transactional
    Map<String, Object> generateGiftOrder(GiftOrderParam giftOrderParam);

    /**
     * 支付成功后的回调
     */
    @Transactional
    Map<String, Object> paySuccess(Long orderId, Integer payType, String giftMessage, String giftPic);

    /**
     * 自动取消超时订单
     */
    @Transactional
    Integer cancelTimeOutOrder();

    /**
     * 取消单个超时订单
     */
    @Transactional
    void cancelOrder(Long orderId);

    /**
     * 发送延迟消息取消订单
     */
    void sendDelayMessageCancelOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmReceiveOrder(Long orderId);

    /**
     * 分页获取用户订单
     */
    CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据订单ID获取订单详情
     */
    OmsOrderDetail detail(Long orderId);

    /**
     * 用户根据订单ID删除订单
     */
    void deleteOrder(Long orderId);

    /**
     * 核销订单
     */
    @Transactional
    Map<String, Object> pickupOrder(String pickupCode, String operator);

    /**
     * 获取核销码二维码
     */
    String getPickupQRCode(String pickupCode);

    /**
     * 根据orderSn来实现的支付成功逻辑
     */
    @Transactional
    void paySuccessByOrderSn(String orderSn, Integer payType);

    /**
     * 检查订单支付状态
     */
    @Transactional
    void checkPayStatus(String orderSn);
    
    /**
     * 发送延迟消息检查支付状态
     */
    void sendDelayMessageCheckPayStatus(String orderSn);
}
