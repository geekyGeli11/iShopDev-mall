package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.mapper.UmsSigninRewardConfigMapper;
import com.macro.mall.mapper.UmsMemberSigninLogMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.dto.SigninCalendarVO;
import com.macro.mall.portal.dto.SigninConfigVO;
import com.macro.mall.portal.dto.SigninResult;
import com.macro.mall.portal.dto.SigninStatusVO;
import com.macro.mall.portal.service.UmsPortalSigninService;
import com.macro.mall.portal.service.UmsMemberCouponService;
import com.macro.mall.portal.service.UmsIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 小程序端签到Service实现类
 * Created by guanghengzhou on 2024/12/23.
 */
@Service
public class UmsPortalSigninServiceImpl implements UmsPortalSigninService {
    
    @Autowired
    private UmsSigninRewardConfigMapper signinRewardConfigMapper;

    @Autowired
    private UmsMemberSigninLogMapper memberSigninLogMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private SmsCouponMapper couponMapper;
    
    @Autowired
    private UmsMemberCouponService memberCouponService;

    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;

    @Autowired
    private UmsIntegrationService integrationService;
    
    @Override
    public SigninStatusVO getSigninStatus(Long memberId) {
        SigninStatusVO status = new SigninStatusVO();
        
        // TODO: 实现获取签到状态的逻辑
        // 1. 检查今日是否已签到
        boolean isSigninToday = checkIsSigninToday(memberId);
        status.setIsSigninToday(isSigninToday);
        
        // 2. 获取连续签到天数
        Integer continuousDays = getCurrentContinuousDays(memberId);
        status.setContinuousDays(continuousDays);
        
        // 3. 计算今日可获得积分
        Integer todayPoints = calculateTodayPoints(continuousDays);
        status.setTodayPoints(todayPoints);
        
        // 4. 获取本月签到天数
        Integer monthSigninDays = getMonthSigninDays(memberId);
        status.setMonthSigninDays(monthSigninDays);
        
        // 5. 检查是否可领取连签奖励
        Boolean canClaimReward = checkCanClaimReward(memberId, continuousDays);
        status.setCanClaimReward(canClaimReward);
        
        // 6. 获取当前积分余额
        Integer currentPoints = getCurrentMemberPoints(memberId);
        status.setCurrentPoints(currentPoints);
        
        // 7. 获取近一周签到情况
        List<String> weekSignedDates = getWeekSigninDates(memberId);
        status.setSignedDates(weekSignedDates);
        
        return status;
    }
    
    @Override
    @Transactional
    public SigninResult checkin(Long memberId, String clientIp) {
        // TODO: 实现签到逻辑
        // 1. 检查今日是否已签到
        if (checkIsSigninToday(memberId)) {
            // 返回友好的错误信息而不是抛出异常
            SigninResult result = new SigninResult();
            result.setPoints(0);
            result.setContinuousDays(getCurrentContinuousDays(memberId));
            result.setCanClaimReward(false);
            result.setMessage("今日已签到，请明天再来！");
            return result;
        }
        
        // 2. 获取签到配置
        UmsSigninRewardConfig config = getActiveConfig();
        
        // 3. 计算签到奖励
        Integer currentContinuousDays = getCurrentContinuousDays(memberId);
        Integer continuousDays = currentContinuousDays + 1;
        Integer points = calculateSigninPoints(continuousDays, config);
        
        // 4. 更新用户积分
        updateMemberPoints(memberId, points);
        
        // 5. 记录签到日志
        recordSigninLog(memberId, points, continuousDays, clientIp);
        
        // 6. 检查连签奖励
        Boolean canClaimReward = checkCanClaimReward(memberId, continuousDays);
        
        SigninResult result = new SigninResult();
        result.setPoints(points);
        result.setContinuousDays(continuousDays);
        result.setCanClaimReward(canClaimReward);
        result.setMessage("签到成功，获得" + points + "积分");
        
        return result;
    }
    
    @Override
    public SigninCalendarVO getSigninCalendar(Long memberId, String month) {
        SigninCalendarVO calendar = new SigninCalendarVO();
        calendar.setMonth(month);
        
        // TODO: 实现获取签到日历的逻辑
        // 1. 查询指定月份的签到记录
        List<String> signinDates = getMonthSigninDates(memberId, month);
        calendar.setSigninDates(signinDates);
        
        // 2. 统计本月签到天数
        calendar.setMonthSigninDays(signinDates.size());
        
        // 3. 获取连续签到天数
        Integer continuousDays = getCurrentContinuousDays(memberId);
        calendar.setContinuousDays(continuousDays);
        
        return calendar;
    }
    
