package com.macro.mall.selfcheck.service.impl;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.selfcheck.util.PickupCodeUtil;

import com.macro.mall.selfcheck.util.StpMemberUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.ApiException;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.selfcheck.dao.SelfcheckOrderDao;
import cn.hutool.core.util.StrUtil;
import com.macro.mall.selfcheck.dao.SelfcheckCartDao;
import com.macro.mall.selfcheck.dao.SelfcheckProductDao;
import com.macro.mall.selfcheck.dao.SelfcheckCouponDao;
import com.macro.mall.selfcheck.dto.*;
import com.macro.mall.selfcheck.service.SelfcheckOrderService;
import com.macro.mall.selfcheck.service.SelfcheckCartService;
import com.macro.mall.selfcheck.service.SelfcheckProductService;
import com.macro.mall.selfcheck.service.SelfcheckStockService;
import com.macro.mall.selfcheck.service.SelfcheckCouponService;
import com.macro.mall.selfcheck.service.SelfcheckBundleMatchService;
import com.macro.mall.selfcheck.util.SelfcheckOrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自助收银订单管理服务实现类
 * 
 * @author macro
 * @date 2025/01/22
 */
@Slf4j
@Service
public class SelfcheckOrderServiceImpl implements SelfcheckOrderService {

    @Autowired
    private SelfcheckOrderDao orderDao;
    
    @Autowired
    private SelfcheckCartDao cartDao;
    
    @Autowired
    private SelfcheckProductDao productDao;
    
    @Autowired
    private SelfcheckCouponDao couponDao;
    
    @Autowired
    private SelfcheckCartService cartService;
    
    @Autowired
    private SelfcheckProductService productService;

    @Autowired
    private SelfcheckCouponService couponService;

    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;

    @Autowired
    private SmsCouponMapper couponMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsGuestMapper guestMapper;

    @Autowired
    private UmsMemberBalanceHistoryMapper balanceHistoryMapper;

    @Autowired
    private RedisService redisService;

    @Autowired(required = false)
    private com.macro.mall.selfcheck.component.CancelOrderSender cancelOrderSender;

    @Autowired(required = false)
    private com.macro.mall.selfcheck.component.PayCheckSender payCheckSender;

    @Autowired
    private SelfcheckStockService stockService;

    @Autowired
    private SelfcheckBundleMatchService bundleMatchService;
    
    @Value("${redis.key.order}")
    private String REDIS_KEY_ORDER_PREFIX;
    
    @Value("${selfcheck.order.timeout:30}")
    private Integer orderTimeoutMinutes;

