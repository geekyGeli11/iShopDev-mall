package com.macro.mall.mapper;

import com.macro.mall.model.CmsActivity;
import com.macro.mall.model.CmsActivityExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsActivityMapper {
    long countByExample(CmsActivityExample example);

    int deleteByExample(CmsActivityExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsActivity row);

    int insertSelective(CmsActivity row);

    List<CmsActivity> selectByExampleWithBLOBs(CmsActivityExample example);

    List<CmsActivity> selectByExample(CmsActivityExample example);

    CmsActivity selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsActivity row, @Param("example") CmsActivityExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsActivity row, @Param("example") CmsActivityExample example);

    int updateByExample(@Param("row") CmsActivity row, @Param("example") CmsActivityExample example);

    int updateByPrimaryKeySelective(CmsActivity row);

    int updateByPrimaryKeyWithBLOBs(CmsActivity row);

    int updateByPrimaryKey(CmsActivity row);
}