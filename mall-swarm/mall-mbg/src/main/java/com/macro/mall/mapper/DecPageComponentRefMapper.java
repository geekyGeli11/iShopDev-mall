package com.macro.mall.mapper;

import com.macro.mall.model.DecPageComponentRef;
import com.macro.mall.model.DecPageComponentRefExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DecPageComponentRefMapper {
    long countByExample(DecPageComponentRefExample example);

    int deleteByExample(DecPageComponentRefExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DecPageComponentRef row);

    int insertSelective(DecPageComponentRef row);

    List<DecPageComponentRef> selectByExampleWithBLOBs(DecPageComponentRefExample example);

    List<DecPageComponentRef> selectByExample(DecPageComponentRefExample example);

    DecPageComponentRef selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DecPageComponentRef row, @Param("example") DecPageComponentRefExample example);

    int updateByExampleWithBLOBs(@Param("row") DecPageComponentRef row, @Param("example") DecPageComponentRefExample example);

    int updateByExample(@Param("row") DecPageComponentRef row, @Param("example") DecPageComponentRefExample example);

    int updateByPrimaryKeySelective(DecPageComponentRef row);

    int updateByPrimaryKeyWithBLOBs(DecPageComponentRef row);

    int updateByPrimaryKey(DecPageComponentRef row);
}