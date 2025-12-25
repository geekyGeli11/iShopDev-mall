package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsDiyMaterialCategoryParam;
import com.macro.mall.model.PmsDiyMaterialCategory;
import com.macro.mall.service.PmsDiyMaterialCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * DIY素材分类管理Controller
 * Created by macro on 2024/12/20.
 */
@Controller
@Tag(name = "PmsDiyMaterialCategoryController", description = "DIY素材分类管理")
@RequestMapping("/diyMaterialCategory")
public class PmsDiyMaterialCategoryController {
    
    @Autowired
    private PmsDiyMaterialCategoryService categoryService;

    @Operation(summary = "创建素材分类")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsDiyMaterialCategoryParam categoryParam) {
        PmsDiyMaterialCategory category = new PmsDiyMaterialCategory();
        BeanUtils.copyProperties(categoryParam, category);
        int count = categoryService.create(category);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("创建素材分类失败");
        }
    }

    @Operation(summary = "更新素材分类")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody PmsDiyMaterialCategoryParam categoryParam) {
        PmsDiyMaterialCategory category = new PmsDiyMaterialCategory();
        BeanUtils.copyProperties(categoryParam, category);
        int count = categoryService.update(id, category);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("更新素材分类失败");
        }
    }

    @Operation(summary = "删除素材分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = categoryService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("删除素材分类失败");
        }
    }

    @Operation(summary = "批量删除素材分类")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = categoryService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("批量删除素材分类失败");
        }
    }

    @Operation(summary = "根据ID获取素材分类")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsDiyMaterialCategory> getById(@PathVariable Long id) {
        PmsDiyMaterialCategory category = categoryService.getById(id);
        return CommonResult.success(category);
    }

    @Operation(summary = "分页查询素材分类")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsDiyMaterialCategory>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsDiyMaterialCategory> categoryList = categoryService.list(keyword, type, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(categoryList));
    }

    @Operation(summary = "获取所有启用的素材分类")
    @RequestMapping(value = "/listEnabled", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsDiyMaterialCategory>> listEnabled() {
        List<PmsDiyMaterialCategory> categoryList = categoryService.listEnabled();
        return CommonResult.success(categoryList);
    }

    @Operation(summary = "批量修改状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("status") Byte status) {
        int count = categoryService.updateStatus(ids, status);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("批量修改状态失败");
        }
    }
}