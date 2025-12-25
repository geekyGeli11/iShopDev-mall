package com.macro.mall.mapper;

import com.macro.mall.model.PmsProductDamageReason;
import com.macro.mall.model.PmsProductDamageReasonExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductDamageReasonMapper {
    long countByExample(PmsProductDamageReasonExample example);

    int deleteByExample(PmsProductDamageReasonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductDamageReason row);

    int insertSelective(PmsProductDamageReason row);

    List<PmsProductDamageReason> selectByExample(PmsProductDamageReasonExample example);

    PmsProductDamageReason selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductDamageReason row, @Param("example") PmsProductDamageReasonExample example);

    int updateByExample(@Param("row") PmsProductDamageReason row, @Param("example") PmsProductDamageReasonExample example);

    int updateByPrimaryKeySelective(PmsProductDamageReason row);

    int updateByPrimaryKey(PmsProductDamageReason row);
}