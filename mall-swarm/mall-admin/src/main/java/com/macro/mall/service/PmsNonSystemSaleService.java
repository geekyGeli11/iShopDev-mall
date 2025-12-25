package com.macro.mall.service;

import com.macro.mall.model.PmsSalesType;
import com.macro.mall.model.PmsNonSystemSale;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.dto.PmsNonSystemSaleCreateRequest;
import com.macro.mall.dto.PmsNonSystemSaleDTO;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 非系统销售服务接口
 * Created by macro on 2025-11-27.
 */
public interface PmsNonSystemSaleService {
    
    /**
     * 获取所有启用的销售类型列表
     */
    List<PmsSalesType> getAllSalesTypes();
    
    /**
     * 获取SKU库存信息
     */
    PmsSkuStock getSkuStock(Long skuId);
    
    /**
     * 提交非系统销售单
     * 包括：保存销售单信息、保存销售明细、扣减库存、记录操作日志
     */
    PmsNonSystemSaleDTO submitSale(PmsNonSystemSaleCreateRequest request, Long operatorId, String operatorName);
    
    /**
     * 获取销售单列表
     */
    PageInfo<PmsNonSystemSaleDTO> getSaleList(Integer pageNum, Integer pageSize);
    
    /**
     * 获取销售单详情
     */
    PmsNonSystemSaleDTO getSaleDetail(Long saleId);
    
    /**
     * 审核销售单
     */
    void approveSale(Long saleId);
    
    /**
     * 驳回销售单
     */
    void rejectSale(Long saleId, String reason);
    
    /**
     * 计算库存扣减方案
     * 根据销售商品和销售门店，计算从哪些门店扣减库存
     */
    List<com.macro.mall.dto.PmsStockDeductionPlan> calculateStockDeductionPlan(Long schoolId, Long storeId, List<PmsNonSystemSaleCreateRequest.PmsNonSystemSaleItemRequest> items);
    
    /**
     * 导出销售单为 Excel
     */
    void exportSaleToExcel(Long saleId, HttpServletResponse response) throws Exception;
    
    /**
     * 生成销售单分享信息（小程序短链接和小程序码）
     */
    com.macro.mall.dto.PmsNonSystemSaleShareResult generateShareInfo(Long saleId);
}
