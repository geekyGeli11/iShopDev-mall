package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.service.PmsCommissionConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 佣金配置管理Controller
 */
@RestController
@Tag(name = "PmsCommissionConfigController", description = "佣金配置管理")
@RequestMapping("/commission/config")
public class PmsCommissionConfigController {
    
    @Autowired
    private PmsCommissionConfigService commissionConfigService;
    
    @Operation(summary = "获取佣金配置列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<PmsCommissionConfigVO>> list(
            @RequestParam(value = "configName", required = false) String configName,
            @RequestParam(value = "productCategoryId", required = false) Long productCategoryId,
            @RequestParam(value = "commissionType", required = false) Byte commissionType,
            @RequestParam(value = "isActive", required = false) Byte isActive,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "createStartTime", required = false) String createStartTime,
            @RequestParam(value = "createEndTime", required = false) String createEndTime,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        
        PmsCommissionConfigQueryParam queryParam = new PmsCommissionConfigQueryParam();
        queryParam.setConfigName(configName);
        queryParam.setProductCategoryId(productCategoryId);
        queryParam.setCommissionType(commissionType);
        queryParam.setIsActive(isActive);
        queryParam.setStartTime(startTime);
        queryParam.setEndTime(endTime);
        queryParam.setCreateStartTime(createStartTime);
        queryParam.setCreateEndTime(createEndTime);
        
        List<PmsCommissionConfigVO> configList = commissionConfigService.list(queryParam, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(configList));
    }
    
    @Operation(summary = "获取佣金配置详情")
    @GetMapping("/detail/{id}")
    public CommonResult<PmsCommissionConfigVO> getDetail(@PathVariable Long id) {
        PmsCommissionConfigVO configVO = commissionConfigService.getDetail(id);
        if (configVO != null) {
            return CommonResult.success(configVO);
        }
        return CommonResult.failed("佣金配置不存在");
    }
    
    @Operation(summary = "创建佣金配置")
    @PostMapping("/create")
    public CommonResult<Integer> create(@RequestBody PmsCommissionConfigParam configParam) {
        try {
            int count = commissionConfigService.create(configParam);
            if (count > 0) {
                return CommonResult.success(count, "创建佣金配置成功");
            }
            return CommonResult.failed("创建佣金配置失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "更新佣金配置")
    @PostMapping("/update")
    public CommonResult<Integer> update(@RequestBody PmsCommissionConfigParam configParam) {
        try {
            int count = commissionConfigService.update(configParam);
            if (count > 0) {
                return CommonResult.success(count, "更新佣金配置成功");
            }
            return CommonResult.failed("更新佣金配置失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "删除佣金配置")
    @PostMapping("/delete/{id}")
    public CommonResult<Integer> delete(@PathVariable Long id) {
        try {
            int count = commissionConfigService.delete(id);
            if (count > 0) {
                return CommonResult.success(count, "删除佣金配置成功");
            }
            return CommonResult.failed("删除佣金配置失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "批量删除佣金配置")
    @PostMapping("/batch/delete")
    public CommonResult<Integer> batchDelete(@RequestParam List<Long> ids) {
        try {
            int count = commissionConfigService.batchDelete(ids);
            if (count > 0) {
                return CommonResult.success(count, "批量删除佣金配置成功，共删除 " + count + " 条");
            }
            return CommonResult.failed("批量删除佣金配置失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "更新佣金配置状态")
    @PostMapping("/status/update")
    public CommonResult<Integer> updateStatus(@RequestParam Long id, @RequestParam Byte isActive) {
        try {
            int count = commissionConfigService.updateStatus(id, isActive);
            if (count > 0) {
                String action = isActive == 1 ? "启用" : "禁用";
                return CommonResult.success(count, action + "佣金配置成功");
            }
            return CommonResult.failed("更新佣金配置状态失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "批量更新佣金配置状态")
    @PostMapping("/batch/status")
    public CommonResult<Integer> batchUpdateStatus(
            @RequestParam List<Long> ids,
            @RequestParam Byte isActive) {
        try {
            int count = commissionConfigService.batchUpdateStatus(ids, isActive);
            if (count > 0) {
                String action = isActive == 1 ? "启用" : "禁用";
                return CommonResult.success(count, "批量" + action + "佣金配置成功，共处理 " + count + " 条");
            }
            return CommonResult.failed("批量更新佣金配置状态失败");
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "获取佣金配置统计数据")
    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = commissionConfigService.getStatistics();
        return CommonResult.success(statistics);
    }
    
    @Operation(summary = "根据商品分类获取生效的佣金配置")
    @GetMapping("/active/{productCategoryId}")
    public CommonResult<PmsCommissionConfigVO> getActiveByCategoryId(@PathVariable Long productCategoryId) {
        PmsCommissionConfigVO configVO = commissionConfigService.getActiveByCategoryId(productCategoryId);
        if (configVO != null) {
            return CommonResult.success(configVO);
        }
        return CommonResult.failed("该商品分类下没有生效的佣金配置");
    }
    
    @Operation(summary = "获取全局默认佣金配置")
    @GetMapping("/global/default")
    public CommonResult<PmsCommissionConfigVO> getGlobalDefault() {
        PmsCommissionConfigVO configVO = commissionConfigService.getGlobalDefault();
        if (configVO != null) {
            return CommonResult.success(configVO);
        }
        return CommonResult.failed("没有配置全局默认佣金");
    }
    
    @Operation(summary = "检查配置名称是否重复")
    @GetMapping("/check/name")
    public CommonResult<Boolean> checkConfigNameExists(
            @RequestParam String configName,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = commissionConfigService.checkConfigNameExists(configName, excludeId);
        return CommonResult.success(exists);
    }
} 