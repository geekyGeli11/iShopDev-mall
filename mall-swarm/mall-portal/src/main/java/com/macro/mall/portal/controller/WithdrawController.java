package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 提现申请Controller
 */
@RestController
@RequestMapping("/api/withdraw")
@Tag(name = "提现管理", description = "提现申请、提现记录查询等功能")
public class WithdrawController {
    
    @Autowired
    private WithdrawService withdrawService;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "获取可提现金额")
    @GetMapping("/available-amount")
    public CommonResult<BigDecimal> getAvailableAmount() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        BigDecimal availableAmount = withdrawService.getAvailableAmount(currentMember.getId());
        return CommonResult.success(availableAmount);
    }
    
    @Operation(summary = "获取提现配置")
    @GetMapping("/config")
    public CommonResult<Map<String, Object>> getWithdrawConfig() {
        Map<String, Object> config = withdrawService.getWithdrawConfig();
        return CommonResult.success(config);
    }
    
    @Operation(summary = "申请提现")
    @PostMapping("/apply")
    public CommonResult<Boolean> applyWithdraw(@Valid @RequestBody WithdrawApplyParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Boolean result = withdrawService.applyWithdraw(currentMember.getId(), param);
            return CommonResult.success(result, result ? "提现申请提交成功" : "提现申请提交失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取我的提现记录")
    @GetMapping("/my-records")
    public CommonResult<Map<String, Object>> getMyWithdrawRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Byte status) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        CommonPage<Map<String, Object>> records = withdrawService.getMyWithdrawRecords(
                currentMember.getId(), page, size, status);
        
        // 构造前端期望的格式
        Map<String, Object> result = new HashMap<>();
        result.put("list", records.getList());
        result.put("total", records.getTotal());
        result.put("page", records.getPageNum());
        result.put("size", records.getPageSize());
        result.put("hasMore", records.getPageNum() < records.getTotalPage());
        
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取提现申请详情")
    @GetMapping("/detail/{id}")
    public CommonResult<Map<String, Object>> getWithdrawDetail(@PathVariable Long id) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> detail = withdrawService.getWithdrawDetail(id, currentMember.getId());
        if (detail == null) {
            return CommonResult.failed("提现记录不存在");
        }
        
        return CommonResult.success(detail);
    }
    
    @Operation(summary = "取消提现申请")
    @PostMapping("/cancel/{id}")
    public CommonResult<Boolean> cancelWithdraw(@PathVariable Long id) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Boolean result = withdrawService.cancelWithdraw(id, currentMember.getId());
            return CommonResult.success(result, result ? "取消成功" : "取消失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取提现统计")
    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getWithdrawStatistics() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> statistics = withdrawService.getWithdrawStatistics(currentMember.getId());
        return CommonResult.success(statistics);
    }
} 