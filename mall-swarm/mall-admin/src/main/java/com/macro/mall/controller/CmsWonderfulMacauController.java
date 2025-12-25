package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsWonderfulMacau;
import com.macro.mall.service.CmsWonderfulMacauService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "CmsWonderfulMacauController", description = "精彩濠江管理")
@RequestMapping("/wonderful-macau")
public class CmsWonderfulMacauController {

    @Autowired
    private CmsWonderfulMacauService wonderfulMacauService;

    @Operation(summary = "添加精彩濠江内容")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CmsWonderfulMacau wonderfulMacau) {
        CmsWonderfulMacau result = wonderfulMacauService.create(wonderfulMacau);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新精彩濠江内容")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CmsWonderfulMacau wonderfulMacau) {
        CmsWonderfulMacau result = wonderfulMacauService.update(id, wonderfulMacau);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除精彩濠江内容")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = wonderfulMacauService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取精彩濠江详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<CmsWonderfulMacau> getById(@PathVariable Long id) {
        CmsWonderfulMacau result = wonderfulMacauService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据条件分页获取精彩濠江列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<CmsWonderfulMacau>> listByFilters(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "isTop", required = false) Boolean isTop,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<CmsWonderfulMacau> list = wonderfulMacauService.listByFilters(title, isTop, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
}
