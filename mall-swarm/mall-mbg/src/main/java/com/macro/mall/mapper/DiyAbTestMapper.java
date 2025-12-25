package com.macro.mall.mapper;

import com.macro.mall.model.DiyAbTest;
import com.macro.mall.model.DiyAbTestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyAbTestMapper {
    long countByExample(DiyAbTestExample example);

    int deleteByExample(DiyAbTestExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyAbTest row);

    int insertSelective(DiyAbTest row);

    List<DiyAbTest> selectByExample(DiyAbTestExample example);

    DiyAbTest selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyAbTest row, @Param("example") DiyAbTestExample example);

    int updateByExample(@Param("row") DiyAbTest row, @Param("example") DiyAbTestExample example);

    int updateByPrimaryKeySelective(DiyAbTest row);

    int updateByPrimaryKey(DiyAbTest row);
}