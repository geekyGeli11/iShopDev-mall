package com.macro.mall.mapper;

import com.macro.mall.model.UmsNotificationLog;
import com.macro.mall.model.UmsNotificationLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsNotificationLogMapper {
    long countByExample(UmsNotificationLogExample example);

    int deleteByExample(UmsNotificationLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsNotificationLog row);

    int insertSelective(UmsNotificationLog row);

    List<UmsNotificationLog> selectByExampleWithBLOBs(UmsNotificationLogExample example);

    List<UmsNotificationLog> selectByExample(UmsNotificationLogExample example);

    UmsNotificationLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsNotificationLog row, @Param("example") UmsNotificationLogExample example);

    int updateByExampleWithBLOBs(@Param("row") UmsNotificationLog row, @Param("example") UmsNotificationLogExample example);

    int updateByExample(@Param("row") UmsNotificationLog row, @Param("example") UmsNotificationLogExample example);

    int updateByPrimaryKeySelective(UmsNotificationLog row);

    int updateByPrimaryKeyWithBLOBs(UmsNotificationLog row);

    int updateByPrimaryKey(UmsNotificationLog row);
}