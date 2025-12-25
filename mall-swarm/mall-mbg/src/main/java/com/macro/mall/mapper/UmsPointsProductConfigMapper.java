package com.macro.mall.mapper;

import com.macro.mall.model.UmsPointsProductConfig;
import com.macro.mall.model.UmsPointsProductConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsPointsProductConfigMapper {
    long countByExample(UmsPointsProductConfigExample example);

    int deleteByExample(UmsPointsProductConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsPointsProductConfig row);

    int insertSelective(UmsPointsProductConfig row);

    List<UmsPointsProductConfig> selectByExampleWithBLOBs(UmsPointsProductConfigExample example);

    List<UmsPointsProductConfig> selectByExample(UmsPointsProductConfigExample example);

    UmsPointsProductConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsPointsProductConfig row, @Param("example") UmsPointsProductConfigExample example);

    int updateByExampleWithBLOBs(@Param("row") UmsPointsProductConfig row, @Param("example") UmsPointsProductConfigExample example);

    int updateByExample(@Param("row") UmsPointsProductConfig row, @Param("example") UmsPointsProductConfigExample example);

    int updateByPrimaryKeySelective(UmsPointsProductConfig row);

    int updateByPrimaryKeyWithBLOBs(UmsPointsProductConfig row);

    int updateByPrimaryKey(UmsPointsProductConfig row);
}