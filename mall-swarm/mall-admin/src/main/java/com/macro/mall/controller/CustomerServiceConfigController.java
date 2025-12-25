package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CustomerServiceConfig;
import com.macro.mall.service.CustomerServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 客服配置Controller
 * Created by macro on 2025/11/28.
 */
@RestController
@Tag(name = "CustomerServiceConfigController", description = "客服配置管理")
@RequestMapping("/api/customerService")
public class CustomerServiceConfigController {
    
    @Autowired
    private CustomerServiceConfigService customerServiceConfigService;
    
    @Operation(summary = "获取客服配置")
    @GetMapping("/get")
    public CommonResult<CustomerServiceConfig> getConfig() {
        CustomerServiceConfig config = customerServiceConfigService.getConfig();
        return CommonResult.success(config);
    }
    
    @Operation(summary = "更新客服配置")
    @PostMapping("/update")
    public CommonResult updateConfig(@RequestBody CustomerServiceConfig config) {
        int count = customerServiceConfigService.updateConfig(config);
        if (count > 0) {
            return CommonResult.success(null, "更新客服配置成功");
        }
        return CommonResult.failed("更新客服配置失败");
    }
}
