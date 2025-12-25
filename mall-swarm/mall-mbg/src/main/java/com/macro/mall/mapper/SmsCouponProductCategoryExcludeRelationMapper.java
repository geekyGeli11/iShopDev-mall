package com.macro.mall.mapper;

import com.macro.mall.model.SmsCouponProductCategoryExcludeRelation;
import com.macro.mall.model.SmsCouponProductCategoryExcludeRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsCouponProductCategoryExcludeRelationMapper {
    long countByExample(SmsCouponProductCategoryExcludeRelationExample example);

    int deleteByExample(SmsCouponProductCategoryExcludeRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsCouponProductCategoryExcludeRelation row);

    int insertSelective(SmsCouponProductCategoryExcludeRelation row);

    List<SmsCouponProductCategoryExcludeRelation> selectByExample(SmsCouponProductCategoryExcludeRelationExample example);

    SmsCouponProductCategoryExcludeRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsCouponProductCategoryExcludeRelation row, @Param("example") SmsCouponProductCategoryExcludeRelationExample example);

    int updateByExample(@Param("row") SmsCouponProductCategoryExcludeRelation row, @Param("example") SmsCouponProductCategoryExcludeRelationExample example);

    int updateByPrimaryKeySelective(SmsCouponProductCategoryExcludeRelation row);

    int updateByPrimaryKey(SmsCouponProductCategoryExcludeRelation row);
}