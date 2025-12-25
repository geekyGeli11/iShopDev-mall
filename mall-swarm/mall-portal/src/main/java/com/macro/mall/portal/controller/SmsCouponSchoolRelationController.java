package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsSchool;
import com.macro.mall.portal.service.SmsCouponSchoolRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券学校关联Controller
 * Created by macro on 2024/12/30.
 */
@Controller
@Tag(name = "SmsCouponSchoolRelationController", description = "优惠券学校关联管理")
@RequestMapping("/couponSchoolRelation")
public class SmsCouponSchoolRelationController {

    @Autowired
    private SmsCouponSchoolRelationService couponSchoolRelationService;

    @Operation(summary = "根据优惠券ID获取关联的学校列表")
    @RequestMapping(value = "/getSchoolsByCoupon/{couponId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsSchool>> getSchoolsByCouponId(@PathVariable Long couponId) {
        List<OmsSchool> schoolList = couponSchoolRelationService.getSchoolsByCouponId(couponId);
        return CommonResult.success(schoolList);
    }

    @Operation(summary = "根据学校ID获取关联的优惠券ID列表")
    @RequestMapping(value = "/getCouponsBySchool/{schoolId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Long>> getCouponIdsBySchoolId(@PathVariable Long schoolId) {
        List<Long> couponIds = couponSchoolRelationService.getCouponIdsBySchoolId(schoolId);
        return CommonResult.success(couponIds);
    }

    @Operation(summary = "检查优惠券是否关联了指定学校")
    @RequestMapping(value = "/checkAssociation", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> checkAssociation(@RequestParam Long couponId, @RequestParam Long schoolId) {
        boolean isAssociated = couponSchoolRelationService.isCouponAssociatedWithSchool(couponId, schoolId);
        return CommonResult.success(isAssociated);
    }

    @Operation(summary = "根据商品ID列表获取关联的学校ID列表")
    @RequestMapping(value = "/getSchoolsByProducts", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<Long>> getSchoolIdsByProductIds(@RequestBody List<Long> productIds) {
        List<Long> schoolIds = couponSchoolRelationService.getSchoolIdsByProductIds(productIds);
        return CommonResult.success(schoolIds);
    }

    @Operation(summary = "根据学校ID列表获取可用的优惠券ID列表")
    @RequestMapping(value = "/getAvailableCouponsBySchools", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<Long>> getAvailableCouponIdsBySchoolIds(@RequestBody List<Long> schoolIds) {
        List<Long> couponIds = couponSchoolRelationService.getAvailableCouponIdsBySchoolIds(schoolIds);
        return CommonResult.success(couponIds);
    }
}
