package com.macro.mall.mapper;

import com.macro.mall.model.PmsStockOperationLog;
import com.macro.mall.model.PmsStockOperationLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsStockOperationLogMapper {
    long countByExample(PmsStockOperationLogExample example);

    int deleteByExample(PmsStockOperationLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsStockOperationLog row);

    int insertSelective(PmsStockOperationLog row);

    List<PmsStockOperationLog> selectByExample(PmsStockOperationLogExample example);

    PmsStockOperationLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsStockOperationLog row, @Param("example") PmsStockOperationLogExample example);

    int updateByExample(@Param("row") PmsStockOperationLog row, @Param("example") PmsStockOperationLogExample example);

    int updateByPrimaryKeySelective(PmsStockOperationLog row);

    int updateByPrimaryKey(PmsStockOperationLog row);
}