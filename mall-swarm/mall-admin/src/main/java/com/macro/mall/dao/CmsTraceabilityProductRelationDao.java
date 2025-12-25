package com.macro.mall.dao;

import com.macro.mall.dto.CmsTraceabilityProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义溯源与产品关系管理Dao
 */
public interface CmsTraceabilityProductRelationDao {

    /**
     * 获取溯源与产品关系及相关商品信息
     * @param traceabilityId 溯源ID
     * @return List<CmsTraceabilityProductDto>
     */
    List<CmsTraceabilityProduct> getList(@Param("traceabilityId") Long traceabilityId);
}
