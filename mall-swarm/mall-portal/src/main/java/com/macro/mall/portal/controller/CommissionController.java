package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.CommissionRecord;
import com.macro.mall.portal.domain.CommissionSummary;
import com.macro.mall.portal.domain.WithdrawApplyResult;
import com.macro.mall.portal.domain.WithdrawRecord;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.portal.service.CommissionService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 佣金管理Controller
 */
@RestController
@RequestMapping("/api/commission")
@Tag(name = "佣金管理", description = "佣金记录、提现申请等功能")
public class CommissionController {
    
    @Autowired
    private CommissionService commissionService;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "获取我的佣金记录")
    @GetMapping("/my-records")
    public CommonResult<List<CommissionRecord>> getMyCommissionRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        List<CommissionRecord> result = commissionService.getMyCommissionRecords(
            currentMember.getId(), page, size, type, startDate, endDate);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取我的佣金汇总")
    @GetMapping("/my-summary")
    public CommonResult<CommissionSummary> getMyCommissionSummary() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        CommissionSummary result = commissionService.getMyCommissionSummary(currentMember.getId());
        return CommonResult.success(result);
    }
    
    @Operation(summary = "申请佣金提现")
    @PostMapping("/withdraw")
    public CommonResult<WithdrawApplyResult> applyWithdraw(@Valid @RequestBody WithdrawApplyParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            WithdrawApplyResult result = commissionService.applyWithdraw(currentMember.getId(), param);
            return CommonResult.success(result, "提现申请提交成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取提现历史")
    @GetMapping("/withdraw/history")
    public CommonResult<List<WithdrawRecord>> getWithdrawHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String timeRange) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        List<WithdrawRecord> result = commissionService.getWithdrawHistory(
            currentMember.getId(), page, size, status, timeRange);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取提现统计")
    @GetMapping("/withdraw/stats")
    public CommonResult<Map<String, Object>> getWithdrawStats() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> result = commissionService.getWithdrawStats(currentMember.getId());
        return CommonResult.success(result);
    }
    
    @Operation(summary = "取消提现申请")
    @PostMapping("/withdraw/cancel")
    public CommonResult<Boolean> cancelWithdraw(@RequestParam Long withdrawId) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Boolean result = commissionService.cancelWithdraw(currentMember.getId(), withdrawId);
            return CommonResult.success(result, result ? "取消成功" : "取消失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
} 