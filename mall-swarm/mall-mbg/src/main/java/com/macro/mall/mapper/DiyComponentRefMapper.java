package com.macro.mall.mapper;

import com.macro.mall.model.DiyComponentRef;
import com.macro.mall.model.DiyComponentRefExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyComponentRefMapper {
    long countByExample(DiyComponentRefExample example);

    int deleteByExample(DiyComponentRefExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyComponentRef row);

    int insertSelective(DiyComponentRef row);

    List<DiyComponentRef> selectByExample(DiyComponentRefExample example);

    DiyComponentRef selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyComponentRef row, @Param("example") DiyComponentRefExample example);

    int updateByExample(@Param("row") DiyComponentRef row, @Param("example") DiyComponentRefExample example);

    int updateByPrimaryKeySelective(DiyComponentRef row);

    int updateByPrimaryKey(DiyComponentRef row);
}