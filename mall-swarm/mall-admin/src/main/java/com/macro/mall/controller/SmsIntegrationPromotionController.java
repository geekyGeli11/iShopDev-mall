package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.SmsIntegrationPromotion;
import com.macro.mall.service.SmsIntegrationPromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分营销活动管理Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "SmsIntegrationPromotionController", description = "积分营销活动管理")
@RequestMapping("/integration/promotion")
public class SmsIntegrationPromotionController {
    
    @Autowired
    private SmsIntegrationPromotionService integrationPromotionService;

    @Operation(summary = "添加积分营销活动")
    @PostMapping("/create")
    public CommonResult create(@RequestBody SmsIntegrationPromotion integrationPromotion) {
        int count = integrationPromotionService.create(integrationPromotion);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "修改积分营销活动")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id, @RequestBody SmsIntegrationPromotion integrationPromotion) {
        int count = integrationPromotionService.update(id, integrationPromotion);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "删除积分营销活动")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = integrationPromotionService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "批量删除积分营销活动")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = integrationPromotionService.deleteBatch(ids);
        return CommonResult.success(count);
    }

    @Operation(summary = "根据ID获取积分营销活动详情")
    @GetMapping("/{id}")
    public CommonResult<SmsIntegrationPromotion> getItem(@PathVariable Long id) {
        SmsIntegrationPromotion integrationPromotion = integrationPromotionService.getItem(id);
        return CommonResult.success(integrationPromotion);
    }

    @Operation(summary = "分页查询积分营销活动")
    @GetMapping("/list")
    public CommonResult<CommonPage<SmsIntegrationPromotion>> list(
            @Parameter(description = "活动名称") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "活动状态：false->禁用；true->启用") @RequestParam(value = "status", required = false) Boolean status,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        List<SmsIntegrationPromotion> integrationPromotionList = integrationPromotionService.list(name, status, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(integrationPromotionList));
    }

    @Operation(summary = "修改积分营销活动状态")
    @PostMapping("/update/status/{id}")
    public CommonResult updateStatus(@PathVariable Long id, @RequestParam Boolean status) {
        int count = integrationPromotionService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "获取所有启用的积分营销活动")
    @GetMapping("/list/enabled")
    public CommonResult<List<SmsIntegrationPromotion>> listEnabled() {
        List<SmsIntegrationPromotion> integrationPromotionList = integrationPromotionService.listEnabled();
        return CommonResult.success(integrationPromotionList);
    }

    @Operation(summary = "获取当前生效的积分营销活动")
    @GetMapping("/current")
    public CommonResult<List<SmsIntegrationPromotion>> getCurrentActive() {
        List<SmsIntegrationPromotion> integrationPromotionList = integrationPromotionService.getCurrentActive();
        return CommonResult.success(integrationPromotionList);
    }
}
