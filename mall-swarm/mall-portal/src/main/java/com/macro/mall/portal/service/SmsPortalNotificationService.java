package com.macro.mall.portal.service;

import com.macro.mall.model.SmsNotification;
import com.macro.mall.model.SmsNotificationRead;
import org.springframework.data.domain.Page;

/**
 * 会员通知管理Service
 */
public interface SmsPortalNotificationService {
    
    /**
     * 分页获取通知列表
     */
    Page<SmsNotification> list(Integer pageNum, Integer pageSize);
    
    /**
     * 获取最新一条未读通知
     */
    SmsNotification getLatestUnread();
    
    /**
     * 创建通知阅读记录
     */
    int createReadRecord(Long notificationId);
    
    /**
     * 获取通知详情
     */
    SmsNotification getNotification(Long id);
    
    /**
     * 检查通知是否已读
     */
    boolean isRead(Long notificationId);
} 