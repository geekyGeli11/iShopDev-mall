package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsSystemConfig;
import com.macro.mall.service.PmsInviteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邀请配置管理Controller
 * Created by guanghengzhou on 2024/01/20.
 */
@Controller
@Tag(name = "PmsInviteConfigController", description = "邀请配置管理")
@RequestMapping("/invite/config")
public class PmsInviteConfigController {
    
    @Autowired
    private PmsInviteConfigService inviteConfigService;
    
    @Operation(summary = "获取邀请功能配置")
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getInviteConfig() {
        Map<String, Object> config = inviteConfigService.getAllInviteConfig();
        return CommonResult.success(config);
    }
    
    @Operation(summary = "更新邀请功能配置")
    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateInviteConfig(@RequestBody Map<String, Object> requestData) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> configItems = (List<Map<String, String>>) requestData.get("configItems");
        String reason = (String) requestData.get("reason");
        
        if (configItems == null || configItems.isEmpty()) {
            return CommonResult.failed("配置项不能为空");
        }
        
        // 转换为PmsSystemConfig对象
        List<PmsSystemConfig> configs = configItems.stream().map(item -> {
            PmsSystemConfig config = new PmsSystemConfig();
            config.setConfigKey(item.get("key"));
            config.setConfigValue(item.get("value"));
            config.setConfigDesc(item.get("desc"));
            return config;
        }).toList();
        
        int count = inviteConfigService.batchUpdateConfig(configs, reason);
        if (count > 0) {
            return CommonResult.success(count, "配置更新成功");
        }
        return CommonResult.failed("配置更新失败");
    }
    
    @Operation(summary = "批量更新配置")
    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult batchUpdateConfig(@RequestBody Map<String, Object> requestData) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> configItems = (List<Map<String, String>>) requestData.get("configItems");
        String reason = (String) requestData.getOrDefault("reason", "批量配置更新");
        
        if (configItems == null || configItems.isEmpty()) {
            return CommonResult.failed("配置项不能为空");
        }
        
        // 转换为PmsSystemConfig对象
        List<PmsSystemConfig> configs = configItems.stream().map(item -> {
            PmsSystemConfig config = new PmsSystemConfig();
            config.setConfigKey(item.get("key"));
            config.setConfigValue(item.get("value"));
            config.setConfigDesc(item.get("desc"));
            return config;
        }).toList();
        
        int count = inviteConfigService.batchUpdateConfig(configs, reason);
        if (count > 0) {
            return CommonResult.success(count, "批量配置更新成功");
        }
        return CommonResult.failed("批量配置更新失败");
    }
    
    @Operation(summary = "获取配置变更历史")
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<Map<String, Object>>> getConfigHistory(
            @RequestParam(value = "configKey", required = false) String configKey,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        
        List<Map<String, Object>> historyList = inviteConfigService.getConfigHistory(configKey, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(historyList));
    }
    
    @Operation(summary = "获取单个配置详情")
    @RequestMapping(value = "/{configKey}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsSystemConfig> getConfigDetail(@PathVariable String configKey) {
        PmsSystemConfig config = inviteConfigService.getConfigByKey(configKey);
        if (config != null) {
            return CommonResult.success(config);
        }
        return CommonResult.failed("配置不存在");
    }
    
    @Operation(summary = "更新单个配置")
    @RequestMapping(value = "/{configKey}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateSingleConfig(@PathVariable String configKey,
                                         @RequestBody Map<String, String> requestData) {
        String configValue = requestData.get("value");
        String configDesc = requestData.get("desc");
        
        if (configValue == null || configValue.trim().isEmpty()) {
            return CommonResult.failed("配置值不能为空");
        }
        
        int count = inviteConfigService.updateConfig(configKey, configValue, configDesc);
        if (count > 0) {
            return CommonResult.success(count, "配置更新成功");
        }
        return CommonResult.failed("配置更新失败");
    }
    
    @Operation(summary = "初始化配置历史记录")
    @RequestMapping(value = "/history/init", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult initConfigHistory() {
        try {
            int count = inviteConfigService.createInitialHistory();
            return CommonResult.success(count, "初始化历史记录成功，创建了" + count + "条记录");
        } catch (Exception e) {
            return CommonResult.failed("初始化历史记录失败：" + e.getMessage());
        }
    }
} 