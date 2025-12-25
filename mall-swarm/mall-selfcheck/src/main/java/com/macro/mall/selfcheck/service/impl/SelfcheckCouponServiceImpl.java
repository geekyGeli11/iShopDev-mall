package com.macro.mall.selfcheck.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.selfcheck.dao.SelfcheckCouponDao;
import com.macro.mall.selfcheck.dto.MemberCouponListParam;
import com.macro.mall.selfcheck.dto.MemberCouponVO;
import com.macro.mall.selfcheck.service.SelfcheckCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 自助收银优惠券服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckCouponServiceImpl implements SelfcheckCouponService {

    @Autowired
    private SelfcheckCouponDao couponDao;

    @Override
    public CommonPage<MemberCouponVO> getMemberCouponList(Long memberId, MemberCouponListParam param) {
        try {
            // 计算偏移量
            int offset = (param.getPageNum() - 1) * param.getPageSize();
            
            // 获取优惠券列表
            List<MemberCouponVO> couponList = couponDao.getMemberCouponList(
                    memberId, 
                    param.getUseStatus(), 
                    param.getOrderBy(),
                    param.getOrderDirection(),
                    offset, 
                    param.getPageSize()
            );

            // 获取总数
            long total = couponDao.getMemberCouponCount(memberId, param.getUseStatus());

            // 构建分页结果
            CommonPage<MemberCouponVO> result = new CommonPage<>();
            result.setPageNum(param.getPageNum());
            result.setPageSize(param.getPageSize());
            result.setTotal(total);
            result.setTotalPage((int) Math.ceil((double) total / param.getPageSize()));
            result.setList(couponList);

            log.info("获取会员优惠券列表成功，会员ID：{}，数量：{}", memberId, couponList.size());
            return result;

        } catch (Exception e) {
            log.error("获取会员优惠券列表失败，会员ID：{}，错误：{}", memberId, e.getMessage(), e);
            throw new RuntimeException("获取优惠券列表失败");
        }
    }

    @Override
    public int getAvailableCouponCount(Long memberId) {
        try {
            return couponDao.getAvailableCouponCount(memberId);
        } catch (Exception e) {
            log.error("获取会员可用优惠券数量失败，会员ID：{}，错误：{}", memberId, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int getNearExpiryCouponCount(Long memberId) {
        try {
            return couponDao.getNearExpiryCouponCount(memberId);
        } catch (Exception e) {
            log.error("获取会员即将过期优惠券数量失败，会员ID：{}，错误：{}", memberId, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public MemberCouponVO getCouponDetail(Long historyId, Long memberId) {
        try {
            MemberCouponVO coupon = couponDao.getCouponDetail(historyId, memberId);
            if (coupon == null) {
                throw new RuntimeException("优惠券不存在或无权限访问");
            }
            
            log.info("获取优惠券详情成功，优惠券历史ID：{}，会员ID：{}", historyId, memberId);
            return coupon;

        } catch (Exception e) {
            log.error("获取优惠券详情失败，优惠券历史ID：{}，会员ID：{}，错误：{}", historyId, memberId, e.getMessage(), e);
            throw new RuntimeException("获取优惠券详情失败：" + e.getMessage());
        }
    }

    @Override
    public boolean validateCouponUsage(Long historyId, Long memberId, BigDecimal orderAmount) {
        try {
            // 获取优惠券详情
            MemberCouponVO coupon = getCouponDetail(historyId, memberId);
            
            if (coupon == null) {
                log.warn("优惠券验证失败：优惠券不存在，优惠券历史ID：{}，会员ID：{}", historyId, memberId);
                return false;
            }

            // 检查使用状态
            if (coupon.getUseStatus() != 0) {
                log.warn("优惠券验证失败：优惠券已使用或已过期，优惠券历史ID：{}，使用状态：{}", historyId, coupon.getUseStatus());
                return false;
            }

            // 检查有效期
            Date now = new Date();
            if (coupon.getStartTime() != null && coupon.getStartTime().after(now)) {
                log.warn("优惠券验证失败：优惠券尚未生效，优惠券历史ID：{}，开始时间：{}", historyId, coupon.getStartTime());
                return false;
            }
            
            if (coupon.getEndTime() != null && coupon.getEndTime().before(now)) {
                log.warn("优惠券验证失败：优惠券已过期，优惠券历史ID：{}，结束时间：{}", historyId, coupon.getEndTime());
                return false;
            }

            // 检查使用门槛
            if (coupon.getMinPoint() != null && 
                orderAmount != null && 
                orderAmount.compareTo(coupon.getMinPoint()) < 0) {
                log.warn("优惠券验证失败：订单金额未达到使用门槛，优惠券历史ID：{}，订单金额：{}，使用门槛：{}", 
                        historyId, orderAmount, coupon.getMinPoint());
                return false;
            }

            log.info("优惠券验证通过，优惠券历史ID：{}，会员ID：{}，订单金额：{}", historyId, memberId, orderAmount);
            return true;

        } catch (Exception e) {
            log.error("优惠券验证异常，优惠券历史ID：{}，会员ID：{}，错误：{}", historyId, memberId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public CommonPage<MemberCouponVO> getAvailableCouponsForOrder(Long memberId, BigDecimal orderAmount) {
        try {
            // 默认参数：第1页，每页10条
            int pageNum = 1;
            int pageSize = 10;
            int offset = (pageNum - 1) * pageSize;

            // 获取可用优惠券列表
            List<MemberCouponVO> couponList = couponDao.getAvailableCouponsForOrder(
                    memberId, 
                    orderAmount, 
                    offset, 
                    pageSize
            );

            // 获取总数
            long total = couponDao.getAvailableCouponsCountForOrder(memberId, orderAmount);

            // 构建分页结果
            CommonPage<MemberCouponVO> result = new CommonPage<>();
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setTotal(total);
            result.setTotalPage((int) Math.ceil((double) total / pageSize));
            result.setList(couponList);

            log.info("获取订单可用优惠券列表成功，会员ID：{}，订单金额：{}，可用数量：{}", memberId, orderAmount, couponList.size());
            return result;

        } catch (Exception e) {
            log.error("获取订单可用优惠券列表失败，会员ID：{}，订单金额：{}，错误：{}", memberId, orderAmount, e.getMessage(), e);
            throw new RuntimeException("获取可用优惠券列表失败");
        }
    }

    @Override
    public boolean useCoupon(Long couponHistoryId, Long orderId) {
        try {
            log.info("开始使用优惠券，优惠券历史ID：{}，订单ID：{}", couponHistoryId, orderId);

            // 更新优惠券使用状态
            int updateCount = couponDao.useCoupon(couponHistoryId, orderId, new Date());

            if (updateCount > 0) {
                log.info("优惠券使用成功，优惠券历史ID：{}，订单ID：{}", couponHistoryId, orderId);
                return true;
            } else {
                log.warn("优惠券使用失败，可能已被使用或不存在，优惠券历史ID：{}，订单ID：{}", couponHistoryId, orderId);
                return false;
            }

        } catch (Exception e) {
            log.error("使用优惠券异常，优惠券历史ID：{}，订单ID：{}，错误：{}", couponHistoryId, orderId, e.getMessage(), e);
            return false;
        }
    }
}