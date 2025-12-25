package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.SmsCoupon;
import com.macro.mall.dto.UmsMemberLevelVO;
import com.macro.mall.service.UmsMemberLevelService;
import com.macro.mall.service.SmsCouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员等级管理Controller
 * Created by macro on 2018/4/26.
 */
@Controller
@Tag(name = "UmsMemberLevelController", description = "会员等级管理")
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {
    @Autowired
    private UmsMemberLevelService memberLevelService;
    @Autowired
    private SmsCouponService couponService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Operation(summary = "查询所有会员等级")
    @ResponseBody
    public CommonResult<List<UmsMemberLevelVO>> list(@RequestParam(value = "defaultStatus", required = false) Integer defaultStatus) {
        List<UmsMemberLevelVO> memberLevelList = memberLevelService.list(defaultStatus);
        return CommonResult.success(memberLevelList);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Operation(summary = "创建会员等级")
    @ResponseBody
    public CommonResult create(@RequestBody UmsMemberLevel memberLevel) {
        int count = memberLevelService.create(memberLevel);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("创建会员等级失败");
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @Operation(summary = "更新会员等级")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody UmsMemberLevel memberLevel) {
        int count = memberLevelService.update(id, memberLevel);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("更新会员等级失败");
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @Operation(summary = "删除会员等级")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = memberLevelService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("删除会员等级失败");
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Operation(summary = "获取会员等级详情")
    @ResponseBody
    public CommonResult<UmsMemberLevel> getItem(@PathVariable Long id) {
        UmsMemberLevel memberLevel = memberLevelService.getItem(id);
        return CommonResult.success(memberLevel);
    }

    @RequestMapping(value = "/coupon/options", method = RequestMethod.GET)
    @Operation(summary = "获取优惠券选项列表")
    @ResponseBody
    public CommonResult<List<SmsCoupon>> getCouponOptions(@RequestParam(value = "keyword", required = false) String keyword) {
        List<SmsCoupon> couponList = couponService.simpleList(keyword, 100);
        return CommonResult.success(couponList);
    }
}
