package com.macro.mall.mapper;

import com.macro.mall.model.DecPage;
import com.macro.mall.model.DecPageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DecPageMapper {
    long countByExample(DecPageExample example);

    int deleteByExample(DecPageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DecPage row);

    int insertSelective(DecPage row);

    List<DecPage> selectByExample(DecPageExample example);

    DecPage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DecPage row, @Param("example") DecPageExample example);

    int updateByExample(@Param("row") DecPage row, @Param("example") DecPageExample example);

    int updateByPrimaryKeySelective(DecPage row);

    int updateByPrimaryKey(DecPage row);
}