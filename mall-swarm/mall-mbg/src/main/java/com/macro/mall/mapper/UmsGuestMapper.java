package com.macro.mall.mapper;

import com.macro.mall.model.UmsGuest;
import com.macro.mall.model.UmsGuestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsGuestMapper {
    long countByExample(UmsGuestExample example);

    int deleteByExample(UmsGuestExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsGuest row);

    int insertSelective(UmsGuest row);

    List<UmsGuest> selectByExample(UmsGuestExample example);

    UmsGuest selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsGuest row, @Param("example") UmsGuestExample example);

    int updateByExample(@Param("row") UmsGuest row, @Param("example") UmsGuestExample example);

    int updateByPrimaryKeySelective(UmsGuest row);

    int updateByPrimaryKey(UmsGuest row);
}