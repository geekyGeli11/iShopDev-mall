package com.macro.mall.mapper;

import com.macro.mall.model.PmsSystemConfigHistory;
import com.macro.mall.model.PmsSystemConfigHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsSystemConfigHistoryMapper {
    long countByExample(PmsSystemConfigHistoryExample example);

    int deleteByExample(PmsSystemConfigHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSystemConfigHistory row);

    int insertSelective(PmsSystemConfigHistory row);

    List<PmsSystemConfigHistory> selectByExampleWithBLOBs(PmsSystemConfigHistoryExample example);

    List<PmsSystemConfigHistory> selectByExample(PmsSystemConfigHistoryExample example);

    PmsSystemConfigHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsSystemConfigHistory row, @Param("example") PmsSystemConfigHistoryExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsSystemConfigHistory row, @Param("example") PmsSystemConfigHistoryExample example);

    int updateByExample(@Param("row") PmsSystemConfigHistory row, @Param("example") PmsSystemConfigHistoryExample example);

    int updateByPrimaryKeySelective(PmsSystemConfigHistory row);

    int updateByPrimaryKeyWithBLOBs(PmsSystemConfigHistory row);

    int updateByPrimaryKey(PmsSystemConfigHistory row);
}