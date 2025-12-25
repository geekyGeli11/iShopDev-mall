package com.macro.mall.service.impl;

import cn.hutool.core.date.DateUtil;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.dao.HomeStatisticsDao;
import com.macro.mall.dto.HomeStatisticsDTO;
import com.macro.mall.service.HomeStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 首页统计信息Service实现类
 * Created by macro on 2023/10/10.
 */
@Service
public class HomeStatisticsServiceImpl implements HomeStatisticsService {
    @Autowired
    private HomeStatisticsDao homeStatisticsDao;
    @Autowired
    private RedisService redisService;
    
    /**
     * Redis缓存统计数据的key前缀
     */
    private static final String REDIS_KEY_HOME_STATISTICS = "home:statistics";
    
    /**
     * Redis缓存订单统计的key前缀
     */
    private static final String REDIS_KEY_ORDER_STATISTICS = "home:order:statistics";
    
    /**
     * 商品库存预警阈值
     */
    private static final int LOW_STOCK_THRESHOLD = 10;
    
    /**
     * 广告位到期预警天数
     */
    private static final int AD_EXPIRING_DAYS = 7;
    
    /**
     * 缓存过期时间：1小时（秒）
     */
    private static final long CACHE_EXPIRE_TIME = 60 * 60;
    
    @Override
    public HomeStatisticsDTO getHomeStatistics() {
        // 先尝试从缓存获取数据
        HomeStatisticsDTO cacheData = (HomeStatisticsDTO) redisService.get(REDIS_KEY_HOME_STATISTICS);
        if (cacheData != null) {
            return cacheData;
        }
        
        // 缓存不存在，重新计算
        HomeStatisticsDTO result = new HomeStatisticsDTO();
        
        // 获取今日日期范围
        Date todayStart = DateUtil.beginOfDay(new Date());
        Date todayEnd = DateUtil.endOfDay(todayStart);
        
        // 获取昨日日期范围
        Date yesterdayStart = DateUtil.offsetDay(todayStart, -1);
        Date yesterdayEnd = DateUtil.endOfDay(yesterdayStart);
        
        // 获取近7天日期范围
        Date last7DaysStart = DateUtil.offsetDay(todayStart, -6);
        
        // 获取本月日期范围
        Date monthStart = DateUtil.beginOfMonth(new Date());
        
        // 1. 设置今日订单数
        Integer todayOrderCount = homeStatisticsDao.getTodayOrderCount(todayStart, todayEnd);
        result.setTodayOrderCount(todayOrderCount);
        System.out.println("=== 首页统计数据 ===");
        System.out.println("今日订单数: " + todayOrderCount);

        // 2. 设置今日销售总额
        BigDecimal todaySaleAmount = homeStatisticsDao.getSaleAmountByDate(todayStart, todayEnd);
        result.setTodaySaleAmount(todaySaleAmount);
        System.out.println("今日销售总额: " + todaySaleAmount);

        // 3. 设置昨日销售总额
        BigDecimal yesterdaySaleAmount = homeStatisticsDao.getSaleAmountByDate(yesterdayStart, yesterdayEnd);
        result.setYesterdaySaleAmount(yesterdaySaleAmount);
        System.out.println("昨日销售总额: " + yesterdaySaleAmount);

        // 4. 设置近7天销售总额
        BigDecimal last7DaysSaleAmount = homeStatisticsDao.getSaleAmountByDate(last7DaysStart, todayEnd);
        result.setLast7DaysSaleAmount(last7DaysSaleAmount);
        System.out.println("近7天销售总额: " + last7DaysSaleAmount);
        
        // 5. 设置待付款订单数(订单状态: 0->待付款)
        result.setPendingPaymentCount(homeStatisticsDao.getOrderCountByStatus(0));
        
        // 6. 设置已完成订单数(订单状态: 3->已完成)
        result.setCompletedOrderCount(homeStatisticsDao.getOrderCountByStatus(3));
        
        // 7. 设置待确认收货订单数(订单状态: 2->已发货)
        result.setPendingConfirmCount(homeStatisticsDao.getOrderCountByStatus(2));
        
        // 8. 设置待发货订单数(订单状态: 1->待发货)
        result.setPendingDeliveryCount(homeStatisticsDao.getOrderCountByStatus(1));
        
        // 9. 设置已发货订单数(订单状态: 2->已发货)
        result.setDeliveredCount(homeStatisticsDao.getOrderCountByStatus(2));
        
        // 10. 设置新增货源记录 (暂时设为0，具体逻辑根据业务调整)
        result.setNewSourceCount(0);
        
        // 9. 设置待处理退款申请数
        result.setPendingRefundCount(homeStatisticsDao.getPendingRefundCount());
        
        // 10. 设置待处理退货订单数
        result.setPendingReturnCount(homeStatisticsDao.getPendingReturnCount());
        
        // 11. 设置广告位即将到期数
        result.setAdExpiringSoonCount(homeStatisticsDao.getAdExpiringSoonCount(AD_EXPIRING_DAYS));
        
        // 12. 设置商品总览
        HomeStatisticsDTO.ProductOverviewDTO productOverview = new HomeStatisticsDTO.ProductOverviewDTO();
        // 已下架商品数(publishStatus=0)
        productOverview.setOffShelfCount(homeStatisticsDao.getProductCountByPublishStatus(0));
        // 已上架商品数(publishStatus=1)
        productOverview.setOnShelfCount(homeStatisticsDao.getProductCountByPublishStatus(1));
        // 库存紧张商品数
        productOverview.setLowStockCount(homeStatisticsDao.getLowStockProductCount(LOW_STOCK_THRESHOLD));
        //
        productOverview.setTotalCount(homeStatisticsDao.getTotalProductCount());
        result.setProductOverview(productOverview);
        
        // 13. 设置用户总览
        HomeStatisticsDTO.UserOverviewDTO userOverview = new HomeStatisticsDTO.UserOverviewDTO();
        // 今日新增
        userOverview.setTodayAddCount(homeStatisticsDao.getNewMemberCountByDate(todayStart, todayEnd));
        // 昨日新增
        userOverview.setYesterdayAddCount(homeStatisticsDao.getNewMemberCountByDate(yesterdayStart, yesterdayEnd));
        // 本月新增
        userOverview.setMonthAddCount(homeStatisticsDao.getNewMemberCountByDate(monthStart, todayEnd));
        // 会员总数
        userOverview.setTotalCount(homeStatisticsDao.getTotalMemberCount());
        result.setUserOverview(userOverview);
        
        // 将结果缓存到Redis，设置1小时过期(秒为单位)
        redisService.set(REDIS_KEY_HOME_STATISTICS, result, CACHE_EXPIRE_TIME);
        
        return result;
    }
    
