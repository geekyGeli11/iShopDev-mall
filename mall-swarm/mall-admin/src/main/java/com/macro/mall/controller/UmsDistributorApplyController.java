package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsDistributorApplyListVO;
import com.macro.mall.dto.UmsDistributorApplyQueryParam;
import com.macro.mall.dto.UmsDistributorApplyReviewParam;
import com.macro.mall.model.UmsDistributorApply;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.UmsAdminService;
import com.macro.mall.service.UmsDistributorApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分销商申请管理Controller
 */
@RestController
@Tag(name = "UmsDistributorApplyController", description = "分销商申请管理")
@RequestMapping("/distributor/apply")
public class UmsDistributorApplyController {

    @Autowired
    private UmsDistributorApplyService distributorApplyService;

    @Autowired
    private UmsAdminService adminService;

    @Operation(summary = "分页查询分销商申请列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<UmsDistributorApplyListVO>> list(
            UmsDistributorApplyQueryParam queryParam,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        List<UmsDistributorApplyListVO> applyList = distributorApplyService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(applyList));
    }

    @Operation(summary = "获取分销商申请详情")
    @GetMapping("/detail/{id}")
    public CommonResult<UmsDistributorApply> getDetail(@PathVariable Long id) {
        UmsDistributorApply apply = distributorApplyService.getDetail(id);
        return CommonResult.success(apply);
    }

    @Operation(summary = "审核分销商申请")
    @PostMapping("/review")
    public CommonResult<Integer> reviewApply(@RequestBody UmsDistributorApplyReviewParam reviewParam) {
        // 获取当前管理员ID
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        if (currentAdmin == null) {
            return CommonResult.failed("获取当前管理员信息失败");
        }

        int count = distributorApplyService.reviewApply(reviewParam, currentAdmin.getId());
        if (count > 0) {
            String action = reviewParam.getStatus() == 1 ? "通过" : "拒绝";
            return CommonResult.success(count, "申请审核" + action + "成功");
        }
        return CommonResult.failed("申请审核失败");
    }

    @Operation(summary = "批量审核分销商申请")
    @PostMapping("/batch/review")
    public CommonResult<Integer> batchReview(
            @RequestParam List<Long> ids,
            @RequestParam Byte status,
            @RequestParam(required = false, defaultValue = "") String reviewComment) {
        // 获取当前管理员ID
        UmsAdmin currentAdmin = adminService.getCurrentAdmin();
        if (currentAdmin == null) {
            return CommonResult.failed("获取当前管理员信息失败");
        }

        int count = distributorApplyService.batchReview(ids, status, reviewComment, currentAdmin.getId());
        if (count > 0) {
            String action = status == 1 ? "通过" : "拒绝";
            return CommonResult.success(count, "批量审核" + action + "成功，共处理 " + count + " 条申请");
        }
        return CommonResult.failed("批量审核失败");
    }

    @Operation(summary = "删除分销商申请")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Integer> deleteApply(@PathVariable Long id) {
        int count = distributorApplyService.deleteApply(id);
        if (count > 0) {
            return CommonResult.success(count, "申请删除成功");
        }
        return CommonResult.failed("申请删除失败");
    }

    @Operation(summary = "获取分销商申请统计数据")
    @GetMapping("/statistics")
    public CommonResult<Object> getStatistics() {
        Object statistics = distributorApplyService.getStatistics();
        return CommonResult.success(statistics);
    }
} 