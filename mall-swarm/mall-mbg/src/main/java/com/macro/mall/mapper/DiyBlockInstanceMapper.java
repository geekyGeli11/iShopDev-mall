package com.macro.mall.mapper;

import com.macro.mall.model.DiyBlockInstance;
import com.macro.mall.model.DiyBlockInstanceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyBlockInstanceMapper {
    long countByExample(DiyBlockInstanceExample example);

    int deleteByExample(DiyBlockInstanceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyBlockInstance row);

    int insertSelective(DiyBlockInstance row);

    List<DiyBlockInstance> selectByExampleWithBLOBs(DiyBlockInstanceExample example);

    List<DiyBlockInstance> selectByExample(DiyBlockInstanceExample example);

    DiyBlockInstance selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyBlockInstance row, @Param("example") DiyBlockInstanceExample example);

    int updateByExampleWithBLOBs(@Param("row") DiyBlockInstance row, @Param("example") DiyBlockInstanceExample example);

    int updateByExample(@Param("row") DiyBlockInstance row, @Param("example") DiyBlockInstanceExample example);

    int updateByPrimaryKeySelective(DiyBlockInstance row);

    int updateByPrimaryKeyWithBLOBs(DiyBlockInstance row);

    int updateByPrimaryKey(DiyBlockInstance row);
}