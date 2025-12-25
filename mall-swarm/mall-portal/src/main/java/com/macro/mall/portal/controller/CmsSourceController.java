package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsActivity;
import com.macro.mall.model.CmsPrincipalPerson;
import com.macro.mall.model.CmsWonderfulMacau;
import com.macro.mall.model.CmsActivitySignup;
import com.macro.mall.portal.domain.LocalGoodsDetail;
import com.macro.mall.portal.service.CmsSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容源管理Controller
 */
@Controller
@Tag(name = "CmsSourceController", description = "内容源管理")
@RequestMapping("/content")
public class CmsSourceController {
    
    @Autowired
    private CmsSourceService cmsSourceService;
    
    @Operation(summary = "根据类型获取内容列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<?>> getList(
            @RequestParam(value = "type", defaultValue = "0") Integer type,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<?> contentList = cmsSourceService.getSourceList(type, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(contentList));
    }
    
    @Operation(summary = "获取本地活动详情")
    @RequestMapping(value = "/activity/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CmsActivity> getActivityDetail(@PathVariable Long id) {
        CmsActivity activity = cmsSourceService.getActivityDetail(id);
        if (activity == null) {
            return CommonResult.failed("活动不存在或已下架");
        }
        return CommonResult.success(activity);
    }
    
    @Operation(summary = "获取本地好物详情")
    @RequestMapping(value = "/localGoods/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<LocalGoodsDetail> getLocalGoodsDetail(@PathVariable Long id) {
        LocalGoodsDetail localGoodsDetail = cmsSourceService.getLocalGoodsDetail(id);
        if (localGoodsDetail == null) {
            return CommonResult.failed("本地好物不存在或已下架");
        }
        return CommonResult.success(localGoodsDetail);
    }
    
    @Operation(summary = "获取精彩濠江详情")
    @RequestMapping(value = "/wonderfulMacau/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CmsWonderfulMacau> getWonderfulMacauDetail(@PathVariable Long id) {
        CmsWonderfulMacau wonderfulMacau = cmsSourceService.getWonderfulMacauDetail(id);
        if (wonderfulMacau == null || !wonderfulMacau.getStatus()) {
            return CommonResult.failed("内容不存在或已下架");
        }
        return CommonResult.success(wonderfulMacau);
    }
    
    @Operation(summary = "获取百大主理人详情")
    @RequestMapping(value = "/principalPerson/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CmsPrincipalPerson> getPrincipalPersonDetail(@PathVariable Long id) {
        CmsPrincipalPerson principalPerson = cmsSourceService.getPrincipalPersonDetail(id);
        if (principalPerson == null || !principalPerson.getStatus()) {
            return CommonResult.failed("主理人不存在或已禁用");
        }
        return CommonResult.success(principalPerson);
    }

    @Operation(summary = "创建活动报名记录")
    @RequestMapping(value = "/activitySignup/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<CmsActivitySignup> createActivitySignup(@RequestBody CmsActivitySignup activitySignup) {
        CmsActivitySignup result = cmsSourceService.createActivitySignup(activitySignup);
        if (result == null) {
            return CommonResult.failed("活动不存在或已下架");
        }
        return CommonResult.success(result);
    }
} 