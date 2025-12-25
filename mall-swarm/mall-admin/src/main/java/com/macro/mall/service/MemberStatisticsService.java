package com.macro.mall.service;

import com.macro.mall.dto.MemberStatisticsQuery;
import com.macro.mall.dto.MemberStatisticsVO;

/**
 * 会员数据统计Service
 * Created by macro on 2025/11/28.
 */
public interface MemberStatisticsService {
    
    /**
     * 获取会员数据统计
     * @param query 查询参数
     * @return 会员数据统计结果
     */
    MemberStatisticsVO getMemberStatistics(MemberStatisticsQuery query);
}
