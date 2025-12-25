package com.macro.mall.dao;

import com.macro.mall.model.SmsCouponProductCategoryExcludeRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义优惠券排除商品分类关系管理Dao
 * Created by macro on 2025/09/01.
 */
public interface SmsCouponProductCategoryExcludeRelationDao {
    /**
     * 批量创建排除商品分类关系
     */
    int insertList(@Param("list") List<SmsCouponProductCategoryExcludeRelation> productCategoryExcludeRelationList);
}
