package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.SmsNotificationMapper;
import com.macro.mall.mapper.SmsNotificationReadMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.service.SmsPortalNotificationService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会员通知管理Service实现类
 */
@Service
public class SmsPortalNotificationServiceImpl implements SmsPortalNotificationService {
    
    @Autowired
    private SmsNotificationMapper notificationMapper;
    
    @Autowired
    private SmsNotificationReadMapper notificationReadMapper;
    
    @Autowired
    private UmsMemberService memberService;
    
    @Override
    public Page<SmsNotification> list(Integer pageNum, Integer pageSize) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);
        
        // 查询条件 - 只查询启用状态的通知
        SmsNotificationExample example = new SmsNotificationExample();
        example.createCriteria().andStatusEqualTo(1);  // 1表示启用状态
        example.setOrderByClause("create_time DESC");
        
        // 获取通知列表
        List<SmsNotification> notificationList = notificationMapper.selectByExampleWithBLOBs(example);
        
        // 转换为Spring Data分页对象
        return new PageImpl<>(notificationList, PageRequest.of(pageNum - 1, pageSize), notificationList.size());
    }
    
    @Override
    public SmsNotification getLatestUnread() {
        UmsMember currentMember = memberService.getCurrentMember();
        
        // 查询所有启用状态的通知
        SmsNotificationExample example = new SmsNotificationExample();
        example.createCriteria().andStatusEqualTo(1);  // 1表示启用状态
        example.setOrderByClause("create_time DESC");
        
        List<SmsNotification> notificationList = notificationMapper.selectByExampleWithBLOBs(example);
        
        // 遍历通知，查找第一条未读的
        for (SmsNotification notification : notificationList) {
            if (!isRead(notification.getId())) {
                return notification;
            }
        }
        
        return null;  // 没有未读通知
    }
    
    @Override
    public int createReadRecord(Long notificationId) {
        UmsMember currentMember = memberService.getCurrentMember();
        Long memberId = currentMember.getId();
        
        // 检查是否已经存在阅读记录
        if (isRead(notificationId)) {
            return 0;  // 已存在阅读记录
        }
        
        // 创建阅读记录
        SmsNotificationRead record = new SmsNotificationRead();
        record.setNotificationId(notificationId);
        record.setUserId(memberId);
        record.setReadTime(new Date());
        
        int count = notificationReadMapper.insert(record);
        
        // 更新通知的阅读数
        if (count > 0) {
            SmsNotification notification = notificationMapper.selectByPrimaryKey(notificationId);
            if (notification != null) {
                notification.setReadCount(notification.getReadCount() + 1);
                notificationMapper.updateByPrimaryKey(notification);
            }
        }
        
        return count;
    }
    
    @Override
    public SmsNotification getNotification(Long id) {
        return notificationMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public boolean isRead(Long notificationId) {
        UmsMember currentMember = memberService.getCurrentMember();
        Long memberId = currentMember.getId();
        
        // 查询是否存在阅读记录
        SmsNotificationReadExample example = new SmsNotificationReadExample();
        example.createCriteria()
                .andNotificationIdEqualTo(notificationId)
                .andUserIdEqualTo(memberId);
        
        long count = notificationReadMapper.countByExample(example);
        return count > 0;
    }
} 