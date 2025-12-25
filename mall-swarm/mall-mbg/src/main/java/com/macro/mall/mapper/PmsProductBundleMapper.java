package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductBundle;
import com.macro.mall.model.PmsProductBundleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductBundleMapper {
    long countByExample(PmsProductBundleExample example);

    int deleteByExample(PmsProductBundleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductBundle row);

    int insertSelective(PmsProductBundle row);

    List<PmsProductBundle> selectByExampleWithBLOBs(PmsProductBundleExample example);

    List<PmsProductBundle> selectByExample(PmsProductBundleExample example);

    PmsProductBundle selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductBundle row, @Param("example") PmsProductBundleExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsProductBundle row, @Param("example") PmsProductBundleExample example);

    int updateByExample(@Param("row") PmsProductBundle row, @Param("example") PmsProductBundleExample example);

    int updateByPrimaryKeySelective(PmsProductBundle row);

    int updateByPrimaryKeyWithBLOBs(PmsProductBundle row);

    int updateByPrimaryKey(PmsProductBundle row);
}