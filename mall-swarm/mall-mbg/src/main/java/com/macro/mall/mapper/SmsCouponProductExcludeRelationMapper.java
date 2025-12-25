package com.macro.mall.mapper;

import com.macro.mall.model.SmsCouponProductExcludeRelation;
import com.macro.mall.model.SmsCouponProductExcludeRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsCouponProductExcludeRelationMapper {
    long countByExample(SmsCouponProductExcludeRelationExample example);

    int deleteByExample(SmsCouponProductExcludeRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsCouponProductExcludeRelation row);

    int insertSelective(SmsCouponProductExcludeRelation row);

    List<SmsCouponProductExcludeRelation> selectByExample(SmsCouponProductExcludeRelationExample example);

    SmsCouponProductExcludeRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsCouponProductExcludeRelation row, @Param("example") SmsCouponProductExcludeRelationExample example);

    int updateByExample(@Param("row") SmsCouponProductExcludeRelation row, @Param("example") SmsCouponProductExcludeRelationExample example);

    int updateByPrimaryKeySelective(SmsCouponProductExcludeRelation row);

    int updateByPrimaryKey(SmsCouponProductExcludeRelation row);
}