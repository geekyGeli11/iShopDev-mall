package com.macro.mall.mapper;

import com.macro.mall.model.CmsTraceabilityProductRelation;
import com.macro.mall.model.CmsTraceabilityProductRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsTraceabilityProductRelationMapper {
    long countByExample(CmsTraceabilityProductRelationExample example);

    int deleteByExample(CmsTraceabilityProductRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsTraceabilityProductRelation row);

    int insertSelective(CmsTraceabilityProductRelation row);

    List<CmsTraceabilityProductRelation> selectByExample(CmsTraceabilityProductRelationExample example);

    CmsTraceabilityProductRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsTraceabilityProductRelation row, @Param("example") CmsTraceabilityProductRelationExample example);

    int updateByExample(@Param("row") CmsTraceabilityProductRelation row, @Param("example") CmsTraceabilityProductRelationExample example);

    int updateByPrimaryKeySelective(CmsTraceabilityProductRelation row);

    int updateByPrimaryKey(CmsTraceabilityProductRelation row);
}