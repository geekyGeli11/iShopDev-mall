package com.macro.mall.mapper;

import com.macro.mall.model.DiyPageVersion;
import com.macro.mall.model.DiyPageVersionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyPageVersionMapper {
    long countByExample(DiyPageVersionExample example);

    int deleteByExample(DiyPageVersionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyPageVersion row);

    int insertSelective(DiyPageVersion row);

    List<DiyPageVersion> selectByExampleWithBLOBs(DiyPageVersionExample example);

    List<DiyPageVersion> selectByExample(DiyPageVersionExample example);

    DiyPageVersion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyPageVersion row, @Param("example") DiyPageVersionExample example);

    int updateByExampleWithBLOBs(@Param("row") DiyPageVersion row, @Param("example") DiyPageVersionExample example);

    int updateByExample(@Param("row") DiyPageVersion row, @Param("example") DiyPageVersionExample example);

    int updateByPrimaryKeySelective(DiyPageVersion row);

    int updateByPrimaryKeyWithBLOBs(DiyPageVersion row);

    int updateByPrimaryKey(DiyPageVersion row);
}