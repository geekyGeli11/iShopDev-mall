package com.macro.mall.service;

import com.macro.mall.model.PmsInviteRelation;

import java.util.List;
import java.util.Map;

/**
 * 邀请关系管理Service
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteRelationService {
    
    /**
     * 分页获取邀请关系列表
     */
    List<Map<String, Object>> list(Long inviterId, Long inviteeId, Integer status, 
                                   String startTime, String endTime, Integer pageSize, Integer pageNum);
    
    /**
     * 获取邀请关系详情
     */
    Map<String, Object> getDetail(Long id);
    
    /**
     * 更新邀请关系状态
     */
    int updateStatus(Long id, Integer status, String remark);
    
    /**
     * 批量更新邀请状态
     */
    int batchUpdateStatus(List<Long> ids, Integer status, String remark);
    
    /**
     * 导出邀请记录
     */
    List<Map<String, Object>> exportInviteRecords(Long inviterId, Long inviteeId, 
                                                   Integer status, String startTime, String endTime);
    
    /**
     * 获取邀请统计概览
     */
    Map<String, Object> getInviteOverview();
} 