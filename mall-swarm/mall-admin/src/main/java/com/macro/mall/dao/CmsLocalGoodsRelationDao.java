package com.macro.mall.dao;

import com.macro.mall.dto.CmsLocalGoodsDetail.RelatedProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本地好物关联商品自定义DAO
 */
public interface CmsLocalGoodsRelationDao {
    /**
     * 获取本地好物关联的商品详情
     */
    List<RelatedProduct> getRelatedProducts(@Param("goodsId") Long goodsId);
} 