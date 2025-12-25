package com.macro.mall.mapper;

import com.macro.mall.model.PmsStyleModelProductRelation;
import com.macro.mall.model.PmsStyleModelProductRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsStyleModelProductRelationMapper {
    long countByExample(PmsStyleModelProductRelationExample example);

    int deleteByExample(PmsStyleModelProductRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsStyleModelProductRelation row);

    int insertSelective(PmsStyleModelProductRelation row);

    List<PmsStyleModelProductRelation> selectByExample(PmsStyleModelProductRelationExample example);

    PmsStyleModelProductRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsStyleModelProductRelation row, @Param("example") PmsStyleModelProductRelationExample example);

    int updateByExample(@Param("row") PmsStyleModelProductRelation row, @Param("example") PmsStyleModelProductRelationExample example);

    int updateByPrimaryKeySelective(PmsStyleModelProductRelation row);

    int updateByPrimaryKey(PmsStyleModelProductRelation row);
}