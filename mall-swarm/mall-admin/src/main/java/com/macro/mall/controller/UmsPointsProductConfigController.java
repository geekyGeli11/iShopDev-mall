package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsPointsProductConfigParam;
import com.macro.mall.model.UmsPointsProductConfig;
import com.macro.mall.service.UmsPointsProductConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分换购商品配置管理Controller
 * Created by macro on 2024/01/20.
 */
@Controller
@Tag(name = "UmsPointsProductConfigController", description = "积分换购商品配置管理")
@RequestMapping("/points/product")
public class UmsPointsProductConfigController {
    
    @Autowired
    private UmsPointsProductConfigService pointsProductConfigService;
    
    @Operation(summary = "添加积分换购商品配置")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody UmsPointsProductConfigParam param) {
        int count = pointsProductConfigService.create(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "修改积分换购商品配置")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody UmsPointsProductConfigParam param) {
        int count = pointsProductConfigService.update(id, param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "删除积分换购商品配置")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = pointsProductConfigService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "批量删除积分换购商品配置")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = pointsProductConfigService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "分页查询积分换购商品配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsPointsProductConfig>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Byte status,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsPointsProductConfig> configList = pointsProductConfigService.list(keyword, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(configList));
    }
    
    @Operation(summary = "获取积分换购商品配置详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsPointsProductConfig> getItem(@PathVariable Long id) {
        UmsPointsProductConfig config = pointsProductConfigService.getItem(id);
        return CommonResult.success(config);
    }
    
    @Operation(summary = "批量修改上架状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("status") Byte status) {
        int count = pointsProductConfigService.updateStatus(ids, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "更新库存")
    @RequestMapping(value = "/updateStock/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStock(@PathVariable Long id,
                                  @RequestParam("totalStock") Integer totalStock) {
        int count = pointsProductConfigService.updateStock(id, totalStock);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "获取所有上架的换购商品")
    @RequestMapping(value = "/listAvailable", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsPointsProductConfig>> listAvailable() {
        List<UmsPointsProductConfig> configList = pointsProductConfigService.listAllAvailable();
        return CommonResult.success(configList);
    }
} 