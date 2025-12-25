package com.macro.mall.mapper;

import com.macro.mall.model.PmsSalesType;
import com.macro.mall.model.PmsSalesTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsSalesTypeMapper {
    long countByExample(PmsSalesTypeExample example);

    int deleteByExample(PmsSalesTypeExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSalesType row);

    int insertSelective(PmsSalesType row);

    List<PmsSalesType> selectByExample(PmsSalesTypeExample example);

    PmsSalesType selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsSalesType row, @Param("example") PmsSalesTypeExample example);

    int updateByExample(@Param("row") PmsSalesType row, @Param("example") PmsSalesTypeExample example);

    int updateByPrimaryKeySelective(PmsSalesType row);

    int updateByPrimaryKey(PmsSalesType row);
}