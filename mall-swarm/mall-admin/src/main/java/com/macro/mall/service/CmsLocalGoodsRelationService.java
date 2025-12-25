package com.macro.mall.service;

import com.macro.mall.model.CmsLocalGoodsRelation;
import java.util.List;

/**
 * 本地好物与商品关联管理Service
 */
public interface CmsLocalGoodsRelationService {
    /**
     * 批量添加本地好物与商品关联
     */
    int create(List<CmsLocalGoodsRelation> relationList);

    /**
     * 根据好物ID批量添加本地好物与商品关联
     */
    int create(Long localGoodsId, List<Long> productIds);

    /**
     * 修改本地好物与商品关联
     */
    int update(Long id, CmsLocalGoodsRelation relation);

    /**
     * 删除本地好物与商品关联
     */
    int delete(Long id);

    /**
     * 根据好物ID删除关联
     */
    int deleteByLocalGoodsId(Long localGoodsId);

    /**
     * 获取指定好物关联的商品
     */
    List<CmsLocalGoodsRelation> listByLocalGoodsId(Long localGoodsId);
} 