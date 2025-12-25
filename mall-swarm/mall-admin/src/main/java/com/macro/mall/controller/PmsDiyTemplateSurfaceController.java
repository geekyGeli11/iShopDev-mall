package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsDiyTemplateSurfaceParam;
import com.macro.mall.model.PmsDiyTemplateSurface;
import com.macro.mall.service.PmsDiyTemplateSurfaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DIY模板面管理Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "PmsDiyTemplateSurfaceController", description = "DIY模板面管理")
@RequestMapping("/diyTemplateSurface")
public class PmsDiyTemplateSurfaceController {
    
    @Autowired
    private PmsDiyTemplateSurfaceService surfaceService;

    @Operation(summary = "创建模板面")
    @PostMapping("/create")
    public CommonResult create(@Validated @RequestBody PmsDiyTemplateSurfaceParam surfaceParam) {
        PmsDiyTemplateSurface surface = new PmsDiyTemplateSurface();
        BeanUtils.copyProperties(surfaceParam, surface);
        int count = surfaceService.create(surface);
        if (count > 0) {
            return CommonResult.success(surface);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新模板面")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody PmsDiyTemplateSurfaceParam surfaceParam) {
        PmsDiyTemplateSurface surface = new PmsDiyTemplateSurface();
        BeanUtils.copyProperties(surfaceParam, surface);
        int count = surfaceService.update(id, surface);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除模板面")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = surfaceService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除模板面")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = surfaceService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取模板面详情")
    @GetMapping("/{id}")
    public CommonResult<PmsDiyTemplateSurface> getItem(@PathVariable Long id) {
        PmsDiyTemplateSurface surface = surfaceService.getItem(id);
        return CommonResult.success(surface);
    }

    @Operation(summary = "根据模板ID获取模板面列表")
    @GetMapping("/listByTemplate/{templateId}")
    public CommonResult<List<PmsDiyTemplateSurface>> listByTemplateId(@PathVariable Long templateId) {
        List<PmsDiyTemplateSurface> surfaceList = surfaceService.listByTemplateId(templateId);
        return CommonResult.success(surfaceList);
    }
}
