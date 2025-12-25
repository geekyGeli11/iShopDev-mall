package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductBundleItem;
import com.macro.mall.model.PmsProductBundleItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductBundleItemMapper {
    long countByExample(PmsProductBundleItemExample example);

    int deleteByExample(PmsProductBundleItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductBundleItem row);

    int insertSelective(PmsProductBundleItem row);

    List<PmsProductBundleItem> selectByExample(PmsProductBundleItemExample example);

    PmsProductBundleItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductBundleItem row, @Param("example") PmsProductBundleItemExample example);

    int updateByExample(@Param("row") PmsProductBundleItem row, @Param("example") PmsProductBundleItemExample example);

    int updateByPrimaryKeySelective(PmsProductBundleItem row);

    int updateByPrimaryKey(PmsProductBundleItem row);
}