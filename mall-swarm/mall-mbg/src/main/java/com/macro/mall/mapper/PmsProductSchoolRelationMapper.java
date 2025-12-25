package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductSchoolRelation;
import com.macro.mall.model.PmsProductSchoolRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductSchoolRelationMapper {
    long countByExample(PmsProductSchoolRelationExample example);

    int deleteByExample(PmsProductSchoolRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductSchoolRelation row);

    int insertSelective(PmsProductSchoolRelation row);

    List<PmsProductSchoolRelation> selectByExample(PmsProductSchoolRelationExample example);

    PmsProductSchoolRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductSchoolRelation row, @Param("example") PmsProductSchoolRelationExample example);

    int updateByExample(@Param("row") PmsProductSchoolRelation row, @Param("example") PmsProductSchoolRelationExample example);

    int updateByPrimaryKeySelective(PmsProductSchoolRelation row);

    int updateByPrimaryKey(PmsProductSchoolRelation row);
}