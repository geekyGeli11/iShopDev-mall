package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsRechargeConfigParam;
import com.macro.mall.dto.UmsRechargeConfigResult;
import com.macro.mall.service.UmsRechargeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 充值配置管理Controller
 */
@RestController
@RequestMapping("/member/balance/recharge")
@Tag(name = "UmsRechargeConfigController", description = "充值配置管理")
public class UmsRechargeConfigController {

    @Autowired
    private UmsRechargeConfigService rechargeConfigService;

    @Operation(summary = "获取充值配置")
    @GetMapping("/config")
    public CommonResult<UmsRechargeConfigResult> getRechargeConfig() {
        try {
            UmsRechargeConfigResult config = rechargeConfigService.getRechargeConfig();
            return CommonResult.success(config);
        } catch (Exception e) {
            return CommonResult.failed("获取充值配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新充值配置")
    @PostMapping("/config")
    public CommonResult<Boolean> updateRechargeConfig(@RequestBody UmsRechargeConfigParam param) {
        try {
            boolean success = rechargeConfigService.updateRechargeConfig(param);
            if (success) {
                return CommonResult.success(true, "充值配置更新成功");
            } else {
                return CommonResult.failed("充值配置更新失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("更新充值配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取充值配置历史")
    @GetMapping("/config/history")
    public CommonResult<List<Map<String, Object>>> getRechargeConfigHistory(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            List<Map<String, Object>> history = rechargeConfigService.getConfigHistory(pageNum, pageSize);
            return CommonResult.success(history);
        } catch (Exception e) {
            return CommonResult.failed("获取配置历史失败：" + e.getMessage());
        }
    }

    @Operation(summary = "验证快速充值选项配置")
    @PostMapping("/config/validate")
    public CommonResult<Map<String, Object>> validateQuickAmounts(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> quickAmounts = (List<Map<String, Object>>) request.get("quickAmounts");
            Map<String, Object> result = rechargeConfigService.validateQuickAmounts(quickAmounts);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("验证配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取充值统计数据")
    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getRechargeStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            Map<String, Object> statistics = rechargeConfigService.getRechargeStatistics(startDate, endDate);
            return CommonResult.success(statistics);
        } catch (Exception e) {
            return CommonResult.failed("获取统计数据失败：" + e.getMessage());
        }
    }

    @Operation(summary = "重置充值配置为默认值")
    @PostMapping("/config/reset")
    public CommonResult<Boolean> resetRechargeConfig() {
        try {
            boolean success = rechargeConfigService.resetRechargeConfig();
            if (success) {
                return CommonResult.success(true, "配置已重置为默认值");
            } else {
                return CommonResult.failed("重置配置失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("重置配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "预览充值配置效果")
    @PostMapping("/config/preview")
    public CommonResult<Map<String, Object>> previewRechargeConfig(@RequestBody UmsRechargeConfigParam param) {
        try {
            Map<String, Object> preview = rechargeConfigService.previewRechargeConfig(param);
            return CommonResult.success(preview);
        } catch (Exception e) {
            return CommonResult.failed("预览配置失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取配置项列表")
    @GetMapping("/config/items")
    public CommonResult<List<Map<String, Object>>> getConfigItems() {
        try {
            List<Map<String, Object>> items = rechargeConfigService.getConfigItems();
            return CommonResult.success(items);
        } catch (Exception e) {
            return CommonResult.failed("获取配置项失败：" + e.getMessage());
        }
    }

    @Operation(summary = "更新单个配置项")
    @PostMapping("/config/item")
    public CommonResult<Boolean> updateConfigItem(@RequestParam String configKey,
                                                  @RequestParam String configValue,
                                                  @RequestParam(required = false) String configDesc) {
        try {
            boolean success = rechargeConfigService.updateConfigItem(configKey, configValue, configDesc);
            if (success) {
                return CommonResult.success(true, "配置项更新成功");
            } else {
                return CommonResult.failed("配置项更新失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("更新配置项失败：" + e.getMessage());
        }
    }
}
