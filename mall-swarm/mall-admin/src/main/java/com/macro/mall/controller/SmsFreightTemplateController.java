package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.SmsFreightTemplate;
import com.macro.mall.service.SmsFreightTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 运费模板管理Controller
 * Created by AI Assistant
 */
@Controller
@Tag(name = "SmsFreightTemplateController", description = "运费模板管理")
@RequestMapping("/freight/template")
public class SmsFreightTemplateController {
    
    private final SmsFreightTemplateService freightTemplateService;
    
    public SmsFreightTemplateController(SmsFreightTemplateService freightTemplateService) {
        this.freightTemplateService = freightTemplateService;
    }
    
    @Operation(summary = "创建运费模板")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Valid @RequestBody SmsFreightTemplateParam param) {
        int count = freightTemplateService.create(param);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("创建运费模板失败");
        }
    }
    
    @Operation(summary = "更新运费模板")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id, @Valid @RequestBody SmsFreightTemplateParam param) {
        int count = freightTemplateService.update(id, param);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("更新运费模板失败");
        }
    }
    
    @Operation(summary = "删除运费模板")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        try {
            int count = freightTemplateService.delete(id);
            if (count > 0) {
                return CommonResult.success(count);
            } else {
                return CommonResult.failed("删除运费模板失败");
            }
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "批量删除运费模板")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        try {
            int count = freightTemplateService.delete(ids);
            if (count > 0) {
                return CommonResult.success(count);
            } else {
                return CommonResult.failed("批量删除运费模板失败");
            }
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "分页查询运费模板")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<SmsFreightTemplate>> list(SmsFreightTemplateQueryParam queryParam,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<SmsFreightTemplate> templateList = freightTemplateService.list(queryParam, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(templateList));
    }
    
    @Operation(summary = "获取运费模板详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SmsFreightTemplateVO> getTemplateDetail(@PathVariable Long id) {
        SmsFreightTemplateVO templateDetail = freightTemplateService.getTemplateDetail(id);
        if (templateDetail != null) {
            return CommonResult.success(templateDetail);
        } else {
            return CommonResult.failed("运费模板不存在");
        }
    }
    
    @Operation(summary = "获取所有启用的运费模板")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsFreightTemplate>> listAllEnabled() {
        List<SmsFreightTemplate> templateList = freightTemplateService.listAllEnabled();
        return CommonResult.success(templateList);
    }
    
    @Operation(summary = "修改运费模板状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids,
                                   @RequestParam("status") Byte status) {
        int count = freightTemplateService.updateStatus(ids, status);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("修改状态失败");
        }
    }
    
    @Operation(summary = "计算运费")
    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<SmsFreightCalculateResult> calculateFreight(@Valid @RequestBody SmsFreightCalculateParam param) {
        try {
            SmsFreightCalculateResult result = freightTemplateService.calculateFreight(param);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("运费计算失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取默认运费模板")
    @RequestMapping(value = "/default", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SmsFreightTemplate> getDefaultTemplate() {
        SmsFreightTemplate defaultTemplate = freightTemplateService.getDefaultTemplate();
        return CommonResult.success(defaultTemplate);
    }
    
    @Operation(summary = "设置默认运费模板")
    @RequestMapping(value = "/setDefault/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult setDefaultTemplate(@PathVariable Long id) {
        try {
            int count = freightTemplateService.setDefaultTemplate(id);
            if (count > 0) {
                return CommonResult.success(count);
            } else {
                return CommonResult.failed("设置默认模板失败");
            }
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "刷新全部商品运费模板")
    @RequestMapping(value = "/refreshAllProduct/{templateId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult refreshAllProductFreightTemplate(@PathVariable Long templateId) {
        try {
            int count = freightTemplateService.refreshAllProductFreightTemplate(templateId);
            return CommonResult.success(count, "成功更新" + count + "个商品的运费模板");
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }
} 