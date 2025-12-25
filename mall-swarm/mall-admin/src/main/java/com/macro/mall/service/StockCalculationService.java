package com.macro.mall.service;

import com.macro.mall.dto.StockDistributionDto;
import com.macro.mall.dto.StockInconsistencyDto;

import java.util.List;

/**
 * 库存计算服务
 * 负责计算总库存、验证库存一致性等
 */
public interface StockCalculationService {
    
    /**
     * 计算商品SKU总库存
     * 总库存 = 所有门店库存 + 地库库存
     * 
     * @param skuId SKU ID
     * @return 计算得出的总库存
     */
    Integer calculateTotalStock(Long skuId);
    
    /**
     * 验证库存一致性
     * 检查 pms_sku_stock.stock 是否等于所有门店库存加地库库存之和
     * 
     * @param skuId SKU ID
     * @return true-一致，false-不一致
     */
    boolean validateStockConsistency(Long skuId);
    
    /**
     * 获取库存分布详情
     * 包含总库存、地库库存、各门店库存明细
     * 
     * @param skuId SKU ID
     * @return 库存分布信息
     */
    StockDistributionDto getStockDistribution(Long skuId);
    
    /**
     * 批量验证库存一致性
     * 
     * @param skuIds SKU ID列表
     * @return 不一致的库存记录列表
     */
    List<StockInconsistencyDto> batchValidateStock(List<Long> skuIds);
}
