package com.macro.mall.selfcheck.service;

import com.macro.mall.selfcheck.dto.CartItemParam;
import com.macro.mall.selfcheck.dto.CartVO;
import com.macro.mall.selfcheck.dto.MemberCouponVO;

import java.util.List;

/**
 * 自助收银购物车服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckCartService {

    /**
     * 添加商品到购物车
     * 
     * @param param 购物车商品参数
     * @return 操作结果
     */
    boolean addItem(CartItemParam param);

    /**
     * 更新购物车商品数量
     * 
     * @param param 购物车商品参数
     * @return 操作结果
     */
    boolean updateItem(CartItemParam param);

    /**
     * 删除购物车商品
     * 
     * @param param 购物车商品参数
     * @return 操作结果
     */
    boolean removeItem(CartItemParam param);

    /**
     * 清空购物车
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @return 操作结果
     */
    boolean clearCart(String guestId);

    /**
     * 获取购物车信息
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @return 购物车信息
     */
    CartVO getCart(String guestId);

    /**
     * 计算购物车价格（含优惠券）
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @param couponIds 应用的优惠券ID列表
     * @param usePoints 使用的积分数量
     * @return 计算后的购物车信息
     */
    CartVO calculateCart(String guestId, List<Long> couponIds, Integer usePoints);

    /**
     * 应用优惠券到购物车
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    boolean applyCoupon(String guestId, Long couponId);

    /**
     * 移除购物车优惠券
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @param couponId 优惠券ID
     * @return 操作结果
     */
    boolean removeCoupon(String guestId, Long couponId);

    /**
     * 批量更新购物车
     * 
     * @param items 购物车商品列表
     * @param guestId 游客ID（可选，会员为null）
     * @return 操作结果
     */
    boolean batchUpdate(List<CartItemParam> items, String guestId);

    /**
     * 同步购物车（验证商品有效性、价格、库存等）
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @return 同步后的购物车信息
     */
    CartVO syncCart(String guestId);

    /**
     * 获取购物车商品数量
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @return 商品总数量
     */
    int getCartItemCount(String guestId);

    /**
     * 检查商品是否在购物车中
     * 
     * @param productId 商品ID
     * @param skuId SKU ID（可选）
     * @param guestId 游客ID（可选，会员为null）
     * @return 是否存在
     */
    boolean isProductInCart(Long productId, Long skuId, String guestId);

    /**
     * 获取购物车可用优惠券
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @return 可用优惠券列表
     */
    List<MemberCouponVO> getAvailableCoupons(String guestId);

    /**
     * 删除无效商品（已下架、删除、库存不足等）
     * 
     * @param guestId 游客ID（可选，会员为null）
     * @return 删除的商品数量
     */
    int removeInvalidItems(String guestId);
} 