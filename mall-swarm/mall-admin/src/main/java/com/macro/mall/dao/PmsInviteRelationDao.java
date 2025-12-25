package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 邀请关系管理自定义Dao
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteRelationDao {
    
    /**
     * 分页获取邀请关系列表（包含用户信息）
     */
    List<Map<String, Object>> getInviteRelationsList(@Param("inviterId") Long inviterId,
                                                     @Param("inviteeId") Long inviteeId,
                                                     @Param("status") Integer status,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime);
    
    /**
     * 获取邀请关系详情（包含用户信息）
     */
    Map<String, Object> getInviteRelationDetail(@Param("id") Long id);
    
    /**
     * 获取邀请统计概览
     */
    Map<String, Object> getInviteOverviewStats();
} 