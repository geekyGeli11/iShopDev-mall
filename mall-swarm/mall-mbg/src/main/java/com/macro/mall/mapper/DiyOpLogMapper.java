package com.macro.mall.mapper;

import com.macro.mall.model.DiyOpLog;
import com.macro.mall.model.DiyOpLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyOpLogMapper {
    long countByExample(DiyOpLogExample example);

    int deleteByExample(DiyOpLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyOpLog row);

    int insertSelective(DiyOpLog row);

    List<DiyOpLog> selectByExampleWithBLOBs(DiyOpLogExample example);

    List<DiyOpLog> selectByExample(DiyOpLogExample example);

    DiyOpLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyOpLog row, @Param("example") DiyOpLogExample example);

    int updateByExampleWithBLOBs(@Param("row") DiyOpLog row, @Param("example") DiyOpLogExample example);

    int updateByExample(@Param("row") DiyOpLog row, @Param("example") DiyOpLogExample example);

    int updateByPrimaryKeySelective(DiyOpLog row);

    int updateByPrimaryKeyWithBLOBs(DiyOpLog row);

    int updateByPrimaryKey(DiyOpLog row);
}