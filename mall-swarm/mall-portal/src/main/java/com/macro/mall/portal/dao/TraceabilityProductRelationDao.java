package com.macro.mall.portal.dao;

import com.macro.mall.portal.dto.TraceabilityProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义溯源与产品关系管理Dao
 */
public interface TraceabilityProductRelationDao {

    /**
     * 获取溯源与产品关系及相关商品信息
     * @param traceabilityId 溯源ID
     * @return List<CmsTraceabilityProductDto>
     */
    List<TraceabilityProduct> getList(@Param("traceabilityId") Long traceabilityId);
}
