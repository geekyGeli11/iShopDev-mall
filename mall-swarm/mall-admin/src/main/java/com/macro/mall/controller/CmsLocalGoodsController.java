package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CmsLocalGoodsDetail;
import com.macro.mall.dto.CmsLocalGoodsParam;
import com.macro.mall.model.CmsLocalGoods;
import com.macro.mall.model.CmsLocalGoodsRelation;
import com.macro.mall.service.CmsLocalGoodsRelationService;
import com.macro.mall.service.CmsLocalGoodsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Tag(name = "CmsLocalGoodsController", description = "本地好物管理")
@RequestMapping("/localGoods")
public class CmsLocalGoodsController {

    @Autowired
    private CmsLocalGoodsService localGoodsService;
    
    @Autowired
    private CmsLocalGoodsRelationService relationService;

    @Operation(summary = "添加本地好物")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody CmsLocalGoods localGoods) {
        CmsLocalGoods result = localGoodsService.create(localGoods);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "添加本地好物(含商品关联)")
    @PostMapping("/createWithRelation")
    @ResponseBody
    public CommonResult createWithRelation(@RequestBody CmsLocalGoodsParam localGoodsParam) {
        CmsLocalGoods result = localGoodsService.create(localGoodsParam);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "更新本地好物")
    @PostMapping("/update/{id}")
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody CmsLocalGoods localGoods) {
        CmsLocalGoods result = localGoodsService.update(id, localGoods);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "更新本地好物(含商品关联)")
    @PostMapping("/updateWithRelation/{id}")
    @ResponseBody
    public CommonResult updateWithRelation(@PathVariable Long id, @RequestBody CmsLocalGoodsParam localGoodsParam) {
        CmsLocalGoods result = localGoodsService.update(id, localGoodsParam);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除本地好物")
    @PostMapping("/delete/{id}")
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        boolean success = localGoodsService.delete(id);
        if (success) {
            return CommonResult.success(success);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "根据ID获取本地好物详情")
    @GetMapping("/{id}")
    @ResponseBody
    public CommonResult<CmsLocalGoods> getById(@PathVariable Long id) {
        CmsLocalGoods result = localGoodsService.getById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "根据ID获取本地好物详情(包含商品信息)")
    @GetMapping("/detail/{id}")
    @ResponseBody
    public CommonResult<CmsLocalGoodsDetail> getDetailById(@PathVariable Long id) {
        CmsLocalGoodsDetail result = localGoodsService.getDetailById(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "根据本地好物ID获取关联商品")
    @GetMapping("/relation/{id}")
    @ResponseBody
    public CommonResult<List<CmsLocalGoodsRelation>> getRelationByGoodsId(@PathVariable Long id) {
        List<CmsLocalGoodsRelation> relationList = relationService.listByLocalGoodsId(id);
        return CommonResult.success(relationList);
    }

    @Operation(summary = "根据条件分页获取本地好物列表")
    @GetMapping("/list")
    @ResponseBody
    public CommonResult<CommonPage<CmsLocalGoods>> listByFilters(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<CmsLocalGoods> list = localGoodsService.listByFilters(name, type, status, startTime, endTime, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
    
    @Operation(summary = "根据条件分页获取本地好物列表(包含商品信息)")
    @GetMapping("/list/detail")
    @ResponseBody
    public CommonResult<CommonPage<CmsLocalGoodsDetail>> listDetailByFilters(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        List<CmsLocalGoodsDetail> list = localGoodsService.listDetailByFilters(name, type, status, startTime, endTime, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }
}
