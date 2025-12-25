package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsPointsExchangeLogQueryParam;
import com.macro.mall.model.UmsPointsExchangeLog;
import com.macro.mall.service.UmsPointsExchangeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 积分兑换记录管理Controller
 * Created by macro on 2024/01/20.
 */
@Controller
@Tag(name = "UmsPointsExchangeLogController", description = "积分兑换记录管理")
@RequestMapping("/points/exchange")
public class UmsPointsExchangeLogController {
    
    @Autowired
    private UmsPointsExchangeLogService pointsExchangeLogService;
    
    @Operation(summary = "分页查询兑换记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsPointsExchangeLog>> list(
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "memberUsername", required = false) String memberUsername,
            @RequestParam(value = "exchangeType", required = false) Byte exchangeType,
            @RequestParam(value = "targetName", required = false) String targetName,
            @RequestParam(value = "exchangeStatus", required = false) Byte exchangeStatus,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "minPointsUsed", required = false) Integer minPointsUsed,
            @RequestParam(value = "maxPointsUsed", required = false) Integer maxPointsUsed,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        
        UmsPointsExchangeLogQueryParam queryParam = new UmsPointsExchangeLogQueryParam();
        queryParam.setMemberId(memberId);
        queryParam.setMemberUsername(memberUsername);
        queryParam.setExchangeType(exchangeType);
        queryParam.setTargetName(targetName);
        queryParam.setExchangeStatus(exchangeStatus);
        queryParam.setStartDate(startDate);
        queryParam.setEndDate(endDate);
        queryParam.setMinPointsUsed(minPointsUsed);
        queryParam.setMaxPointsUsed(maxPointsUsed);
        
        List<UmsPointsExchangeLog> logList = pointsExchangeLogService.list(queryParam, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(logList));
    }
    
    @Operation(summary = "获取兑换记录详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsPointsExchangeLog> getItem(@PathVariable Long id) {
        UmsPointsExchangeLog log = pointsExchangeLogService.getItem(id);
        return CommonResult.success(log);
    }
    
    @Operation(summary = "获取兑换统计数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getStatistics(
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "memberUsername", required = false) String memberUsername,
            @RequestParam(value = "exchangeType", required = false) Byte exchangeType,
            @RequestParam(value = "targetName", required = false) String targetName,
            @RequestParam(value = "exchangeStatus", required = false) Byte exchangeStatus,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "minPointsUsed", required = false) Integer minPointsUsed,
            @RequestParam(value = "maxPointsUsed", required = false) Integer maxPointsUsed) {
        
        UmsPointsExchangeLogQueryParam queryParam = new UmsPointsExchangeLogQueryParam();
        queryParam.setMemberId(memberId);
        queryParam.setMemberUsername(memberUsername);
        queryParam.setExchangeType(exchangeType);
        queryParam.setTargetName(targetName);
        queryParam.setExchangeStatus(exchangeStatus);
        queryParam.setStartDate(startDate);
        queryParam.setEndDate(endDate);
        queryParam.setMinPointsUsed(minPointsUsed);
        queryParam.setMaxPointsUsed(maxPointsUsed);
        
        Map<String, Object> statistics = pointsExchangeLogService.getStatistics(queryParam);
        return CommonResult.success(statistics);
    }
    
    @Operation(summary = "获取用户兑换记录")
    @RequestMapping(value = "/user/{memberId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsPointsExchangeLog>> getUserExchangeList(
            @PathVariable Long memberId,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsPointsExchangeLog> logList = pointsExchangeLogService.getUserExchangeList(memberId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(logList));
    }
    
    @Operation(summary = "导出兑换记录")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsPointsExchangeLog>> exportList(
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "memberUsername", required = false) String memberUsername,
            @RequestParam(value = "exchangeType", required = false) Byte exchangeType,
            @RequestParam(value = "targetName", required = false) String targetName,
            @RequestParam(value = "exchangeStatus", required = false) Byte exchangeStatus,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "minPointsUsed", required = false) Integer minPointsUsed,
            @RequestParam(value = "maxPointsUsed", required = false) Integer maxPointsUsed) {
        
        UmsPointsExchangeLogQueryParam queryParam = new UmsPointsExchangeLogQueryParam();
        queryParam.setMemberId(memberId);
        queryParam.setMemberUsername(memberUsername);
        queryParam.setExchangeType(exchangeType);
        queryParam.setTargetName(targetName);
        queryParam.setExchangeStatus(exchangeStatus);
        queryParam.setStartDate(startDate);
        queryParam.setEndDate(endDate);
        queryParam.setMinPointsUsed(minPointsUsed);
        queryParam.setMaxPointsUsed(maxPointsUsed);
        
        List<UmsPointsExchangeLog> logList = pointsExchangeLogService.exportList(queryParam);
        return CommonResult.success(logList);
    }
} 