package com.macro.mall.mapper;

import com.macro.mall.model.AppDevice;
import com.macro.mall.model.AppDeviceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppDeviceMapper {
    long countByExample(AppDeviceExample example);

    int deleteByExample(AppDeviceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AppDevice row);

    int insertSelective(AppDevice row);

    List<AppDevice> selectByExample(AppDeviceExample example);

    AppDevice selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") AppDevice row, @Param("example") AppDeviceExample example);

    int updateByExample(@Param("row") AppDevice row, @Param("example") AppDeviceExample example);

    int updateByPrimaryKeySelective(AppDevice row);

    int updateByPrimaryKey(AppDevice row);
}