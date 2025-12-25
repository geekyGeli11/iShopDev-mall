package com.macro.mall.mapper;

import com.macro.mall.model.PmsStoreSkuStock;
import com.macro.mall.model.PmsStoreSkuStockExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsStoreSkuStockMapper {
    long countByExample(PmsStoreSkuStockExample example);

    int deleteByExample(PmsStoreSkuStockExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsStoreSkuStock row);

    int insertSelective(PmsStoreSkuStock row);

    List<PmsStoreSkuStock> selectByExample(PmsStoreSkuStockExample example);

    PmsStoreSkuStock selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsStoreSkuStock row, @Param("example") PmsStoreSkuStockExample example);

    int updateByExample(@Param("row") PmsStoreSkuStock row, @Param("example") PmsStoreSkuStockExample example);

    int updateByPrimaryKeySelective(PmsStoreSkuStock row);

    int updateByPrimaryKey(PmsStoreSkuStock row);
}