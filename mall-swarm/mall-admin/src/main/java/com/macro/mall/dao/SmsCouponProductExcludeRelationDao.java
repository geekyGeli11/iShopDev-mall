package com.macro.mall.dao;

import com.macro.mall.model.SmsCouponProductExcludeRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义优惠券排除商品关系管理Dao
 * Created by macro on 2025/09/01.
 */
public interface SmsCouponProductExcludeRelationDao {
    /**
     * 批量创建排除商品关系
     */
    int insertList(@Param("list") List<SmsCouponProductExcludeRelation> productExcludeRelationList);
}