    @Override
    @Transactional
    public boolean claimContinuousReward(Long memberId) {
        // TODO: 实现领取连签奖励的逻辑
        // 1. 检查是否满足连签条件
        Integer continuousDays = getCurrentContinuousDays(memberId);
        UmsSigninRewardConfig config = getActiveConfig();
        
        if (continuousDays < config.getContinuousDaysForReward()) {
            throw new RuntimeException("连续签到天数不足");
        }
        
        // 2. 检查是否已领取过奖励
        if (checkAlreadyClaimedReward(memberId)) {
            throw new RuntimeException("已领取过连签奖励");
        }
        
        // 3. 发放奖励优惠券
        if (config.getRewardCouponId() != null) {
            // TODO: 调用优惠券发放接口
            grantCouponToMember(memberId, config.getRewardCouponId());
        }
        
        // 4. 更新奖励领取状态
        updateRewardClaimStatus(memberId);
        
        return true;
    }
    
    @Override
    public List<?> getSigninLogs(Long memberId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("signin_date desc");

        return memberSigninLogMapper.selectByExample(example);
    }

    @Override
    public SigninConfigVO getSigninConfig() {
        UmsSigninRewardConfig config = getActiveConfig();
        if (config == null) {
            return null;
        }

        SigninConfigVO configVO = new SigninConfigVO();
        configVO.setConfigName(config.getConfigName());
        configVO.setBasePoints(config.getBasePoints());
        configVO.setIncrementPoints(config.getIncrementPoints());
        configVO.setMaxDailyPoints(config.getMaxDailyPoints());
        configVO.setContinuousDaysForReward(config.getContinuousDaysForReward());
        configVO.setCycleDays(config.getCycleDays());
        configVO.setRewardCouponId(config.getRewardCouponId());

        // 如果配置了奖励优惠券，获取优惠券详细信息
        if (config.getRewardCouponId() != null) {
            SmsCoupon coupon = couponMapper.selectByPrimaryKey(config.getRewardCouponId());
            if (coupon != null) {
                configVO.setRewardCouponName(coupon.getName());
                configVO.setRewardCouponAmount(coupon.getAmount() != null ? coupon.getAmount().intValue() : 0);
                configVO.setRewardCouponMinPoint(coupon.getMinPoint() != null ? coupon.getMinPoint().intValue() : 0);

                // 格式化日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                if (coupon.getStartTime() != null) {
                    configVO.setRewardCouponStartTime(sdf.format(coupon.getStartTime()));
                }
                if (coupon.getEndTime() != null) {
                    configVO.setRewardCouponEndTime(sdf.format(coupon.getEndTime()));
                }
            }
        }

