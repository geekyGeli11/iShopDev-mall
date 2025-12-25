package com.macro.mall.mapper;

import com.macro.mall.model.PmsDiyArea;
import com.macro.mall.model.PmsDiyAreaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsDiyAreaMapper {
    long countByExample(PmsDiyAreaExample example);

    int deleteByExample(PmsDiyAreaExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsDiyArea row);

    int insertSelective(PmsDiyArea row);

    List<PmsDiyArea> selectByExampleWithBLOBs(PmsDiyAreaExample example);

    List<PmsDiyArea> selectByExample(PmsDiyAreaExample example);

    PmsDiyArea selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsDiyArea row, @Param("example") PmsDiyAreaExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsDiyArea row, @Param("example") PmsDiyAreaExample example);

    int updateByExample(@Param("row") PmsDiyArea row, @Param("example") PmsDiyAreaExample example);

    int updateByPrimaryKeySelective(PmsDiyArea row);

    int updateByPrimaryKeyWithBLOBs(PmsDiyArea row);

    int updateByPrimaryKey(PmsDiyArea row);
}