package com.macro.mall.mapper;

import com.macro.mall.model.CmsWonderfulMacau;
import com.macro.mall.model.CmsWonderfulMacauExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CmsWonderfulMacauMapper {
    long countByExample(CmsWonderfulMacauExample example);

    int deleteByExample(CmsWonderfulMacauExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsWonderfulMacau row);

    int insertSelective(CmsWonderfulMacau row);

    List<CmsWonderfulMacau> selectByExampleWithBLOBs(CmsWonderfulMacauExample example);

    List<CmsWonderfulMacau> selectByExample(CmsWonderfulMacauExample example);

    CmsWonderfulMacau selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsWonderfulMacau row, @Param("example") CmsWonderfulMacauExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsWonderfulMacau row, @Param("example") CmsWonderfulMacauExample example);

    int updateByExample(@Param("row") CmsWonderfulMacau row, @Param("example") CmsWonderfulMacauExample example);

    int updateByPrimaryKeySelective(CmsWonderfulMacau row);

    int updateByPrimaryKeyWithBLOBs(CmsWonderfulMacau row);

    int updateByPrimaryKey(CmsWonderfulMacau row);
}