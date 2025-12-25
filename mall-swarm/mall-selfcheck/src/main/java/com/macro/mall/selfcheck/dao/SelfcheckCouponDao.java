package com.macro.mall.selfcheck.dao;

import com.macro.mall.selfcheck.dto.MemberCouponVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 自助收银优惠券数据访问层
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckCouponDao {

    /**
     * 获取会员优惠券列表（关联查询）
     * @param memberId 会员ID
     * @param useStatus 使用状态，null表示查询全部
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @param offset 偏移量
     * @param limit 查询数量
     * @return 优惠券列表
     */
    List<MemberCouponVO> getMemberCouponList(@Param("memberId") Long memberId,
                                           @Param("useStatus") Integer useStatus,
                                           @Param("orderBy") String orderBy,
                                           @Param("orderDirection") String orderDirection,
                                           @Param("offset") Integer offset,
                                           @Param("limit") Integer limit);

    /**
     * 获取会员优惠券总数
     * @param memberId 会员ID
     * @param useStatus 使用状态，null表示查询全部
     * @return 优惠券总数
     */
    long getMemberCouponCount(@Param("memberId") Long memberId,
                             @Param("useStatus") Integer useStatus);

    /**
     * 获取会员可用优惠券数量
     * @param memberId 会员ID
     * @return 可用优惠券数量
     */
    int getAvailableCouponCount(@Param("memberId") Long memberId);

    /**
     * 获取会员即将过期的优惠券数量（3天内过期）
     * @param memberId 会员ID
     * @return 即将过期优惠券数量
     */
    int getNearExpiryCouponCount(@Param("memberId") Long memberId);

    /**
     * 根据优惠券历史ID获取详细信息
     * @param historyId 优惠券历史ID
     * @param memberId 会员ID
     * @return 优惠券详细信息
     */
    MemberCouponVO getCouponDetail(@Param("historyId") Long historyId,
                                  @Param("memberId") Long memberId);

    /**
     * 获取会员在指定金额下可用的优惠券
     * @param memberId 会员ID
     * @param orderAmount 订单金额
     * @param offset 偏移量
     * @param limit 查询数量
     * @return 可用优惠券列表
     */
    List<MemberCouponVO> getAvailableCouponsForOrder(@Param("memberId") Long memberId,
                                                    @Param("orderAmount") BigDecimal orderAmount,
                                                    @Param("offset") Integer offset,
                                                    @Param("limit") Integer limit);

    /**
     * 获取会员在指定金额下可用的优惠券总数
     * @param memberId 会员ID
     * @param orderAmount 订单金额
     * @return 可用优惠券总数
     */
    long getAvailableCouponsCountForOrder(@Param("memberId") Long memberId,
                                         @Param("orderAmount") BigDecimal orderAmount);

    /**
     * 使用优惠券
     * @param couponHistoryId 优惠券历史ID
     * @param orderId 订单ID
     * @param useTime 使用时间
     * @return 更新记录数
     */
    int useCoupon(@Param("couponHistoryId") Long couponHistoryId,
                  @Param("orderId") Long orderId,
                  @Param("useTime") java.util.Date useTime);
}