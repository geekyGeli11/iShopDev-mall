package com.macro.mall.mapper;

import com.macro.mall.model.AppVersion;
import com.macro.mall.model.AppVersionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppVersionMapper {
    long countByExample(AppVersionExample example);

    int deleteByExample(AppVersionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AppVersion row);

    int insertSelective(AppVersion row);

    List<AppVersion> selectByExampleWithBLOBs(AppVersionExample example);

    List<AppVersion> selectByExample(AppVersionExample example);

    AppVersion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") AppVersion row, @Param("example") AppVersionExample example);

    int updateByExampleWithBLOBs(@Param("row") AppVersion row, @Param("example") AppVersionExample example);

    int updateByExample(@Param("row") AppVersion row, @Param("example") AppVersionExample example);

    int updateByPrimaryKeySelective(AppVersion row);

    int updateByPrimaryKeyWithBLOBs(AppVersion row);

    int updateByPrimaryKey(AppVersion row);
}