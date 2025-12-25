package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.InviteGenerateResult;
import com.macro.mall.portal.domain.InviteRewardRecord;
import com.macro.mall.portal.domain.InviteStatisticsResult;
import com.macro.mall.portal.domain.RewardSummaryResult;
import com.macro.mall.portal.domain.WithdrawApplyResult;
import com.macro.mall.portal.dto.InviteGenerateParam;
import com.macro.mall.portal.dto.InviteVerifyParam;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.portal.service.InviteService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import java.util.List;

/**
 * 邀请功能管理Controller
 */
@RestController
@RequestMapping("/api/invite")
@Tag(name = "邀请功能管理", description = "邀请新人有礼相关功能")
public class InviteController {
    
    @Autowired
    private InviteService inviteService;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Operation(summary = "生成邀请参数")
    @PostMapping("/generate")
    public CommonResult<InviteGenerateResult> generateInviteParam(@RequestBody InviteGenerateParam param) {
        // 获取当前用户ID
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        param.setUserId(currentMember.getId());
        InviteGenerateResult result = inviteService.generateInviteParam(param);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "验证邀请参数")
    @PostMapping("/verify")
    public CommonResult<Boolean> verifyInviteParam(@RequestBody InviteVerifyParam param) {
        boolean result = inviteService.verifyInviteParam(param);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "建立邀请关系")
    @PostMapping("/establish")
    public CommonResult<Boolean> establishInviteRelation(@RequestParam String inviteParam) {
        // 从认证上下文获取当前用户ID
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        // 解析新格式邀请参数获取邀请者ID
        try {
            // 验证邀请参数格式：INVITE_v2_{userId}_{timestamp}_{random}
            if (!inviteParam.startsWith("INVITE_v2_")) {
                return CommonResult.failed("邀请参数格式不正确");
            }

            String[] parts = inviteParam.split("_");
            if (parts.length != 5) {
                return CommonResult.failed("邀请参数格式不正确");
            }

            Long inviterUserId = Long.parseLong(parts[2]); // 第3个部分是用户ID

            Boolean result = inviteService.establishInviteRelation(inviterUserId, currentMember.getId());
            return CommonResult.success(result, result ? "邀请关系建立成功" : "邀请关系建立失败");
        } catch (NumberFormatException e) {
            return CommonResult.failed("邀请参数中的用户ID格式不正确");
        } catch (Exception e) {
            return CommonResult.failed("邀请参数解析失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取我的邀请统计")
    @GetMapping("/my-statistics")
    public CommonResult<InviteStatisticsResult> getMyInviteStatistics() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        InviteStatisticsResult result = inviteService.getMyInviteStatistics(currentMember.getId());
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取我的奖励记录")
    @GetMapping("/my-rewards")
    public CommonResult<List<InviteRewardRecord>> getMyRewardRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        List<InviteRewardRecord> result = inviteService.getMyInviteRewards(currentMember.getId(), page, size);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取奖励汇总")
    @GetMapping("/reward-summary")
    public CommonResult<RewardSummaryResult> getRewardSummary(
            @RequestParam(required = false) Integer year) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        // 如果没有指定年份，默认为当前年份
        if (year == null) {
            year = java.time.Year.now().getValue();
        }
        
        RewardSummaryResult result = inviteService.getRewardSummary(currentMember.getId(), year);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "申请提现")
    @PostMapping("/apply-withdraw")
    public CommonResult<WithdrawApplyResult> applyWithdraw(@Valid @RequestBody WithdrawApplyParam param) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.failed("请先登录");
        }
        
        try {
            WithdrawApplyResult result = inviteService.applyWithdraw(currentMember.getId(), param);
            return CommonResult.success(result, "提现申请提交成功");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取提现配置")
    @GetMapping("/withdraw-config")
    public CommonResult<Map<String, Object>> getWithdrawConfig() {
        Map<String, Object> config = inviteService.getWithdrawConfig();
        return CommonResult.success(config);
    }
}