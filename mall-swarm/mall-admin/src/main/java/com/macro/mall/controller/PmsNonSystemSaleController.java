package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsSalesType;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.OmsStoreAddress;
import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.dto.PmsNonSystemSaleCreateRequest;
import com.macro.mall.dto.PmsNonSystemSaleDTO;
import com.macro.mall.service.PmsNonSystemSaleService;
import com.macro.mall.service.OmsStoreAddressService;
import com.macro.mall.service.PmsStoreSkuStockService;
import com.macro.mall.common.dto.UserDto;
import com.macro.mall.common.constant.AuthConstant;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 非系统销售管理Controller
 * Created by macro on 2025-11-27.
 */
@RestController
@Tag(name = "PmsNonSystemSaleController", description = "非系统销售管理")
@RequestMapping("/pmsNonSystemSale")
public class PmsNonSystemSaleController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsNonSystemSaleController.class);
    
    @Autowired
    private PmsNonSystemSaleService nonSystemSaleService;
    
    @Autowired
    private OmsStoreAddressService storeAddressService;
    
    @Autowired
    private PmsStoreSkuStockService storeSkuStockService;
    
    @Operation(summary = "获取所有销售类型")
    @GetMapping("/saleTypes")
    public CommonResult<List<PmsSalesType>> getSaleTypes() {
        try {
            List<PmsSalesType> typeList = nonSystemSaleService.getAllSalesTypes();
            return CommonResult.success(typeList);
        } catch (Exception e) {
            LOGGER.error("获取销售类型列表失败", e);
            return CommonResult.failed("获取销售类型失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取门店列表（支持按学校过滤）")
    @GetMapping("/stores")
    public CommonResult<List<OmsStoreAddress>> getStoreList(
            @Parameter(description = "学校ID（可选）") @RequestParam(required = false) Integer schoolId) {
        try {
            List<OmsStoreAddress> storeList;
            
            if (schoolId != null && schoolId > 0) {
                // 根据学校ID查询门店（包括地库）
                storeList = storeAddressService.getStoresBySchoolIdWithWarehouse(schoolId);
            } else {
                // 查询所有门店
                storeList = storeAddressService.list(null, null, null, null, 1, 100);
            }
            
            return CommonResult.success(storeList);
        } catch (Exception e) {
            LOGGER.error("获取门店列表失败", e);
            return CommonResult.failed("获取门店列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取门店SKU库存")
    @GetMapping("/storeSkuStock")
    public CommonResult<PmsStoreSkuStock> getStoreSkuStock(
            @Parameter(description = "门店ID") @RequestParam Long storeId,
            @Parameter(description = "SKU ID") @RequestParam Long skuId) {
        try {
            PmsStoreSkuStock storeSkuStock = storeSkuStockService.getStoreSkuStock(storeId, skuId);
            return CommonResult.success(storeSkuStock);
        } catch (Exception e) {
            LOGGER.error("获取门店SKU库存失败", e);
            return CommonResult.failed("获取门店SKU库存失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取SKU库存信息")
    @GetMapping("/skuStock/{skuId}")
    public CommonResult<PmsSkuStock> getSkuStock(
            @Parameter(description = "SKU ID") @PathVariable Long skuId) {
        try {
            PmsSkuStock skuStock = nonSystemSaleService.getSkuStock(skuId);
            if (skuStock == null) {
                return CommonResult.failed("SKU不存在");
            }
            return CommonResult.success(skuStock);
        } catch (Exception e) {
            LOGGER.error("获取SKU库存失败", e);
            return CommonResult.failed("获取SKU库存失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "提交非系统销售单")
    @PostMapping("/submit")
    public CommonResult<PmsNonSystemSaleDTO> submitSale(
            @RequestBody PmsNonSystemSaleCreateRequest request) {
        try {
            // ✓ 验证请求体
            if (request == null) {
                return CommonResult.failed("请求体不能为空");
            }
            
            // ✓ 验证销售类型ID
            if (request.getSaleTypeId() == null) {
                return CommonResult.failed("销售类型ID不能为空");
            }
            
            // ✓ 验证门店ID
            if (request.getStoreId() == null) {
                return CommonResult.failed("门店ID不能为空");
            }
            
            // ✓ 验证销售明细
            if (request.getItems() == null || request.getItems().isEmpty()) {
                return CommonResult.failed("销售明细不能为空");
            }
            
            // ✓ 验证每个销售明细项
            for (PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest item : request.getItems()) {
                if (item.getSkuId() == null) {
                    return CommonResult.failed("SKU ID不能为空");
                }
                if (item.getSkuCode() == null || item.getSkuCode().trim().isEmpty()) {
                    return CommonResult.failed("SKU编码不能为空");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    return CommonResult.failed("销售数量必须大于0");
                }
                if (item.getSalePrice() == null) {
                    return CommonResult.failed("销售价格不能为空");
                }
            }
            
            // 从当前登录用户获取operatorId和operatorName
            Long operatorId = getCurrentUserId();
            String operatorName = getCurrentUserName();
            
            if (operatorId == null || operatorName == null) {
                return CommonResult.failed("获取当前用户信息失败，请重新登录");
            }
            
            PmsNonSystemSaleDTO result = nonSystemSaleService.submitSale(request, operatorId, operatorName);
            return CommonResult.success(result, "销售单提交成功");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("提交销售单参数错误", e);
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("提交销售单失败", e);
            return CommonResult.failed("提交销售单失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
            if (userDto != null) {
                return userDto.getId();
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("获取当前用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 获取当前登录用户名
     */
    private String getCurrentUserName() {
        try {
            UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
            if (userDto != null) {
                return userDto.getUsername();
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("获取当前用户名失败", e);
            return null;
        }
    }
    
    @Operation(summary = "获取销售单列表")
    @GetMapping("/list")
    public CommonResult<PageInfo<PmsNonSystemSaleDTO>> getSaleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            PageInfo<PmsNonSystemSaleDTO> pageInfo = nonSystemSaleService.getSaleList(pageNum, pageSize);
            return CommonResult.success(pageInfo);
        } catch (Exception e) {
            LOGGER.error("获取销售单列表失败", e);
            return CommonResult.failed("获取销售单列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取销售单详情")
    @GetMapping("/{saleId}")
    public CommonResult<PmsNonSystemSaleDTO> getSaleDetail(
            @Parameter(description = "销售单ID") @PathVariable Long saleId) {
        try {
            PmsNonSystemSaleDTO saleDetail = nonSystemSaleService.getSaleDetail(saleId);
            return CommonResult.success(saleDetail);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("获取销售单详情参数错误", e);
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("获取销售单详情失败", e);
            return CommonResult.failed("获取销售单详情失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "审核销售单")
    @PostMapping("/{saleId}/approve")
    public CommonResult<Void> approveSale(
            @Parameter(description = "销售单ID") @PathVariable Long saleId) {
        try {
            nonSystemSaleService.approveSale(saleId);
            return CommonResult.success(null, "销售单审核成功");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("审核销售单参数错误", e);
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("审核销售单失败", e);
            return CommonResult.failed("审核销售单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "驳回销售单")
    @PostMapping("/{saleId}/reject")
    public CommonResult<Void> rejectSale(
            @Parameter(description = "销售单ID") @PathVariable Long saleId,
            @Parameter(description = "驳回原因") @RequestParam String reason) {
        try {
            nonSystemSaleService.rejectSale(saleId, reason);
            return CommonResult.success(null, "销售单驳回成功");
        } catch (IllegalArgumentException e) {
            LOGGER.warn("驳回销售单参数错误", e);
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("驳回销售单失败", e);
            return CommonResult.failed("驳回销售单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "计算库存扣减方案")
    @PostMapping("/calculateStockDeductionPlan")
    public CommonResult<java.util.List<com.macro.mall.dto.PmsStockDeductionPlan>> calculateStockDeductionPlan(
            @RequestBody java.util.Map<String, Object> request) {
        try {
            // ✓ 验证必需参数
            if (request == null || request.isEmpty()) {
                return CommonResult.failed("请求体不能为空");
            }
            
            Object schoolIdObj = request.get("schoolId");
            Object storeIdObj = request.get("storeId");
            Object itemsObj = request.get("items");
            
            if (schoolIdObj == null || storeIdObj == null || itemsObj == null) {
                return CommonResult.failed("缺少必需参数：schoolId、storeId 或 items");
            }
            
            Long schoolId = ((Number) schoolIdObj).longValue();
            Long storeId = ((Number) storeIdObj).longValue();
            java.util.List<java.util.Map<String, Object>> itemsData = (java.util.List<java.util.Map<String, Object>>) itemsObj;
            
            if (itemsData == null || itemsData.isEmpty()) {
                return CommonResult.failed("商品列表不能为空");
            }
            
            java.util.List<PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest> items = new java.util.ArrayList<>();
            for (java.util.Map<String, Object> itemData : itemsData) {
                PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest item = new PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest();
                
                Object skuIdObj = itemData.get("skuId");
                Object quantityObj = itemData.get("quantity");
                Object skuCodeObj = itemData.get("skuCode");
                
                // ✓ 验证必需字段
                if (skuIdObj == null || quantityObj == null) {
                    return CommonResult.failed("商品项缺少必需字段：skuId 或 quantity");
                }
                
                if (skuCodeObj == null || ((String) skuCodeObj).trim().isEmpty()) {
                    return CommonResult.failed("商品项缺少必需字段：skuCode");
                }
                
                Integer quantity = ((Number) quantityObj).intValue();
                if (quantity <= 0) {
                    return CommonResult.failed("商品数量必须大于0");
                }
                
                item.setSkuId(((Number) skuIdObj).longValue());
                item.setSkuCode((String) skuCodeObj);
                item.setProductName((String) itemData.get("productName"));
                item.setQuantity(quantity);
                items.add(item);
            }
            
            java.util.List<com.macro.mall.dto.PmsStockDeductionPlan> plans = nonSystemSaleService.calculateStockDeductionPlan(schoolId, storeId, items);
            return CommonResult.success(plans, "库存扣减方案计算成功");
        } catch (ClassCastException e) {
            LOGGER.error("计算库存扣减方案失败：参数类型转换错误", e);
            return CommonResult.failed("参数类型错误：" + e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.warn("计算库存扣减方案失败：业务错误", e);
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("计算库存扣减方案失败", e);
            return CommonResult.failed("计算库存扣减方案失败：" + e.getMessage());
        }
    }

    @Operation(summary = "导出销售单为 Excel")
    @GetMapping("/export/{saleId}")
    public void exportSale(
            @Parameter(description = "销售单ID") @PathVariable Long saleId,
            HttpServletResponse response) {
        try {
            nonSystemSaleService.exportSaleToExcel(saleId, response);
        } catch (Exception e) {
            LOGGER.error("导出销售单失败", e);
            try {
                response.getWriter().write("导出销售单失败：" + e.getMessage());
            } catch (Exception ex) {
                LOGGER.error("写入错误响应失败", ex);
            }
        }
    }
    
    @Operation(summary = "生成销售单分享信息")
    @PostMapping("/generateShareInfo/{saleId}")
    @ResponseBody
    public CommonResult<com.macro.mall.dto.PmsNonSystemSaleShareResult> generateShareInfo(
            @Parameter(description = "销售单ID") @PathVariable Long saleId) {
        try {
            com.macro.mall.dto.PmsNonSystemSaleShareResult shareResult = nonSystemSaleService.generateShareInfo(saleId);
            return CommonResult.success(shareResult, "分享信息生成成功");
        } catch (Exception e) {
            LOGGER.error("生成销售单分享信息失败", e);
            return CommonResult.failed("生成分享信息失败：" + e.getMessage());
        }
    }
}
