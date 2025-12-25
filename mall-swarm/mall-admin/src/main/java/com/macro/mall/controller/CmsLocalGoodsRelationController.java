package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsLocalGoodsRelation;
import com.macro.mall.service.CmsLocalGoodsRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 本地好物与商品关联管理Controller
 */
@RestController
@Tag(name = "CmsLocalGoodsRelationController", description = "本地好物商品关联管理")
@RequestMapping("/localGoods/relation")
public class CmsLocalGoodsRelationController {
    @Autowired
    private CmsLocalGoodsRelationService relationService;

    @Operation(summary = "批量添加本地好物与商品关联")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody List<CmsLocalGoodsRelation> relationList) {
        int count = relationService.create(relationList);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "添加本地好物与商品关联")
    @PostMapping("/create/{localGoodsId}")
    @ResponseBody
    public CommonResult create(@PathVariable Long localGoodsId, @RequestBody List<Long> productIds) {
        int count = relationService.create(localGoodsId, productIds);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改本地好物与商品关联")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CmsLocalGoodsRelation relation) {
        int count = relationService.update(id, relation);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除本地好物与商品关联")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = relationService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据本地好物ID删除关联")
    @PostMapping("/delete/goods/{localGoodsId}")
    @ResponseBody
    public CommonResult deleteByLocalGoodsId(@PathVariable Long localGoodsId) {
        int count = relationService.deleteByLocalGoodsId(localGoodsId);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取指定好物关联的商品")
    @GetMapping("/list/{localGoodsId}")
    @ResponseBody
    public CommonResult<List<CmsLocalGoodsRelation>> listByLocalGoodsId(@PathVariable Long localGoodsId) {
        List<CmsLocalGoodsRelation> relationList = relationService.listByLocalGoodsId(localGoodsId);
        return CommonResult.success(relationList);
    }
} 