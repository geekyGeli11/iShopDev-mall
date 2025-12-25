package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.service.UmsDistributorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分销商管理Controller
 */
@RestController
@Tag(name = "UmsDistributorController", description = "分销商管理")
@RequestMapping("/distributor")
public class UmsDistributorController {
    
    @Autowired
    private UmsDistributorService distributorService;
    
    @Operation(summary = "获取分销商列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsDistributorListVO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "distributorStatus", required = false) Byte distributorStatus,
            @RequestParam(value = "distributorLevel", required = false) Byte distributorLevel,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "approvedStartTime", required = false) String approvedStartTime,
            @RequestParam(value = "approvedEndTime", required = false) String approvedEndTime,
            @RequestParam(value = "minCommission", required = false) String minCommission,
            @RequestParam(value = "maxCommission", required = false) String maxCommission,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        
        UmsDistributorQueryParam queryParam = new UmsDistributorQueryParam();
        queryParam.setKeyword(keyword);
        queryParam.setDistributorStatus(distributorStatus);
        queryParam.setDistributorLevel(distributorLevel);
        queryParam.setStartTime(startTime);
        queryParam.setEndTime(endTime);
        queryParam.setApprovedStartTime(approvedStartTime);
        queryParam.setApprovedEndTime(approvedEndTime);
        queryParam.setMinCommission(minCommission);
        queryParam.setMaxCommission(maxCommission);
        
        List<UmsDistributorListVO> list = distributorService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(list));
    }
    
    @Operation(summary = "获取分销商详情")
    @GetMapping("/detail/{userId}")
    public CommonResult<UmsDistributorListVO> getDetail(@PathVariable Long userId) {
        UmsDistributorListVO detail = distributorService.getDetail(userId);
        return CommonResult.success(detail);
    }
    
    @Operation(summary = "更新分销商状态")
    @PostMapping("/status/update")
    public CommonResult<Integer> updateStatus(@RequestBody UmsDistributorStatusParam statusParam) {
        int count = distributorService.updateStatus(statusParam);
        if (count > 0) {
            return CommonResult.success(count, "分销商状态更新成功");
        }
        return CommonResult.failed("分销商状态更新失败");
    }
    
    @Operation(summary = "更新分销商等级")
    @PostMapping("/level/update")
    public CommonResult<Integer> updateLevel(@RequestBody UmsDistributorLevelParam levelParam) {
        int count = distributorService.updateLevel(levelParam);
        if (count > 0) {
            return CommonResult.success(count, "分销商等级更新成功");
        }
        return CommonResult.failed("分销商等级更新失败");
    }
    
    @Operation(summary = "启用分销商")
    @PostMapping("/enable/{userId}")
    public CommonResult<Integer> enableDistributor(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        int count = distributorService.enableDistributor(userId, reason);
        if (count > 0) {
            return CommonResult.success(count, "分销商启用成功");
        }
        return CommonResult.failed("分销商启用失败");
    }
    
    @Operation(summary = "禁用分销商")
    @PostMapping("/disable/{userId}")
    public CommonResult<Integer> disableDistributor(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        int count = distributorService.disableDistributor(userId, reason);
        if (count > 0) {
            return CommonResult.success(count, "分销商禁用成功");
        }
        return CommonResult.failed("分销商禁用失败");
    }
    
    @Operation(summary = "暂停分销商")
    @PostMapping("/pause/{userId}")
    public CommonResult<Integer> pauseDistributor(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        int count = distributorService.pauseDistributor(userId, reason);
        if (count > 0) {
            return CommonResult.success(count, "分销商暂停成功");
        }
        return CommonResult.failed("分销商暂停失败");
    }
    
    @Operation(summary = "批量更新分销商状态")
    @PostMapping("/batch/status")
    public CommonResult<Integer> batchUpdateStatus(
            @RequestParam List<Long> userIds,
            @RequestParam Byte status,
            @RequestParam(required = false, defaultValue = "") String reason) {
        int count = distributorService.batchUpdateStatus(userIds, status, reason);
        if (count > 0) {
            return CommonResult.success(count, "批量更新分销商状态成功，共处理 " + count + " 个分销商");
        }
        return CommonResult.failed("批量更新分销商状态失败");
    }
    
    @Operation(summary = "获取分销商统计数据")
    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = distributorService.getStatistics();
        return CommonResult.success(statistics);
    }
} 