    @Override
    public Map<String, Object> createQuickOrder(Long productId, Long skuId, Integer quantity, 
                                              String guestId, String terminalCode, String deviceInfo) {
        log.info("创建快速订单：productId={}, skuId={}, quantity={}, guestId={}", 
                productId, skuId, quantity, guestId);
        
        try {
            // 1. 验证参数
            if (productId == null || quantity == null || quantity <= 0) {
                throw new ApiException("商品ID和数量不能为空");
            }
            
            // 2. 验证用户身份（会员或游客）
            Long memberId = null;
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
            } else if (StrUtil.isBlank(guestId)) {
                throw new ApiException("游客模式下必须提供游客ID");
            }
            
            // 3. 查询商品信息
            ProductScanVO productInfo = productService.getProductDetail(productId, true, false);
            if (productInfo == null) {
                throw new ApiException("商品不存在或已下架");
            }
            
            // 4. 检查库存
            if (productInfo.getStock() < quantity) {
                throw new ApiException("商品库存不足");
            }
            
            // 5. 构建订单参数
            OrderCreateParam orderParam = new OrderCreateParam();
            orderParam.setOrderType("QUICK");
            orderParam.setGuestId(guestId);
            orderParam.setTerminalCode(terminalCode);
            orderParam.setDeviceInfo(deviceInfo);

            // 6. 获取门店和学校信息
            Long storeId = null;
            Long schoolId = null;

            if (StpMemberUtil.isLogin()) {
                // 会员模式：从会员信息获取学校ID
                Long currentMemberId = StpMemberUtil.getLoginIdAsLong();
                UmsMember member = memberMapper.selectByPrimaryKey(currentMemberId);
                if (member != null && member.getSchoolId() != null) {
                    schoolId = member.getSchoolId();
                }
                // 从本地存储或会话中获取门店ID（这里需要前端传递）
                storeId = getCurrentStoreId();
            } else {
                // 游客模式：从游客信息获取门店和学校ID
                if (StrUtil.isNotBlank(guestId)) {
                    UmsGuestExample guestExample = new UmsGuestExample();
                    guestExample.createCriteria().andGuestIdEqualTo(guestId);
                    List<UmsGuest> guests = guestMapper.selectByExample(guestExample);
                    if (!guests.isEmpty()) {
                        UmsGuest guest = guests.get(0);
                        // UmsGuest模型中没有storeId字段，暂时跳过
                        // storeId = guest.getStoreId();
                        schoolId = guest.getSchoolId();
                    }
                }
            }

            orderParam.setStoreId(storeId);
            orderParam.setSchoolId(schoolId);

            log.info("快速下单门店信息：storeId={}, schoolId={}", storeId, schoolId);
            
            // 构建订单商品项
            OrderItemParam orderItem = new OrderItemParam();
            orderItem.setProductId(productId);
            orderItem.setSkuId(skuId);
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(productInfo.getCurrentPrice());
            orderItem.setTotalPrice(productInfo.getCurrentPrice().multiply(new BigDecimal(quantity)));
            
            orderParam.setOrderItems(Collections.singletonList(orderItem));
            
            // 6. 创建订单
            return createOrder(orderParam);
            
        } catch (Exception e) {
            log.error("创建快速订单失败：", e);
            throw new ApiException("创建订单失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> createCartOrder(List<Long> cartItemIds, OrderCreateParam orderParam) {
        log.info("创建购物车订单：cartItemIds={}, orderParam={}", cartItemIds, orderParam);
        
        try {
            // 1. 验证参数
            if (CollectionUtils.isEmpty(cartItemIds)) {
                throw new ApiException("购物车商品不能为空");
            }
            
            // 2. 验证用户身份
            Long memberId = null;
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
            } else if (StrUtil.isBlank(orderParam.getGuestId())) {
                throw new ApiException("游客模式下必须提供游客ID");
            }
            
            // 3. 获取购物车商品
            List<CartItemVO> cartItems = new ArrayList<>();
            // 根据会员或游客获取购物车
            if (memberId != null) {
                CartVO cart = cartService.getCart(null);
                cartItems = cart.getItems();
            } else {
                CartVO cart = cartService.getCart(orderParam.getGuestId());
                cartItems = cart.getItems();
            }
            if (CollectionUtils.isEmpty(cartItems)) {
                throw new ApiException("购物车商品不存在");
            }
            
            // 4. 验证购物车商品库存
            for (CartItemVO cartItem : cartItems) {
                if (cartItem.getStock() < cartItem.getQuantity()) {
                    throw new ApiException("商品【" + cartItem.getProductName() + "】库存不足");
                }
            }
            
            // 5. 转换为订单商品项
            List<OrderItemParam> orderItems = cartItems.stream().map(cartItem -> {
                OrderItemParam orderItem = new OrderItemParam();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setSkuId(cartItem.getSkuId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getCurrentPrice());
                orderItem.setTotalPrice(cartItem.getCurrentPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                return orderItem;
            }).collect(Collectors.toList());
            
            orderParam.setOrderItems(orderItems);
            orderParam.setOrderType("CART");
            
            // 6. 创建订单
            Map<String, Object> result = createOrder(orderParam);
            
            // 7. 清空购物车（如果需要的话）
            // if (result.get("orderId") != null) {
            //     cartService.clearCart(orderParam.getGuestId());
            // }
            
            return result;
            
        } catch (Exception e) {
            log.error("创建购物车订单失败：", e);
            throw new ApiException("创建订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createOrder(OrderCreateParam orderParam) {
        log.info("创建订单：{}", orderParam);
        
        try {
            // 1. 参数验证
            validateOrderParam(orderParam);
            
            // 2. 计算订单金额
            Map<String, Object> amountInfo = calculateOrderAmount(orderParam);
            
            // 3. 验证库存
            Map<String, Object> stockInfo = validateOrderStock(orderParam);
            if (!(Boolean) stockInfo.get("success")) {
                throw new ApiException("库存验证失败：" + stockInfo.get("message"));
            }
            
            // 4. 创建订单主记录
            OmsOrder order = buildOrder(orderParam, amountInfo);
            orderMapper.insert(order);
            Long orderId = order.getId();
            
            // 5. 创建订单商品记录（包含组合优惠分摊）
            List<OmsOrderItem> orderItems = buildOrderItems(orderId, orderParam, amountInfo);
            for (OmsOrderItem orderItem : orderItems) {
                orderItemMapper.insert(orderItem);
            }

            // 6. 预占库存（在使用优惠券前先确保库存操作成功）
            boolean stockReserved = reserveStock(orderId);
            if (!stockReserved) {
                throw new ApiException("库存预占失败，订单创建失败");
            }

            // 7. 处理优惠券（在所有可能失败的操作完成后再使用优惠券）
            if (!CollectionUtils.isEmpty(orderParam.getCouponHistoryIds())) {
                processCoupons(orderId, orderParam.getCouponHistoryIds());
            }
            
            // 8. 记录操作历史
            insertOrderOperateHistory(orderId, "system", 0, "订单创建");
            
            // 9. 缓存订单信息
            cacheOrderInfo(orderId, orderParam);
            
            // 10. 发送延迟取消订单消息
            sendDelayMessageCancelOrder(orderId);
            
            // 11. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("orderSn", order.getOrderSn());
            result.put("totalAmount", order.getTotalAmount());
            result.put("payAmount", order.getPayAmount());
            result.put("memberDiscount", amountInfo.get("memberDiscount"));
            result.put("couponDiscount", amountInfo.get("couponDiscount"));
            result.put("pointsDiscount", amountInfo.get("pointsDiscount"));
            result.put("bundleDiscount", amountInfo.get("bundleDiscount"));
            result.put("bundleMatched", amountInfo.get("bundleMatched"));
            result.put("bundleName", amountInfo.get("bundleName"));
            result.put("createTime", order.getCreateTime());
            
            log.info("订单创建成功：orderId={}, orderSn={}", orderId, order.getOrderSn());
            return result;
            
        } catch (Exception e) {
            log.error("创建订单失败：", e);
            throw new ApiException("创建订单失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> calculateOrderAmount(OrderCreateParam orderParam) {
        log.info("计算订单金额：{}", orderParam);
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 计算商品小计
            BigDecimal subtotal = BigDecimal.ZERO;
            for (OrderItemParam item : orderParam.getOrderItems()) {
                subtotal = subtotal.add(item.getTotalPrice());
            }
            
            // 2. 检测组合优惠
            BigDecimal bundleDiscount = BigDecimal.ZERO;
            BundleMatchResult bundleMatchResult = null;
            List<BundleMatchResult> allBundleMatches = null;
            
            try {
                // 获取学校ID用于筛选组合
                Long schoolId = orderParam.getSchoolId();
                if (schoolId == null && StpMemberUtil.isLogin()) {
                    Long memberId = StpMemberUtil.getLoginIdAsLong();
                    UmsMember member = memberMapper.selectByPrimaryKey(memberId);
                    if (member != null) {
                        schoolId = member.getSchoolId();
                    }
                }
                
                // 检测所有可匹配的组合优惠
                allBundleMatches = bundleMatchService.matchAllBundlePromotions(orderParam.getOrderItems(), schoolId);
                
                if (!CollectionUtils.isEmpty(allBundleMatches)) {
                    // 计算所有组合优惠的总金额
                    for (BundleMatchResult match : allBundleMatches) {
                        bundleDiscount = bundleDiscount.add(match.getTotalSavedAmount());
                    }
                    // 取第一个匹配结果作为主要展示
                    bundleMatchResult = allBundleMatches.get(0);
                    log.info("检测到组合优惠，共 {} 个组合，总优惠金额：{}", allBundleMatches.size(), bundleDiscount);
                }
            } catch (Exception e) {
                log.warn("组合优惠检测失败，继续计算订单金额：{}", e.getMessage());
            }
            
            // 3. 会员折扣（如果是会员）
            BigDecimal memberDiscount = BigDecimal.ZERO;
            if (StpMemberUtil.isLogin()) {
                // 这里可以实现会员折扣逻辑
                // memberDiscount = calculateMemberDiscount(subtotal);
            }
            
            // 4. 优惠券折扣（基于扣除组合优惠后的金额计算）
            BigDecimal couponDiscount = BigDecimal.ZERO;
            BigDecimal afterBundleSubtotal = subtotal.subtract(bundleDiscount);
            if (!CollectionUtils.isEmpty(orderParam.getCouponHistoryIds())) {
                couponDiscount = calculateCouponDiscount(orderParam.getCouponHistoryIds(), afterBundleSubtotal);
            }
            
            // 5. 积分抵扣
            BigDecimal pointsDiscount = BigDecimal.ZERO;
            if (orderParam.getUsePoints() != null && orderParam.getUsePoints() > 0) {
                pointsDiscount = calculatePointsDiscount(orderParam.getUsePoints());
            }
            
            // 6. 运费（自提时为0）
            BigDecimal freight = BigDecimal.ZERO;
            if ("DELIVERY".equals(orderParam.getDeliveryType())) {
                // 自助收银暂时免运费
                freight = BigDecimal.ZERO;
            }
            
            // 7. 计算总金额（包含组合优惠）
            BigDecimal totalAmount = subtotal.subtract(bundleDiscount)
                                            .subtract(memberDiscount)
                                            .subtract(couponDiscount)
                                            .subtract(pointsDiscount)
                                            .add(freight);
            
            // 确保金额不为负数
            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
            }
            
            result.put("subtotal", subtotal);
            result.put("bundleDiscount", bundleDiscount);
            result.put("memberDiscount", memberDiscount);
            result.put("couponDiscount", couponDiscount);
            result.put("pointsDiscount", pointsDiscount);
            result.put("freight", freight);
            result.put("totalAmount", totalAmount);
            result.put("payAmount", totalAmount);
            
            // 添加组合优惠详情
            if (bundleMatchResult != null) {
                result.put("bundleMatched", true);
                result.put("bundleId", bundleMatchResult.getBundleId());
                result.put("bundleName", bundleMatchResult.getBundleName());
                result.put("bundleMatchedQuantity", bundleMatchResult.getMatchedQuantity());
                result.put("bundleMatchedItems", bundleMatchResult.getMatchedItems());
                result.put("bundleDiscountDistribution", bundleMatchResult.getDiscountDistribution());
                result.put("skuDiscountDistribution", bundleMatchResult.getSkuDiscountDistribution());
            } else {
                result.put("bundleMatched", false);
            }
            
            // 添加所有组合匹配结果
            if (!CollectionUtils.isEmpty(allBundleMatches)) {
                result.put("allBundleMatches", allBundleMatches);
            }
            
            log.info("订单金额计算完成：{}", result);
            return result;
            
        } catch (Exception e) {
            log.error("计算订单金额失败：", e);
            throw new ApiException("计算订单金额失败：" + e.getMessage());
        }
    }

    @Override
    public OmsOrder getOrderById(Long orderId) {
        log.info("根据ID查询订单：orderId={}", orderId);
        try {
            return orderMapper.selectByPrimaryKey(orderId);
        } catch (Exception e) {
            log.error("查询订单失败：orderId={}", orderId, e);
            return null;
        }
    }

    @Override
    public OrderVO getOrderDetail(Long orderId, String guestId) {
        log.info("查询订单详情：orderId={}, guestId={}", orderId, guestId);

        try {
            // 验证用户权限
            Long memberId = null;
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
            }

            OrderVO orderDetail = orderDao.getOrderDetail(orderId, memberId, guestId);
            if (orderDetail == null) {
                throw new ApiException("订单不存在或无权访问");
            }

            return orderDetail;

        } catch (Exception e) {
            log.error("查询订单详情失败：", e);
            throw new ApiException("查询订单详情失败：" + e.getMessage());
        }
    }

    @Override
    public CommonPage<OrderVO> getOrderHistory(Integer status, Integer pageNum, Integer pageSize) {
        log.info("查询订单历史：status={}, pageNum={}, pageSize={}", status, pageNum, pageSize);
        
        try {
            // 必须是会员才能查询历史
            if (!StpMemberUtil.isLogin()) {
                throw new ApiException("请先登录");
            }
            
            Long memberId = StpMemberUtil.getLoginIdAsLong();
            
            PageHelper.startPage(pageNum, pageSize);
            List<OrderVO> orderList = orderDao.getMemberOrderList(memberId, status);
            
            return CommonPage.restPage(orderList);
            
        } catch (Exception e) {
            log.error("查询订单历史失败：", e);
            throw new ApiException("查询订单历史失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getOrderStatus(Long orderId, String guestId) {
        log.info("查询订单状态：orderId={}, guestId={}", orderId, guestId);
        
        try {
            Long memberId = null;
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
            }
            
            OmsOrder order = orderDao.getOrderById(orderId, memberId, guestId);
            if (order == null) {
                throw new ApiException("订单不存在或无权访问");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getId());
            result.put("orderSn", order.getOrderSn());
            result.put("status", order.getStatus());
            result.put("statusText", getOrderStatusText(order.getStatus()));
            result.put("totalAmount", order.getTotalAmount());
            result.put("payAmount", order.getPayAmount());
            result.put("createTime", order.getCreateTime());
            result.put("paymentTime", order.getPaymentTime());
            
            return result;
            
        } catch (Exception e) {
            log.error("查询订单状态失败：", e);
            throw new ApiException("查询订单状态失败：" + e.getMessage());
        }
    }

    @Override
    public boolean cancelOrder(Long orderId, String guestId, String reason) {
        log.info("取消订单：orderId={}, guestId={}, reason={}", orderId, guestId, reason);
        
        try {
            Long memberId = null;
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
            }
            
            // 查询订单
            OmsOrder order = orderDao.getOrderById(orderId, memberId, guestId);
            if (order == null) {
                throw new ApiException("订单不存在或无权访问");
            }
            
            // 只有待付款状态的订单可以取消
            if (order.getStatus() != 0) {
                throw new ApiException("订单状态不允许取消");
            }
            
            // 更新订单状态
            int count = orderDao.updateOrderStatus(orderId, 4, reason);
            if (count > 0) {
                // 释放库存
                releaseStock(orderId);
                
                // 记录操作历史
                String operateMan = StpMemberUtil.isLogin() ? "会员" : "游客";
                insertOrderOperateHistory(orderId, operateMan, 4, "订单取消：" + reason);
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("取消订单失败：", e);
            throw new ApiException("取消订单失败：" + e.getMessage());
        }
    }

    @Override
    public boolean confirmReceiveOrder(Long orderId, String guestId) {
        log.info("确认收货：orderId={}, guestId={}", orderId, guestId);
        
        try {
            Long memberId = null;
            if (StpMemberUtil.isLogin()) {
                memberId = StpMemberUtil.getLoginIdAsLong();
            }
            
            // 查询订单
            OmsOrder order = orderDao.getOrderById(orderId, memberId, guestId);
            if (order == null) {
                throw new ApiException("订单不存在或无权访问");
            }
            
            // 只有已发货状态的订单可以确认收货
            if (order.getStatus() != 2) {
                throw new ApiException("订单状态不允许确认收货");
            }
            
            // 更新订单状态为已完成
            int count = orderDao.updateOrderStatus(orderId, 3, "用户确认收货");
            if (count > 0) {
                // 记录操作历史
                String operateMan = StpMemberUtil.isLogin() ? "会员" : "游客";
                insertOrderOperateHistory(orderId, operateMan, 3, "确认收货");
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("确认收货失败：", e);
            throw new ApiException("确认收货失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean paymentSuccess(Long orderId, Integer payType, String transactionId) {
        log.info("支付成功回调：orderId={}, payType={}, transactionId={}", orderId, payType, transactionId);

        try {
            // 查询订单
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                log.error("订单不存在：orderId={}", orderId);
                throw new ApiException("订单不存在");
            }

            log.info("订单信息：orderId={}, status={}, payAmount={}, totalAmount={}, couponAmount={}, memberId={}",
                    orderId, order.getStatus(), order.getPayAmount(), order.getTotalAmount(), order.getCouponAmount(), order.getMemberId());

            // 只有待付款状态的订单可以支付
            if (order.getStatus() != 0) {
                log.warn("订单状态不是待付款，orderId={}, status={}", orderId, order.getStatus());
                return false;
            }

            // 检查payAmount是否为null
            if (order.getPayAmount() == null) {
                log.error("订单应付金额为null：orderId={}", orderId);
                throw new ApiException("订单应付金额异常");
            }

            // 如果是余额支付(payType=3)，需要扣减用户余额
            if (payType == 3) {
                log.info("余额支付，开始扣减用户余额：orderId={}, memberId={}, amount={}",
                        orderId, order.getMemberId(), order.getPayAmount());

                // 验证是否为会员订单
                if (order.getMemberId() == null) {
                    log.error("游客不能使用余额支付：orderId={}", orderId);
                    throw new ApiException("游客不能使用余额支付");
                }

                // 扣减余额
                boolean deductResult = deductMemberBalance(order.getMemberId(), order.getPayAmount(),
                        "order", String.valueOf(orderId), "订单支付：" + orderId);

                if (!deductResult) {
                    log.error("余额扣减失败：orderId={}, memberId={}", orderId, order.getMemberId());
                    throw new ApiException("余额扣减失败，可能余额不足");
                }

                log.info("余额扣减成功：orderId={}, memberId={}, amount={}",
                        orderId, order.getMemberId(), order.getPayAmount());
            }

            // 更新支付信息
            log.info("开始更新订单支付信息：orderId={}, payType={}, transactionId={}, payAmount={}",
                    orderId, payType, transactionId, order.getPayAmount());
            int count = orderDao.updateOrderPayment(orderId, payType, transactionId, order.getPayAmount().doubleValue());
            log.info("更新订单支付信息完成：orderId={}, 影响行数={}", orderId, count);

            if (count > 0) {
                // 支付成功后扣减库存
                log.info("开始扣减库存：orderId={}", orderId);
                boolean stockDeductSuccess = deductStockAfterPayment(orderId);
                if (!stockDeductSuccess) {
                    log.warn("订单支付成功但库存扣减失败，订单ID：{}，需要人工处理", orderId);
                    // 这里可以选择是否回滚支付，或者记录异常情况供后续处理
                    // 根据业务需求决定，这里选择继续完成订单但记录警告
                }

                // 自助结算系统支付成功后直接完成订单，无需发货流程
                log.info("开始更新订单状态为已完成：orderId={}", orderId);
                int statusUpdateCount = orderDao.updateOrderStatus(orderId, 3, "支付成功，订单完成");
                log.info("更新订单状态完成：orderId={}, 影响行数={}", orderId, statusUpdateCount);

                // 记录操作历史
                String historyNote = "支付成功，订单完成，支付方式：" + getPayTypeText(payType);
                if (!stockDeductSuccess) {
                    historyNote += "（库存扣减异常，需人工核查）";
                }
                insertOrderOperateHistory(orderId, "system", 3, historyNote);

                // 清除订单缓存
                clearOrderCache(orderId);

                log.info("支付成功处理完成：orderId={}", orderId);
                return true;
            } else {
                log.error("更新订单支付信息失败，影响行数为0：orderId={}", orderId);
                return false;
            }

        } catch (Exception e) {
            log.error("支付成功回调处理失败：orderId={}, payType={}, transactionId={}, 错误：{}",
                    orderId, payType, transactionId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean paymentFailed(Long orderId, String reason) {
        log.info("支付失败处理：orderId={}, reason={}", orderId, reason);
        
        try {
            // 记录操作历史
            insertOrderOperateHistory(orderId, "system", 0, "支付失败：" + reason);
            
            return true;
            
        } catch (Exception e) {
            log.error("支付失败处理异常：", e);
            return false;
        }
    }

    @Override
    public CommonPage<OrderVO> getGuestOrderHistory(String guestId, Integer pageNum, Integer pageSize) {
        log.info("查询游客订单历史：guestId={}, pageNum={}, pageSize={}", guestId, pageNum, pageSize);
        
        try {
            if (StrUtil.isBlank(guestId)) {
                throw new ApiException("游客ID不能为空");
            }
            
            PageHelper.startPage(pageNum, pageSize);
            List<OrderVO> orderList = orderDao.getGuestOrderList(guestId);
            
            return CommonPage.restPage(orderList);
            
        } catch (Exception e) {
            log.error("查询游客订单历史失败：", e);
            throw new ApiException("查询游客订单历史失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateOrderStock(OrderCreateParam orderParam) {
        log.info("验证订单库存：{}", orderParam);
        
        try {
            Map<String, Object> result = new HashMap<>();
            List<String> insufficientItems = new ArrayList<>();
            
            for (OrderItemParam item : orderParam.getOrderItems()) {
                // 查询商品库存
                ProductScanVO productInfo = productService.getProductDetail(item.getProductId(), true, false);
                
                if (productInfo == null) {
                    insufficientItems.add("商品ID:" + item.getProductId() + " 不存在");
                } else if (productInfo.getStock() < item.getQuantity()) {
                    insufficientItems.add(productInfo.getProductName() + " 库存不足，当前库存:" + productInfo.getStock() + "，需要:" + item.getQuantity());
                }
            }
            
            if (insufficientItems.isEmpty()) {
                result.put("success", true);
                result.put("message", "库存验证通过");
            } else {
                result.put("success", false);
                result.put("message", String.join("; ", insufficientItems));
                result.put("insufficientItems", insufficientItems);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("验证订单库存失败：", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "库存验证失败：" + e.getMessage());
            return result;
        }
    }

    @Override
    public boolean reserveStock(Long orderId) {
        log.info("预占库存：orderId={}", orderId);
        
        try {
            // 查询订单商品
            List<OmsOrderItem> orderItems = orderDao.getOrderItemsByOrderId(orderId);
            if (CollectionUtils.isEmpty(orderItems)) {
                return false;
            }
            
            // TODO: 实现库存预占逻辑
            // 这里可以调用库存服务进行预占
            
            return true;
            
        } catch (Exception e) {
            log.error("预占库存失败：", e);
            return false;
        }
    }

    @Override
    public boolean releaseStock(Long orderId) {
        log.info("释放库存：orderId={}", orderId);
        
        try {
            // 查询订单商品
            List<OmsOrderItem> orderItems = orderDao.getOrderItemsByOrderId(orderId);
            if (CollectionUtils.isEmpty(orderItems)) {
                return false;
            }
            
            // TODO: 实现库存释放逻辑
            // 这里可以调用库存服务进行释放
            
            return true;
            
        } catch (Exception e) {
            log.error("释放库存失败：", e);
            return false;
        }
    }

    /**
     * 验证订单参数
     */
    private void validateOrderParam(OrderCreateParam orderParam) {
        if (orderParam == null) {
            throw new ApiException("订单参数不能为空");
        }
        if (CollectionUtils.isEmpty(orderParam.getOrderItems())) {
            throw new ApiException("订单商品不能为空");
        }
        if (StrUtil.isBlank(orderParam.getOrderType())) {
            throw new ApiException("订单类型不能为空");
        }
    }

    /**
     * 构建订单主记录
     */
    private OmsOrder buildOrder(OrderCreateParam orderParam, Map<String, Object> amountInfo) {
        OmsOrder order = new OmsOrder();
        
        // 基本信息
        order.setOrderSn(SelfcheckOrderUtil.generateOrderSn());
        order.setCreateTime(new Date());
        order.setStatus(0); // 待付款
        order.setSourceType(2); // 自助收银终端
        order.setOrderType(0); // 正常订单
        
        // 用户信息
        if (StpMemberUtil.isLogin()) {
            Long memberId = StpMemberUtil.getLoginIdAsLong();
            order.setMemberId(memberId);
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member != null) {
                order.setMemberUsername(member.getUsername());
            }
        } else {
            String noteText = "游客订单，游客ID：" + orderParam.getGuestId();
            if (orderParam.getNote() != null) {
                noteText += "，备注：" + orderParam.getNote();
            }
            order.setNote(noteText);
        }

        // 门店和学校信息
        if (orderParam.getStoreId() != null) {
            order.setStoreId(orderParam.getStoreId());
            log.info("订单关联门店ID：{}", orderParam.getStoreId());
        }
        if (orderParam.getSchoolId() != null) {
            order.setSourceSchoolId(orderParam.getSchoolId());
            log.info("订单关联学校ID：{}", orderParam.getSchoolId());
        }
        
        // 金额信息
        order.setTotalAmount((BigDecimal) amountInfo.get("subtotal")); // 商品原价总计
        order.setPayAmount((BigDecimal) amountInfo.get("payAmount"));
        order.setCouponAmount((BigDecimal) amountInfo.get("couponDiscount")); // 优惠券抵扣金额
        order.setIntegrationAmount((BigDecimal) amountInfo.get("pointsDiscount")); // 积分抵扣金额
        order.setDiscountAmount((BigDecimal) amountInfo.get("memberDiscount")); // 会员折扣金额
        
        // 组合优惠金额记录到促销优惠字段
        BigDecimal bundleDiscount = (BigDecimal) amountInfo.get("bundleDiscount");
        order.setPromotionAmount(bundleDiscount != null ? bundleDiscount : BigDecimal.ZERO);
        
        // 如果有组合优惠，记录组合信息到备注
        Boolean bundleMatched = (Boolean) amountInfo.get("bundleMatched");
        if (bundleMatched != null && bundleMatched) {
            String bundleName = (String) amountInfo.get("bundleName");
            String promotionInfo = "组合优惠：" + bundleName + "，优惠金额：" + bundleDiscount;
            order.setPromotionInfo(promotionInfo);
        }
        
        order.setFreightAmount((BigDecimal) amountInfo.get("freight")); // 运费

        // 支付方式转换：WECHAT->2, ALIPAY->1, BALANCE->3
        Integer payType = 0; // 默认未支付
        if ("WECHAT".equals(orderParam.getPayType())) {
            payType = 2;
        } else if ("ALIPAY".equals(orderParam.getPayType())) {
            payType = 1;
        } else if ("BALANCE".equals(orderParam.getPayType())) {
            payType = 3;
        }
        order.setPayType(payType);
        
        // 自助收银订单都是自提，设置固定的收货人信息
        order.setDeliveryType(1); // 自提
        order.setReceiverName("自助收银-店内自提");
        order.setReceiverPhone("00000000000");
        order.setReceiverProvince("店内");
        order.setReceiverCity("店内");
        order.setReceiverRegion("店内");
        order.setReceiverDetailAddress("店内自提");
        
        // 设备信息
        if (StrUtil.isNotBlank(orderParam.getTerminalCode())) {
            order.setNote((order.getNote() == null ? "" : order.getNote()) + 
                         "，终端编码：" + orderParam.getTerminalCode());
        }
        
        // 设置订单状态相关字段
        order.setDeleteStatus(0); // 未删除
        order.setConfirmStatus(0); // 未确认
        order.setIsGift(false); // 非赠品
        
        return order;
    }

    /**
     * 构建订单商品记录（包含组合优惠分摊）
     */
    @SuppressWarnings("unchecked")
    private List<OmsOrderItem> buildOrderItems(Long orderId, OrderCreateParam orderParam, Map<String, Object> amountInfo) {
        List<OmsOrderItem> orderItems = new ArrayList<>();
        
        // 获取组合优惠分摊信息
        Map<Long, BigDecimal> productDiscountMap = null;
        Map<Long, BigDecimal> skuDiscountMap = null;
        Long bundleId = null;
        Boolean bundleMatched = (Boolean) amountInfo.get("bundleMatched");
        
        if (bundleMatched != null && bundleMatched) {
            bundleId = (Long) amountInfo.get("bundleId");
            productDiscountMap = (Map<Long, BigDecimal>) amountInfo.get("bundleDiscountDistribution");
            skuDiscountMap = (Map<Long, BigDecimal>) amountInfo.get("skuDiscountDistribution");
        }
        
        for (OrderItemParam itemParam : orderParam.getOrderItems()) {
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(itemParam.getProductId());
            orderItem.setProductSkuId(itemParam.getSkuId());
            orderItem.setProductQuantity(itemParam.getQuantity());
            orderItem.setProductPrice(itemParam.getUnitPrice());
            
            // 计算组合优惠分摊金额
            BigDecimal bundleDiscount = BigDecimal.ZERO;
            if (bundleMatched != null && bundleMatched) {
                // 优先使用SKU级别的分摊
                if (skuDiscountMap != null && itemParam.getSkuId() != null) {
                    bundleDiscount = skuDiscountMap.getOrDefault(itemParam.getSkuId(), BigDecimal.ZERO);
                } else if (productDiscountMap != null) {
                    // 使用商品级别的分摊
                    bundleDiscount = productDiscountMap.getOrDefault(itemParam.getProductId(), BigDecimal.ZERO);
                }
                
                // 设置组合商品ID
                orderItem.setBundleId(bundleId);
                orderItem.setBundleDiscount(bundleDiscount);
            }
            
            // 计算实付金额（原价 - 组合优惠分摊）
            BigDecimal realAmount = itemParam.getTotalPrice().subtract(bundleDiscount);
            if (realAmount.compareTo(BigDecimal.ZERO) < 0) {
                realAmount = BigDecimal.ZERO;
            }
            orderItem.setRealAmount(realAmount);
            
            // 设置促销优惠金额
            orderItem.setPromotionAmount(bundleDiscount);
            orderItem.setCouponAmount(BigDecimal.ZERO);
            orderItem.setIntegrationAmount(BigDecimal.ZERO);
            
            // 获取商品详细信息
            ProductScanVO productInfo;
            if (itemParam.getSkuId() != null) {
                productInfo = productService.getSkuDetail(itemParam.getSkuId(), false, false);
            } else {
                productInfo = productService.getProductDetail(itemParam.getProductId(), false, false);
            }
            
            if (productInfo != null) {
                orderItem.setProductName(productInfo.getProductName());
                orderItem.setProductBrand(productInfo.getBrandName());
                orderItem.setProductSn(productInfo.getProductSn());
                orderItem.setProductPic(productInfo.getProductPic());
                if (itemParam.getSkuId() != null && StrUtil.isNotBlank(productInfo.getSkuCode())) {
                    orderItem.setProductSkuCode(productInfo.getSkuCode());
                }
                // 设置商品规格属性（spData包含规格JSON数据，如：[{"key":"颜色","value":"红色"}]）
                if (StrUtil.isNotBlank(productInfo.getSpData())) {
                    orderItem.setProductAttr(productInfo.getSpData());
                }
            }
            
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }

    /**
     * 处理优惠券
     */
    private void processCoupons(Long orderId, List<Long> couponHistoryIds) {
        log.info("处理订单优惠券：orderId={}, couponHistoryIds={}", orderId, couponHistoryIds);

        if (CollectionUtils.isEmpty(couponHistoryIds)) {
            return;
        }

        // 获取当前用户ID
        Long memberId = null;
        if (StpMemberUtil.isLogin()) {
            memberId = StpMemberUtil.getLoginIdAsLong();
        }

        for (Long couponHistoryId : couponHistoryIds) {
            try {
                // 1. 验证优惠券是否可用
                boolean isValid = couponService.validateCouponUsage(couponHistoryId, memberId, null);
                if (!isValid) {
                    log.warn("优惠券验证失败，跳过使用：couponHistoryId={}", couponHistoryId);
                    continue;
                }

                // 2. 标记优惠券为已使用
                boolean used = couponService.useCoupon(couponHistoryId, orderId);
                if (used) {
                    log.info("优惠券使用成功：couponHistoryId={}, orderId={}", couponHistoryId, orderId);
                } else {
                    log.warn("优惠券使用失败：couponHistoryId={}, orderId={}", couponHistoryId, orderId);
                }

            } catch (Exception e) {
                log.error("处理优惠券异常：couponHistoryId={}, orderId={}, error={}",
                         couponHistoryId, orderId, e.getMessage(), e);
            }
        }
    }

    /**
     * 记录订单操作历史
     */
    private void insertOrderOperateHistory(Long orderId, String operator, int status, String note) {
        // TODO: 实现订单操作历史记录
        log.info("记录订单操作历史：orderId={}, operator={}, status={}, note={}", orderId, operator, status, note);
    }

    /**
     * 缓存订单信息
     */
    private void cacheOrderInfo(Long orderId, OrderCreateParam orderParam) {
        try {
            String key = REDIS_KEY_ORDER_PREFIX + orderId;
            redisService.set(key, orderParam, orderTimeoutMinutes * 60);
        } catch (Exception e) {
            log.warn("缓存订单信息失败：", e);
        }
    }

    /**
     * 计算优惠券折扣
     */
    private BigDecimal calculateCouponDiscount(List<Long> couponHistoryIds, BigDecimal subtotal) {
        if (CollectionUtils.isEmpty(couponHistoryIds)) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Long couponHistoryId : couponHistoryIds) {
            try {
                // 获取优惠券历史记录
                SmsCouponHistory couponHistory = couponHistoryMapper.selectByPrimaryKey(couponHistoryId);
                if (couponHistory == null) {
                    log.warn("优惠券历史记录不存在：couponHistoryId={}", couponHistoryId);
                    continue;
                }

                // 获取优惠券信息
                SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponHistory.getCouponId());
                if (coupon == null) {
                    log.warn("优惠券不存在：couponId={}", couponHistory.getCouponId());
                    continue;
                }

                // 检查使用门槛
                if (coupon.getMinPoint() != null && subtotal.compareTo(coupon.getMinPoint()) < 0) {
                    log.warn("订单金额不满足优惠券使用门槛：couponId={}, minPoint={}, subtotal={}",
                            coupon.getId(), coupon.getMinPoint(), subtotal);
                    continue;
                }

                // 计算折扣金额
                BigDecimal discount = BigDecimal.ZERO;

                if (coupon.getCouponType() != null && coupon.getCouponType() == 1) {
                    // 打折券：订单金额 * (1 - 折扣率) = 优惠金额
                    if (coupon.getDiscountRate() != null) {
                        discount = subtotal.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()))
                                          .setScale(2, BigDecimal.ROUND_HALF_UP);
                        log.info("打折券计算：couponId={}, discountRate={}, subtotal={}, discount={}",
                                coupon.getId(), coupon.getDiscountRate(), subtotal, discount);
                    }
                } else {
                    // 满减券：直接使用面额
                    if (coupon.getAmount() != null) {
                        discount = coupon.getAmount();
                        // 确保折扣不超过订单金额
                        if (discount.compareTo(subtotal) > 0) {
                            discount = subtotal;
                        }
                        log.info("满减券计算：couponId={}, amount={}, discount={}",
                                coupon.getId(), coupon.getAmount(), discount);
                    }
                }

                totalDiscount = totalDiscount.add(discount);

            } catch (Exception e) {
                log.error("计算优惠券折扣异常：couponHistoryId={}", couponHistoryId, e);
            }
        }

        log.info("优惠券总折扣：totalDiscount={}", totalDiscount);
        return totalDiscount;
    }

    /**
     * 计算积分抵扣
     */
    private BigDecimal calculatePointsDiscount(Integer usePoints) {
        // TODO: 实现积分抵扣计算  
        return BigDecimal.ZERO;
    }

    /**
     * 获取支付方式文本
     */
    private String getPayTypeText(Integer payType) {
        switch (payType) {
            case 1: return "微信支付";
            case 2: return "支付宝";
            case 3: return "现金";
            default: return "未知";
        }
    }

    /**
     * 清除订单缓存
     */
    private void clearOrderCache(Long orderId) {
        try {
            String key = REDIS_KEY_ORDER_PREFIX + orderId;
            redisService.del(key);
        } catch (Exception e) {
            log.warn("清除订单缓存失败：", e);
        }
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已关闭";
            case 5: return "无效订单";
            default: return "未知状态";
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        log.info("发送延迟取消订单消息：orderId={}", orderId);
        if (cancelOrderSender != null) {
            cancelOrderSender.sendMessage(orderId);
        } else {
            log.warn("CancelOrderSender未启用，跳过发送延迟取消订单消息");
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId, Integer delayMinutes) {
        log.info("发送延迟取消订单消息：orderId={}, delayMinutes={}", orderId, delayMinutes);
        if (cancelOrderSender != null) {
            long delayTimes = delayMinutes * 60 * 1000L;
            cancelOrderSender.sendMessage(orderId, delayTimes);
        } else {
            log.warn("CancelOrderSender未启用，跳过发送延迟取消订单消息");
        }
    }

    @Override
    public boolean cancelTimeoutOrder(Long orderId) {
        log.info("处理超时取消订单：orderId={}", orderId);
        
        try {
            // 查询订单
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                log.warn("订单不存在，跳过取消：orderId={}", orderId);
                return false;
            }
            
            // 只有待付款状态的订单可以自动取消
            if (order.getStatus() != 0) {
                log.info("订单状态非待付款，跳过自动取消：orderId={}, status={}", orderId, order.getStatus());
                return false;
            }
            
            // 检查订单创建时间，确认确实超时
            long orderTime = order.getCreateTime().getTime();
            long currentTime = System.currentTimeMillis();
            long timeoutMillis = orderTimeoutMinutes * 60 * 1000L;
            
            if (currentTime - orderTime < timeoutMillis) {
                log.info("订单未超时，跳过自动取消：orderId={}", orderId);
                return false;
            }
            
            // 更新订单状态为已关闭
            int count = orderDao.updateOrderStatus(orderId, 4, "系统超时自动取消");
            if (count > 0) {
                // 释放库存
                releaseStock(orderId);
                
                // 记录操作历史
                insertOrderOperateHistory(orderId, "系统", 4, "订单超时自动取消");
                
                // 清除订单缓存
                clearOrderCache(orderId);
                
                log.info("超时取消订单成功：orderId={}", orderId);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("超时取消订单失败：orderId={}", orderId, e);
            return false;
        }
    }

    @Override
    public void sendDelayMessageCheckPayStatus(Long orderId) {
        log.info("发送延迟检查支付状态消息：orderId={}", orderId);
        if (payCheckSender != null) {
            payCheckSender.sendDelayMessageCheckPayStatus(orderId);
        } else {
            log.warn("PayCheckSender未启用，跳过发送延迟检查支付状态消息");
        }
    }

    @Override
    public void checkPaymentStatus(Long orderId) {
        log.info("检查订单支付状态：orderId={}", orderId);
        
        try {
            // 查询订单
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                log.warn("订单不存在，跳过支付状态检查：orderId={}", orderId);
                return;
            }
            
            // 只检查待付款状态的订单
            if (order.getStatus() != 0) {
                log.info("订单状态非待付款，跳过支付状态检查：orderId={}, status={}", orderId, order.getStatus());
                return;
            }
            
            // TODO: 这里可以对接第三方支付平台查询支付状态
            // 比如调用微信支付、支付宝的订单查询接口
            // 如果发现已支付，则更新订单状态
            
            log.info("支付状态检查完成：orderId={}", orderId);
            
        } catch (Exception e) {
            log.error("检查支付状态失败：orderId={}", orderId, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> pickupOrder(String pickupCode, String operator, String deviceCode) {
        log.info("开始核销订单，核销码：{}，操作员：{}，设备编码：{}", pickupCode, operator, deviceCode);

        // 创建核销工具实例（这里需要注入）
        PickupCodeUtil pickupCodeUtil = new PickupCodeUtil();
        // 临时解决方案：通过Redis手动验证
        
        // 验证核销码格式
        if (pickupCode == null || pickupCode.trim().isEmpty() || !pickupCode.matches("^\\d{6}$")) {
            throw new RuntimeException("核销码格式不正确");
        }

        // 检查核销码是否存在
        String orderKey = "pickup_code:" + pickupCode;
        Object orderIdObj = redisService.get(orderKey);
        if (orderIdObj == null) {
            throw new RuntimeException("核销码不存在或已失效");
        }

        Long orderId = Long.valueOf(orderIdObj.toString());

        // 检查核销码是否已使用
        String usedKey = "pickup_used:" + pickupCode;
        if (redisService.hasKey(usedKey)) {
            throw new RuntimeException("核销码已使用");
        }

        // 查询订单详情
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单状态
        if (order.getStatus() == null || order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确，只有已付款订单才能核销");
        }

        // 验证是否为自提订单
        if (order.getDeliveryType() == null || order.getDeliveryType() != 1) {
            throw new RuntimeException("该订单不是自提订单");
        }

        // 检查是否已核销
        if (order.getPickupStatus() != null && order.getPickupStatus() == 1) {
            throw new RuntimeException("订单已核销");
        }

        // 更新订单核销状态
        OmsOrder updateOrder = new OmsOrder();
        updateOrder.setId(orderId);
        updateOrder.setPickupStatus((byte) 1); // 已核销
        updateOrder.setPickupTime(new Date());
        updateOrder.setPickupOperator(operator + (StrUtil.isNotBlank(deviceCode) ? "@" + deviceCode : ""));
        updateOrder.setStatus(3); // 已完成

        int updateResult = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (updateResult <= 0) {
            throw new RuntimeException("更新订单状态失败");
        }

        // 标记核销码为已使用
        try {
            String value = operator + ":" + System.currentTimeMillis();
            redisService.set(usedKey, value, 30 * 24 * 3600); // 30天过期
            log.info("标记核销码为已使用，核销码：{}，操作员：{}", pickupCode, operator);
        } catch (Exception e) {
            log.warn("标记核销码为已使用失败，但订单状态已更新，订单ID:{}, 核销码:{}", orderId, pickupCode);
        }

        // 记录操作历史
        insertOrderOperateHistory(orderId, operator, 3, "自助设备扫码核销");

        log.info("订单核销成功，订单ID:{}, 核销码:{}, 操作员:{}", orderId, pickupCode, operator);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("orderSn", order.getOrderSn());
        result.put("pickupCode", pickupCode);
        result.put("operator", operator);
        result.put("deviceCode", deviceCode);
        result.put("pickupTime", updateOrder.getPickupTime());
        result.put("totalAmount", order.getTotalAmount());
        result.put("payAmount", order.getPayAmount());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStockAfterPayment(Long orderId) {
        log.info("订单支付成功后扣减库存，订单ID：{}", orderId);

        try {
            // 1. 查询订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                log.error("订单不存在，订单ID：{}", orderId);
                return false;
            }

            // 2. 获取门店ID（优先从订单的store_id字段获取）
            Long storeId = order.getStoreId();

            // 3. 如果订单没有门店ID，尝试从用户绑定信息获取
            if (storeId == null && order.getSourceSchoolId() != null) {
                // 可以根据学校ID获取默认门店，这里暂时跳过门店库存扣减
                log.info("订单没有指定门店，将直接扣减总库存，订单ID：{}", orderId);
            }

            // 4. 执行库存扣减
            return deductStockByStore(storeId, orderId);

        } catch (Exception e) {
            log.error("订单支付成功后扣减库存失败，订单ID：{}", orderId, e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStockByStore(Long storeId, Long orderId) {
        log.info("根据门店扣减库存，门店ID：{}，订单ID：{}", storeId, orderId);

        try {
            // 1. 查询订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                log.error("订单不存在，订单ID：{}", orderId);
                return false;
            }

            // 2. 查询订单商品
            List<OmsOrderItem> orderItems = orderDao.getOrderItemsByOrderId(orderId);
            if (CollectionUtils.isEmpty(orderItems)) {
                log.error("订单商品为空，订单ID：{}", orderId);
                return false;
            }

            // 3. 获取操作人信息
            Long operatorId = 0L; // 系统操作
            String operatorName = "系统";

            // 如果是会员订单，记录会员信息
            if (order.getMemberId() != null) {
                operatorId = order.getMemberId();
                operatorName = "会员ID:" + order.getMemberId();
            }

            // 4. 批量智能库存扣减
            List<SelfcheckStockService.StockDeductResult> results = stockService.batchSmartDeductStock(
                storeId, orderId, order.getOrderSn(), orderItems, operatorId, operatorName);

            // 5. 检查扣减结果
            boolean allSuccess = true;
            StringBuilder errorMessages = new StringBuilder();

            for (int i = 0; i < results.size(); i++) {
                SelfcheckStockService.StockDeductResult result = results.get(i);
                OmsOrderItem item = orderItems.get(i);

                if (!result.isSuccess()) {
                    allSuccess = false;
                    errorMessages.append("商品[").append(item.getProductName()).append("]库存扣减失败：")
                               .append(result.getMessage()).append("; ");
                    log.error("商品库存扣减失败，商品ID：{}，SKU ID：{}，错误：{}",
                             item.getProductId(), item.getProductSkuId(), result.getMessage());
                } else {
                    log.info("商品库存扣减成功，商品ID：{}，SKU ID：{}，扣减类型：{}，操作单号：{}",
                            item.getProductId(), item.getProductSkuId(), result.getStockType(), result.getOperationNo());
                }
            }

            if (!allSuccess) {
                log.error("部分商品库存扣减失败，订单ID：{}，错误信息：{}", orderId, errorMessages.toString());
                // 这里可以选择是否回滚整个事务，或者记录失败信息继续处理
                // 根据业务需求决定
            }

            return allSuccess;

        } catch (Exception e) {
            log.error("根据门店扣减库存失败，门店ID：{}，订单ID：{}", storeId, orderId, e);
            return false;
        }
    }

    /**
     * 获取当前门店ID
     * 这里可以从请求头、会话或其他方式获取门店ID
     * 暂时返回null，实际项目中需要根据具体需求实现
     */
    private Long getCurrentStoreId() {
        // TODO: 实现获取当前门店ID的逻辑
        // 可以从请求头、会话、Redis等获取
        return null;
    }

    /**
     * 扣减会员余额
     *
     * @param memberId 会员ID
     * @param amount 扣减金额
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param remark 备注
     * @return 是否成功
     */
    private boolean deductMemberBalance(Long memberId, BigDecimal amount,
                                       String businessType, String businessId, String remark) {
        try {
            log.info("开始扣减会员余额：memberId={}, amount={}, businessType={}, businessId={}",
                    memberId, amount, businessType, businessId);

            // 1. 查询会员信息
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                log.error("会员不存在：memberId={}", memberId);
                return false;
            }

            // 2. 检查余额是否充足
            BigDecimal currentBalance = member.getBalance() != null ? member.getBalance() : BigDecimal.ZERO;
            if (currentBalance.compareTo(amount) < 0) {
                log.error("会员余额不足：memberId={}, currentBalance={}, needAmount={}",
                        memberId, currentBalance, amount);
                return false;
            }

            // 3. 计算扣减后的余额
            BigDecimal newBalance = currentBalance.subtract(amount);

            // 4. 更新会员余额
            UmsMember updateMember = new UmsMember();
            updateMember.setId(memberId);
            updateMember.setBalance(newBalance);
            int updateCount = memberMapper.updateByPrimaryKeySelective(updateMember);

            if (updateCount <= 0) {
                log.error("更新会员余额失败：memberId={}", memberId);
                return false;
            }

            // 5. 记录余额变动历史
            UmsMemberBalanceHistory history = new UmsMemberBalanceHistory();
            history.setMemberId(memberId);
            history.setChangeType((byte) 2); // 2-消费
            history.setAmount(amount);
            history.setBalanceBefore(currentBalance);
            history.setBalanceAfter(newBalance);
            history.setBusinessType(businessType);
            history.setBusinessId(businessId);
            history.setRemark(remark);
            history.setOperator("系统");
            history.setCreateTime(new Date());
            history.setUpdateTime(new Date()); // 添加更新时间

            balanceHistoryMapper.insert(history);

            // 6. 清除会员缓存，确保下次查询获取最新余额
            // 注意：缓存键前缀必须与 SelfcheckMemberServiceImpl 中定义的一致
            String memberCacheKey = "selfcheck:member:" + memberId;
            try {
                redisService.del(memberCacheKey);
                log.info("清除会员缓存成功：memberId={}, cacheKey={}", memberId, memberCacheKey);
            } catch (Exception e) {
                log.warn("清除会员缓存失败：memberId={}, 错误：{}", memberId, e.getMessage());
                // 缓存清除失败不影响主流程
            }

            log.info("会员余额扣减成功：memberId={}, amount={}, balanceBefore={}, balanceAfter={}",
                    memberId, amount, currentBalance, newBalance);

            return true;

        } catch (Exception e) {
            log.error("扣减会员余额失败：memberId={}, amount={}", memberId, amount, e);
            return false;
        }
    }
}