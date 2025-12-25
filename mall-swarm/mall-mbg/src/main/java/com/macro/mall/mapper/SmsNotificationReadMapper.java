package com.macro.mall.mapper;

import com.macro.mall.model.SmsNotificationRead;
import com.macro.mall.model.SmsNotificationReadExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsNotificationReadMapper {
    long countByExample(SmsNotificationReadExample example);

    int deleteByExample(SmsNotificationReadExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsNotificationRead row);

    int insertSelective(SmsNotificationRead row);

    List<SmsNotificationRead> selectByExample(SmsNotificationReadExample example);

    SmsNotificationRead selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsNotificationRead row, @Param("example") SmsNotificationReadExample example);

    int updateByExample(@Param("row") SmsNotificationRead row, @Param("example") SmsNotificationReadExample example);

    int updateByPrimaryKeySelective(SmsNotificationRead row);

    int updateByPrimaryKey(SmsNotificationRead row);
}