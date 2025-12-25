package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.portal.service.UmsIntegrationChangeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户积分变动历史记录Controller
 */
@Controller
@Tag(name = "UmsIntegrationHistoryController", description = "用户积分变动历史记录管理")
@RequestMapping("/member/integration/history")
public class UmsIntegrationHistoryController {
    
    @Autowired
    private UmsIntegrationChangeHistoryService integrationChangeHistoryService;
    
    @Operation(summary = "获取当前用户积分变动历史记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsIntegrationChangeHistory>> list(
            @RequestParam(value = "type", defaultValue = "0") Integer type,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<UmsIntegrationChangeHistory> historyList = integrationChangeHistoryService.list(type, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(historyList));
    }
    
    @Operation(summary = "获取积分变动历史详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsIntegrationChangeHistory> getItem(@PathVariable Long id) {
        UmsIntegrationChangeHistory history = integrationChangeHistoryService.getItem(id);
        if (history != null) {
            return CommonResult.success(history);
        }
        return CommonResult.failed("没有找到相关记录");
    }
    
    @Operation(summary = "获取指定会员的积分变动记录")
    @RequestMapping(value = "/member/{memberId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsIntegrationChangeHistory>> listByMemberId(
            @PathVariable Long memberId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<UmsIntegrationChangeHistory> historyList = integrationChangeHistoryService.listByMemberId(memberId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(historyList));
    }
    
    @Operation(summary = "获取当前用户积分总数")
    @RequestMapping(value = "/integration", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> getCurrentIntegration() {
        Integer integration = integrationChangeHistoryService.getCurrentIntegration();
        return CommonResult.success(integration);
    }
} 