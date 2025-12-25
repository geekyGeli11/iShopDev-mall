package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsActivitySignup;
import com.macro.mall.service.CmsActivitySignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动报名管理Controller
 */
@RestController
@Tag(name = "CmsActivitySignupController", description = "活动报名管理")
@RequestMapping("/activitySignup")
public class CmsActivitySignupController {
    @Autowired
    private CmsActivitySignupService activitySignupService;

    @Operation(summary = "添加活动报名")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CmsActivitySignup activitySignup) {
        CmsActivitySignup result = activitySignupService.create(activitySignup);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改活动报名")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CmsActivitySignup activitySignup) {
        CmsActivitySignup result = activitySignupService.update(id, activitySignup);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除活动报名")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = activitySignupService.delete(id);
        if (success) {
            return CommonResult.success(null);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取活动报名详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<CmsActivitySignup> getById(@PathVariable Long id) {
        CmsActivitySignup result = activitySignupService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取活动报名列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<CmsActivitySignup>> list(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "activityId", required = false) Long activityId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<CmsActivitySignup> list = activitySignupService.list(name, phone, activityId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
} 