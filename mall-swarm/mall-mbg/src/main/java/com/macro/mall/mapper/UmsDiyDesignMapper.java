package com.macro.mall.mapper;

import com.macro.mall.model.UmsDiyDesign;
import com.macro.mall.model.UmsDiyDesignExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsDiyDesignMapper {
    long countByExample(UmsDiyDesignExample example);

    int deleteByExample(UmsDiyDesignExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsDiyDesign row);

    int insertSelective(UmsDiyDesign row);

    List<UmsDiyDesign> selectByExampleWithBLOBs(UmsDiyDesignExample example);

    List<UmsDiyDesign> selectByExample(UmsDiyDesignExample example);

    UmsDiyDesign selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsDiyDesign row, @Param("example") UmsDiyDesignExample example);

    int updateByExampleWithBLOBs(@Param("row") UmsDiyDesign row, @Param("example") UmsDiyDesignExample example);

    int updateByExample(@Param("row") UmsDiyDesign row, @Param("example") UmsDiyDesignExample example);

    int updateByPrimaryKeySelective(UmsDiyDesign row);

    int updateByPrimaryKeyWithBLOBs(UmsDiyDesign row);

    int updateByPrimaryKey(UmsDiyDesign row);
}