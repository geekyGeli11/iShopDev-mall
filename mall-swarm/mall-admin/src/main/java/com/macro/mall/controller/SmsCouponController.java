package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.SmsCouponListVO;
import com.macro.mall.dto.SmsCouponParam;
import com.macro.mall.dto.SmsCouponShareResult;
 import com.macro.mall.mapper.OmsSchoolMapper;
 import com.macro.mall.mapper.SmsCouponSchoolRelationMapper;
 import com.macro.mall.model.OmsSchool;
 import com.macro.mall.model.OmsSchoolExample;
 import com.macro.mall.model.SmsCouponSchoolRelation;
 import com.macro.mall.model.SmsCouponSchoolRelationExample;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.service.SmsCouponService;
import com.macro.mall.util.CouponApplicabilityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 优惠券管理Controller
 * Created by macro on 2018/8/28.
 */
@Controller
@Tag(name = "SmsCouponController", description = "优惠券管理")
@RequestMapping("/coupon")
public class SmsCouponController {
    @Autowired
    private SmsCouponService couponService;
    @Autowired
    private SmsCouponSchoolRelationMapper couponSchoolRelationMapper;
    @Autowired
    private OmsSchoolMapper schoolMapper;
    @Autowired
    private CouponApplicabilityUtil couponApplicabilityUtil;
    @Operation(summary = "添加优惠券")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult add(@RequestBody SmsCouponParam couponParam) {
        int count = couponService.create(couponParam);
        if(count>0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除优惠券")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = couponService.delete(id);
        if(count>0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改优惠券")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,@RequestBody SmsCouponParam couponParam) {
        int count = couponService.update(id,couponParam);
        if(count>0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据优惠券名称和类型分页获取优惠券列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<SmsCouponListVO>> list(
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "type",required = false) Integer type,
            @RequestParam(value = "schoolId",required = false) Long schoolId,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<SmsCouponListVO> couponList = couponService.listWithSchools(name,type,schoolId,pageSize,pageNum);
        return CommonResult.success(CommonPage.restPage(couponList));
    }

    @Operation(summary = "获取单个优惠券的详细信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SmsCouponParam> getItem(@PathVariable Long id) {
        SmsCouponParam couponParam = couponService.getItem(id);
        return CommonResult.success(couponParam);
    }

    @Operation(summary="简易优惠券列表")
    @GetMapping("/simple/list")
    @ResponseBody
    public CommonResult<List<SmsCoupon>> simpleList(@RequestParam(value="keyword",required=false) String keyword,
                                                    @RequestParam(value="limit",defaultValue="20") Integer limit){
        return CommonResult.success(couponService.simpleList(keyword,limit));
    }

    @Operation(summary = "生成优惠券分享信息")
    @RequestMapping(value = "/generateShareInfo/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<SmsCouponShareResult> generateShareInfo(@PathVariable Long couponId) {
        try {
            SmsCouponShareResult shareResult = couponService.generateShareInfo(couponId);
            return CommonResult.success(shareResult, "分享信息生成成功");
        } catch (Exception e) {
            return CommonResult.failed("生成分享信息失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取优惠券关联的学校列表")
    @RequestMapping(value = "/getSchools/{couponId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsSchool>> getCouponSchools(@PathVariable Long couponId) {
        try {
            // 查询优惠券学校关联关系
            SmsCouponSchoolRelationExample example = new SmsCouponSchoolRelationExample();
            example.createCriteria().andCouponIdEqualTo(couponId);
            List<SmsCouponSchoolRelation> relations = couponSchoolRelationMapper.selectByExample(example);

            if (relations.isEmpty()) {
                return CommonResult.success(new ArrayList<>());
            }

            // 获取学校ID列表
            List<Long> schoolIds = relations.stream()
                    .map(SmsCouponSchoolRelation::getSchoolId)
                    .collect(Collectors.toList());

            // 查询学校信息
            OmsSchoolExample schoolExample = new OmsSchoolExample();
            schoolExample.createCriteria().andIdIn(schoolIds).andStatusEqualTo(true);
            List<OmsSchool> schools = schoolMapper.selectByExample(schoolExample);

            return CommonResult.success(schools);
        } catch (Exception e) {
            return CommonResult.failed("获取优惠券关联学校失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新优惠券关联的学校")
    @RequestMapping(value = "/updateSchools/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateCouponSchools(@PathVariable Long couponId, @RequestBody List<Long> schoolIds) {
        try {
            boolean result = couponService.updateCouponSchools(couponId, schoolIds);
            if (result) {
                return CommonResult.success(null, "更新学校关联成功");
            } else {
                return CommonResult.failed("更新学校关联失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("更新学校关联失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查优惠券适用性")
    @RequestMapping(value = "/checkApplicability/{couponId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Boolean> checkCouponApplicability(
            @PathVariable Long couponId,
            @RequestParam Long productId,
            @RequestParam Long productCategoryId) {
        try {
            SmsCoupon coupon = couponService.getItem(couponId);
            if (coupon == null) {
                return CommonResult.failed("优惠券不存在");
            }

            boolean applicable = couponApplicabilityUtil.isCouponApplicable(coupon, productId, productCategoryId);
            return CommonResult.success(applicable);
        } catch (Exception e) {
            return CommonResult.failed("检查优惠券适用性失败：" + e.getMessage());
        }
    }

    @Operation(summary = "计算优惠金额")
    @RequestMapping(value = "/calculateDiscount/{couponId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<BigDecimal> calculateDiscountAmount(
            @PathVariable Long couponId,
            @RequestParam BigDecimal orderAmount) {
        try {
            SmsCoupon coupon = couponService.getItem(couponId);
            if (coupon == null) {
                return CommonResult.failed("优惠券不存在");
            }

            BigDecimal discountAmount = couponApplicabilityUtil.calculateDiscountAmount(coupon, orderAmount);
            return CommonResult.success(discountAmount);
        } catch (Exception e) {
            return CommonResult.failed("计算优惠金额失败：" + e.getMessage());
        }
    }
}
