package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsProductBundleDetail;
import com.macro.mall.dto.PmsProductBundleParam;
import com.macro.mall.model.PmsProductBundle;
import com.macro.mall.service.PmsProductBundleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组合商品管理Controller
 */
@Controller
@Tag(name = "PmsProductBundleController", description = "组合商品管理")
@RequestMapping("/productBundle")
public class PmsProductBundleController {

    @Autowired
    private PmsProductBundleService bundleService;

    @Operation(summary = "分页查询组合商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductBundle>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "publishStatus", required = false) Integer publishStatus,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<PmsProductBundle> bundleList = bundleService.list(keyword, publishStatus, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(bundleList));
    }

    @Operation(summary = "创建组合商品")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody PmsProductBundleParam param) {
        int count = bundleService.create(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新组合商品")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable("id") Long id,
                               @Validated @RequestBody PmsProductBundleParam param) {
        int count = bundleService.update(id, param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取组合商品详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductBundleDetail> getDetail(@PathVariable("id") Long id) {
        PmsProductBundleDetail detail = bundleService.getDetail(id);
        if (detail != null) {
            return CommonResult.success(detail);
        }
        return CommonResult.failed("组合商品不存在");
    }

    @Operation(summary = "删除组合商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable("id") Long id) {
        int count = bundleService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除组合商品")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = bundleService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量更新上架状态")
    @RequestMapping(value = "/update/publishStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePublishStatus(@RequestParam("ids") List<Long> ids,
                                            @RequestParam("publishStatus") Integer publishStatus) {
        int count = bundleService.updatePublishStatus(ids, publishStatus);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
