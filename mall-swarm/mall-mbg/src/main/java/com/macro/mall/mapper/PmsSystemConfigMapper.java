package com.macro.mall.mapper;

import com.macro.mall.model.PmsSystemConfig;
import com.macro.mall.model.PmsSystemConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsSystemConfigMapper {
    long countByExample(PmsSystemConfigExample example);

    int deleteByExample(PmsSystemConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSystemConfig row);

    int insertSelective(PmsSystemConfig row);

    List<PmsSystemConfig> selectByExample(PmsSystemConfigExample example);

    PmsSystemConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsSystemConfig row, @Param("example") PmsSystemConfigExample example);

    int updateByExample(@Param("row") PmsSystemConfig row, @Param("example") PmsSystemConfigExample example);

    int updateByPrimaryKeySelective(PmsSystemConfig row);

    int updateByPrimaryKey(PmsSystemConfig row);
}