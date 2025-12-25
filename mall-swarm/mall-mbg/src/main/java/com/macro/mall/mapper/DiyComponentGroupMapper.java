package com.macro.mall.mapper;

import com.macro.mall.model.DiyComponentGroup;
import com.macro.mall.model.DiyComponentGroupExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyComponentGroupMapper {
    long countByExample(DiyComponentGroupExample example);

    int deleteByExample(DiyComponentGroupExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyComponentGroup row);

    int insertSelective(DiyComponentGroup row);

    List<DiyComponentGroup> selectByExample(DiyComponentGroupExample example);

    DiyComponentGroup selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyComponentGroup row, @Param("example") DiyComponentGroupExample example);

    int updateByExample(@Param("row") DiyComponentGroup row, @Param("example") DiyComponentGroupExample example);

    int updateByPrimaryKeySelective(DiyComponentGroup row);

    int updateByPrimaryKey(DiyComponentGroup row);
}