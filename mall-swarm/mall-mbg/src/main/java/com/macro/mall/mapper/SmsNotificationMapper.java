package com.macro.mall.mapper;

import com.macro.mall.model.SmsNotification;
import com.macro.mall.model.SmsNotificationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsNotificationMapper {
    long countByExample(SmsNotificationExample example);

    int deleteByExample(SmsNotificationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsNotification row);

    int insertSelective(SmsNotification row);

    List<SmsNotification> selectByExampleWithBLOBs(SmsNotificationExample example);

    List<SmsNotification> selectByExample(SmsNotificationExample example);

    SmsNotification selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsNotification row, @Param("example") SmsNotificationExample example);

    int updateByExampleWithBLOBs(@Param("row") SmsNotification row, @Param("example") SmsNotificationExample example);

    int updateByExample(@Param("row") SmsNotification row, @Param("example") SmsNotificationExample example);

    int updateByPrimaryKeySelective(SmsNotification row);

    int updateByPrimaryKeyWithBLOBs(SmsNotification row);

    int updateByPrimaryKey(SmsNotification row);
}