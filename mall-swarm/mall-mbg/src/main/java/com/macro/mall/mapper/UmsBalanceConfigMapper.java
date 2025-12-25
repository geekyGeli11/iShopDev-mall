package com.macro.mall.mapper;

import com.macro.mall.model.UmsBalanceConfig;
import com.macro.mall.model.UmsBalanceConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsBalanceConfigMapper {
    long countByExample(UmsBalanceConfigExample example);

    int deleteByExample(UmsBalanceConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsBalanceConfig row);

    int insertSelective(UmsBalanceConfig row);

    List<UmsBalanceConfig> selectByExample(UmsBalanceConfigExample example);

    UmsBalanceConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsBalanceConfig row, @Param("example") UmsBalanceConfigExample example);

    int updateByExample(@Param("row") UmsBalanceConfig row, @Param("example") UmsBalanceConfigExample example);

    int updateByPrimaryKeySelective(UmsBalanceConfig row);

    int updateByPrimaryKey(UmsBalanceConfig row);
}