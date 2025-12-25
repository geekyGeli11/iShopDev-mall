package com.macro.mall.mapper;

import com.macro.mall.model.PmsDiyMaterialCategory;
import com.macro.mall.model.PmsDiyMaterialCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsDiyMaterialCategoryMapper {
    long countByExample(PmsDiyMaterialCategoryExample example);

    int deleteByExample(PmsDiyMaterialCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsDiyMaterialCategory row);

    int insertSelective(PmsDiyMaterialCategory row);

    List<PmsDiyMaterialCategory> selectByExample(PmsDiyMaterialCategoryExample example);

    PmsDiyMaterialCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsDiyMaterialCategory row, @Param("example") PmsDiyMaterialCategoryExample example);

    int updateByExample(@Param("row") PmsDiyMaterialCategory row, @Param("example") PmsDiyMaterialCategoryExample example);

    int updateByPrimaryKeySelective(PmsDiyMaterialCategory row);

    int updateByPrimaryKey(PmsDiyMaterialCategory row);
}