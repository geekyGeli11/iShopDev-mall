package com.macro.mall.selfcheck.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.selfcheck.dto.MemberCouponListParam;
import com.macro.mall.selfcheck.dto.MemberCouponVO;

/**
 * 自助收银优惠券服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckCouponService {

    /**
     * 获取会员优惠券列表
     * @param memberId 会员ID
     * @param param 查询参数
     * @return 优惠券分页列表
     */
    CommonPage<MemberCouponVO> getMemberCouponList(Long memberId, MemberCouponListParam param);

    /**
     * 获取会员可用优惠券数量
     * @param memberId 会员ID
     * @return 可用优惠券数量
     */
    int getAvailableCouponCount(Long memberId);

    /**
     * 获取会员即将过期的优惠券数量
     * @param memberId 会员ID
     * @return 即将过期优惠券数量
     */
    int getNearExpiryCouponCount(Long memberId);

    /**
     * 根据优惠券历史ID获取详细信息
     * @param historyId 优惠券历史ID
     * @param memberId 会员ID（用于权限验证）
     * @return 优惠券详细信息
     */
    MemberCouponVO getCouponDetail(Long historyId, Long memberId);

    /**
     * 验证优惠券是否可用
     * @param historyId 优惠券历史ID
     * @param memberId 会员ID
     * @param orderAmount 订单金额
     * @return 是否可用
     */
    boolean validateCouponUsage(Long historyId, Long memberId, java.math.BigDecimal orderAmount);

    /**
     * 获取会员在指定金额下可用的优惠券
     * @param memberId 会员ID
     * @param orderAmount 订单金额
     * @return 可用优惠券列表
     */
    CommonPage<MemberCouponVO> getAvailableCouponsForOrder(Long memberId, java.math.BigDecimal orderAmount);

    /**
     * 使用优惠券
     * @param couponHistoryId 优惠券历史ID
     * @param orderId 订单ID
     * @return 是否使用成功
     */
    boolean useCoupon(Long couponHistoryId, Long orderId);
}