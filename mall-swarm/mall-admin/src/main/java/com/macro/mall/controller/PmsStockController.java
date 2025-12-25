package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsStockOperationLogQueryParam;
import com.macro.mall.dto.PmsStockOperationParam;
import com.macro.mall.model.PmsStockOperationLog;
import com.macro.mall.service.PmsStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理Controller
 * Created by macro on 2024-01-20.
 */
@Controller
@Tag(name = "PmsStockController", description = "库存管理")
@RequestMapping("/stock")
public class PmsStockController {

    @Autowired
    private PmsStockService stockService;

    @Operation(summary = "执行库存操作")
    @RequestMapping(value = "/operation", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult executeStockOperation(@RequestBody PmsStockOperationParam param) {
        int count = stockService.executeStockOperation(param);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "获取库存操作记录")
    @RequestMapping(value = "/operation/logs", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsStockOperationLog>> getStockOperationLogs(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "operationType", required = false) String operationType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "storeId", required = false) Long storeId) {
        
        PmsStockOperationLogQueryParam queryParam = new PmsStockOperationLogQueryParam();
        queryParam.setPageNum(pageNum);
        queryParam.setPageSize(pageSize);
        queryParam.setProductId(productId);
        queryParam.setKeyword(keyword);
        queryParam.setOperationType(operationType);
        queryParam.setStartTime(startTime);
        queryParam.setEndTime(endTime);
        queryParam.setStoreId(storeId);
        
        List<PmsStockOperationLog> logs = stockService.getStockOperationLogs(queryParam);
        return CommonResult.success(CommonPage.restPage(logs));
    }

    @Operation(summary = "获取库存统计")
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getStockStats() {
        // TODO: 实现库存统计功能
        return CommonResult.success(null, "功能开发中");
    }

    @Operation(summary = "检查库存是否充足")
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult checkStockAvailable(
            @RequestParam("productId") Long productId,
            @RequestParam("skuId") Long skuId,
            @RequestParam("quantity") Integer quantity) {
        
        boolean available = stockService.checkStockAvailable(productId, skuId, quantity);
        return CommonResult.success(available);
    }

    // ==================== 地库相关 API ====================

    @Operation(summary = "验证门店（包括地库）库存充足性")
    @RequestMapping(value = "/validateAvailability", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult validateStockAvailability(
            @RequestParam("storeId") Long storeId,
            @RequestParam("skuId") Long skuId,
            @RequestParam("quantity") Integer quantity) {
        
        boolean available = stockService.validateStockAvailability(storeId, skuId, quantity);
        return CommonResult.success(available);
    }
} 