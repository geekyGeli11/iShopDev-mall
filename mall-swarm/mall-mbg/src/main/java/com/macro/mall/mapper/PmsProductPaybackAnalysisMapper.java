package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductPaybackAnalysis;
import com.macro.mall.model.PmsProductPaybackAnalysisExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductPaybackAnalysisMapper {
    long countByExample(PmsProductPaybackAnalysisExample example);

    int deleteByExample(PmsProductPaybackAnalysisExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductPaybackAnalysis row);

    int insertSelective(PmsProductPaybackAnalysis row);

    List<PmsProductPaybackAnalysis> selectByExample(PmsProductPaybackAnalysisExample example);

    PmsProductPaybackAnalysis selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductPaybackAnalysis row, @Param("example") PmsProductPaybackAnalysisExample example);

    int updateByExample(@Param("row") PmsProductPaybackAnalysis row, @Param("example") PmsProductPaybackAnalysisExample example);

    int updateByPrimaryKeySelective(PmsProductPaybackAnalysis row);

    int updateByPrimaryKey(PmsProductPaybackAnalysis row);
}