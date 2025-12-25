package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsActivity;
import com.macro.mall.service.CmsActivityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Tag(name = "CmsActivityController", description = "活动管理")
@RequestMapping("/activity")
public class CmsActivityController {

    @Autowired
    private CmsActivityService activityService;

    @Operation(summary = "添加活动")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CmsActivity activity) {
        CmsActivity result = activityService.create(activity);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新活动")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CmsActivity activity) {
        CmsActivity result = activityService.update(id, activity);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除活动")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = activityService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取活动详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<CmsActivity> getById(@PathVariable Long id) {
        CmsActivity result = activityService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取活动列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<CmsActivity>> listByFilters(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<CmsActivity> list = activityService.listByFilters(name, location, startTime, endTime, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
}