    @Override
    public HomeStatisticsDTO getOrderStatistics(Date startDate, Date endDate) {
        // 构建缓存key
        String cacheKey = REDIS_KEY_ORDER_STATISTICS + ":" + DateUtil.format(startDate, "yyyyMMdd") + "-" + DateUtil.format(endDate, "yyyyMMdd");
        
        // 先尝试从缓存获取数据
        HomeStatisticsDTO cacheData = (HomeStatisticsDTO) redisService.get(cacheKey);
        if (cacheData != null) {
            return cacheData;
        }
        
        // 缓存不存在，重新计算
        HomeStatisticsDTO result = new HomeStatisticsDTO();
        
        // 获取订单统计数据
        List<Map<String, Object>> orderStatsList = homeStatisticsDao.getOrderStatisticsByDate(startDate, endDate);
        System.out.println("查询日期范围: " + DateUtil.format(startDate, "yyyy-MM-dd") + " 到 " + DateUtil.format(endDate, "yyyy-MM-dd"));
        System.out.println("订单统计查询结果数量: " + (orderStatsList != null ? orderStatsList.size() : 0));
        
        // 解析订单统计数据
        Map<String, Integer> orderCountMap = new HashMap<>();
        Map<String, BigDecimal> orderAmountMap = new HashMap<>();
        
        for (Map<String, Object> stat : orderStatsList) {
            String date = (String) stat.get("date");
            Integer orderCount = ((Number) stat.get("order_count")).intValue();
            BigDecimal saleAmount = (BigDecimal) stat.get("sale_amount");
            
            orderCountMap.put(date, orderCount);
            orderAmountMap.put(date, saleAmount);
        }
        
        result.setOrderCountStatistics(orderCountMap);
        result.setOrderAmountStatistics(orderAmountMap);
        
        // 同时设置首页基础统计数据
        Date today = new Date();
        Date todayStart = DateUtil.beginOfDay(today);
        Date todayEnd = DateUtil.endOfDay(today);
        Date yesterdayStart = DateUtil.offsetDay(todayStart, -1);
        Date yesterdayEnd = DateUtil.endOfDay(yesterdayStart);
        Date last7DaysStart = DateUtil.offsetDay(todayStart, -6);
        
        // 设置今日、昨日、近7天数据
        Integer todayOrderCountResult = homeStatisticsDao.getTodayOrderCount(todayStart, todayEnd);
        System.out.println("今日订单数查询结果: " + todayOrderCountResult);
        result.setTodayOrderCount(todayOrderCountResult != null ? todayOrderCountResult : 0);
        
        BigDecimal todaySaleAmount = homeStatisticsDao.getSaleAmountByDate(todayStart, todayEnd);
        result.setTodaySaleAmount(todaySaleAmount != null ? todaySaleAmount : BigDecimal.ZERO);
        
        BigDecimal yesterdaySaleAmount = homeStatisticsDao.getSaleAmountByDate(yesterdayStart, yesterdayEnd);
        result.setYesterdaySaleAmount(yesterdaySaleAmount != null ? yesterdaySaleAmount : BigDecimal.ZERO);
        
        BigDecimal last7DaysSaleAmount = homeStatisticsDao.getSaleAmountByDate(last7DaysStart, todayEnd);
        result.setLast7DaysSaleAmount(last7DaysSaleAmount != null ? last7DaysSaleAmount : BigDecimal.ZERO);
        
        // 设置订单状态统计
        Integer pendingPaymentCount = homeStatisticsDao.getOrderCountByStatus(0);
        result.setPendingPaymentCount(pendingPaymentCount != null ? pendingPaymentCount : 0);
        
        Integer completedOrderCount = homeStatisticsDao.getOrderCountByStatus(3);
        result.setCompletedOrderCount(completedOrderCount != null ? completedOrderCount : 0);
        
        Integer pendingConfirmCount = homeStatisticsDao.getOrderCountByStatus(2);
        result.setPendingConfirmCount(pendingConfirmCount != null ? pendingConfirmCount : 0);
        
        Integer pendingDeliveryCount = homeStatisticsDao.getOrderCountByStatus(1);
        result.setPendingDeliveryCount(pendingDeliveryCount != null ? pendingDeliveryCount : 0);
        
        Integer deliveredCount = homeStatisticsDao.getOrderCountByStatus(2);
        result.setDeliveredCount(deliveredCount != null ? deliveredCount : 0);
        
        result.setNewSourceCount(0);
        
        Integer pendingRefundCount = homeStatisticsDao.getPendingRefundCount();
        result.setPendingRefundCount(pendingRefundCount != null ? pendingRefundCount : 0);
        
        Integer pendingReturnCount = homeStatisticsDao.getPendingReturnCount();
        result.setPendingReturnCount(pendingReturnCount != null ? pendingReturnCount : 0);
        
        Integer adExpiringSoonCount = homeStatisticsDao.getAdExpiringSoonCount(AD_EXPIRING_DAYS);
        result.setAdExpiringSoonCount(adExpiringSoonCount != null ? adExpiringSoonCount : 0);
        
        // 设置商品总览
        HomeStatisticsDTO.ProductOverviewDTO productOverview = new HomeStatisticsDTO.ProductOverviewDTO();
        Integer offShelfCount = homeStatisticsDao.getProductCountByPublishStatus(0);
        productOverview.setOffShelfCount(offShelfCount != null ? offShelfCount : 0);
        
        Integer onShelfCount = homeStatisticsDao.getProductCountByPublishStatus(1);
        productOverview.setOnShelfCount(onShelfCount != null ? onShelfCount : 0);
        
        Integer lowStockCount = homeStatisticsDao.getLowStockProductCount(LOW_STOCK_THRESHOLD);
        productOverview.setLowStockCount(lowStockCount != null ? lowStockCount : 0);
        
        Integer totalProductCount = homeStatisticsDao.getTotalProductCount();
        productOverview.setTotalCount(totalProductCount != null ? totalProductCount : 0);
        result.setProductOverview(productOverview);
        
        // 设置用户总览
        HomeStatisticsDTO.UserOverviewDTO userOverview = new HomeStatisticsDTO.UserOverviewDTO();
        Integer todayAddCount = homeStatisticsDao.getNewMemberCountByDate(todayStart, todayEnd);
        userOverview.setTodayAddCount(todayAddCount != null ? todayAddCount : 0);
        
        Integer yesterdayAddCount = homeStatisticsDao.getNewMemberCountByDate(yesterdayStart, yesterdayEnd);
        userOverview.setYesterdayAddCount(yesterdayAddCount != null ? yesterdayAddCount : 0);
        
        Date monthStartForUser = DateUtil.beginOfMonth(today);
        Integer monthAddCount = homeStatisticsDao.getNewMemberCountByDate(monthStartForUser, todayEnd);
        userOverview.setMonthAddCount(monthAddCount != null ? monthAddCount : 0);
        
        Integer totalMemberCount = homeStatisticsDao.getTotalMemberCount();
        userOverview.setTotalCount(totalMemberCount != null ? totalMemberCount : 0);
        result.setUserOverview(userOverview);
        
        // 计算相关的合计数据 - 重新定义月周范围
        // 本月订单总数和销售额
        Date monthStart = DateUtil.beginOfMonth(today);
        Date monthEnd = DateUtil.endOfMonth(today);
        Integer monthOrderCountResult = homeStatisticsDao.getTodayOrderCount(monthStart, monthEnd);
        int monthOrderCount = monthOrderCountResult != null ? monthOrderCountResult : 0;
        BigDecimal monthSaleAmount = homeStatisticsDao.getSaleAmountByDate(monthStart, monthEnd);
        if (monthSaleAmount == null) {
            monthSaleAmount = BigDecimal.ZERO;
        }
        
        // 本周订单总数和销售额
        Date weekStart = DateUtil.beginOfWeek(today);
        Date weekEnd = DateUtil.endOfWeek(today);
        Integer weekOrderCountResult = homeStatisticsDao.getTodayOrderCount(weekStart, weekEnd);
        int weekOrderCount = weekOrderCountResult != null ? weekOrderCountResult : 0;
        BigDecimal weekSaleAmount = homeStatisticsDao.getSaleAmountByDate(weekStart, weekEnd);
        if (weekSaleAmount == null) {
            weekSaleAmount = BigDecimal.ZERO;
        }
        
        // 上月数据（用于计算趋势）
        Date lastMonthStart = DateUtil.beginOfMonth(DateUtil.offsetMonth(today, -1));
        Date lastMonthEnd = DateUtil.endOfMonth(DateUtil.offsetMonth(today, -1));
        Integer lastMonthOrderCountResult = homeStatisticsDao.getTodayOrderCount(lastMonthStart, lastMonthEnd);
        int lastMonthOrderCount = lastMonthOrderCountResult != null ? lastMonthOrderCountResult : 0;
        BigDecimal lastMonthSaleAmount = homeStatisticsDao.getSaleAmountByDate(lastMonthStart, lastMonthEnd);
        if (lastMonthSaleAmount == null) {
            lastMonthSaleAmount = BigDecimal.ZERO;
        }
        
        // 上周数据（用于计算趋势）
        Date lastWeekStart = DateUtil.beginOfWeek(DateUtil.offsetWeek(today, -1));
        Date lastWeekEnd = DateUtil.endOfWeek(DateUtil.offsetWeek(today, -1));
        Integer lastWeekOrderCountResult = homeStatisticsDao.getTodayOrderCount(lastWeekStart, lastWeekEnd);
        int lastWeekOrderCount = lastWeekOrderCountResult != null ? lastWeekOrderCountResult : 0;
        BigDecimal lastWeekSaleAmount = homeStatisticsDao.getSaleAmountByDate(lastWeekStart, lastWeekEnd);
        if (lastWeekSaleAmount == null) {
            lastWeekSaleAmount = BigDecimal.ZERO;
        }
        
        // 设置统计数据
        result.setMonthOrderCount(monthOrderCount);
        result.setWeekOrderCount(weekOrderCount);
        result.setMonthSaleAmount(monthSaleAmount);
        result.setWeekSaleAmount(weekSaleAmount);

        System.out.println("=== 订单统计数据设置 ===");
        System.out.println("本月订单数: " + monthOrderCount + ", 本周订单数: " + weekOrderCount);
        System.out.println("本月销售额: " + monthSaleAmount + ", 本周销售额: " + weekSaleAmount);
        System.out.println("上月订单数: " + lastMonthOrderCount + ", 上周订单数: " + lastWeekOrderCount);
        System.out.println("上月销售额: " + lastMonthSaleAmount + ", 上周销售额: " + lastWeekSaleAmount);
        
        // 计算趋势百分比
        double monthOrderCountTrend = lastMonthOrderCount == 0 ? 0 : 
            ((double)(monthOrderCount - lastMonthOrderCount) / lastMonthOrderCount) * 100;
        double weekOrderCountTrend = lastWeekOrderCount == 0 ? 0 : 
            ((double)(weekOrderCount - lastWeekOrderCount) / lastWeekOrderCount) * 100;
        
        double monthSaleAmountTrend = lastMonthSaleAmount.compareTo(BigDecimal.ZERO) == 0 ? 0 :
            monthSaleAmount.subtract(lastMonthSaleAmount).divide(lastMonthSaleAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue();
        double weekSaleAmountTrend = lastWeekSaleAmount.compareTo(BigDecimal.ZERO) == 0 ? 0 :
            weekSaleAmount.subtract(lastWeekSaleAmount).divide(lastWeekSaleAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue();
        
        result.setMonthOrderCountTrend(monthOrderCountTrend);
        result.setWeekOrderCountTrend(weekOrderCountTrend);
        result.setMonthSaleAmountTrend(monthSaleAmountTrend);
        result.setWeekSaleAmountTrend(weekSaleAmountTrend);

        System.out.println("=== 趋势数据设置 ===");
        System.out.println("本月订单数趋势: " + monthOrderCountTrend + "%");
        System.out.println("本周订单数趋势: " + weekOrderCountTrend + "%");
        System.out.println("本月销售额趋势: " + monthSaleAmountTrend + "%");
        System.out.println("本周销售额趋势: " + weekSaleAmountTrend + "%");
        
        // 将结果缓存到Redis，设置1小时过期(秒为单位)
        redisService.set(cacheKey, result, CACHE_EXPIRE_TIME);
        
        return result;
    }
    
    @Override
    public void refreshStatisticsCache() {
        // 删除首页统计数据缓存
        redisService.del(REDIS_KEY_HOME_STATISTICS);

        // 删除所有订单统计缓存（使用通配符删除所有以该前缀开头的key）
        Set<String> orderStatsCacheKeys = redisService.keys(REDIS_KEY_ORDER_STATISTICS + ":*");
        if (orderStatsCacheKeys != null && !orderStatsCacheKeys.isEmpty()) {
            for (String key : orderStatsCacheKeys) {
                redisService.del(key);
            }
        }

        // 重新加载统计数据到缓存
        getHomeStatistics();

        System.out.println("=== 缓存刷新完成 ===");
        System.out.println("已删除首页统计缓存");
        System.out.println("已删除订单统计缓存数量: " + (orderStatsCacheKeys != null ? orderStatsCacheKeys.size() : 0));
    }
}