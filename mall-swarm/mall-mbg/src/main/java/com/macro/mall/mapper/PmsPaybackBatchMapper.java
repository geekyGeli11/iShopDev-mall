package com.macro.mall.mapper;

import com.macro.mall.model.PmsPaybackBatch;
import com.macro.mall.model.PmsPaybackBatchExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsPaybackBatchMapper {
    long countByExample(PmsPaybackBatchExample example);

    int deleteByExample(PmsPaybackBatchExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsPaybackBatch row);

    int insertSelective(PmsPaybackBatch row);

    List<PmsPaybackBatch> selectByExample(PmsPaybackBatchExample example);

    PmsPaybackBatch selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsPaybackBatch row, @Param("example") PmsPaybackBatchExample example);

    int updateByExample(@Param("row") PmsPaybackBatch row, @Param("example") PmsPaybackBatchExample example);

    int updateByPrimaryKeySelective(PmsPaybackBatch row);

    int updateByPrimaryKey(PmsPaybackBatch row);
}