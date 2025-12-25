package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.SmsNotificationMapper;
import com.macro.mall.mapper.SmsNotificationReadMapper;
import com.macro.mall.model.SmsNotification;
import com.macro.mall.model.SmsNotificationExample;
import com.macro.mall.model.SmsNotificationRead;
import com.macro.mall.model.SmsNotificationReadExample;
import com.macro.mall.service.SmsNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知管理Service实现类
 */
@Service
public class SmsNotificationServiceImpl implements SmsNotificationService {

    @Autowired
    private SmsNotificationMapper notificationMapper;
    
    @Autowired
    private SmsNotificationReadMapper notificationReadMapper;

    @Override
    public SmsNotification create(SmsNotification notification) {
        notification.setCreateTime(new Date());
        notification.setReadCount(0);
        notificationMapper.insert(notification);
        return notification;
    }

    @Override
    public SmsNotification update(Long id, SmsNotification notification) {
        SmsNotification existing = notificationMapper.selectByPrimaryKey(id);
        if (existing == null) {
            return null;
        }
        notification.setId(id);
        notificationMapper.updateByPrimaryKeyWithBLOBs(notification);
        return notification;
    }

    @Override
    public boolean delete(Long id) {
        int count = notificationMapper.deleteByPrimaryKey(id);
        return count > 0;
    }

    @Override
    public SmsNotification getById(Long id) {
        return notificationMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<SmsNotification> listByFilters(String title, Integer status, Date startTime, Date endTime, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);

        // 构建查询条件
        SmsNotificationExample example = new SmsNotificationExample();
        SmsNotificationExample.Criteria criteria = example.createCriteria();

        if (StringUtils.hasText(title)) {
            criteria.andTitleLike("%" + title + "%");
        }
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        if (startTime != null) {
            criteria.andCreateTimeGreaterThanOrEqualTo(startTime);
        }
        if (endTime != null) {
            criteria.andCreateTimeLessThanOrEqualTo(endTime);
        }

        // 排序条件
        example.setOrderByClause("create_time DESC");

        return notificationMapper.selectByExampleWithBLOBs(example);
    }
    
    @Override
    public Map<String, Object> getReadStats(Long id) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取通知信息
        SmsNotification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            return result;
        }
        
        // 获取总阅读数
        result.put("readCount", notification.getReadCount());
        
        // 获取阅读记录数量
        SmsNotificationReadExample example = new SmsNotificationReadExample();
        example.createCriteria().andNotificationIdEqualTo(id);
        long readRecordCount = notificationReadMapper.countByExample(example);
        result.put("readRecordCount", readRecordCount);
        
        return result;
    }
    
    @Override
    public List<SmsNotificationRead> listReadRecords(Long notificationId, int pageNum, int pageSize) {
        // 设置分页信息
        PageHelper.startPage(pageNum, pageSize);
        
        // 构建查询条件
        SmsNotificationReadExample example = new SmsNotificationReadExample();
        example.createCriteria().andNotificationIdEqualTo(notificationId);
        
        // 按阅读时间倒序排序
        example.setOrderByClause("read_time DESC");
        
        return notificationReadMapper.selectByExample(example);
    }
} 