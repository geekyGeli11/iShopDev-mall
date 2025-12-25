package com.macro.mall.mapper;

import com.macro.mall.model.UmsDistributorApply;
import com.macro.mall.model.UmsDistributorApplyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsDistributorApplyMapper {
    long countByExample(UmsDistributorApplyExample example);

    int deleteByExample(UmsDistributorApplyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsDistributorApply row);

    int insertSelective(UmsDistributorApply row);

    List<UmsDistributorApply> selectByExampleWithBLOBs(UmsDistributorApplyExample example);

    List<UmsDistributorApply> selectByExample(UmsDistributorApplyExample example);

    UmsDistributorApply selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsDistributorApply row, @Param("example") UmsDistributorApplyExample example);

    int updateByExampleWithBLOBs(@Param("row") UmsDistributorApply row, @Param("example") UmsDistributorApplyExample example);

    int updateByExample(@Param("row") UmsDistributorApply row, @Param("example") UmsDistributorApplyExample example);

    int updateByPrimaryKeySelective(UmsDistributorApply row);

    int updateByPrimaryKeyWithBLOBs(UmsDistributorApply row);

    int updateByPrimaryKey(UmsDistributorApply row);
}