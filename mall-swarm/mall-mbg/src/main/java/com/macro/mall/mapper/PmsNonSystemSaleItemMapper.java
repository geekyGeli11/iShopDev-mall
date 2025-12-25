package com.macro.mall.mapper;

import com.macro.mall.model.PmsNonSystemSaleItem;
import com.macro.mall.model.PmsNonSystemSaleItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsNonSystemSaleItemMapper {
    long countByExample(PmsNonSystemSaleItemExample example);

    int deleteByExample(PmsNonSystemSaleItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsNonSystemSaleItem row);

    int insertSelective(PmsNonSystemSaleItem row);

    List<PmsNonSystemSaleItem> selectByExampleWithBLOBs(PmsNonSystemSaleItemExample example);

    List<PmsNonSystemSaleItem> selectByExample(PmsNonSystemSaleItemExample example);

    PmsNonSystemSaleItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsNonSystemSaleItem row, @Param("example") PmsNonSystemSaleItemExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsNonSystemSaleItem row, @Param("example") PmsNonSystemSaleItemExample example);

    int updateByExample(@Param("row") PmsNonSystemSaleItem row, @Param("example") PmsNonSystemSaleItemExample example);

    int updateByPrimaryKeySelective(PmsNonSystemSaleItem row);

    int updateByPrimaryKeyWithBLOBs(PmsNonSystemSaleItem row);

    int updateByPrimaryKey(PmsNonSystemSaleItem row);
}