package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductDamageLog;
import com.macro.mall.model.PmsProductDamageLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductDamageLogMapper {
    long countByExample(PmsProductDamageLogExample example);

    int deleteByExample(PmsProductDamageLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductDamageLog row);

    int insertSelective(PmsProductDamageLog row);

    List<PmsProductDamageLog> selectByExample(PmsProductDamageLogExample example);

    PmsProductDamageLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductDamageLog row, @Param("example") PmsProductDamageLogExample example);

    int updateByExample(@Param("row") PmsProductDamageLog row, @Param("example") PmsProductDamageLogExample example);

    int updateByPrimaryKeySelective(PmsProductDamageLog row);

    int updateByPrimaryKey(PmsProductDamageLog row);
}