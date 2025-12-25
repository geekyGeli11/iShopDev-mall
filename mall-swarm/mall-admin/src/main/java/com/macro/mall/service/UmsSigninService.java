package com.macro.mall.service;

import com.macro.mall.dto.UmsSigninConfigParam;
import com.macro.mall.dto.UmsSigninLogQueryParam;
import com.macro.mall.dto.UmsSigninStatisticsResult;
import com.macro.mall.model.UmsSigninRewardConfig;
import com.macro.mall.model.UmsMemberSigninLog;

import java.util.List;

/**
 * 签到管理Service
 * Created by guanghengzhou on 2024/12/23.
 */
public interface UmsSigninService {
    
    /**
     * 获取签到配置
     */
    UmsSigninRewardConfig getConfig();
    
    /**
     * 更新签到配置
     */
    int updateConfig(UmsSigninConfigParam configParam);
    
    /**
     * 获取签到记录列表
     */
    List<UmsMemberSigninLog> getLogs(UmsSigninLogQueryParam queryParam);
    
    /**
     * 获取签到统计数据
     */
    UmsSigninStatisticsResult getStatistics(String startDate, String endDate);
    
    /**
     * 导出签到记录
     */
    String exportLogs(UmsSigninLogQueryParam queryParam);
} 