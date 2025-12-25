package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 会员数据统计Dao
 * Created by macro on 2025/11/28.
 */
public interface MemberStatisticsDao {
    
    /**
     * 获取会员统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @return 统计数据Map，包含newMemberCount、totalActiveMembers
     */
    Map<String, Object> getMemberStatistics(@Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate,
                                             @Param("schoolId") Long schoolId);
    
    /**
     * 获取会员消费排行榜
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param topLimit 排行榜数量限制
     * @return 会员消费排行列表
     */
    List<Map<String, Object>> getTopSpenders(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              @Param("schoolId") Long schoolId,
                                              @Param("topLimit") Integer topLimit);
    
    /**
     * 获取会员增长趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @return 会员增长趋势列表
     */
    List<Map<String, Object>> getMemberTrendData(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("schoolId") Long schoolId);
    
    /**
     * 获取上一周期的新增会员数（用于计算增长率）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @return 新增会员数
     */
    Integer getPreviousPeriodNewMembers(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("schoolId") Long schoolId);
}
