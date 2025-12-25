package com.macro.mall.portal.dao;

import com.macro.mall.model.OmsGiftRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 礼物记录管理自定义DAO
 */
public interface OmsGiftRecordDao {
    
    /**
     * 获取用户的送礼记录
     */
    List<OmsGiftRecord> getSentGiftList(@Param("senderId") Long senderId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 获取用户的收礼记录
     */
    List<OmsGiftRecord> getReceivedGiftList(@Param("receiverId") Long receiverId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
} 