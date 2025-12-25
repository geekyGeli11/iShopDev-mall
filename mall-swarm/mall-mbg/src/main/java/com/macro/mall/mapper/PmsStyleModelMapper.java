package com.macro.mall.mapper;

import com.macro.mall.model.PmsStyleModel;
import com.macro.mall.model.PmsStyleModelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsStyleModelMapper {
    long countByExample(PmsStyleModelExample example);

    int deleteByExample(PmsStyleModelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsStyleModel row);

    int insertSelective(PmsStyleModel row);

    List<PmsStyleModel> selectByExample(PmsStyleModelExample example);

    PmsStyleModel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsStyleModel row, @Param("example") PmsStyleModelExample example);

    int updateByExample(@Param("row") PmsStyleModel row, @Param("example") PmsStyleModelExample example);

    int updateByPrimaryKeySelective(PmsStyleModel row);

    int updateByPrimaryKey(PmsStyleModel row);
}