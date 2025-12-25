package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsTraceabilityList;
import com.macro.mall.service.CmsTraceabilityListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Tag(name = "CmsTraceabilityListController", description = "溯源管理")
@RequestMapping("/traceability")
public class CmsTraceabilityListController {

    @Autowired
    private CmsTraceabilityListService traceabilityListService;

    @Operation(summary = "添加溯源记录")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CmsTraceabilityList traceabilityList) {
        CmsTraceabilityList result = traceabilityListService.create(traceabilityList);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新溯源记录")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Integer id, @RequestBody CmsTraceabilityList traceabilityList) {
        CmsTraceabilityList result = traceabilityListService.update(id, traceabilityList);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除溯源记录")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Integer id) {
        boolean success = traceabilityListService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取溯源记录")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<CmsTraceabilityList> getById(@PathVariable Integer id) {
        CmsTraceabilityList result = traceabilityListService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取溯源记录")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<CmsTraceabilityList>> listByFilters(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<CmsTraceabilityList> list = traceabilityListService.listByFilters(title, category, startTime, endTime, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
}
