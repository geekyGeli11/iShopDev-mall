package com.macro.mall.mapper;

import com.macro.mall.model.DiyPage;
import com.macro.mall.model.DiyPageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyPageMapper {
    long countByExample(DiyPageExample example);

    int deleteByExample(DiyPageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyPage row);

    int insertSelective(DiyPage row);

    List<DiyPage> selectByExample(DiyPageExample example);

    DiyPage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyPage row, @Param("example") DiyPageExample example);

    int updateByExample(@Param("row") DiyPage row, @Param("example") DiyPageExample example);

    int updateByPrimaryKeySelective(DiyPage row);

    int updateByPrimaryKey(DiyPage row);
}