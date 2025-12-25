package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsDiyAreaParam;
import com.macro.mall.model.PmsDiyArea;
import com.macro.mall.service.PmsDiyAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DIY区域管理Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "PmsDiyAreaController", description = "DIY区域管理")
@RequestMapping("/diyArea")
public class PmsDiyAreaController {
    
    @Autowired
    private PmsDiyAreaService areaService;

    @Operation(summary = "创建DIY区域")
    @PostMapping("/create")
    public CommonResult create(@Validated @RequestBody PmsDiyAreaParam areaParam) {
        PmsDiyArea area = new PmsDiyArea();
        BeanUtils.copyProperties(areaParam, area);
        int count = areaService.create(area);
        if (count > 0) {
            return CommonResult.success(area);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新DIY区域")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody PmsDiyAreaParam areaParam) {
        PmsDiyArea area = new PmsDiyArea();
        BeanUtils.copyProperties(areaParam, area);
        int count = areaService.update(id, area);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除DIY区域")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = areaService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除DIY区域")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = areaService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取DIY区域详情")
    @GetMapping("/{id}")
    public CommonResult<PmsDiyArea> getItem(@PathVariable Long id) {
        PmsDiyArea area = areaService.getItem(id);
        return CommonResult.success(area);
    }

    @Operation(summary = "根据面ID获取DIY区域列表")
    @GetMapping("/listBySurface/{surfaceId}")
    public CommonResult<List<PmsDiyArea>> listBySurfaceId(@PathVariable Long surfaceId) {
        List<PmsDiyArea> areaList = areaService.listBySurfaceId(surfaceId);
        return CommonResult.success(areaList);
    }
}