        return configVO;
    }
    
    // 私有辅助方法
    private boolean checkIsSigninToday(Long memberId) {
        // TODO: 实现检查今日是否已签到
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninDateEqualTo(java.sql.Date.valueOf(today));
        
        List<UmsMemberSigninLog> logs = memberSigninLogMapper.selectByExample(example);
        return !logs.isEmpty();
    }
    
    private Integer getCurrentContinuousDays(Long memberId) {
        // 获取用户最新的签到记录
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("signin_date desc");

        List<UmsMemberSigninLog> logs = memberSigninLogMapper.selectByExample(example);
        if (logs.isEmpty()) {
            return 0;
        }

        UmsMemberSigninLog latestLog = logs.get(0);
        Date latestSigninDate = latestLog.getSigninDate();
        Date today = new Date();
        Date yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);

        // 将日期转换为字符串进行比较（只比较日期部分）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String latestDateStr = sdf.format(latestSigninDate);
        String todayStr = sdf.format(today);
        String yesterdayStr = sdf.format(yesterday);

        if (latestDateStr.equals(todayStr)) {
            // 今天已签到，返回今天的连续天数
            return latestLog.getContinuousDays();
        } else if (latestDateStr.equals(yesterdayStr)) {
            // 昨天签到了，今天还没签到，连续天数保持不变
            return latestLog.getContinuousDays();
        } else {
            // 签到中断了，连续天数重置为0
            return 0;
        }
    }
    
    private Integer calculateTodayPoints(Integer continuousDays) {
        UmsSigninRewardConfig config = getActiveConfig();
        if (config == null) {
            return 5; // 默认积分
        }

        // 如果今天还没签到，计算明天签到可获得的积分
        Integer tomorrowContinuousDays = continuousDays + 1;

        // 计算积分：基础积分 + (连续天数-1) * 递增积分，不超过每日上限
        int points = config.getBasePoints() + (tomorrowContinuousDays - 1) * config.getIncrementPoints();
        return Math.min(points, config.getMaxDailyPoints());
    }
    
    private Integer getMonthSigninDays(Long memberId) {
        // TODO: 实现获取本月签到天数
        String monthPrefix = new SimpleDateFormat("yyyy-MM").format(new Date());
        
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninMonthEqualTo(monthPrefix);
        
        List<UmsMemberSigninLog> logs = memberSigninLogMapper.selectByExample(example);
        return logs.size();
    }
    
    private Boolean checkCanClaimReward(Long memberId, Integer continuousDays) {
        UmsSigninRewardConfig config = getActiveConfig();
        if (config == null || config.getRewardCouponId() == null) {
            return false;
        }
        
        // 检查连续天数是否达到要求且未领取过奖励
        return continuousDays >= config.getContinuousDaysForReward() 
                && !checkAlreadyClaimedReward(memberId);
    }
    
    private Integer getCurrentMemberPoints(Long memberId) {
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member != null) {
            Integer integration = member.getIntegration();
            return integration != null ? integration : 0;
        }
        return 0;
    }
    
    private UmsSigninRewardConfig getActiveConfig() {
        UmsSigninRewardConfigExample example = new UmsSigninRewardConfigExample();
        example.createCriteria().andIsActiveEqualTo((byte) 1);
        
        List<UmsSigninRewardConfig> configs = signinRewardConfigMapper.selectByExample(example);
        return configs.isEmpty() ? null : configs.get(0);
    }
    
    private Integer calculateSigninPoints(Integer continuousDays, UmsSigninRewardConfig config) {
        if (config == null) {
            return 5; // 默认积分
        }
        
        int points = config.getBasePoints() + (continuousDays - 1) * config.getIncrementPoints();
        return Math.min(points, config.getMaxDailyPoints());
    }
    
    private void updateMemberPoints(Long memberId, Integer points) {
        // 使用统一积分服务更新积分并检查等级升级
        boolean result = integrationService.updateIntegrationAndCheckLevel(
            memberId,
            points,
            "签到获得积分",
            2 // 2->签到
        );

        if (!result) {
            throw new RuntimeException("签到积分发放失败");
        }
    }
    
    private void recordSigninLog(Long memberId, Integer points, Integer continuousDays, String clientIp) {
        // TODO: 实现记录签到日志
        UmsMemberSigninLog log = new UmsMemberSigninLog();
        log.setMemberId(memberId);
        
        // 获取用户名，确保不为null
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        String username = "";
        if (member != null && member.getNickname() != null && !member.getNickname().trim().isEmpty()) {
            username = member.getNickname();
        } else {
            // 如果获取不到用户名，使用默认格式
            username = "member_" + memberId;
        }
        log.setMemberUsername(username);
        
        log.setSigninDate(new Date());
        log.setSigninTime(new Date());
        log.setPointsEarned(points);
        log.setContinuousDays(continuousDays);
        log.setCycleSigninDays(continuousDays);
        log.setIsRewardClaimed((byte) 0);
        log.setSigninMonth(new SimpleDateFormat("yyyy-MM").format(new Date()));
        log.setClientIp(clientIp);
        
        memberSigninLogMapper.insertSelective(log);
    }
    
    private List<String> getMonthSigninDates(Long memberId, String month) {
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninMonthEqualTo(month);
        
        List<UmsMemberSigninLog> logs = memberSigninLogMapper.selectByExample(example);
        
        return logs.stream()
                .map(log -> new SimpleDateFormat("yyyy-MM-dd").format(log.getSigninDate()))
                .collect(Collectors.toList());
    }
    
    private List<String> getWeekSigninDates(Long memberId) {
        // 获取近一周（7天）的签到记录
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        
        // 计算本周一的日期
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysFromMonday = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - 2;
        calendar.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
        Date weekStart = calendar.getTime();
        
        // 查询本周的签到记录
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninDateGreaterThanOrEqualTo(weekStart)
                .andSigninDateLessThanOrEqualTo(today);
        
        List<UmsMemberSigninLog> logs = memberSigninLogMapper.selectByExample(example);
        
        return logs.stream()
                .map(log -> new SimpleDateFormat("yyyy-MM-dd").format(log.getSigninDate()))
                .collect(Collectors.toList());
    }
    
    private boolean checkAlreadyClaimedReward(Long memberId) {
        // TODO: 实现检查是否已领取过连签奖励
        String monthPrefix = new SimpleDateFormat("yyyy-MM").format(new Date());
        
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninMonthEqualTo(monthPrefix)
                .andIsRewardClaimedEqualTo((byte) 1);
        
        List<UmsMemberSigninLog> logs = memberSigninLogMapper.selectByExample(example);
        return !logs.isEmpty();
    }
    
    private void grantCouponToMember(Long memberId, Long couponId) {
        try {
            // 调用优惠券服务为指定会员发放优惠券
            memberCouponService.addForMember(memberId, couponId);
        } catch (Exception e) {
            // 记录异常，但不阻断签到流程
            // 可以考虑记录到日志中，以便后续处理
            System.err.println("优惠券发放失败，会员ID: " + memberId + ", 优惠券ID: " + couponId + ", 错误: " + e.getMessage());
        }
    }
    
    private void updateRewardClaimStatus(Long memberId) {
        // TODO: 实现更新奖励领取状态
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        example.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninDateEqualTo(java.sql.Date.valueOf(today));
        
        UmsMemberSigninLog updateRecord = new UmsMemberSigninLog();
        updateRecord.setIsRewardClaimed((byte) 1);
        
        memberSigninLogMapper.updateByExampleSelective(updateRecord, example);
    }
} 