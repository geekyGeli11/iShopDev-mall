package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dto.SigninCalendarVO;
import com.macro.mall.portal.dto.SigninConfigVO;
import com.macro.mall.portal.dto.SigninResult;
import com.macro.mall.portal.dto.SigninStatusVO;
import com.macro.mall.portal.service.UmsPortalSigninService;
import com.macro.mall.portal.util.StpMemberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 小程序端签到Controller
 * Created by guanghengzhou on 2024/12/23.
 */
@Controller
@Tag(name = "UmsPortalSigninController", description = "小程序端签到管理")
@RequestMapping("/signin")
public class UmsPortalSigninController {
    
    @Autowired
    private UmsPortalSigninService signinService;
    
    @Operation(summary = "获取用户签到状态")
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SigninStatusVO> getStatus(HttpServletRequest request) {
        Long memberId = getCurrentMemberId();
        // TODO: 需要实现UmsPortalSigninService.getSigninStatus()
        SigninStatusVO status = signinService.getSigninStatus(memberId);
        return CommonResult.success(status);
    }
    
    @Operation(summary = "执行签到")
    @RequestMapping(value = "/checkin", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<SigninResult> checkin(HttpServletRequest request) {
        Long memberId = getCurrentMemberId();
        String clientIp = getClientIpAddr(request);
        
        // TODO: 需要实现UmsPortalSigninService.checkin()
        SigninResult result = signinService.checkin(memberId, clientIp);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取签到日历")
    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SigninCalendarVO> getCalendar(
            @RequestParam String month,
            HttpServletRequest request) {
        
        Long memberId = getCurrentMemberId();
        // TODO: 需要实现UmsPortalSigninService.getSigninCalendar()
        SigninCalendarVO calendar = signinService.getSigninCalendar(memberId, month);
        return CommonResult.success(calendar);
    }
    
    @Operation(summary = "领取连签奖励")
    @RequestMapping(value = "/claim-reward", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult claimReward(HttpServletRequest request) {
        Long memberId = getCurrentMemberId();
        
        // TODO: 需要实现UmsPortalSigninService.claimContinuousReward()
        boolean success = signinService.claimContinuousReward(memberId);
        
        if (success) {
            return CommonResult.success(null, "连签奖励领取成功");
        } else {
            return CommonResult.failed("连签奖励领取失败");
        }
    }
    
    @Operation(summary = "获取签到记录")
    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getSigninLogs(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {

        Long memberId = getCurrentMemberId();
        // TODO: 需要实现UmsPortalSigninService.getSigninLogs()
        return CommonResult.success(signinService.getSigninLogs(memberId, pageNum, pageSize));
    }

    @Operation(summary = "获取签到配置信息")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<SigninConfigVO> getSigninConfig() {
        SigninConfigVO config = signinService.getSigninConfig();
        return CommonResult.success(config);
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentMemberId() {
        return StpMemberUtil.getLoginIdAsLong();
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 