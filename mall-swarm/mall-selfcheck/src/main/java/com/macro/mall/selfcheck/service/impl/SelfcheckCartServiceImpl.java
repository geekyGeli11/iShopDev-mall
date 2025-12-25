package com.macro.mall.selfcheck.service.impl;


import com.macro.mall.selfcheck.dao.SelfcheckCartDao;
import com.macro.mall.selfcheck.dto.*;
import com.macro.mall.selfcheck.service.SelfcheckCartService;
import com.macro.mall.selfcheck.service.SelfcheckCouponService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 自助收银购物车服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckCartServiceImpl implements SelfcheckCartService {

    @Autowired
    private SelfcheckCartDao cartDao;

    @Autowired
    private SelfcheckCouponService couponService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 游客购物车Redis键前缀
     */
    private static final String GUEST_CART_KEY_PREFIX = "selfcheck:cart:guest:";

    /**
     * 游客购物车过期时间（分钟）
     */
    private static final int GUEST_CART_EXPIRE_MINUTES = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addItem(CartItemParam param) {
        try {
            if (isLoggedInMember()) {
                return addMemberCartItem(param);
            } else {
                return addGuestCartItem(param);
            }
        } catch (Exception e) {
            log.error("添加购物车商品失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateItem(CartItemParam param) {
        try {
            if (isLoggedInMember()) {
                return updateMemberCartItem(param);
            } else {
                return updateGuestCartItem(param);
            }
        } catch (Exception e) {
            log.error("更新购物车商品失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeItem(CartItemParam param) {
        try {
            if (isLoggedInMember()) {
                return removeMemberCartItem(param);
            } else {
                return removeGuestCartItem(param);
            }
        } catch (Exception e) {
            log.error("删除购物车商品失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearCart(String guestId) {
        try {
            if (isLoggedInMember()) {
                Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
                return cartDao.clearMemberCart(memberId) > 0;
            } else {
                String redisKey = GUEST_CART_KEY_PREFIX + guestId;
                redisTemplate.delete(redisKey);
                return true;
            }
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            return false;
        }
    }

    @Override
    public CartVO getCart(String guestId) {
        try {
            if (isLoggedInMember()) {
                return getMemberCart();
            } else {
                return getGuestCart(guestId);
            }
        } catch (Exception e) {
            log.error("获取购物车信息失败", e);
            return createEmptyCart(guestId);
        }
    }

    @Override
    public CartVO calculateCart(String guestId, List<Long> couponIds, Integer usePoints) {
        try {
            CartVO cart = getCart(guestId);
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                return cart;
            }

            // 计算商品总价
            calculateCartTotals(cart);

            // 应用优惠券
            if (couponIds != null && !couponIds.isEmpty() && isLoggedInMember()) {
                applyCouponsToCart(cart, couponIds);
            }

            // 应用积分
            if (usePoints != null && usePoints > 0 && isLoggedInMember()) {
                applyPointsToCart(cart, usePoints);
            }

            // 计算最终金额
            calculateFinalAmount(cart);

            return cart;
        } catch (Exception e) {
            log.error("计算购物车价格失败", e);
            return getCart(guestId);
        }
    }

    @Override
    public boolean applyCoupon(String guestId, Long couponId) {
        try {
            if (!isLoggedInMember()) {
                log.warn("游客无法使用优惠券");
                return false;
            }

            // 验证优惠券是否可用
            // 这里可以调用优惠券服务验证
            
            // 将优惠券信息保存到Redis或数据库
            String redisKey = "selfcheck:cart:coupon:" + StpMemberUtil.getLoginId();
            HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
            hashOps.put(redisKey, couponId.toString(), System.currentTimeMillis());
            redisTemplate.expire(redisKey, GUEST_CART_EXPIRE_MINUTES, TimeUnit.MINUTES);

            return true;
        } catch (Exception e) {
            log.error("应用优惠券失败", e);
            return false;
        }
    }

    @Override
    public boolean removeCoupon(String guestId, Long couponId) {
        try {
            if (!isLoggedInMember()) {
                return false;
            }

            String redisKey = "selfcheck:cart:coupon:" + StpMemberUtil.getLoginId();
            HashOperations<String, String, Long> hashOps = redisTemplate.opsForHash();
            hashOps.delete(redisKey, couponId.toString());

            return true;
        } catch (Exception e) {
            log.error("移除优惠券失败", e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdate(List<CartItemParam> items, String guestId) {
        try {
            boolean allSuccess = true;
            for (CartItemParam item : items) {
                if (guestId != null) {
                    item.setGuestId(guestId);
                }

                boolean success;
                switch (item.getOperation().toUpperCase()) {
                    case "ADD":
                        success = addItem(item);
                        break;
                    case "UPDATE":
                        success = updateItem(item);
                        break;
                    case "REMOVE":
                        success = removeItem(item);
                        break;
                    default:
                        log.warn("未知的操作类型: {}", item.getOperation());
                        success = false;
                }

                if (!success) {
                    allSuccess = false;
                    log.warn("批量操作失败，商品ID: {}, 操作: {}", item.getProductId(), item.getOperation());
                }
            }
            return allSuccess;
        } catch (Exception e) {
            log.error("批量更新购物车失败", e);
            return false;
        }
    }

    @Override
    public CartVO syncCart(String guestId) {
        try {
            CartVO cart = getCart(guestId);
            if (cart == null || cart.getItems() == null) {
                return cart;
            }

            // 验证商品有效性
            List<CartItemVO> validItems = new ArrayList<>();
            List<CartItemVO> invalidItems = new ArrayList<>();

            for (CartItemVO item : cart.getItems()) {
                if (Boolean.TRUE.equals(item.getAvailable())) {
                    validItems.add(item);
                } else {
                    invalidItems.add(item);
                }
            }

            // 移除无效商品
            if (!invalidItems.isEmpty() && isLoggedInMember()) {
                List<Long> invalidProductIds = invalidItems.stream()
                    .map(CartItemVO::getProductId)
                    .collect(Collectors.toList());
                
                Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
                cartDao.batchRemoveMemberCartItems(memberId, invalidProductIds);
            }

            cart.setItems(validItems);
            calculateCartTotals(cart);

            return cart;
        } catch (Exception e) {
            log.error("同步购物车失败", e);
            return getCart(guestId);
        }
    }

    @Override
    public int getCartItemCount(String guestId) {
        try {
            if (isLoggedInMember()) {
                Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
                return cartDao.getMemberCartTotalQuantity(memberId);
            } else {
                String redisKey = GUEST_CART_KEY_PREFIX + guestId;
                HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
                Map<String, Object> cartItems = hashOps.entries(redisKey);
                
                return cartItems.values().stream()
                    .mapToInt(item -> {
                        if (item instanceof Map) {
                            Object qty = ((Map<?, ?>) item).get("quantity");
                            return qty instanceof Integer ? (Integer) qty : 0;
                        }
                        return 0;
                    })
                    .sum();
            }
        } catch (Exception e) {
            log.error("获取购物车商品数量失败", e);
            return 0;
        }
    }

    @Override
    public boolean isProductInCart(Long productId, Long skuId, String guestId) {
        try {
            if (isLoggedInMember()) {
                Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
                return cartDao.isProductInCart(memberId, productId, skuId);
            } else {
                String redisKey = GUEST_CART_KEY_PREFIX + guestId;
                String itemKey = buildCartItemKey(productId, skuId);
                HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
                return hashOps.hasKey(redisKey, itemKey);
            }
        } catch (Exception e) {
            log.error("检查商品是否在购物车中失败", e);
            return false;
        }
    }

    @Override
    public List<MemberCouponVO> getAvailableCoupons(String guestId) {
        try {
            if (!isLoggedInMember()) {
                return Collections.emptyList();
            }

            // 获取购物车信息
            CartVO cart = getCart(guestId);
            if (cart == null || cart.getAvailableAmount() == null) {
                return Collections.emptyList();
            }

            // 调用优惠券服务获取可用优惠券
            Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
            
            // 根据订单金额获取可用优惠券
            return couponService.getAvailableCouponsForOrder(memberId, cart.getAvailableAmount()).getList();
        } catch (Exception e) {
            log.error("获取可用优惠券失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeInvalidItems(String guestId) {
        try {
            if (isLoggedInMember()) {
                Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
                return cartDao.removeInvalidCartItems(memberId);
            } else {
                // 游客购物车的无效商品清理
                // 这里可以实现游客购物车的无效商品清理逻辑
                return 0;
            }
        } catch (Exception e) {
            log.error("删除无效商品失败", e);
            return 0;
        }
    }

    /**
     * 检查当前用户是否为已登录会员
     */
    private boolean isLoggedInMember() {
        return StpMemberUtil.isLogin();
    }

    /**
     * 添加商品到会员购物车
     */
    private boolean addMemberCartItem(CartItemParam param) {
        Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
        
        // 检查商品是否已在购物车中
        CartItemVO existingItem = cartDao.getMemberCartItem(memberId, param.getProductId(), param.getSkuId());
        
        if (existingItem != null) {
            // 更新数量
            int newQuantity = existingItem.getQuantity() + param.getQuantity();
            return cartDao.updateMemberCartItemQuantity(memberId, param.getProductId(), param.getSkuId(), newQuantity) > 0;
        } else {
            // 添加新商品
            return cartDao.addMemberCartItem(memberId, param.getProductId(), param.getSkuId(), 
                param.getQuantity(), param.getRemark()) > 0;
        }
    }

    /**
     * 添加商品到游客购物车
     */
    private boolean addGuestCartItem(CartItemParam param) {
        String redisKey = GUEST_CART_KEY_PREFIX + param.getGuestId();
        String itemKey = buildCartItemKey(param.getProductId(), param.getSkuId());
        
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        
        // 检查商品是否已存在
        Object existingItem = hashOps.get(redisKey, itemKey);
        
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productId", param.getProductId());
        cartItem.put("skuId", param.getSkuId());
        cartItem.put("remark", param.getRemark());
        cartItem.put("createTime", System.currentTimeMillis());
        cartItem.put("updateTime", System.currentTimeMillis());
        
        if (existingItem != null && existingItem instanceof Map) {
            // 更新数量
            Map<String, Object> existing = (Map<String, Object>) existingItem;
            Integer currentQty = (Integer) existing.get("quantity");
            cartItem.put("quantity", (currentQty != null ? currentQty : 0) + param.getQuantity());
        } else {
            // 新增商品
            cartItem.put("quantity", param.getQuantity());
        }
        
        hashOps.put(redisKey, itemKey, cartItem);
        redisTemplate.expire(redisKey, GUEST_CART_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        return true;
    }

    /**
     * 更新会员购物车商品
     */
    private boolean updateMemberCartItem(CartItemParam param) {
        Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
        return cartDao.updateMemberCartItemQuantity(memberId, param.getProductId(), 
            param.getSkuId(), param.getQuantity()) > 0;
    }

    /**
     * 更新游客购物车商品
     */
    private boolean updateGuestCartItem(CartItemParam param) {
        String redisKey = GUEST_CART_KEY_PREFIX + param.getGuestId();
        String itemKey = buildCartItemKey(param.getProductId(), param.getSkuId());
        
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        Object existingItem = hashOps.get(redisKey, itemKey);
        
        if (existingItem != null && existingItem instanceof Map) {
            Map<String, Object> cartItem = (Map<String, Object>) existingItem;
            cartItem.put("quantity", param.getQuantity());
            cartItem.put("updateTime", System.currentTimeMillis());
            
            hashOps.put(redisKey, itemKey, cartItem);
            redisTemplate.expire(redisKey, GUEST_CART_EXPIRE_MINUTES, TimeUnit.MINUTES);
            return true;
        }
        
        return false;
    }

    /**
     * 删除会员购物车商品
     */
    private boolean removeMemberCartItem(CartItemParam param) {
        Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
        return cartDao.removeMemberCartItem(memberId, param.getProductId(), param.getSkuId()) > 0;
    }

    /**
     * 删除游客购物车商品
     */
    private boolean removeGuestCartItem(CartItemParam param) {
        String redisKey = GUEST_CART_KEY_PREFIX + param.getGuestId();
        String itemKey = buildCartItemKey(param.getProductId(), param.getSkuId());
        
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        hashOps.delete(redisKey, itemKey);
        
        return true;
    }

    /**
     * 获取会员购物车
     */
    private CartVO getMemberCart() {
        Long memberId = Long.valueOf(StpMemberUtil.getLoginId().toString());
        List<CartItemVO> items = cartDao.getMemberCartDetailItems(memberId, getMemberLevel());
        
        CartVO cart = createEmptyCart(null);
        cart.setUserType("MEMBER");
        cart.setUserId(memberId.toString());
        cart.setItems(items);
        cart.setMemberLevel(getMemberLevel());
        
        calculateCartTotals(cart);
        
        return cart;
    }

    /**
     * 获取游客购物车
     */
    private CartVO getGuestCart(String guestId) {
        String redisKey = GUEST_CART_KEY_PREFIX + guestId;
        HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
        Map<String, Object> cartItems = hashOps.entries(redisKey);
        
        List<CartItemVO> items = new ArrayList<>();
        // 这里需要根据Redis中的数据构建CartItemVO
        // 由于游客购物车只存储基本信息，需要查询商品详情
        
        CartVO cart = createEmptyCart(guestId);
        cart.setUserType("GUEST");
        cart.setUserId(guestId);
        cart.setItems(items);
        
        calculateCartTotals(cart);
        
        return cart;
    }

    /**
     * 创建空购物车
     */
    private CartVO createEmptyCart(String guestId) {
        CartVO cart = new CartVO();
        cart.setUserType(guestId != null ? "GUEST" : "MEMBER");
        cart.setUserId(guestId != null ? guestId : (isLoggedInMember() ? StpMemberUtil.getLoginId().toString() : null));
        cart.setItems(new ArrayList<>());
        cart.setTotalQuantity(0);
        cart.setAvailableQuantity(0);
        cart.setUnavailableQuantity(0);
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setAvailableAmount(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setFinalAmount(BigDecimal.ZERO);
        cart.setCartStatus("NORMAL");
        cart.setCreateTime(new Date());
        cart.setUpdateTime(new Date());
        cart.setMessages(new ArrayList<>());
        cart.setErrors(new ArrayList<>());
        
        return cart;
    }

    /**
     * 计算购物车统计信息
     */
    private void calculateCartTotals(CartVO cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return;
        }

        int totalQty = 0;
        int availableQty = 0;
        int unavailableQty = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal availableAmount = BigDecimal.ZERO;

        for (CartItemVO item : cart.getItems()) {
            totalQty += item.getQuantity();
            
            if (Boolean.TRUE.equals(item.getAvailable())) {
                availableQty += item.getQuantity();
                if (item.getSubtotal() != null) {
                    availableAmount = availableAmount.add(item.getSubtotal());
                }
            } else {
                unavailableQty += item.getQuantity();
            }
            
            if (item.getSubtotal() != null) {
                totalAmount = totalAmount.add(item.getSubtotal());
            }
        }

        cart.setTotalQuantity(totalQty);
        cart.setAvailableQuantity(availableQty);
        cart.setUnavailableQuantity(unavailableQty);
        cart.setTotalAmount(totalAmount);
        cart.setAvailableAmount(availableAmount);
    }

    /**
     * 应用优惠券到购物车
     */
    private void applyCouponsToCart(CartVO cart, List<Long> couponIds) {
        // 这里实现优惠券应用逻辑
        // 调用优惠券服务计算优惠金额
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (Long couponId : couponIds) {
            // 计算单个优惠券的优惠金额
            // BigDecimal discount = couponService.calculateDiscount(couponId, cart.getAvailableAmount());
            // totalDiscount = totalDiscount.add(discount);
        }
        
        cart.setDiscountAmount(totalDiscount);
    }

    /**
     * 应用积分到购物车
     */
    private void applyPointsToCart(CartVO cart, Integer usePoints) {
        // 这里实现积分应用逻辑
        // 假设100积分=1元
        BigDecimal pointsAmount = new BigDecimal(usePoints).divide(new BigDecimal(100), 2, BigDecimal.ROUND_DOWN);
        cart.setUsedPoints(usePoints);
        cart.setPointsAmount(pointsAmount);
    }

    /**
     * 计算最终金额
     */
    private void calculateFinalAmount(CartVO cart) {
        BigDecimal finalAmount = cart.getAvailableAmount();
        
        if (cart.getDiscountAmount() != null) {
            finalAmount = finalAmount.subtract(cart.getDiscountAmount());
        }
        
        if (cart.getPointsAmount() != null) {
            finalAmount = finalAmount.subtract(cart.getPointsAmount());
        }
        
        if (cart.getShippingFee() != null) {
            finalAmount = finalAmount.add(cart.getShippingFee());
        }
        
        // 确保最终金额不为负数
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }
        
        cart.setFinalAmount(finalAmount);
    }

    /**
     * 构建购物车商品键
     */
    private String buildCartItemKey(Long productId, Long skuId) {
        if (skuId != null) {
            return productId + "_" + skuId;
        } else {
            return productId + "_0";
        }
    }

    /**
     * 获取会员等级
     */
    private Integer getMemberLevel() {
        // 这里应该从会员信息中获取等级
        // 暂时返回默认等级
        return 1;
    }
} 