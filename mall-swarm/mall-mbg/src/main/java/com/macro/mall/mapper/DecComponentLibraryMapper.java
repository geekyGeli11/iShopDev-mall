package com.macro.mall.mapper;

import com.macro.mall.model.DecComponentLibrary;
import com.macro.mall.model.DecComponentLibraryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DecComponentLibraryMapper {
    long countByExample(DecComponentLibraryExample example);

    int deleteByExample(DecComponentLibraryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DecComponentLibrary row);

    int insertSelective(DecComponentLibrary row);

    List<DecComponentLibrary> selectByExampleWithBLOBs(DecComponentLibraryExample example);

    List<DecComponentLibrary> selectByExample(DecComponentLibraryExample example);

    DecComponentLibrary selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DecComponentLibrary row, @Param("example") DecComponentLibraryExample example);

    int updateByExampleWithBLOBs(@Param("row") DecComponentLibrary row, @Param("example") DecComponentLibraryExample example);

    int updateByExample(@Param("row") DecComponentLibrary row, @Param("example") DecComponentLibraryExample example);

    int updateByPrimaryKeySelective(DecComponentLibrary row);

    int updateByPrimaryKeyWithBLOBs(DecComponentLibrary row);

    int updateByPrimaryKey(DecComponentLibrary row);
}