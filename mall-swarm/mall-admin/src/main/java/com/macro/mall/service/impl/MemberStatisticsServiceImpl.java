package com.macro.mall.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.dao.MemberStatisticsDao;
import com.macro.mall.dto.*;
import com.macro.mall.service.MemberStatisticsService;
import com.macro.mall.service.WechatVisitDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 会员数据统计Service实现类
 * Created by macro on 2025/11/28.
 */
@Service
public class MemberStatisticsServiceImpl implements MemberStatisticsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberStatisticsServiceImpl.class);
    
    @Autowired
    private MemberStatisticsDao memberStatisticsDao;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private WechatVisitDataService wechatVisitDataService;
    
    /**
     * Redis缓存key前缀
     */
    private static final String REDIS_KEY_PREFIX = "dashboard:member:";
    
    /**
     * 缓存过期时间：5分钟（秒）
     */
    private static final long CACHE_EXPIRE_TIME = 5 * 60;
    
    @Override
    public MemberStatisticsVO getMemberStatistics(MemberStatisticsQuery query) {
        // 构建缓存key
        String cacheKey = buildCacheKey(query);
        
        // 尝试从缓存获取
        MemberStatisticsVO cachedData = (MemberStatisticsVO) redisService.get(cacheKey);
        if (cachedData != null) {
            LOGGER.info("从缓存获取会员数据统计: {}", cacheKey);
            return cachedData;
        }
        
        LOGGER.info("查询会员数据统计: startDate={}, endDate={}, schoolId={}", 
                    query.getStartDate(), query.getEndDate(), query.getSchoolId());
        
        MemberStatisticsVO result = new MemberStatisticsVO();
        
        // 1. 获取会员统计数据
        Map<String, Object> statistics = memberStatisticsDao.getMemberStatistics(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId()
        );
        
        Integer newMemberCount = 0;
        Integer totalActiveMembers = 0;
        
        if (statistics != null) {
            Object newMemberObj = statistics.get("newMemberCount");
            if (newMemberObj != null) {
                if (newMemberObj instanceof Long) {
                    newMemberCount = ((Long) newMemberObj).intValue();
                } else if (newMemberObj instanceof Integer) {
                    newMemberCount = (Integer) newMemberObj;
                }
            }
            
            Object activeMembersObj = statistics.get("totalActiveMembers");
            if (activeMembersObj != null) {
                if (activeMembersObj instanceof Long) {
                    totalActiveMembers = ((Long) activeMembersObj).intValue();
                } else if (activeMembersObj instanceof Integer) {
                    totalActiveMembers = (Integer) activeMembersObj;
                }
            }
        }
        
        result.setNewMemberCount(newMemberCount);
        result.setTotalActiveMembers(totalActiveMembers);
        
        // 2. 计算会员增长率
        // 计算上一周期的日期范围
        long daysBetween = ChronoUnit.DAYS.between(query.getStartDate(), query.getEndDate()) + 1;
        LocalDate previousStartDate = query.getStartDate().minusDays(daysBetween);
        LocalDate previousEndDate = query.getStartDate().minusDays(1);
        
        Integer previousNewMembers = memberStatisticsDao.getPreviousPeriodNewMembers(
            previousStartDate, 
            previousEndDate, 
            query.getSchoolId()
        );
        
        if (previousNewMembers == null) {
            previousNewMembers = 0;
        }
        
        BigDecimal growthRate = BigDecimal.ZERO;
        if (previousNewMembers > 0) {
            growthRate = new BigDecimal(newMemberCount - previousNewMembers)
                .divide(new BigDecimal(previousNewMembers), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
        } else if (newMemberCount > 0) {
            growthRate = new BigDecimal(100);
        }
        result.setGrowthRate(growthRate);
        
        // 3. 获取会员消费排行榜
        List<Map<String, Object>> topSpendersData = memberStatisticsDao.getTopSpenders(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getTopLimit()
        );
        
        List<TopSpenderVO> topSpenders = new ArrayList<>();
        for (Map<String, Object> data : topSpendersData) {
            TopSpenderVO vo = new TopSpenderVO();
            
            Object memberIdObj = data.get("memberId");
            if (memberIdObj != null) {
                vo.setMemberId(memberIdObj instanceof Long ? (Long) memberIdObj : ((Integer) memberIdObj).longValue());
            }
            
            vo.setMemberName((String) data.get("memberName"));
            vo.setMemberCode((String) data.get("memberCode"));
            
            BigDecimal totalSpending = (BigDecimal) data.get("totalSpending");
            vo.setTotalSpending(totalSpending != null ? totalSpending : BigDecimal.ZERO);
            
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
            
            topSpenders.add(vo);
        }
        result.setTopSpenders(topSpenders);
        
        // 4. 获取会员增长趋势数据
        List<Map<String, Object>> trendData = memberStatisticsDao.getMemberTrendData(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId()
        );
        
        List<MemberTrendDataVO> memberTrendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        if (trendData != null && !trendData.isEmpty()) {
            LOGGER.info("获取会员增长趋势数据: 共 {} 条记录", trendData.size());
            
            for (Map<String, Object> data : trendData) {
                MemberTrendDataVO vo = new MemberTrendDataVO();
                
                // 处理日期 - 支持多种类型
                Object dateObj = data.get("date");
                String dateStr = null;
                
                if (dateObj != null) {
                    if (dateObj instanceof LocalDate) {
                        dateStr = ((LocalDate) dateObj).format(formatter);
                    } else if (dateObj instanceof java.sql.Date) {
                        dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.sql.Date) dateObj);
                    } else if (dateObj instanceof java.util.Date) {
                        dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) dateObj);
                    } else if (dateObj instanceof String) {
                        dateStr = (String) dateObj;
                    } else {
                        dateStr = dateObj.toString();
                    }
                }
                
                if (dateStr != null) {
                    vo.setDate(dateStr);
                    LOGGER.debug("会员趋势数据 - 日期: {}, 新增: {}, 活跃: {}", 
                        dateStr, data.get("newMembers"), data.get("activeMembers"));
                } else {
                    LOGGER.warn("会员趋势数据日期为空，跳过此条记录");
                    continue;
                }
                
                Object newMembersObj = data.get("newMembers");
                Integer newMembers = 0;
                if (newMembersObj != null) {
                    if (newMembersObj instanceof Long) {
                        newMembers = ((Long) newMembersObj).intValue();
                    } else if (newMembersObj instanceof Integer) {
                        newMembers = (Integer) newMembersObj;
                    } else if (newMembersObj instanceof Number) {
                        newMembers = ((Number) newMembersObj).intValue();
                    }
                }
                vo.setNewMembers(newMembers);
                
                Object activeMembersObj = data.get("activeMembers");
                Integer activeMembers = 0;
                if (activeMembersObj != null) {
                    if (activeMembersObj instanceof Long) {
                        activeMembers = ((Long) activeMembersObj).intValue();
                    } else if (activeMembersObj instanceof Integer) {
                        activeMembers = (Integer) activeMembersObj;
                    } else if (activeMembersObj instanceof Number) {
                        activeMembers = ((Number) activeMembersObj).intValue();
                    }
                }
                vo.setActiveMembers(activeMembers);
                
                memberTrendData.add(vo);
            }
        } else {
            LOGGER.info("会员增长趋势数据为空");
        }
        result.setMemberTrendData(memberTrendData);
        
        // 5. 获取小程序访问数据
        try {
            WechatVisitDataVO wechatVisitData = wechatVisitDataService.getWechatVisitData(
                query.getStartDate(), 
                query.getEndDate()
            );
            result.setWechatVisitData(wechatVisitData);
        } catch (Exception e) {
            LOGGER.warn("获取小程序访问数据失败，继续返回其他数据", e);
        }
        
        // 缓存结果
        redisService.set(cacheKey, result, CACHE_EXPIRE_TIME);
        LOGGER.info("会员数据统计已缓存: {}", cacheKey);
        
        return result;
    }
    
    /**
     * 构建缓存key
     */
    private String buildCacheKey(MemberStatisticsQuery query) {
        StringBuilder sb = new StringBuilder(REDIS_KEY_PREFIX);
        sb.append(query.getStartDate()).append(":");
        sb.append(query.getEndDate()).append(":");
        sb.append(query.getSchoolId() != null ? query.getSchoolId() : "all").append(":");
        sb.append(query.getTopLimit());
        return sb.toString();
    }
}
