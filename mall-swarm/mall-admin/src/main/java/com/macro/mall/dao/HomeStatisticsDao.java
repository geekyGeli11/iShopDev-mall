package com.macro.mall.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页统计数据自定义Dao
 * Created by macro on 2023/10/10.
 */
public interface HomeStatisticsDao {
    
    /**
     * 获取今日订单数
     */
    Integer getTodayOrderCount(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    /**
     * 获取指定时间段内的销售总额
     */
    BigDecimal getSaleAmountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    /**
     * 获取指定订单状态的订单数
     */
    Integer getOrderCountByStatus(@Param("status") Integer status);
    
    /**
     * 获取待处理退款申请数
     */
    Integer getPendingRefundCount();
    
    /**
     * 获取待处理退货订单数
     */
    Integer getPendingReturnCount();
    
    /**
     * 获取即将到期广告数
     */
    Integer getAdExpiringSoonCount(@Param("days") Integer days);
    
    /**
     * 获取不同上架状态的商品数
     */
    Integer getProductCountByPublishStatus(@Param("publishStatus") Integer publishStatus);
    
    /**
     * 获取库存紧张商品数
     */
    Integer getLowStockProductCount(@Param("stock") Integer stock);
    
    /**
     * 获取商品总数
     */
    Integer getTotalProductCount();
    
    /**
     * 获取指定日期范围内新增会员数
     */
    Integer getNewMemberCountByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
    
    /**
     * 获取会员总数
     */
    Integer getTotalMemberCount();
    
    /**
     * 获取指定日期范围内的订单统计数据(按天分组)
     */
    List<Map<String, Object>> getOrderStatisticsByDate(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
} 