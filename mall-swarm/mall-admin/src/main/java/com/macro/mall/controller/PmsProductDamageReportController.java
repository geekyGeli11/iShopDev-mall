package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.PmsProductDamageReport;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.PmsProductDamageReportService;
import com.macro.mall.service.UmsAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品报损管理Controller
 */
@Controller
@Tag(name = "PmsProductDamageReportController", description = "产品报损管理")
@RequestMapping("/damage/report")
public class PmsProductDamageReportController {
    
    @Autowired
    private PmsProductDamageReportService damageReportService;
    
    @Autowired
    private UmsAdminService adminService;
    
    @Operation(summary = "创建报损记录")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody PmsProductDamageReportParam param) {
        UmsAdmin admin = adminService.getCurrentAdmin();
        int count = damageReportService.create(param, admin.getId(), admin.getNickName());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "分页查询报损记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductDamageReport>> list(
            PmsProductDamageReportQueryParam queryParam,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsProductDamageReport> list = damageReportService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(list));
    }
    
    @Operation(summary = "获取报损详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductDamageReportResult> getDetail(@PathVariable Long id) {
        PmsProductDamageReportResult result = damageReportService.getDetail(id);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "开始处理报损")
    @RequestMapping(value = "/handle/start/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult startHandle(@PathVariable Long id) {
        UmsAdmin admin = adminService.getCurrentAdmin();
        int count = damageReportService.startHandle(id, admin.getId(), admin.getNickName());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("开始处理失败，请检查报损状态");
    }
    
    @Operation(summary = "更新处理信息")
    @RequestMapping(value = "/handle/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateHandle(@PathVariable Long id, @RequestBody PmsProductDamageHandleParam param) {
        UmsAdmin admin = adminService.getCurrentAdmin();
        int count = damageReportService.updateHandle(id, param, admin.getId(), admin.getNickName());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "验收")
    @RequestMapping(value = "/acceptance/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult acceptance(
            @PathVariable Long id,
            @RequestParam Integer acceptanceStatus,
            @RequestParam(required = false) String acceptanceRemark) {
        UmsAdmin admin = adminService.getCurrentAdmin();
        int count = damageReportService.acceptance(id, acceptanceStatus, acceptanceRemark, admin.getId(), admin.getNickName());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("验收失败，请检查报损状态");
    }
    
    @Operation(summary = "完成处理")
    @RequestMapping(value = "/complete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult complete(@PathVariable Long id) {
        UmsAdmin admin = adminService.getCurrentAdmin();
        int count = damageReportService.complete(id, admin.getId(), admin.getNickName());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("完成处理失败");
    }
    
    @Operation(summary = "关闭报损")
    @RequestMapping(value = "/close/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult close(@PathVariable Long id, @RequestParam(required = false) String remark) {
        UmsAdmin admin = adminService.getCurrentAdmin();
        int count = damageReportService.close(id, remark, admin.getId(), admin.getNickName());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("关闭失败");
    }
    
    @Operation(summary = "批量删除报损记录")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = damageReportService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "获取待处理报损数量")
    @RequestMapping(value = "/pending/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> getPendingCount() {
        return CommonResult.success(damageReportService.getPendingCount());
    }
    
    @Operation(summary = "按门店统计报损")
    @RequestMapping(value = "/statistics/store", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductDamageStatistics>> statisticsByStore(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return CommonResult.success(damageReportService.statisticsByStore(startDate, endDate));
    }
    
    @Operation(summary = "按报损类型统计")
    @RequestMapping(value = "/statistics/type", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsProductDamageStatistics>> statisticsByType(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return CommonResult.success(damageReportService.statisticsByType(startDate, endDate));
    }
}
