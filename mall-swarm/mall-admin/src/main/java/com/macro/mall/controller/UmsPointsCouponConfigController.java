package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsPointsCouponConfigParam;
import com.macro.mall.model.UmsPointsCouponConfig;
import com.macro.mall.service.UmsPointsCouponConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分换购优惠券配置管理Controller
 * Created by macro on 2024/01/20.
 */
@Controller
@Tag(name = "UmsPointsCouponConfigController", description = "积分换购优惠券配置管理")
@RequestMapping("/points/coupon")
public class UmsPointsCouponConfigController {
    
    @Autowired
    private UmsPointsCouponConfigService pointsCouponConfigService;
    
    @Operation(summary = "添加积分换购优惠券配置")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody UmsPointsCouponConfigParam param) {
        int count = pointsCouponConfigService.create(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "修改积分换购优惠券配置")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody UmsPointsCouponConfigParam param) {
        int count = pointsCouponConfigService.update(id, param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "删除积分换购优惠券配置")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = pointsCouponConfigService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "批量删除积分换购优惠券配置")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = pointsCouponConfigService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "分页查询积分换购优惠券配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsPointsCouponConfig>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Byte status,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsPointsCouponConfig> configList = pointsCouponConfigService.list(keyword, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(configList));
    }
    
    @Operation(summary = "获取积分换购优惠券配置详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsPointsCouponConfig> getItem(@PathVariable Long id) {
        UmsPointsCouponConfig config = pointsCouponConfigService.getItem(id);
        return CommonResult.success(config);
    }
    
    @Operation(summary = "批量修改启用状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("status") Byte status) {
        int count = pointsCouponConfigService.updateStatus(ids, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "更新发放数量")
    @RequestMapping(value = "/updateCount/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateCount(@PathVariable Long id,
                                  @RequestParam("totalCount") Integer totalCount) {
        int count = pointsCouponConfigService.updateCount(id, totalCount);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "获取所有启用的换购优惠券")
    @RequestMapping(value = "/listAvailable", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsPointsCouponConfig>> listAvailable() {
        List<UmsPointsCouponConfig> configList = pointsCouponConfigService.listAllAvailable();
        return CommonResult.success(configList);
    }
} 