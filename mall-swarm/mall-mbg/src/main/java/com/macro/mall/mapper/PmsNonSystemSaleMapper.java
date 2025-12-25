package com.macro.mall.mapper;

import com.macro.mall.model.PmsNonSystemSale;
import com.macro.mall.model.PmsNonSystemSaleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsNonSystemSaleMapper {
    long countByExample(PmsNonSystemSaleExample example);

    int deleteByExample(PmsNonSystemSaleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsNonSystemSale row);

    int insertSelective(PmsNonSystemSale row);

    List<PmsNonSystemSale> selectByExample(PmsNonSystemSaleExample example);

    PmsNonSystemSale selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsNonSystemSale row, @Param("example") PmsNonSystemSaleExample example);

    int updateByExample(@Param("row") PmsNonSystemSale row, @Param("example") PmsNonSystemSaleExample example);

    int updateByPrimaryKeySelective(PmsNonSystemSale row);

    int updateByPrimaryKey(PmsNonSystemSale row);
}