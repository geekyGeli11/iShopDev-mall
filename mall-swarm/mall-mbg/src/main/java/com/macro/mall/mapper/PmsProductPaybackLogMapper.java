package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductPaybackLog;
import com.macro.mall.model.PmsProductPaybackLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductPaybackLogMapper {
    long countByExample(PmsProductPaybackLogExample example);

    int deleteByExample(PmsProductPaybackLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductPaybackLog row);

    int insertSelective(PmsProductPaybackLog row);

    List<PmsProductPaybackLog> selectByExample(PmsProductPaybackLogExample example);

    PmsProductPaybackLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductPaybackLog row, @Param("example") PmsProductPaybackLogExample example);

    int updateByExample(@Param("row") PmsProductPaybackLog row, @Param("example") PmsProductPaybackLogExample example);

    int updateByPrimaryKeySelective(PmsProductPaybackLog row);

    int updateByPrimaryKey(PmsProductPaybackLog row);
}