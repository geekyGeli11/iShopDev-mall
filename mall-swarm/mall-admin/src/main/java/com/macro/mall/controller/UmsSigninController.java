package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.UmsSigninConfigParam;
import com.macro.mall.dto.UmsSigninLogQueryParam;
import com.macro.mall.dto.UmsSigninStatisticsResult;
import com.macro.mall.model.UmsSigninRewardConfig;
import com.macro.mall.model.UmsMemberSigninLog;
import com.macro.mall.service.UmsSigninService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 签到管理Controller
 * Created by guanghengzhou on 2024/12/23.
 */
@Controller
@Tag(name = "UmsSigninController", description = "签到管理")
@RequestMapping("/signin")
public class UmsSigninController {
    
    @Autowired
    private UmsSigninService signinService;
    
    @Operation(summary = "获取签到配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsSigninRewardConfig> getConfig() {
        // TODO: 需要实现UmsSigninService.getConfig()
        UmsSigninRewardConfig config = signinService.getConfig();
        return CommonResult.success(config);
    }
    
    @Operation(summary = "更新签到配置")
    @RequestMapping(value = "/config", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateConfig(@RequestBody UmsSigninConfigParam configParam) {
        // TODO: 需要实现UmsSigninService.updateConfig()
        int count = signinService.updateConfig(configParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("更新配置失败");
    }
    
    @Operation(summary = "获取签到记录列表")
    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberSigninLog>> getLogs(
            @RequestParam(value = "memberUsername", required = false) String memberUsername,
            @RequestParam(value = "signinMonth", required = false) String signinMonth,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        
        UmsSigninLogQueryParam queryParam = new UmsSigninLogQueryParam();
        queryParam.setMemberUsername(memberUsername);
        queryParam.setSigninMonth(signinMonth);
        queryParam.setStartDate(startDate);
        queryParam.setEndDate(endDate);
        queryParam.setPageSize(pageSize);
        queryParam.setPageNum(pageNum);
        
        // TODO: 需要实现UmsSigninService.getLogs()
        List<UmsMemberSigninLog> logs = signinService.getLogs(queryParam);
        return CommonResult.success(CommonPage.restPage(logs));
    }
    
    @Operation(summary = "获取签到统计数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsSigninStatisticsResult> getStatistics(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        
        // TODO: 需要实现UmsSigninService.getStatistics()
        UmsSigninStatisticsResult statistics = signinService.getStatistics(startDate, endDate);
        return CommonResult.success(statistics);
    }
    
    @Operation(summary = "导出签到记录")
    @RequestMapping(value = "/logs/export", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult exportLogs(
            @RequestParam(value = "memberUsername", required = false) String memberUsername,
            @RequestParam(value = "signinMonth", required = false) String signinMonth,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        
        UmsSigninLogQueryParam queryParam = new UmsSigninLogQueryParam();
        queryParam.setMemberUsername(memberUsername);
        queryParam.setSigninMonth(signinMonth);
        queryParam.setStartDate(startDate);
        queryParam.setEndDate(endDate);
        
        // TODO: 需要实现UmsSigninService.exportLogs()
        String exportResult = signinService.exportLogs(queryParam);
        return CommonResult.success(exportResult, "导出成功");
    }
} 