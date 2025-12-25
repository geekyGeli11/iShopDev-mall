package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductDamageReport;
import com.macro.mall.model.PmsProductDamageReportExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductDamageReportMapper {
    long countByExample(PmsProductDamageReportExample example);

    int deleteByExample(PmsProductDamageReportExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductDamageReport row);

    int insertSelective(PmsProductDamageReport row);

    List<PmsProductDamageReport> selectByExampleWithBLOBs(PmsProductDamageReportExample example);

    List<PmsProductDamageReport> selectByExample(PmsProductDamageReportExample example);

    PmsProductDamageReport selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductDamageReport row, @Param("example") PmsProductDamageReportExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsProductDamageReport row, @Param("example") PmsProductDamageReportExample example);

    int updateByExample(@Param("row") PmsProductDamageReport row, @Param("example") PmsProductDamageReportExample example);

    int updateByPrimaryKeySelective(PmsProductDamageReport row);

    int updateByPrimaryKeyWithBLOBs(PmsProductDamageReport row);

    int updateByPrimaryKey(PmsProductDamageReport row);
}