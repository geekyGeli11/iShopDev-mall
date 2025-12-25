package com.macro.mall.portal.service;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户优惠券管理Service
 * Created by macro on 2018/8/29.
 */
public interface UmsMemberCouponService {
    /**
     * 会员添加优惠券
     */
    @Transactional
    void add(Long couponId);

    /**
     * 系统为指定会员添加优惠券（用于签到奖励等场景）
     */
    @Transactional
    void addForMember(Long memberId, Long couponId);

    /**
     * 获取优惠券历史列表
     */
    List<SmsCouponHistory> listHistory(Integer useStatus);

    /**
     * 根据购物车信息获取可用优惠券
     */
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type);

    /**
     * 根据购物车信息获取可用优惠券（支持学校过滤）
     * @param cartItemList 购物车商品列表
     * @param type 优惠券类型
     * @param filterBySchool 是否按学校过滤
     */
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type, boolean filterBySchool);

    /**
     * 获取当前商品相关优惠券
     */
    List<SmsCoupon> listByProduct(Long productId);

    /**
     * 获取用户优惠券列表
     */
    List<SmsCoupon> list(Integer useStatus);

    /**
     * 获取用户优惠券列表（支持学校过滤）
     * @param useStatus 使用状态
     * @param schoolId 学校ID，为null时不过滤学校
     */
    List<SmsCoupon> list(Integer useStatus, Long schoolId);

    /**
     * 获取当前可领取的优惠券列表
     */
    List<SmsCoupon> listAvailable();

    /**
     * 获取当前可领取的优惠券列表（支持学校过滤）
     * @param schoolId 学校ID，为null时不过滤学校
     */
    List<SmsCoupon> listAvailable(Long schoolId);

    /**
     * 获取优惠券详情
     */
    SmsCoupon getCouponDetail(Long couponId);
}
