package com.macro.mall.mapper;

import com.macro.mall.model.UmsPointsCouponConfig;
import com.macro.mall.model.UmsPointsCouponConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsPointsCouponConfigMapper {
    long countByExample(UmsPointsCouponConfigExample example);

    int deleteByExample(UmsPointsCouponConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsPointsCouponConfig row);

    int insertSelective(UmsPointsCouponConfig row);

    List<UmsPointsCouponConfig> selectByExampleWithBLOBs(UmsPointsCouponConfigExample example);

    List<UmsPointsCouponConfig> selectByExample(UmsPointsCouponConfigExample example);

    UmsPointsCouponConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsPointsCouponConfig row, @Param("example") UmsPointsCouponConfigExample example);

    int updateByExampleWithBLOBs(@Param("row") UmsPointsCouponConfig row, @Param("example") UmsPointsCouponConfigExample example);

    int updateByExample(@Param("row") UmsPointsCouponConfig row, @Param("example") UmsPointsCouponConfigExample example);

    int updateByPrimaryKeySelective(UmsPointsCouponConfig row);

    int updateByPrimaryKeyWithBLOBs(UmsPointsCouponConfig row);

    int updateByPrimaryKey(UmsPointsCouponConfig row);
}