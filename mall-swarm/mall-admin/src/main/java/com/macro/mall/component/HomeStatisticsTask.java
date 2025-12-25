package com.macro.mall.component;

import com.macro.mall.service.HomeStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 首页统计数据定时任务
 * Created by macro on 2023/10/10.
 */
@Component
public class HomeStatisticsTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeStatisticsTask.class);
    @Autowired
    private HomeStatisticsService homeStatisticsService;

    /**
     * 每小时刷新一次统计数据
     * cron表达式：秒 分 时 日 月 星期
     */
    @Scheduled(cron = "0 0 * * * ?")
    private void refreshStatisticsTask() {
        LOGGER.info("开始执行刷新首页统计数据任务");
        homeStatisticsService.refreshStatisticsCache();
        LOGGER.info("刷新首页统计数据任务完成");
    }
} 