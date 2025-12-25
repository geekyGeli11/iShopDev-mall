package com.macro.mall.mapper;

import com.macro.mall.model.PmsStoreSkuStock;
import org.apache.ibatis.annotations.Param;

/**
 * 门店SKU库存扩展Mapper
 * Created by macro on 2025-11-27.
 */
public interface PmsStoreSkuStockExtMapper {
    
    /**
     * 根据门店ID和SKU ID查询门店库存
     */
    PmsStoreSkuStock selectByStoreIdAndSkuId(@Param("storeId") Long storeId, @Param("skuId") Long skuId);
    
    /**
     * 根据SKU ID查询所有门店的库存
     */
    java.util.List<PmsStoreSkuStock> selectBySkuId(@Param("skuId") Long skuId);
}
