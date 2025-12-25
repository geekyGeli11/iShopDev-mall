package com.macro.mall.service;

import com.macro.mall.dto.PmsStockOperationParam;
import com.macro.mall.dto.PmsStockOperationLogQueryParam;
import com.macro.mall.model.PmsStockOperationLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 库存管理Service
 * Created by macro on 2024-01-20.
 */
public interface PmsStockService {
    
    /**
     * 执行库存操作
     * @param param 操作参数
     * @return 操作结果
     */
    @Transactional
    int executeStockOperation(PmsStockOperationParam param);
    
    /**
     * 获取库存操作记录
     * @param queryParam 查询参数
     * @return 操作记录列表
     */
    List<PmsStockOperationLog> getStockOperationLogs(PmsStockOperationLogQueryParam queryParam);
    
    /**
     * 检查库存是否充足
     * @param productId 商品ID
     * @param skuId SKU ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    boolean checkStockAvailable(Long productId, Long skuId, Integer quantity);
    
    /**
     * 验证库存充足性（支持地库）
     * @param storeId 门店ID（包括地库）
     * @param skuId SKU ID
     * @param quantity 需要的数量
     * @return 是否充足
     */
    boolean validateStockAvailability(Long storeId, Long skuId, Integer quantity);
    
    /**
     * 生成操作单号
     * @param operationType 操作类型
     * @return 操作单号
     */
    String generateOperationNo(String operationType);
} 