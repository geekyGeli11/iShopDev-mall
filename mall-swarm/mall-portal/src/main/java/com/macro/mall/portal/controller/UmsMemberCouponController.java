package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.model.SmsCouponHistory;
import com.macro.mall.portal.domain.CartPromotionItem;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import com.macro.mall.portal.service.OmsCartItemService;
import com.macro.mall.portal.service.UmsMemberCouponService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员优惠券管理Controller
 * Created by macro on 2018/8/29.
 */
@Controller
@Tag(name = "UmsMemberCouponController", description = "用户优惠券管理")
@RequestMapping("/member/coupon")
public class UmsMemberCouponController {
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberService memberService;

    @Operation(summary = "领取指定优惠券")
    @RequestMapping(value = "/add/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@PathVariable Long couponId) {
        try {
            memberCouponService.add(couponId);
            // 返回成功状态和领取信息
            Map<String, Object> result = new HashMap<>();
            result.put("couponId", couponId);
            result.put("received", true);
            result.put("message", "领取成功");
            return CommonResult.success(result, "领取成功");
        } catch (com.macro.mall.common.exception.ApiException e) {
            // 捕获自定义的API异常，返回200状态码但包含错误信息
            Map<String, Object> result = new HashMap<>();
            result.put("couponId", couponId);
            result.put("received", false);
            result.put("message", e.getMessage());

            // 特殊处理已达到领取上限的情况
            if (e.getMessage().contains("已经达到领取次数上限")) {
                result.put("alreadyReceived", true);
            } else {
                result.put("alreadyReceived", false);
            }

            return CommonResult.success(result, e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，也返回200状态码
            Map<String, Object> result = new HashMap<>();
            result.put("couponId", couponId);
            result.put("received", false);
            result.put("message", "领取优惠券异常，请稍后重试");
            result.put("alreadyReceived", false);
            return CommonResult.success(result, "领取优惠券异常，请稍后重试");
        }
    }

    @Operation(summary = "获取会员优惠券历史列表")
    @Parameter(name = "useStatus", description = "优惠券筛选类型:-1->全部；0->未使用；1->已使用；2->已过期",
            in = ParameterIn.QUERY,schema = @Schema(type = "integer",allowableValues = {"-1","0","1","2"}))
    @RequestMapping(value = "/listHistory", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsCouponHistory>> listHistory(@RequestParam(value = "useStatus", required = false) Integer useStatus) {
        List<SmsCouponHistory> couponHistoryList = memberCouponService.listHistory(useStatus);
        return CommonResult.success(couponHistoryList);
    }

    @Operation(summary = "获取会员优惠券列表")
    @Parameter(name = "useStatus", description = "优惠券筛选类型:-1->全部；0->未使用；1->已使用；2->已过期",
            in = ParameterIn.QUERY,schema = @Schema(type = "integer",allowableValues = {"-1","0","1","2"}))
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsCoupon>> list(@RequestParam(value = "useStatus", required = false) Integer useStatus,
                                              @RequestParam(value = "schoolId", required = false) Long schoolId) {
        List<SmsCoupon> couponList = memberCouponService.list(useStatus, schoolId);
        return CommonResult.success(couponList);
    }

    @Operation(summary = "获取登录会员购物车的相关优惠券")
    @Parameter(name = "type", description = "使用可用:0->不可用；1->可用",
            in = ParameterIn.PATH,schema = @Schema(type = "integer",defaultValue = "1",allowableValues = {"0","1"}))
    @RequestMapping(value = "/list/cart/{type}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsCouponHistoryDetail>> listCart(@PathVariable Integer type) {
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(memberService.getCurrentMember().getId(), null);
        List<SmsCouponHistoryDetail> couponHistoryList = memberCouponService.listCart(cartPromotionItemList, type);
        return CommonResult.success(couponHistoryList);
    }

    @Operation(summary = "获取当前商品相关优惠券")
    @RequestMapping(value = "/listByProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsCoupon>> listByProduct(@PathVariable Long productId) {
        List<SmsCoupon> couponHistoryList = memberCouponService.listByProduct(productId);
        return CommonResult.success(couponHistoryList);
    }

    @Operation(summary = "获取当前可领取的优惠券列表")
    @RequestMapping(value = "/listAvailable", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsCoupon>> listAvailable(@RequestParam(value = "schoolId", required = false) Long schoolId) {
        List<SmsCoupon> coupons = memberCouponService.listAvailable(schoolId);
        return CommonResult.success(coupons);
    }

    @Operation(summary = "获取优惠券详情")
    @RequestMapping(value = "/detail/{couponId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SmsCoupon> getCouponDetail(@PathVariable Long couponId) {
        try {
            SmsCoupon coupon = memberCouponService.getCouponDetail(couponId);
            if (coupon == null) {
                return CommonResult.failed("优惠券不存在或已下架");
            }
            return CommonResult.success(coupon);
        } catch (Exception e) {
            return CommonResult.failed("获取优惠券详情失败：" + e.getMessage());
        }
    }
}
