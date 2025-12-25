package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsStoreSkuStockParam;
import com.macro.mall.dto.PmsStoreSkuStockExtendDto;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.service.PmsStoreSkuStockService;
import com.macro.mall.service.OmsStoreAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门店SKU库存管理Controller
 * Created by macro on 2024-01-20.
 */
@RestController
@Tag(name = "PmsStoreSkuStockController", description = "门店SKU库存管理")
@RequestMapping("/storeSkuStock")
public class PmsStoreSkuStockController {
    
    @Autowired
    private PmsStoreSkuStockService storeSkuStockService;
    
    @Autowired
    private OmsStoreAddressService storeAddressService;
    
    @Operation(summary = "根据商品ID获取门店库存分配情况")
    @GetMapping("/listByProduct/{productId}")
    public CommonResult<List<PmsStoreSkuStockExtendDto>> getStoreSkuStockByProductId(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        List<PmsStoreSkuStockExtendDto> storeSkuStockList = storeSkuStockService.getStoreSkuStockWithSpecByProductId(productId);
        return CommonResult.success(storeSkuStockList);
    }
    
    @Operation(summary = "根据SKU ID获取门店库存分配情况")
    @GetMapping("/listBySku/{skuId}")
    public CommonResult<List<PmsStoreSkuStock>> getStoreSkuStockBySkuId(
            @Parameter(description = "SKU ID") @PathVariable Long skuId) {
        List<PmsStoreSkuStock> storeSkuStockList = storeSkuStockService.getStoreSkuStockBySkuId(skuId);
        return CommonResult.success(storeSkuStockList);
    }
    
    @Operation(summary = "获取指定门店的SKU库存")
    @GetMapping("/get")
    public CommonResult<PmsStoreSkuStock> getStoreSkuStock(
            @Parameter(description = "门店ID") @RequestParam Long storeId,
            @Parameter(description = "SKU ID") @RequestParam Long skuId) {
        PmsStoreSkuStock storeSkuStock = storeSkuStockService.getStoreSkuStock(storeId, skuId);
        return CommonResult.success(storeSkuStock);
    }
    
    @Operation(summary = "批量保存门店SKU库存分配")
    @PostMapping("/batchSave")
    public CommonResult<Integer> batchSaveStoreSkuStock(@RequestBody List<PmsStoreSkuStock> storeSkuStockList) {
        int count = storeSkuStockService.batchSaveStoreSkuStock(storeSkuStockList);
        if (count > 0) {
            return CommonResult.success(count, "批量保存成功");
        }
        return CommonResult.failed("批量保存失败");
    }
    
    @Operation(summary = "保存单个门店SKU库存")
    @PostMapping("/save")
    public CommonResult<PmsStoreSkuStock> saveStoreSkuStock(@RequestBody PmsStoreSkuStock storeSkuStock) {
        PmsStoreSkuStock result = storeSkuStockService.saveStoreSkuStock(storeSkuStock);
        if (result != null) {
            return CommonResult.success(result, "保存成功");
        }
        return CommonResult.failed("保存失败");
    }
    
    @Operation(summary = "删除门店SKU库存记录")
    @DeleteMapping("/delete")
    public CommonResult<Boolean> deleteStoreSkuStock(
            @Parameter(description = "门店ID") @RequestParam Long storeId,
            @Parameter(description = "SKU ID") @RequestParam Long skuId) {
        boolean result = storeSkuStockService.deleteStoreSkuStock(storeId, skuId);
        if (result) {
            return CommonResult.success(result, "删除成功");
        }
        return CommonResult.failed("删除失败");
    }
    
    @Operation(summary = "校验门店库存总和是否等于SKU总库存")
    @GetMapping("/validate")
    public CommonResult<Boolean> validateStoreStockSum(
            @Parameter(description = "SKU ID") @RequestParam Long skuId,
            @Parameter(description = "SKU总库存") @RequestParam Integer skuTotalStock) {
        boolean isValid = storeSkuStockService.validateStoreStockSum(skuId, skuTotalStock);
        return CommonResult.success(isValid, isValid ? "库存校验通过" : "库存总和不匹配");
    }
    
    @Operation(summary = "自动均分库存到所有门店")
    @PostMapping("/autoDistribute")
    public CommonResult<List<PmsStoreSkuStock>> autoDistributeStock(
            @Parameter(description = "SKU ID") @RequestParam Long skuId,
            @Parameter(description = "总库存") @RequestParam Integer totalStock) {
        List<PmsStoreSkuStock> result = storeSkuStockService.autoDistributeStock(skuId, totalStock);
        if (!result.isEmpty()) {
            return CommonResult.success(result, "自动分配库存成功");
        }
        return CommonResult.failed("自动分配库存失败");
    }
    
    @Operation(summary = "根据商品ID和门店库存参数批量保存")
    @PostMapping("/batchSaveByProduct/{productId}")
    public CommonResult<Boolean> batchSaveStoreSkuStockByProduct(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @RequestBody List<PmsStoreSkuStockParam> storeStockParams) {
        boolean result = storeSkuStockService.batchSaveStoreSkuStockByProduct(productId, storeStockParams);
        if (result) {
            return CommonResult.success(result, "批量保存成功");
        }
        return CommonResult.failed("批量保存失败");
    }
    
    @Operation(summary = "根据商品ID删除所有门店库存记录")
    @DeleteMapping("/deleteByProduct/{productId}")
    public CommonResult<Integer> deleteStoreSkuStockByProductId(
            @Parameter(description = "商品ID") @PathVariable Long productId) {
        int count = storeSkuStockService.deleteStoreSkuStockByProductId(productId);
        return CommonResult.success(count, "删除成功，共删除" + count + "条记录");
    }
    
    @Operation(summary = "获取所有门店列表")
    @GetMapping("/stores")
    public CommonResult<List<OmsStoreAddress>> getAllStores() {
        List<OmsStoreAddress> stores = storeAddressService.list(null, null, null, 1, 100);
        return CommonResult.success(stores);
    }
    
    @Operation(summary = "根据门店ID获取该门店所有SKU库存")
    @GetMapping("/listByStore/{storeId}")
    public CommonResult<List<PmsStoreSkuStockExtendDto>> getStoreSkuStockByStoreId(
            @Parameter(description = "门店ID") @PathVariable Long storeId) {
        List<PmsStoreSkuStockExtendDto> storeSkuStockList = storeSkuStockService.getStoreSkuStockByStoreId(storeId);
        return CommonResult.success(storeSkuStockList);
    }
} 