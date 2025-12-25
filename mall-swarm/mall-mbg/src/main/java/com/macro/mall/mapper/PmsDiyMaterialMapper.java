package com.macro.mall.mapper;

import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyMaterialExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsDiyMaterialMapper {
    long countByExample(PmsDiyMaterialExample example);

    int deleteByExample(PmsDiyMaterialExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsDiyMaterial row);

    int insertSelective(PmsDiyMaterial row);

    List<PmsDiyMaterial> selectByExample(PmsDiyMaterialExample example);

    PmsDiyMaterial selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsDiyMaterial row, @Param("example") PmsDiyMaterialExample example);

    int updateByExample(@Param("row") PmsDiyMaterial row, @Param("example") PmsDiyMaterialExample example);

    int updateByPrimaryKeySelective(PmsDiyMaterial row);

    int updateByPrimaryKey(PmsDiyMaterial row);
}