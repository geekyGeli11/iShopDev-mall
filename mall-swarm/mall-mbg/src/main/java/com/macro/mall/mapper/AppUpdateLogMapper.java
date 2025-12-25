package com.macro.mall.mapper;

import com.macro.mall.model.AppUpdateLog;
import com.macro.mall.model.AppUpdateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppUpdateLogMapper {
    long countByExample(AppUpdateLogExample example);

    int deleteByExample(AppUpdateLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AppUpdateLog row);

    int insertSelective(AppUpdateLog row);

    List<AppUpdateLog> selectByExampleWithBLOBs(AppUpdateLogExample example);

    List<AppUpdateLog> selectByExample(AppUpdateLogExample example);

    AppUpdateLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") AppUpdateLog row, @Param("example") AppUpdateLogExample example);

    int updateByExampleWithBLOBs(@Param("row") AppUpdateLog row, @Param("example") AppUpdateLogExample example);

    int updateByExample(@Param("row") AppUpdateLog row, @Param("example") AppUpdateLogExample example);

    int updateByPrimaryKeySelective(AppUpdateLog row);

    int updateByPrimaryKeyWithBLOBs(AppUpdateLog row);

    int updateByPrimaryKey(AppUpdateLog row);
}