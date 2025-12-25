package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsDiyMaterialParam;
import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.service.PmsDiyMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DIY素材管理Controller
 * Created by macro on 2024/12/20.
 */
@Controller
@Tag(name = "PmsDiyMaterialController", description = "DIY素材管理")
@RequestMapping("/diyMaterial")
public class PmsDiyMaterialController {
    
    @Autowired
    private PmsDiyMaterialService materialService;

    @Operation(summary = "创建素材")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsDiyMaterialParam materialParam) {
        PmsDiyMaterial material = new PmsDiyMaterial();
        BeanUtils.copyProperties(materialParam, material);
        int count = materialService.create(material);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("创建素材失败");
        }
    }

    @Operation(summary = "更新素材")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody PmsDiyMaterialParam materialParam) {
        PmsDiyMaterial material = new PmsDiyMaterial();
        BeanUtils.copyProperties(materialParam, material);
        int count = materialService.update(id, material);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("更新素材失败");
        }
    }

    @Operation(summary = "删除素材")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = materialService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("删除素材失败");
        }
    }

    @Operation(summary = "批量删除素材")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = materialService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("批量删除素材失败");
        }
    }

    @Operation(summary = "根据ID获取素材")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsDiyMaterial> getById(@PathVariable Long id) {
        PmsDiyMaterial material = materialService.getById(id);
        return CommonResult.success(material);
    }

    @Operation(summary = "分页查询素材")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsDiyMaterial>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsDiyMaterial> materialList = materialService.list(keyword, categoryId, fileType, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(materialList));
    }

    @Operation(summary = "根据分类ID获取素材列表")
    @RequestMapping(value = "/listByCategory/{categoryId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsDiyMaterial>> listByCategory(@PathVariable Long categoryId) {
        List<PmsDiyMaterial> materialList = materialService.listByCategory(categoryId);
        return CommonResult.success(materialList);
    }

    @Operation(summary = "批量修改状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("status") Byte status) {
        int count = materialService.updateStatus(ids, status);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("批量修改状态失败");
        }
    }

    @Operation(summary = "增加使用次数")
    @RequestMapping(value = "/incrementUsage/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult incrementUsageCount(@PathVariable Long id) {
        int count = materialService.incrementUsageCount(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("增加使用次数失败");
        }
    }
}