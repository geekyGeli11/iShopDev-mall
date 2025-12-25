package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.InviteStatisticsDTO;
import com.macro.mall.service.PmsInviteStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 邀请数据统计Controller
 * Created by guanghengzhou on 2024/01/20.
 */
@Controller
@Tag(name = "邀请数据统计", description = "邀请数据统计相关接口")
@RequestMapping("/invite/statistics")
public class PmsInviteStatisticsController {
    
    @Autowired
    private PmsInviteStatisticsService statisticsService;
    
    @Operation(summary = "获取完整的邀请统计数据（推荐使用）")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<InviteStatisticsDTO> getAllStatistics(
            @Parameter(description = "开始日期") @RequestParam(value = "startDate", required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(value = "endDate", required = false) String endDate,
            @Parameter(description = "趋势数据类型") @RequestParam(value = "trendType", defaultValue = "invite") String trendType,
            @Parameter(description = "页大小") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        InviteStatisticsDTO data = statisticsService.getAllStatistics(startDate, endDate, trendType, pageSize, pageNum);
        return CommonResult.success(data);
    }
    
    @Operation(summary = "刷新统计数据缓存")
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> refreshCache() {
        statisticsService.refreshStatisticsCache();
        return CommonResult.success("缓存刷新成功");
    }
    
    @Operation(summary = "获取邀请数据总览")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getOverviewData() {
        Map<String, Object> data = statisticsService.getOverviewData();
        return CommonResult.success(data);
    }
    
    @Operation(summary = "获取邀请趋势数据")
    @RequestMapping(value = "/trend", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getTrendData(
            @Parameter(description = "开始日期") @RequestParam(value = "startDate", required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(value = "endDate", required = false) String endDate,
            @Parameter(description = "数据类型") @RequestParam(value = "type", defaultValue = "invite") String type) {
        List<Map<String, Object>> data = statisticsService.getTrendData(startDate, endDate, type);
        return CommonResult.success(data);
    }
    
    @Operation(summary = "获取用户邀请排行榜")
    @RequestMapping(value = "/ranking", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getUserRanking(
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<Map<String, Object>> data = statisticsService.getUserRanking(pageSize, pageNum);
        return CommonResult.success(data);
    }
    
    @Operation(summary = "获取奖励发放统计")
    @RequestMapping(value = "/reward", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getRewardStatistics() {
        Map<String, Object> data = statisticsService.getRewardStatistics();
        return CommonResult.success(data);
    }
    
    @Operation(summary = "获取提现申请统计")
    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getWithdrawStatistics() {
        Map<String, Object> data = statisticsService.getWithdrawStatistics();
        return CommonResult.success(data);
    }
    
    @Operation(summary = "获取转化率分析")
    @RequestMapping(value = "/conversion", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getConversionAnalysis() {
        Map<String, Object> data = statisticsService.getConversionAnalysis();
        return CommonResult.success(data);
    }
    
    @Operation(summary = "获取地域分布统计")
    @RequestMapping(value = "/region", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getRegionDistribution() {
        List<Map<String, Object>> data = statisticsService.getRegionDistribution();
        return CommonResult.success(data);
    }
} 