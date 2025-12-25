package com.macro.mall.mapper;

import com.macro.mall.model.PmsCommissionConfig;
import com.macro.mall.model.PmsCommissionConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsCommissionConfigMapper {
    long countByExample(PmsCommissionConfigExample example);

    int deleteByExample(PmsCommissionConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsCommissionConfig row);

    int insertSelective(PmsCommissionConfig row);

    List<PmsCommissionConfig> selectByExample(PmsCommissionConfigExample example);

    PmsCommissionConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsCommissionConfig row, @Param("example") PmsCommissionConfigExample example);

    int updateByExample(@Param("row") PmsCommissionConfig row, @Param("example") PmsCommissionConfigExample example);

    int updateByPrimaryKeySelective(PmsCommissionConfig row);

    int updateByPrimaryKey(PmsCommissionConfig row);
}