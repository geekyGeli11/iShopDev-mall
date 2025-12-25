package com.macro.mall.mapper;

import com.macro.mall.model.DiyComponentLibrary;
import com.macro.mall.model.DiyComponentLibraryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DiyComponentLibraryMapper {
    long countByExample(DiyComponentLibraryExample example);

    int deleteByExample(DiyComponentLibraryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DiyComponentLibrary row);

    int insertSelective(DiyComponentLibrary row);

    List<DiyComponentLibrary> selectByExampleWithBLOBs(DiyComponentLibraryExample example);

    List<DiyComponentLibrary> selectByExample(DiyComponentLibraryExample example);

    DiyComponentLibrary selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") DiyComponentLibrary row, @Param("example") DiyComponentLibraryExample example);

    int updateByExampleWithBLOBs(@Param("row") DiyComponentLibrary row, @Param("example") DiyComponentLibraryExample example);

    int updateByExample(@Param("row") DiyComponentLibrary row, @Param("example") DiyComponentLibraryExample example);

    int updateByPrimaryKeySelective(DiyComponentLibrary row);

    int updateByPrimaryKeyWithBLOBs(DiyComponentLibrary row);

    int updateByPrimaryKey(DiyComponentLibrary row);
}