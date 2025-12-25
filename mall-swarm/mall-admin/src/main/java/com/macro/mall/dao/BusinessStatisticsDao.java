package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 营业数据统计Dao
 * Created by macro on 2025/11/28.
 */
public interface BusinessStatisticsDao {
    
    /**
     * 获取营业数据统计（聚合所有渠道）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param storeId 门店ID（可选）
     * @return 统计数据Map，包含totalRevenue、orderCount
     */
    Map<String, Object> getBusinessStatistics(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("schoolId") Long schoolId,
                                               @Param("storeId") Long storeId);
    
    /**
     * 获取各销售渠道的数据细分
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param storeId 门店ID（可选）
     * @return 渠道细分数据列表，每项包含channel、revenue、orderCount
     */
    List<Map<String, Object>> getChannelBreakdown(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("schoolId") Long schoolId,
                                                   @Param("storeId") Long storeId);
    
    /**
     * 获取趋势数据（按日期分组）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param storeId 门店ID（可选）
     * @return 趋势数据列表，每项包含date、revenue、orderCount
     */
    List<Map<String, Object>> getTrendData(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("schoolId") Long schoolId,
                                            @Param("storeId") Long storeId);
    
    /**
     * 获取学校营业数据统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 学校统计数据列表，每项包含schoolId、schoolName、totalAmount、orderCount
     */
    List<Map<String, Object>> getSchoolStatistics(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 获取门店营业数据统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 门店统计数据列表，每项包含storeId、storeName、totalAmount、orderCount
     */
    List<Map<String, Object>> getStoreStatistics(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
}
