package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.DistributionStatsResult;
import com.macro.mall.portal.domain.DistributorApplyResult;
import com.macro.mall.portal.domain.CommissionRecord;
import com.macro.mall.portal.domain.CustomerRecord;
import com.macro.mall.portal.dto.DistributorApplyParam;
import com.macro.mall.portal.service.DistributionService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 分销功能管理Controller
 */
@RestController
@RequestMapping("/api/distribution")
@Tag(name = "分销功能管理", description = "分销商申请、分销统计、佣金管理等功能")
public class DistributionController {
    
    @Autowired
    private DistributionService distributionService;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "获取分销商申请状态")
    @GetMapping("/apply/status")
    public CommonResult<DistributorApplyResult> getApplyStatus() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        DistributorApplyResult result = distributionService.getDistributorApplyStatus(currentMember.getId());
        return CommonResult.success(result);
    }
    
    @Operation(summary = "提交分销商申请")
    @PostMapping("/apply")
    public CommonResult<Boolean> submitApply(@Valid @RequestBody DistributorApplyParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Boolean result = distributionService.submitDistributorApply(currentMember.getId(), param);
            return CommonResult.success(result, result ? "申请提交成功，请等待审核" : "申请提交失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取分销商申请详情")
    @GetMapping("/apply/detail")
    public CommonResult<Map<String, Object>> getApplyDetail() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> detail = distributionService.getDistributorApplyDetail(currentMember.getId());
        return CommonResult.success(detail);
    }

    @Operation(summary = "检查申请资格")
    @GetMapping("/apply/check-eligibility")
    public CommonResult<Map<String, Object>> checkApplyEligibility() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> eligibility = distributionService.checkApplyEligibility(currentMember.getId());
        return CommonResult.success(eligibility);
    }
    
    @Operation(summary = "获取分销商申请要求")
    @GetMapping("/apply/requirements")
    public CommonResult<Map<String, Object>> getApplyRequirements() {
        Map<String, Object> requirements = distributionService.getDistributorRequirements();
        return CommonResult.success(requirements);
    }

    @Operation(summary = "补充申请信息")
    @PostMapping("/apply/supplement")
    public CommonResult<Boolean> supplementApplyInfo(@RequestBody Map<String, Object> params) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }

        Long applyId = Long.valueOf(params.get("applyId").toString());
        String workScreenshots = (String) params.get("workScreenshots");

        if (applyId == null) {
            return CommonResult.failed("申请ID不能为空");
        }

        if (workScreenshots == null || workScreenshots.trim().isEmpty()) {
            return CommonResult.failed("工作截图不能为空");
        }

        Boolean result = distributionService.supplementApplyInfo(applyId, workScreenshots);
        if (result) {
            return CommonResult.success(true, "补充信息提交成功");
        } else {
            return CommonResult.failed("补充信息提交失败");
        }
    }
    
    @Operation(summary = "生成分销码")
    @PostMapping("/generate-code")
    public CommonResult<String> generateDistributionCode() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        // 检查是否为分销商
        if (!distributionService.isDistributor(currentMember.getId())) {
            return CommonResult.failed("您还不是分销商，无法生成分销码");
        }
        
        String distributionCode = distributionService.generateDistributionCode(currentMember.getId());
        return CommonResult.success(distributionCode);
    }
    
    @Operation(summary = "获取我的分销码信息")
    @GetMapping("/my-code")
    public CommonResult<String> getMyDistributionCode() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        String distributionCode = distributionService.getMyDistributionCode(currentMember.getId());
        return CommonResult.success(distributionCode);
    }
    
    @Operation(summary = "获取我的分销统计")
    @GetMapping("/my-stats")
    public CommonResult<DistributionStatsResult> getMyStats() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        DistributionStatsResult result = distributionService.getMyDistributionStats(currentMember.getId());
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取我的客户列表")
    @GetMapping("/my-customers")
    public CommonResult<Map<String, Object>> getMyCustomers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String level) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> result = distributionService.getMyCustomers(currentMember.getId(), page, size, level);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取我的客户统计")
    @GetMapping("/my-customer-stats")
    public CommonResult<Map<String, Object>> getMyCustomerStats() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> stats = distributionService.getMyCustomerStats(currentMember.getId());
        return CommonResult.success(stats);
    }
    
    @Operation(summary = "获取我的佣金记录")
    @GetMapping("/my-commission-records")
    public CommonResult<Map<String, Object>> getMyCommissionRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String filter) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> records = distributionService.getMyCommissionRecords(currentMember.getId(), page, size, filter);
        return CommonResult.success(records);
    }
    
    @Operation(summary = "获取我的佣金汇总")
    @GetMapping("/my-commission-summary")
    public CommonResult<Map<String, Object>> getMyCommissionSummary(
            @RequestParam(required = false) String period) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        Map<String, Object> summary = distributionService.getMyCommissionSummary(currentMember.getId(), period);
        return CommonResult.success(summary);
    }
    
    @Operation(summary = "绑定分销关系")
    @PostMapping("/bind-relation")
    public CommonResult<Boolean> bindDistributionRelation(@RequestParam String distributionCode) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            Boolean result = distributionService.bindDistributionRelation(distributionCode, currentMember.getId());
            return CommonResult.success(result, result ? "分销关系绑定成功" : "分销关系绑定失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
} 