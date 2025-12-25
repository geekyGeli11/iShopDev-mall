package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.service.NonSystemSalePortalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 非系统销售单小程序端Controller
 * 用于客户查看销售单详情
 * Created by macro on 2025-12-12.
 */
@RestController
@Tag(name = "NonSystemSaleController", description = "非系统销售单查看")
@RequestMapping("/sale")
public class NonSystemSaleController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NonSystemSaleController.class);
    
    @Autowired
    private NonSystemSalePortalService nonSystemSalePortalService;
    
    @Operation(summary = "获取销售单详情（需要验证权限）")
    @GetMapping("/detail/{saleId}")
    public CommonResult<Map<String, Object>> getSaleDetail(
            @Parameter(description = "销售单ID") @PathVariable Long saleId) {
        try {
            Map<String, Object> result = nonSystemSalePortalService.getSaleDetailWithAuth(saleId);
            return CommonResult.success(result);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("获取销售单详情参数错误: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (SecurityException e) {
            LOGGER.warn("获取销售单详情权限不足: {}", e.getMessage());
            // 返回403状态码，message放在data中
            Map<String, Object> errorData = new java.util.HashMap<>();
            errorData.put("message", e.getMessage());
            return CommonResult.forbidden(errorData);
        } catch (Exception e) {
            LOGGER.error("获取销售单详情失败", e);
            return CommonResult.failed("获取销售单详情失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "检查是否有权限查看销售单")
    @GetMapping("/checkAuth/{saleId}")
    public CommonResult<Map<String, Object>> checkSaleAuth(
            @Parameter(description = "销售单ID") @PathVariable Long saleId) {
        try {
            Map<String, Object> result = nonSystemSalePortalService.checkSaleAuth(saleId);
            return CommonResult.success(result);
        } catch (Exception e) {
            LOGGER.error("检查销售单权限失败", e);
            return CommonResult.failed("检查权限失败：" + e.getMessage());
        }
    }
}
