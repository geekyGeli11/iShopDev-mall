package com.macro.mall.mapper;

import com.macro.mall.model.UmsPointsExchangeLog;
import com.macro.mall.model.UmsPointsExchangeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsPointsExchangeLogMapper {
    long countByExample(UmsPointsExchangeLogExample example);

    int deleteByExample(UmsPointsExchangeLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsPointsExchangeLog row);

    int insertSelective(UmsPointsExchangeLog row);

    List<UmsPointsExchangeLog> selectByExample(UmsPointsExchangeLogExample example);

    UmsPointsExchangeLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsPointsExchangeLog row, @Param("example") UmsPointsExchangeLogExample example);

    int updateByExample(@Param("row") UmsPointsExchangeLog row, @Param("example") UmsPointsExchangeLogExample example);

    int updateByPrimaryKeySelective(UmsPointsExchangeLog row);

    int updateByPrimaryKey(UmsPointsExchangeLog row);
}