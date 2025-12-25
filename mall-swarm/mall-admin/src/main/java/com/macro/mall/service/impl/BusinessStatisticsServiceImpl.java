package com.macro.mall.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.dao.BusinessStatisticsDao;
import com.macro.mall.dto.*;
import com.macro.mall.service.BusinessStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 营业数据统计Service实现类
 * Created by macro on 2025/11/28.
 */
@Service
public class BusinessStatisticsServiceImpl implements BusinessStatisticsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessStatisticsServiceImpl.class);
    
    @Autowired
    private BusinessStatisticsDao businessStatisticsDao;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * Redis缓存key前缀
     */
    private static final String REDIS_KEY_PREFIX = "dashboard:business:";
    
    /**
     * 缓存过期时间：5分钟（秒）
     */
    private static final long CACHE_EXPIRE_TIME = 5 * 60;
    
    @Override
    public BusinessStatisticsVO getBusinessStatistics(BusinessStatisticsQuery query) {
        // 构建缓存key
        String cacheKey = buildCacheKey(query);
        
        // 尝试从缓存获取
        BusinessStatisticsVO cachedData = (BusinessStatisticsVO) redisService.get(cacheKey);
        if (cachedData != null) {
            LOGGER.info("从缓存获取营业数据统计: {}", cacheKey);
            return cachedData;
        }
        
        LOGGER.info("查询营业数据统计: startDate={}, endDate={}, schoolId={}, storeId={}", 
                    query.getStartDate(), query.getEndDate(), query.getSchoolId(), query.getStoreId());
        
        BusinessStatisticsVO result = new BusinessStatisticsVO();
        
        // 1. 获取总体统计数据
        Map<String, Object> statistics = businessStatisticsDao.getBusinessStatistics(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getStoreId()
        );
        
        if (statistics != null) {
            // 总收入
            BigDecimal totalRevenue = (BigDecimal) statistics.get("totalRevenue");
            result.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            
            // 订单数量
            Object orderCountObj = statistics.get("orderCount");
            Integer orderCount = 0;
            if (orderCountObj != null) {
                if (orderCountObj instanceof Long) {
                    orderCount = ((Long) orderCountObj).intValue();
                } else if (orderCountObj instanceof Integer) {
                    orderCount = (Integer) orderCountObj;
                }
            }
            result.setOrderCount(orderCount);
            
            // 平均订单金额
            BigDecimal avgOrderValue = BigDecimal.ZERO;
            if (orderCount > 0 && totalRevenue != null) {
                avgOrderValue = totalRevenue.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            }
            result.setAvgOrderValue(avgOrderValue);
        } else {
            result.setTotalRevenue(BigDecimal.ZERO);
            result.setOrderCount(0);
            result.setAvgOrderValue(BigDecimal.ZERO);
        }
        
        // 2. 获取销售渠道细分数据
        List<Map<String, Object>> channelData = businessStatisticsDao.getChannelBreakdown(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getStoreId()
        );
        
        List<ChannelBreakdownVO> channelBreakdown = new ArrayList<>();
        BigDecimal totalRevenue = result.getTotalRevenue();
        
        for (Map<String, Object> data : channelData) {
            ChannelBreakdownVO vo = new ChannelBreakdownVO();
            vo.setChannel((String) data.get("channel"));
            
            BigDecimal revenue = (BigDecimal) data.get("revenue");
            vo.setRevenue(revenue != null ? revenue : BigDecimal.ZERO);
            
            Object orderCountObj = data.get("orderCount");
            Integer orderCount = 0;
            if (orderCountObj != null) {
                if (orderCountObj instanceof Long) {
                    orderCount = ((Long) orderCountObj).intValue();
                } else if (orderCountObj instanceof Integer) {
                    orderCount = (Integer) orderCountObj;
                }
            }
            vo.setOrderCount(orderCount);
            
            // 计算占比
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0 && revenue != null) {
                percentage = revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                   .multiply(new BigDecimal(100))
                                   .setScale(2, RoundingMode.HALF_UP);
            }
            vo.setPercentage(percentage);
            
            channelBreakdown.add(vo);
        }
        result.setChannelBreakdown(channelBreakdown);
        
        // 3. 获取趋势数据
        List<Map<String, Object>> trendData = businessStatisticsDao.getTrendData(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getStoreId()
        );
        
        List<TrendDataVO> trendDataList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Map<String, Object> data : trendData) {
            TrendDataVO vo = new TrendDataVO();
            
            // 处理日期
            Object dateObj = data.get("date");
            if (dateObj instanceof LocalDate) {
                vo.setDate(((LocalDate) dateObj).format(formatter));
            } else if (dateObj instanceof String) {
                vo.setDate((String) dateObj);
            }
            
            BigDecimal revenue = (BigDecimal) data.get("revenue");
            vo.setRevenue(revenue != null ? revenue : BigDecimal.ZERO);
            
            Object orderCountObj = data.get("orderCount");
            Integer orderCount = 0;
            if (orderCountObj != null) {
                if (orderCountObj instanceof Long) {
                    orderCount = ((Long) orderCountObj).intValue();
                } else if (orderCountObj instanceof Integer) {
                    orderCount = (Integer) orderCountObj;
                }
            }
            vo.setOrderCount(orderCount);
            
            trendDataList.add(vo);
        }
        result.setTrendData(trendDataList);
        
        // 4. 获取学校营业数据统计
        List<Map<String, Object>> schoolData = businessStatisticsDao.getSchoolStatistics(
            query.getStartDate(), 
            query.getEndDate()
        );
        
        List<SchoolStatisticsVO> schoolStatistics = new ArrayList<>();
        for (Map<String, Object> data : schoolData) {
            SchoolStatisticsVO vo = new SchoolStatisticsVO();
            
            Object schoolIdObj = data.get("schoolId");
            if (schoolIdObj != null) {
                vo.setSchoolId(schoolIdObj instanceof Long ? (Long) schoolIdObj : ((Integer) schoolIdObj).longValue());
            }
            
            vo.setSchoolName((String) data.get("schoolName"));
            
            BigDecimal totalAmount = (BigDecimal) data.get("totalAmount");
            vo.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
            
            Object orderCountObj = data.get("orderCount");
            Integer orderCount = 0;
            if (orderCountObj != null) {
                if (orderCountObj instanceof Long) {
                    orderCount = ((Long) orderCountObj).intValue();
                } else if (orderCountObj instanceof Integer) {
                    orderCount = (Integer) orderCountObj;
                }
            }
            vo.setOrderCount(orderCount);
            
            // 计算平均订单金额
            BigDecimal avgOrderAmount = BigDecimal.ZERO;
            if (orderCount > 0 && totalAmount != null) {
                avgOrderAmount = totalAmount.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            }
            vo.setAvgOrderAmount(avgOrderAmount);
            
            schoolStatistics.add(vo);
        }
        result.setSchoolStatistics(schoolStatistics);
        
        // 5. 获取门店营业数据统计
        List<Map<String, Object>> storeData = businessStatisticsDao.getStoreStatistics(
            query.getStartDate(), 
            query.getEndDate()
        );
        
        List<StoreStatisticsVO> storeStatistics = new ArrayList<>();
        for (Map<String, Object> data : storeData) {
            StoreStatisticsVO vo = new StoreStatisticsVO();
            
            Object storeIdObj = data.get("storeId");
            if (storeIdObj != null) {
                vo.setStoreId(storeIdObj instanceof Long ? (Long) storeIdObj : ((Integer) storeIdObj).longValue());
            }
            
            vo.setStoreName((String) data.get("storeName"));
            
            BigDecimal totalAmount = (BigDecimal) data.get("totalAmount");
            vo.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
            
            Object orderCountObj = data.get("orderCount");
            Integer orderCount = 0;
            if (orderCountObj != null) {
                if (orderCountObj instanceof Long) {
                    orderCount = ((Long) orderCountObj).intValue();
                } else if (orderCountObj instanceof Integer) {
                    orderCount = (Integer) orderCountObj;
                }
            }
            vo.setOrderCount(orderCount);
            
            // 计算平均订单金额
            BigDecimal avgOrderAmount = BigDecimal.ZERO;
            if (orderCount > 0 && totalAmount != null) {
                avgOrderAmount = totalAmount.divide(new BigDecimal(orderCount), 2, RoundingMode.HALF_UP);
            }
            vo.setAvgOrderAmount(avgOrderAmount);
            
            storeStatistics.add(vo);
        }
        result.setStoreStatistics(storeStatistics);
        
        // 缓存结果
        redisService.set(cacheKey, result, CACHE_EXPIRE_TIME);
        LOGGER.info("营业数据统计已缓存: {}", cacheKey);
        
        return result;
    }
    
    /**
     * 构建缓存key
     */
    private String buildCacheKey(BusinessStatisticsQuery query) {
        StringBuilder sb = new StringBuilder(REDIS_KEY_PREFIX);
        sb.append(query.getStartDate()).append(":");
        sb.append(query.getEndDate()).append(":");
        sb.append(query.getSchoolId() != null ? query.getSchoolId() : "all").append(":");
        sb.append(query.getStoreId() != null ? query.getStoreId() : "all");
        return sb.toString();
    }
}
