package com.macro.mall.service;

import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.dto.PmsStoreSkuStockParam;
import com.macro.mall.dto.PmsStoreSkuStockExtendDto;

import java.util.List;

/**
 * 门店SKU库存管理Service
 * Created by macro on 2024-01-20.
 */
public interface PmsStoreSkuStockService {
    
    /**
     * 根据商品ID查询所有门店的SKU库存分配情况
     * @param productId 商品ID
     * @return 门店SKU库存列表
     */
    List<PmsStoreSkuStock> getStoreSkuStockByProductId(Long productId);
    
    /**
     * 根据商品ID查询所有门店的SKU库存分配情况（包含规格信息）
     * @param productId 商品ID
     * @return 门店SKU库存扩展列表
     */
    List<PmsStoreSkuStockExtendDto> getStoreSkuStockWithSpecByProductId(Long productId);
    
    /**
     * 根据SKU ID查询所有门店的库存分配情况
     * @param skuId SKU ID
     * @return 门店SKU库存列表
     */
    List<PmsStoreSkuStock> getStoreSkuStockBySkuId(Long skuId);
    
    /**
     * 根据门店ID和SKU ID查询特定门店的库存
     * @param storeId 门店ID
     * @param skuId SKU ID
     * @return 门店SKU库存信息
     */
    PmsStoreSkuStock getStoreSkuStock(Long storeId, Long skuId);
    
    /**
     * 批量保存门店SKU库存分配
     * @param storeSkuStockList 门店SKU库存列表
     * @return 保存成功的数量
     */
    int batchSaveStoreSkuStock(List<PmsStoreSkuStock> storeSkuStockList);
    
    /**
     * 保存单个门店SKU库存
     * @param storeSkuStock 门店SKU库存信息
     * @return 保存结果
     */
    PmsStoreSkuStock saveStoreSkuStock(PmsStoreSkuStock storeSkuStock);
    
    /**
     * 删除门店SKU库存记录
     * @param storeId 门店ID
     * @param skuId SKU ID
     * @return 删除结果
     */
    boolean deleteStoreSkuStock(Long storeId, Long skuId);
    
    /**
     * 校验门店库存总和是否等于SKU总库存
     * @param skuId SKU ID
     * @param skuTotalStock SKU总库存
     * @return 校验结果
     */
    boolean validateStoreStockSum(Long skuId, Integer skuTotalStock);
    
    /**
     * 为指定SKU自动均分库存到所有启用的门店
     * @param skuId SKU ID
     * @param totalStock 总库存
     * @return 分配结果
     */
    List<PmsStoreSkuStock> autoDistributeStock(Long skuId, Integer totalStock);
    
    /**
     * 根据商品ID和门店库存参数批量保存门店SKU库存
     * @param productId 商品ID
     * @param storeStockParams 门店库存参数列表
     * @return 保存结果
     */
    boolean batchSaveStoreSkuStockByProduct(Long productId, List<PmsStoreSkuStockParam> storeStockParams);
    
    /**
     * 根据商品ID删除所有门店库存记录（用于商品删除时清理）
     * @param productId 商品ID
     * @return 删除数量
     */
    int deleteStoreSkuStockByProductId(Long productId);
    
    /**
     * 为新商品初始化门店库存记录
     * @param productId 商品ID
     */
    void initStoreSkuStockForProduct(Long productId);
    
    /**
     * 同步商品的门店库存记录
     * @param productId 商品ID
     * @param currentSkuIds 当前有效的SKU ID列表
     */
    void syncStoreSkuStockForProduct(Long productId, List<Long> currentSkuIds);
    
    /**
     * 根据门店ID查询该门店所有SKU库存（包含规格信息）
     * @param storeId 门店ID
     * @return 门店SKU库存扩展列表
     */
    List<PmsStoreSkuStockExtendDto> getStoreSkuStockByStoreId(Long storeId);
} 