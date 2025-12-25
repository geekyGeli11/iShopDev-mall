package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 商品销售数据统计Dao
 * Created by macro on 2025/11/28.
 */
public interface ProductStatisticsDao {
    
    /**
     * 获取商品销售排行榜（按金额）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param topLimit 排行榜数量限制
     * @return 商品销售排行列表
     */
    List<Map<String, Object>> getTopSellingByAmount(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("schoolId") Long schoolId,
                                                     @Param("topLimit") Integer topLimit);
    
    /**
     * 获取商品销售排行榜（按数量）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param topLimit 排行榜数量限制
     * @return 商品销售排行列表
     */
    List<Map<String, Object>> getTopSellingByQuantity(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("schoolId") Long schoolId,
                                                       @Param("topLimit") Integer topLimit);
    
    /**
     * 获取盈利汇总数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @return 盈利汇总数据
     */
    Map<String, Object> getProfitSummary(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          @Param("schoolId") Long schoolId);
    
    /**
     * 获取利润最高的商品
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @param topLimit 排行榜数量限制
     * @return 利润最高的商品列表
     */
    List<Map<String, Object>> getTopProfitProducts(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("schoolId") Long schoolId,
                                                    @Param("topLimit") Integer topLimit);
    
    /**
     * 获取品类销售分布
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @return 品类销售分布列表
     */
    List<Map<String, Object>> getCategoryDistribution(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate,
                                                       @Param("schoolId") Long schoolId);
    
    /**
     * 获取商品总览数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param schoolId 学校ID（可选）
     * @return 商品总览数据（总商品数、销售总额、销售总数量、热销商品数）
     */
    Map<String, Object> getProductOverview(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("schoolId") Long schoolId);
}
