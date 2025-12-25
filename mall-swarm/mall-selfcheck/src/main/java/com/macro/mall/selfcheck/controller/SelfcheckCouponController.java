package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.selfcheck.dto.MemberCouponListParam;
import com.macro.mall.selfcheck.dto.MemberCouponVO;
import com.macro.mall.selfcheck.service.SelfcheckCouponService;
import com.macro.mall.selfcheck.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 自助收银优惠券Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/coupon")
@Tag(name = "SelfcheckCouponController", description = "自助收银优惠券管理")
public class SelfcheckCouponController {

    @Autowired
    private SelfcheckCouponService couponService;

    @Operation(summary = "获取会员优惠券列表")
    @PostMapping("/list")
    public CommonResult<CommonPage<MemberCouponVO>> getCouponList(@Valid @RequestBody MemberCouponListParam param) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取优惠券列表
            CommonPage<MemberCouponVO> couponPage = couponService.getMemberCouponList(memberId, param);

            log.info("获取会员优惠券列表成功，会员ID：{}，查询条件：{}", memberId, param);
            return CommonResult.success(couponPage);

        } catch (Exception e) {
            log.error("获取会员优惠券列表失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("获取优惠券列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取优惠券统计信息")
    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getCouponStatistics() {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取统计信息
            int availableCount = couponService.getAvailableCouponCount(memberId);
            int nearExpiryCount = couponService.getNearExpiryCouponCount(memberId);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("availableCount", availableCount);
            statistics.put("nearExpiryCount", nearExpiryCount);
            statistics.put("memberId", memberId);

            log.info("获取优惠券统计信息成功，会员ID：{}，可用：{}，即将过期：{}", memberId, availableCount, nearExpiryCount);
            return CommonResult.success(statistics);

        } catch (Exception e) {
            log.error("获取优惠券统计信息失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("获取统计信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取优惠券详情")
    @GetMapping("/detail/{historyId}")
    public CommonResult<MemberCouponVO> getCouponDetail(
            @Parameter(description = "优惠券历史ID", required = true)
            @PathVariable Long historyId) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取优惠券详情
            MemberCouponVO coupon = couponService.getCouponDetail(historyId, memberId);

            log.info("获取优惠券详情成功，优惠券历史ID：{}，会员ID：{}", historyId, memberId);
            return CommonResult.success(coupon);

        } catch (Exception e) {
            log.error("获取优惠券详情失败，优惠券历史ID：{}，错误：{}", historyId, e.getMessage(), e);
            return CommonResult.failed("获取优惠券详情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "验证优惠券是否可用")
    @PostMapping("/validate/{historyId}")
    public CommonResult<Map<String, Object>> validateCoupon(
            @Parameter(description = "优惠券历史ID", required = true)
            @PathVariable Long historyId,
            @Parameter(description = "订单金额", required = true)
            @RequestParam BigDecimal orderAmount) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 验证优惠券
            boolean isValid = couponService.validateCouponUsage(historyId, memberId, orderAmount);

            Map<String, Object> result = new HashMap<>();
            result.put("historyId", historyId);
            result.put("orderAmount", orderAmount);
            result.put("isValid", isValid);
            result.put("message", isValid ? "优惠券可以使用" : "优惠券不可用");

            log.info("验证优惠券结果，优惠券历史ID：{}，会员ID：{}，订单金额：{}，结果：{}", 
                    historyId, memberId, orderAmount, isValid);
            return CommonResult.success(result);

        } catch (Exception e) {
            log.error("验证优惠券失败，优惠券历史ID：{}，订单金额：{}，错误：{}", historyId, orderAmount, e.getMessage(), e);
            return CommonResult.failed("验证优惠券失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取订单可用优惠券")
    @GetMapping("/available")
    public CommonResult<CommonPage<MemberCouponVO>> getAvailableCouponsForOrder(
            @Parameter(description = "订单金额", required = true)
            @RequestParam BigDecimal orderAmount) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 获取可用优惠券列表
            CommonPage<MemberCouponVO> availableCoupons = couponService.getAvailableCouponsForOrder(memberId, orderAmount);

            log.info("获取订单可用优惠券成功，会员ID：{}，订单金额：{}，可用数量：{}", 
                    memberId, orderAmount, availableCoupons.getList().size());
            return CommonResult.success(availableCoupons);

        } catch (Exception e) {
            log.error("获取订单可用优惠券失败，订单金额：{}，错误：{}", orderAmount, e.getMessage(), e);
            return CommonResult.failed("获取可用优惠券失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取未使用优惠券列表")
    @GetMapping("/unused")
    public CommonResult<CommonPage<MemberCouponVO>> getUnusedCoupons(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 构建查询参数
            MemberCouponListParam param = new MemberCouponListParam();
            param.setUseStatus(0); // 0表示未使用
            param.setPageNum(pageNum);
            param.setPageSize(pageSize);
            param.setOrderBy("createTime");
            param.setOrderDirection("desc");

            // 获取未使用优惠券列表
            CommonPage<MemberCouponVO> couponPage = couponService.getMemberCouponList(memberId, param);

            log.info("获取未使用优惠券列表成功，会员ID：{}，数量：{}", memberId, couponPage.getList().size());
            return CommonResult.success(couponPage);

        } catch (Exception e) {
            log.error("获取未使用优惠券列表失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("获取优惠券列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取即将过期优惠券列表")
    @GetMapping("/nearExpiry")
    public CommonResult<CommonPage<MemberCouponVO>> getNearExpiryCoupons(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            // 检查登录状态
            if (!StpMemberUtil.isLogin()) {
                return CommonResult.unauthorized("用户未登录");
            }

            // 获取当前登录的会员ID
            Long memberId = StpMemberUtil.getLoginIdAsLong();

            // 构建查询参数：查询未使用的优惠券，按到期时间排序
            MemberCouponListParam param = new MemberCouponListParam();
            param.setUseStatus(0); // 0表示未使用
            param.setPageNum(pageNum);
            param.setPageSize(pageSize);
            param.setOrderBy("endTime");
            param.setOrderDirection("asc"); // 按到期时间升序，即将过期的在前面

            // 获取优惠券列表
            CommonPage<MemberCouponVO> couponPage = couponService.getMemberCouponList(memberId, param);

            // 过滤出即将过期的优惠券（在VO中已经计算了nearExpiry字段）
            log.info("获取即将过期优惠券列表成功，会员ID：{}，数量：{}", memberId, couponPage.getList().size());
            return CommonResult.success(couponPage);

        } catch (Exception e) {
            log.error("获取即将过期优惠券列表失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("获取优惠券列表失败：" + e.getMessage());
        }
    }
} 