package com.macro.mall.selfcheck.dao;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.selfcheck.dto.OrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 自助收银订单数据访问接口
 * 
 * @author macro
 * @date 2025/01/22
 */
public interface SelfcheckOrderDao {

    /**
     * 根据订单ID和用户信息查询订单详情
     * 
     * @param orderId 订单ID
     * @param memberId 会员ID（可为空）
     * @param guestId 游客ID（可为空）
     * @return 订单详情
     */
    OrderVO getOrderDetail(@Param("orderId") Long orderId, 
                          @Param("memberId") Long memberId, 
                          @Param("guestId") String guestId);

    /**
     * 查询会员订单列表
     * 
     * @param memberId 会员ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderVO> getMemberOrderList(@Param("memberId") Long memberId, 
                                    @Param("status") Integer status);

    /**
     * 查询游客订单列表
     * 
     * @param guestId 游客ID
     * @return 订单列表
     */
    List<OrderVO> getGuestOrderList(@Param("guestId") String guestId);

    /**
     * 根据订单ID查询订单商品列表
     * 
     * @param orderId 订单ID
     * @return 订单商品列表
     */
    List<OmsOrderItem> getOrderItemsByOrderId(@Param("orderId") Long orderId);

    /**
     * 查询订单基本信息
     * 
     * @param orderId 订单ID
     * @param memberId 会员ID（可为空）
     * @param guestId 游客ID（可为空）
     * @return 订单基本信息
     */
    OmsOrder getOrderById(@Param("orderId") Long orderId, 
                         @Param("memberId") Long memberId, 
                         @Param("guestId") String guestId);

    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param status 新状态
     * @param note 备注
     * @return 更新数量
     */
    int updateOrderStatus(@Param("orderId") Long orderId, 
                         @Param("status") Integer status, 
                         @Param("note") String note);

    /**
     * 更新订单支付信息
     * 
     * @param orderId 订单ID
     * @param payType 支付方式
     * @param transactionId 支付流水号
     * @param paidAmount 支付金额
     * @return 更新数量
     */
    int updateOrderPayment(@Param("orderId") Long orderId,
                          @Param("payType") Integer payType,
                          @Param("transactionId") String transactionId,
                          @Param("paidAmount") Double paidAmount);

    /**
     * 获取订单统计信息
     * 
     * @param memberId 会员ID（可为空）
     * @param guestId 游客ID（可为空）
     * @return 统计信息
     */
    Map<String, Object> getOrderStatistics(@Param("memberId") Long memberId, 
                                          @Param("guestId") String guestId);

    /**
     * 查询待付款订单数量
     * 
     * @param memberId 会员ID（可为空）
     * @param guestId 游客ID（可为空）
     * @return 待付款订单数量
     */
    int getPendingPaymentOrderCount(@Param("memberId") Long memberId, 
                                   @Param("guestId") String guestId);

    /**
     * 查询超时未支付订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    List<Long> getTimeoutOrders(@Param("timeoutMinutes") Integer timeoutMinutes);

    /**
     * 批量更新订单状态为已关闭
     * 
     * @param orderIds 订单ID列表
     * @return 更新数量
     */
    int batchCloseOrders(@Param("orderIds") List<Long> orderIds);

    /**
     * 插入订单操作历史
     * 
     * @param orderId 订单ID
     * @param operateMan 操作人
     * @param orderStatus 订单状态
     * @param note 备注
     * @return 插入数量
     */
    int insertOrderOperateHistory(@Param("orderId") Long orderId,
                                 @Param("operateMan") String operateMan,
                                 @Param("orderStatus") Integer orderStatus,
                                 @Param("note") String note);
} 