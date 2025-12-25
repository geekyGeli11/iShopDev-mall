package com.macro.mall.mapper;

import com.macro.mall.model.CmsTraceabilityList;
import com.macro.mall.model.CmsTraceabilityListExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsTraceabilityListMapper {
    long countByExample(CmsTraceabilityListExample example);

    int deleteByExample(CmsTraceabilityListExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CmsTraceabilityList row);

    int insertSelective(CmsTraceabilityList row);

    List<CmsTraceabilityList> selectByExampleWithBLOBs(CmsTraceabilityListExample example);

    List<CmsTraceabilityList> selectByExample(CmsTraceabilityListExample example);

    CmsTraceabilityList selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") CmsTraceabilityList row, @Param("example") CmsTraceabilityListExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsTraceabilityList row, @Param("example") CmsTraceabilityListExample example);

    int updateByExample(@Param("row") CmsTraceabilityList row, @Param("example") CmsTraceabilityListExample example);

    int updateByPrimaryKeySelective(CmsTraceabilityList row);

    int updateByPrimaryKeyWithBLOBs(CmsTraceabilityList row);

    int updateByPrimaryKey(CmsTraceabilityList row);
}