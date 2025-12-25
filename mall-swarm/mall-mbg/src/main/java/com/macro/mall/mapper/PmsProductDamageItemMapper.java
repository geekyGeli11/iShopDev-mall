package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductDamageItem;
import com.macro.mall.model.PmsProductDamageItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductDamageItemMapper {
    long countByExample(PmsProductDamageItemExample example);

    int deleteByExample(PmsProductDamageItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductDamageItem row);

    int insertSelective(PmsProductDamageItem row);

    List<PmsProductDamageItem> selectByExample(PmsProductDamageItemExample example);

    PmsProductDamageItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductDamageItem row, @Param("example") PmsProductDamageItemExample example);

    int updateByExample(@Param("row") PmsProductDamageItem row, @Param("example") PmsProductDamageItemExample example);

    int updateByPrimaryKeySelective(PmsProductDamageItem row);

    int updateByPrimaryKey(PmsProductDamageItem row);
}