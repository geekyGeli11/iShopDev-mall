package com.macro.mall.service;

import com.macro.mall.dto.HomeStatisticsDTO;

import java.util.Date;

/**
 * 首页统计信息Service
 * Created by macro on 2023/10/10.
 */
public interface HomeStatisticsService {
    
    /**
     * 获取首页统计数据
     */
    HomeStatisticsDTO getHomeStatistics();
    
    /**
     * 获取订单统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单统计数据
     */
    HomeStatisticsDTO getOrderStatistics(Date startDate, Date endDate);
    
    /**
     * 刷新统计数据缓存
     */
    void refreshStatisticsCache();
} 