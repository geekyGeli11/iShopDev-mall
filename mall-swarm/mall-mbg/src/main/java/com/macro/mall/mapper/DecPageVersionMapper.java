package com.macro.mall.mapper;

import com.macro.mall.model.DecPageVersion;
import com.macro.mall.model.DecPageVersionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DecPageVersionMapper {
    long countByExample(DecPageVersionExample example);

    int deleteByExample(DecPageVersionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DecPageVersion row);

    int insertSelective(DecPageVersion row);

    List<DecPageVersion> selectByExampleWithBLOBs(DecPageVersionExample example);

    List<DecPageVersion> selectByExample(DecPageVersionExample example);

    DecPageVersion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DecPageVersion row, @Param("example") DecPageVersionExample example);

    int updateByExampleWithBLOBs(@Param("row") DecPageVersion row, @Param("example") DecPageVersionExample example);

    int updateByExample(@Param("row") DecPageVersion row, @Param("example") DecPageVersionExample example);

    int updateByPrimaryKeySelective(DecPageVersion row);

    int updateByPrimaryKeyWithBLOBs(DecPageVersion row);

    int updateByPrimaryKey(DecPageVersion row);
}