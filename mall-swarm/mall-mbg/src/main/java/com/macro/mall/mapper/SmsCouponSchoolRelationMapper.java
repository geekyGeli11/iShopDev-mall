package com.macro.mall.mapper;

import com.macro.mall.model.SmsCouponSchoolRelation;
import com.macro.mall.model.SmsCouponSchoolRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsCouponSchoolRelationMapper {
    long countByExample(SmsCouponSchoolRelationExample example);

    int deleteByExample(SmsCouponSchoolRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsCouponSchoolRelation row);

    int insertSelective(SmsCouponSchoolRelation row);

    List<SmsCouponSchoolRelation> selectByExample(SmsCouponSchoolRelationExample example);

    SmsCouponSchoolRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsCouponSchoolRelation row, @Param("example") SmsCouponSchoolRelationExample example);

    int updateByExample(@Param("row") SmsCouponSchoolRelation row, @Param("example") SmsCouponSchoolRelationExample example);

    int updateByPrimaryKeySelective(SmsCouponSchoolRelation row);

    int updateByPrimaryKey(SmsCouponSchoolRelation row);
}