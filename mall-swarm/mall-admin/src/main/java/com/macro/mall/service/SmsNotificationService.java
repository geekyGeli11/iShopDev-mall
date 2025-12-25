package com.macro.mall.service;

import com.macro.mall.model.SmsNotification;
import com.macro.mall.model.SmsNotificationRead;
import java.util.List;
import java.util.Date;
import java.util.Map;

/**
 * 通知管理Service
 */
public interface SmsNotificationService {
    /**
     * 创建通知
     */
    SmsNotification create(SmsNotification notification);

    /**
     * 更新通知
     */
    SmsNotification update(Long id, SmsNotification notification);

    /**
     * 删除通知
     */
    boolean delete(Long id);

    /**
     * 根据ID获取通知详情
     */
    SmsNotification getById(Long id);

    /**
     * 根据条件分页获取通知列表
     */
    List<SmsNotification> listByFilters(String title, Integer status, Date startTime, Date endTime, int pageNum, int pageSize);
    
    /**
     * 获取通知阅读统计
     */
    Map<String, Object> getReadStats(Long id);
    
    /**
     * 分页获取通知阅读记录
     */
    List<SmsNotificationRead> listReadRecords(Long notificationId, int pageNum, int pageSize);
} 