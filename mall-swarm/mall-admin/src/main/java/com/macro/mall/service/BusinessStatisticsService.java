package com.macro.mall.service;

import com.macro.mall.dto.BusinessStatisticsQuery;
import com.macro.mall.dto.BusinessStatisticsVO;

/**
 * 营业数据统计Service
 * Created by macro on 2025/11/28.
 */
public interface BusinessStatisticsService {
    
    /**
     * 获取营业数据统计
     * @param query 查询参数
     * @return 营业数据统计结果
     */
    BusinessStatisticsVO getBusinessStatistics(BusinessStatisticsQuery query);
}
