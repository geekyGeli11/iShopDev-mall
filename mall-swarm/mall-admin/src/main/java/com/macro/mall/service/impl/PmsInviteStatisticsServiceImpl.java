package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.PmsInviteStatisticsDao;
import com.macro.mall.dto.InviteStatisticsDTO;
import com.macro.mall.mapper.PmsInviteRelationMapper;
import com.macro.mall.mapper.PmsInviteRewardMapper;
import com.macro.mall.mapper.PmsInviteWithdrawApplyMapper;
import com.macro.mall.model.PmsInviteRelationExample;
import com.macro.mall.model.PmsInviteRewardExample;
import com.macro.mall.model.PmsInviteWithdrawApplyExample;
import com.macro.mall.service.PmsInviteStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PmsInviteStatisticsServiceImpl implements PmsInviteStatisticsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsInviteStatisticsServiceImpl.class);
    
    private static final String CACHE_KEY_PREFIX = "invite:statistics:";
    private static final String CACHE_KEY_ALL = CACHE_KEY_PREFIX + "all";
    private static final int CACHE_EXPIRE_MINUTES = 30; // 缓存30分钟
    
    @Autowired
    private PmsInviteRelationMapper inviteRelationMapper;
    
    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;
    
    @Autowired
    private PmsInviteWithdrawApplyMapper withdrawApplyMapper;
    
    @Autowired
    private PmsInviteStatisticsDao statisticsDao;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public InviteStatisticsDTO getAllStatistics(String startDate, String endDate, String trendType, Integer pageSize, Integer pageNum) {
        String cacheKey = CACHE_KEY_ALL + ":" + (startDate != null ? startDate : "") + ":" + 
                         (endDate != null ? endDate : "") + ":" + (trendType != null ? trendType : "invite") + 
                         ":" + pageSize + ":" + pageNum;
        
        try {
            // 尝试从缓存获取
            InviteStatisticsDTO cachedResult = (InviteStatisticsDTO) redisTemplate.opsForValue().get(cacheKey);
            if (cachedResult != null) {
                LOGGER.info("从缓存获取邀请统计数据成功");
                return cachedResult;
            }
            
            // 缓存未命中，从数据库获取
            LOGGER.info("缓存未命中，从数据库获取邀请统计数据");
            InviteStatisticsDTO result = buildAllStatistics(startDate, endDate, trendType, pageSize, pageNum);
            
            // 存入缓存
            redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            LOGGER.info("邀请统计数据已存入缓存，过期时间：{}分钟", CACHE_EXPIRE_MINUTES);
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("获取邀请统计数据失败", e);
            // 缓存异常时直接查询数据库
            return buildAllStatistics(startDate, endDate, trendType, pageSize, pageNum);
        }
    }
    
    private InviteStatisticsDTO buildAllStatistics(String startDate, String endDate, String trendType, Integer pageSize, Integer pageNum) {
        InviteStatisticsDTO result = new InviteStatisticsDTO();
        
        try {
            // 1. 概览数据
            InviteStatisticsDTO.OverviewData overview = new InviteStatisticsDTO.OverviewData();
            Map<String, Object> overviewData = getOverviewData();
            overview.setTotalInvites(getLongValue(overviewData.get("totalInvites")));
            overview.setRegisteredUsers(getLongValue(overviewData.get("registeredCount")));
            overview.setFirstOrderUsers(getLongValue(overviewData.get("firstOrderCount")));
            overview.setTotalRewards(getLongValue(overviewData.get("totalRewards")));
            result.setOverviewData(overview);
            
            // 2. 奖励统计
            InviteStatisticsDTO.RewardStats rewardStats = new InviteStatisticsDTO.RewardStats();
            Map<String, Object> rewardData = getRewardStatistics();
            rewardStats.setTotalRewards(getLongValue(rewardData.get("totalRewards")));
            rewardStats.setSuccessRewards(getLongValue(rewardData.get("sentRewards")));
            rewardStats.setFailRewards(getLongValue(rewardData.get("failedRewards")));
            rewardStats.setPendingRewards(getLongValue(rewardData.get("pendingRewards")));
            rewardStats.setRewardTypeCount((Map<String, Object>) rewardData.get("typeStatistics"));
            result.setRewardStats(rewardStats);
            
            // 3. 提现统计
            InviteStatisticsDTO.WithdrawStats withdrawStats = new InviteStatisticsDTO.WithdrawStats();
            Map<String, Object> withdrawData = getWithdrawStatistics();
            withdrawStats.setTotalApplies(getLongValue(withdrawData.get("totalApplications")));
            withdrawStats.setPendingApplies(getLongValue(withdrawData.get("pendingCount")));
            withdrawStats.setApprovedApplies(getLongValue(withdrawData.get("status2")));
            withdrawStats.setSuccessRate(getDoubleValue(withdrawData.get("successRate")));
            result.setWithdrawStats(withdrawStats);
            
            // 4. 转化分析
            InviteStatisticsDTO.ConversionData conversionData = new InviteStatisticsDTO.ConversionData();
            Map<String, Object> conversionAnalysis = getConversionAnalysis();
            Double registerRate = getDoubleValue(conversionAnalysis.get("registerRate"));
            Double orderRate = getDoubleValue(conversionAnalysis.get("orderRate"));
            conversionData.setInviteToRegisterRate(String.format("%.2f", registerRate * 100));
            conversionData.setRegisterToOrderRate(String.format("%.2f", orderRate * 100));
            conversionData.setTotalInvites(getLongValue(conversionAnalysis.get("totalInvites")));
            conversionData.setTotalRegistered(getLongValue(conversionAnalysis.get("totalRegistered")));
            conversionData.setTotalOrdered(getLongValue(conversionAnalysis.get("totalOrdered")));
            result.setConversionData(conversionData);
            
            // 5. 用户排行榜
            List<Map<String, Object>> rankingData = getUserRanking(pageSize, pageNum);
            List<InviteStatisticsDTO.UserRankingItem> userRanking = new ArrayList<>();
            for (int i = 0; i < rankingData.size(); i++) {
                Map<String, Object> item = rankingData.get(i);
                InviteStatisticsDTO.UserRankingItem rankingItem = new InviteStatisticsDTO.UserRankingItem();
                rankingItem.setRank(i + 1 + (pageNum - 1) * pageSize);
                rankingItem.setNickname(getStringValue(item.get("userNickname")));
                rankingItem.setInviteCount(getIntValue(item.get("inviteCount")));
                rankingItem.setUserId(getLongValue(item.get("userId")));
                rankingItem.setRewardAmount(getStringValue(item.get("rewardAmount")));
                userRanking.add(rankingItem);
            }
            result.setUserRanking(userRanking);
            
            // 6. 地域分布
            List<Map<String, Object>> regionDistribution = getRegionDistribution();
            List<InviteStatisticsDTO.RegionItem> regionData = new ArrayList<>();
            for (Map<String, Object> item : regionDistribution) {
                InviteStatisticsDTO.RegionItem regionItem = new InviteStatisticsDTO.RegionItem();
                regionItem.setRegion(getStringValue(item.get("region")));
                regionItem.setCount(getIntValue(item.get("inviteCount")));
                regionData.add(regionItem);
            }
            result.setRegionData(regionData);
            
            // 7. 趋势数据
            List<Map<String, Object>> trendDataList = getTrendData(startDate, endDate, trendType);
            List<InviteStatisticsDTO.TrendItem> trendData = new ArrayList<>();
            for (Map<String, Object> item : trendDataList) {
                InviteStatisticsDTO.TrendItem trendItem = new InviteStatisticsDTO.TrendItem();
                trendItem.setDate(getStringValue(item.get("date")));
                trendItem.setCount(getIntValue(item.get("count")));
                trendData.add(trendItem);
            }
            result.setTrendData(trendData);
            
        } catch (Exception e) {
            LOGGER.error("构建邀请统计数据失败", e);
        }
        
        return result;
    }
    
    @Override
    public void refreshStatisticsCache() {
        try {
            // 删除所有相关缓存
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                LOGGER.info("已清理邀请统计缓存，共清理{}个key", keys.size());
            }
        } catch (Exception e) {
            LOGGER.error("刷新邀请统计缓存失败", e);
        }
    }
    
    // 辅助方法
    private Long getLongValue(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    private Integer getIntValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private Double getDoubleValue(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private String getStringValue(Object value) {
        return value != null ? value.toString() : "";
    }
    
    @Override
    public Map<String, Object> getOverviewData() {
        Map<String, Object> overview = new HashMap<>();
        
        try {
            // 总邀请关系数
            long totalInvites = inviteRelationMapper.countByExample(new PmsInviteRelationExample());
            overview.put("totalInvites", totalInvites);
            
            // 已注册用户数
            PmsInviteRelationExample registeredExample = new PmsInviteRelationExample();
            registeredExample.createCriteria().andStatusGreaterThanOrEqualTo((byte)1);
            long registeredCount = inviteRelationMapper.countByExample(registeredExample);
            overview.put("registeredCount", registeredCount);
            
            // 已完成首单用户数
            PmsInviteRelationExample firstOrderExample = new PmsInviteRelationExample();
            firstOrderExample.createCriteria().andStatusEqualTo((byte)2);
            long firstOrderCount = inviteRelationMapper.countByExample(firstOrderExample);
            overview.put("firstOrderCount", firstOrderCount);
            
            // 计算转化率
            if (totalInvites > 0) {
                overview.put("registerRate", (double) registeredCount / totalInvites);
                overview.put("orderRate", (double) firstOrderCount / totalInvites);
            } else {
                overview.put("registerRate", 0.0);
                overview.put("orderRate", 0.0);
            }
            
            // 总奖励数
            long totalRewards = inviteRewardMapper.countByExample(new PmsInviteRewardExample());
            overview.put("totalRewards", totalRewards);
            
            // 总提现申请数
            long totalWithdraws = withdrawApplyMapper.countByExample(new PmsInviteWithdrawApplyExample());
            overview.put("totalWithdraws", totalWithdraws);
            
        } catch (Exception e) {
            LOGGER.error("获取邀请数据总览失败", e);
        }
        
        return overview;
    }
    
    @Override
    public List<Map<String, Object>> getTrendData(String startDate, String endDate, String type) {
        try {
            // 使用自定义Dao获取真实的趋势数据
            return statisticsDao.getInviteTrendData(startDate, endDate, type);
        } catch (Exception e) {
            LOGGER.error("获取邀请趋势数据失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getUserRanking(Integer pageSize, Integer pageNum) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            // 使用自定义Dao获取真实的排行榜数据
            return statisticsDao.getUserInviteRanking(pageSize, pageNum);
        } catch (Exception e) {
            LOGGER.error("获取用户邀请排行榜失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> getRewardStatistics() {
        Map<String, Object> rewardStats = new HashMap<>();
        
        try {
            // 总奖励数
            long totalRewards = inviteRewardMapper.countByExample(new PmsInviteRewardExample());
            rewardStats.put("totalRewards", totalRewards);
            
            // 已发放奖励数
            PmsInviteRewardExample sentExample = new PmsInviteRewardExample();
            sentExample.createCriteria().andStatusEqualTo((byte)1);
            long sentRewards = inviteRewardMapper.countByExample(sentExample);
            rewardStats.put("sentRewards", sentRewards);
            
            // 待发放奖励数
            PmsInviteRewardExample pendingExample = new PmsInviteRewardExample();
            pendingExample.createCriteria().andStatusEqualTo((byte)0);
            long pendingRewards = inviteRewardMapper.countByExample(pendingExample);
            rewardStats.put("pendingRewards", pendingRewards);
            
            // 发放失败奖励数
            PmsInviteRewardExample failedExample = new PmsInviteRewardExample();
            failedExample.createCriteria().andStatusEqualTo((byte)2);
            long failedRewards = inviteRewardMapper.countByExample(failedExample);
            rewardStats.put("failedRewards", failedRewards);
            
            // 按奖励类型统计
            Map<String, Object> typeStats = new HashMap<>();
            for (int type = 1; type <= 4; type++) {
                PmsInviteRewardExample typeExample = new PmsInviteRewardExample();
                typeExample.createCriteria().andRewardTypeEqualTo((byte)type);
                long typeCount = inviteRewardMapper.countByExample(typeExample);
                typeStats.put("type" + type, typeCount);
            }
            rewardStats.put("typeStatistics", typeStats);
            
        } catch (Exception e) {
            LOGGER.error("获取奖励发放统计失败", e);
        }
        
        return rewardStats;
    }
    
    @Override
    public Map<String, Object> getWithdrawStatistics() {
        Map<String, Object> withdrawStats = new HashMap<>();
        
        try {
            // 总申请数
            long totalApplies = withdrawApplyMapper.countByExample(new PmsInviteWithdrawApplyExample());
            withdrawStats.put("totalApplications", totalApplies);
            
            // 按状态统计数量
            Map<Integer, Long> statusCounts = new HashMap<>();
            for (int status = 0; status <= 5; status++) {
                PmsInviteWithdrawApplyExample statusExample = new PmsInviteWithdrawApplyExample();
                statusExample.createCriteria().andStatusEqualTo((byte)status);
                long statusCount = withdrawApplyMapper.countByExample(statusExample);
                statusCounts.put(status, statusCount);
                withdrawStats.put("status" + status, statusCount);
            }
            
            // 待审核申请数
            withdrawStats.put("pendingCount", statusCounts.get(0));
            
            // 成功提现数量
            long successCount = statusCounts.get(2);
            
            // 成功率
            if (totalApplies > 0) {
                withdrawStats.put("successRate", (double) successCount / totalApplies);
            } else {
                withdrawStats.put("successRate", 0.0);
            }
            
            // 计算金额统计
            try {
                // 使用自定义SQL查询金额统计
                Map<String, Object> amountStats = statisticsDao.getWithdrawAmountStatistics();
                
                // 申请提现总额
                Double totalAmount = getDoubleValue(amountStats.get("totalAmount"));
                withdrawStats.put("totalAmount", totalAmount);
                
                // 成功提现金额
                Double successAmount = getDoubleValue(amountStats.get("successAmount"));
                withdrawStats.put("successAmount", successAmount);
                
                // 待审核金额
                Double pendingAmount = getDoubleValue(amountStats.get("pendingAmount"));
                withdrawStats.put("pendingAmount", pendingAmount);
                
                // 审核通过待处理金额
                Double approvedAmount = getDoubleValue(amountStats.get("approvedAmount"));
                withdrawStats.put("approvedAmount", approvedAmount);
                
            } catch (Exception e) {
                LOGGER.warn("获取提现金额统计失败，设置默认值", e);
                withdrawStats.put("totalAmount", 0.0);
                withdrawStats.put("successAmount", 0.0);
                withdrawStats.put("pendingAmount", 0.0);
                withdrawStats.put("approvedAmount", 0.0);
            }
            
        } catch (Exception e) {
            LOGGER.error("获取提现申请统计失败", e);
            // 设置默认值
            withdrawStats.put("totalApplications", 0);
            withdrawStats.put("pendingCount", 0);
            withdrawStats.put("successRate", 0.0);
            withdrawStats.put("totalAmount", 0.0);
            withdrawStats.put("successAmount", 0.0);
            withdrawStats.put("pendingAmount", 0.0);
            withdrawStats.put("approvedAmount", 0.0);
        }
        
        return withdrawStats;
    }
    
    @Override
    public Map<String, Object> getConversionAnalysis() {
        try {
            // 使用自定义Dao获取真实的转化分析数据
            return statisticsDao.getConversionAnalysisData();
        } catch (Exception e) {
            LOGGER.error("获取转化率分析失败", e);
            return new HashMap<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getRegionDistribution() {
        try {
            // 使用自定义Dao获取真实的地域分布数据
            return statisticsDao.getRegionDistributionData();
        } catch (Exception e) {
            LOGGER.error("获取地域分布统计失败", e);
            return new ArrayList<>();
        }
    }
} 