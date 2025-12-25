package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.SigninCalendarVO;
import com.macro.mall.portal.dto.SigninConfigVO;
import com.macro.mall.portal.dto.SigninResult;
import com.macro.mall.portal.dto.SigninStatusVO;

import java.util.List;

/**
 * 小程序端签到Service
 * Created by guanghengzhou on 2024/12/23.
 */
public interface UmsPortalSigninService {
    
    /**
     * 获取用户签到状态
     */
    SigninStatusVO getSigninStatus(Long memberId);
    
    /**
     * 执行签到
     */
    SigninResult checkin(Long memberId, String clientIp);
    
    /**
     * 获取签到日历
     */
    SigninCalendarVO getSigninCalendar(Long memberId, String month);
    
    /**
     * 领取连签奖励
     */
    boolean claimContinuousReward(Long memberId);
    
    /**
     * 获取签到记录
     */
    List<?> getSigninLogs(Long memberId, Integer pageNum, Integer pageSize);

    /**
     * 获取签到配置信息
     */
    SigninConfigVO getSigninConfig();
} 