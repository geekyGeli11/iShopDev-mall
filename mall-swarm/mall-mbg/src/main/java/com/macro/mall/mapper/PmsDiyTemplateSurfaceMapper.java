package com.macro.mall.mapper;

import com.macro.mall.model.PmsDiyTemplateSurface;
import com.macro.mall.model.PmsDiyTemplateSurfaceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsDiyTemplateSurfaceMapper {
    long countByExample(PmsDiyTemplateSurfaceExample example);

    int deleteByExample(PmsDiyTemplateSurfaceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsDiyTemplateSurface row);

    int insertSelective(PmsDiyTemplateSurface row);

    List<PmsDiyTemplateSurface> selectByExample(PmsDiyTemplateSurfaceExample example);

    PmsDiyTemplateSurface selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsDiyTemplateSurface row, @Param("example") PmsDiyTemplateSurfaceExample example);

    int updateByExample(@Param("row") PmsDiyTemplateSurface row, @Param("example") PmsDiyTemplateSurfaceExample example);

    int updateByPrimaryKeySelective(PmsDiyTemplateSurface row);

    int updateByPrimaryKey(PmsDiyTemplateSurface row);
}