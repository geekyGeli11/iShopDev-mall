package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CmsTraceabilityProduct;
import com.macro.mall.model.CmsTraceabilityProductRelation;
import com.macro.mall.service.CmsTraceabilityProductRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 溯源与产品关系管理Controller
 */
@RestController
@Tag(name = "CmsTraceabilityProductRelationController", description = "溯源与产品关系管理")
@RequestMapping("/traceabilityProductRelation")
public class CmsTraceabilityProductRelationController {

    @Autowired
    private CmsTraceabilityProductRelationService relationService;

    @Operation(summary = "批量添加溯源与产品关系")
    @PostMapping("/create")
    public CommonResult<Integer> create(@RequestBody List<CmsTraceabilityProductRelation> relationList) {
        int count = relationService.create(relationList);
        if (count > 0) {
            return CommonResult.success(count, "添加成功");
        }
        return CommonResult.failed("添加失败");
    }

    @Operation(summary = "更新溯源与产品关系")
    @PostMapping("/update/{id}")
    public CommonResult<Integer> update(@PathVariable Long id, @RequestBody CmsTraceabilityProductRelation relation) {
        int count = relationService.update(id, relation);
        if (count > 0) {
            return CommonResult.success(count, "更新成功");
        }
        return CommonResult.failed("更新失败");
    }

    @Operation(summary = "删除溯源与产品关系")
    @PostMapping("/delete/{id}")
    public CommonResult<Integer> delete(@PathVariable Long id) {
        int count = relationService.delete(id);
        if (count > 0) {
            return CommonResult.success(count, "删除成功");
        }
        return CommonResult.failed("删除失败");
    }

    @Operation(summary = "获取单条溯源与产品关系详情")
    @GetMapping("/{id}")
    public CommonResult<CmsTraceabilityProductRelation> getItem(@PathVariable Long id) {
        CmsTraceabilityProductRelation relation = relationService.getItem(id);
        if (relation != null) {
            return CommonResult.success(relation, "查询成功");
        }
        return CommonResult.failed("未找到相关信息");
    }

    @Operation(summary = "分页查询溯源与产品关系")
    @GetMapping("/list")
    public CommonResult<CommonPage<CmsTraceabilityProduct>> list(
            @RequestParam(value = "traceabilityId", required = false) Long traceabilityId,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<CmsTraceabilityProduct> relationList = relationService.list(traceabilityId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(relationList), "查询成功");
    }

    @Operation(summary = "获取溯源与产品关系的总数")
    @GetMapping("/count")
    public CommonResult<Long> getCount(@RequestParam(value = "traceabilityId", required = false) Long traceabilityId) {
        long count = relationService.getCount(traceabilityId);
        return CommonResult.success(count, "查询成功");
    }
}
