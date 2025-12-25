package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsProductDamageReason;
import com.macro.mall.service.PmsProductDamageReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报损原因配置Controller
 */
@Controller
@Tag(name = "PmsProductDamageReasonController", description = "报损原因配置")
@RequestMapping("/damage/reason")
public class PmsProductDamageReasonController {
    
    @Autowired
    private PmsProductDamageReasonService damageReasonService;
    
    @Operation(summary = "创建报损原因")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsProductDamageReason reason) {
        int count = damageReasonService.create(reason);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "更新报损原因")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @RequestBody PmsProductDamageReason reason) {
        int count = damageReasonService.update(id, reason);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "删除报损原因")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = damageReasonService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "获取所有启用的报损原因")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductDamageReason>> listAll() {
        return CommonResult.success(damageReasonService.listAll());
    }
    
    @Operation(summary = "按类型获取报损原因")
    @RequestMapping(value = "/listByType", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductDamageReason>> listByType(@RequestParam Integer type) {
        return CommonResult.success(damageReasonService.listByType(type));
    }
    
    @Operation(summary = "分页查询报损原因")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductDamageReason>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProductDamageReason> list = damageReasonService.list(keyword, type, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(list));
    }
    
    @Operation(summary = "获取报损原因详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductDamageReason> getItem(@PathVariable Long id) {
        return CommonResult.success(damageReasonService.getItem(id));
    }
    
    @Operation(summary = "更新状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        int count = damageReasonService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
