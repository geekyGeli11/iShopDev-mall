package com.macro.mall.selfcheck.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.selfcheck.dto.OrderCreateParam;
import com.macro.mall.selfcheck.dto.OrderVO;
import com.macro.mall.selfcheck.dto.CartItemParam;

import java.util.List;
import java.util.Map;

/**
 * 自助收银订单管理服务接口
 * 
 * @author macro
 * @date 2025/01/22
 */
public interface SelfcheckOrderService {

    /**
     * 快速下单 - 扫码后直接下单单个商品
     * 
     * @param productId 商品ID
     * @param skuId SKU ID（可选）
     * @param quantity 购买数量
     * @param guestId 游客ID（游客模式时必填）
     * @param terminalCode 终端编码
     * @param deviceInfo 设备信息
     * @return 订单创建结果
     */
    Map<String, Object> createQuickOrder(Long productId, Long skuId, Integer quantity, 
                                       String guestId, String terminalCode, String deviceInfo);

    /**
     * 购物车下单 - 将购物车中的商品批量下单
     * 
     * @param cartItemIds 购物车商品ID列表
     * @param orderParam 订单参数
     * @return 订单创建结果
     */
    Map<String, Object> createCartOrder(List<Long> cartItemIds, OrderCreateParam orderParam);

    /**
     * 创建订单（通用方法）
     * 
     * @param orderParam 订单创建参数
     * @return 订单创建结果，包含订单ID、订单号、支付信息等
     */
    Map<String, Object> createOrder(OrderCreateParam orderParam);

    /**
     * 计算订单金额
     * 
     * @param orderParam 订单参数
     * @return 订单金额详情（小计、优惠、总计等）
     */
    Map<String, Object> calculateOrderAmount(OrderCreateParam orderParam);

    /**
     * 根据订单ID获取订单（内部使用）
     *
     * @param orderId 订单ID
     * @return 订单对象
     */
    com.macro.mall.model.OmsOrder getOrderById(Long orderId);

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @param guestId 游客ID（游客模式时必填）
     * @return 订单详情
     */
    OrderVO getOrderDetail(Long orderId, String guestId);

    /**
     * 获取订单历史记录（仅会员）
     * 
     * @param status 订单状态：-1-全部；0-待付款；1-待发货；2-已发货；3-已完成；4-已关闭
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    CommonPage<OrderVO> getOrderHistory(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 查询订单状态
     * 
     * @param orderId 订单ID
     * @param guestId 游客ID（游客模式时可选）
     * @return 订单状态信息
     */
    Map<String, Object> getOrderStatus(Long orderId, String guestId);

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @param guestId 游客ID（游客模式时必填）
     * @param reason 取消原因
     * @return 操作结果
     */
    boolean cancelOrder(Long orderId, String guestId, String reason);

    /**
     * 确认收货
     * 
     * @param orderId 订单ID
     * @param guestId 游客ID（游客模式时必填）
     * @return 操作结果
     */
    boolean confirmReceiveOrder(Long orderId, String guestId);

    /**
     * 订单支付成功回调
     * 
     * @param orderId 订单ID
     * @param payType 支付方式
     * @param transactionId 支付流水号
     * @return 处理结果
     */
    boolean paymentSuccess(Long orderId, Integer payType, String transactionId);

    /**
     * 订单支付失败处理
     * 
     * @param orderId 订单ID
     * @param reason 失败原因
     * @return 处理结果
     */
    boolean paymentFailed(Long orderId, String reason);

    /**
     * 获取游客订单列表
     * 
     * @param guestId 游客ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    CommonPage<OrderVO> getGuestOrderHistory(String guestId, Integer pageNum, Integer pageSize);

    /**
     * 验证订单商品库存
     * 
     * @param orderParam 订单参数
     * @return 库存验证结果
     */
    Map<String, Object> validateOrderStock(OrderCreateParam orderParam);

    /**
     * 预占库存
     * 
     * @param orderId 订单ID
     * @return 操作结果
     */
    boolean reserveStock(Long orderId);

    /**
     * 释放库存
     * 
     * @param orderId 订单ID
     * @return 操作结果
     */
    boolean releaseStock(Long orderId);

    /**
     * 发送延迟取消订单消息
     * 
     * @param orderId 订单ID
     */
    void sendDelayMessageCancelOrder(Long orderId);

    /**
     * 发送延迟取消订单消息（自定义延迟时间）
     * 
     * @param orderId 订单ID
     * @param delayMinutes 延迟时间（分钟）
     */
    void sendDelayMessageCancelOrder(Long orderId, Integer delayMinutes);

    /**
     * 超时取消订单（由消息队列触发）
     * 
     * @param orderId 订单ID
     * @return 操作结果
     */
    boolean cancelTimeoutOrder(Long orderId);

    /**
     * 发送延迟消息检查支付状态
     * 
     * @param orderId 订单ID
     */
    void sendDelayMessageCheckPayStatus(Long orderId);

    /**
     * 检查订单支付状态（由消息队列触发）
     * 
     * @param orderId 订单ID
     */
    void checkPaymentStatus(Long orderId);

    /**
     * 核销订单
     *
     * @param pickupCode 核销码
     * @param operator 操作员
     * @param deviceCode 设备编码
     * @return 核销结果
     */
    Map<String, Object> pickupOrder(String pickupCode, String operator, String deviceCode);

    /**
     * 订单支付成功后扣减库存
     *
     * @param orderId 订单ID
     * @return 扣减结果
     */
    boolean deductStockAfterPayment(Long orderId);

    /**
     * 根据门店ID和订单信息扣减库存
     *
     * @param storeId 门店ID
     * @param orderId 订单ID
     * @return 扣减结果
     */
    boolean deductStockByStore(Long storeId, Long orderId);
}