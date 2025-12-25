package com.macro.mall.dao;

import com.macro.mall.dto.PmsProductResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 自定义商品管理Dao
 * Created by macro on 2018/4/26.
 */
public interface PmsProductDao {
    /**
     * 获取商品编辑信息
     */
    PmsProductResult getUpdateInfo(@Param("id") Long id);
    
    /**
     * 将商品的运费模板ID更新为null
     */
    int updateFreightTemplateIdToNull(@Param("id") Long id);

    /**
     * 根据商品货号或SKU编码获取商品ID列表
     */
    List<Long> getProductIdsBySnOrSkuCode(@Param("keyword") String keyword);
}
