package com.macro.mall.service;
import com.macro.mall.dto.CmsTraceabilityProduct;
import com.macro.mall.model.CmsTraceabilityProductRelation;
import com.macro.mall.model.CmsTraceabilityProductRelationExample;
import java.util.List;

/**
 * 追溯与产品关系管理Service
 */
public interface CmsTraceabilityProductRelationService {
    /**
     * 批量添加追溯与产品关系
     */
    int create(List<CmsTraceabilityProductRelation> relationList);

    /**
     * 更新追溯与产品关系
     */
    int update(Long id, CmsTraceabilityProductRelation relation);

    /**
     * 删除追溯与产品关系
     */
    int delete(Long id);

    /**
     * 获取单条追溯与产品关系
     */
    CmsTraceabilityProductRelation getItem(Long id);

    /**
     * 分页查询追溯与产品关系
     */
    List<CmsTraceabilityProduct> list(Long traceabilityId, Integer pageSize, Integer pageNum);

    /**
     * 获取追溯与产品关系总数
     */
    long getCount(Long traceabilityId);
}
