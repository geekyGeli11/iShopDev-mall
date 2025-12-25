package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.service.BusinessStatisticsService;
import com.macro.mall.service.HomeStatisticsService;
import com.macro.mall.service.MemberStatisticsService;
import com.macro.mall.service.ProductStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页统计数据Controller
 * Created by fgh on 2025/04/27.
 */
@Controller
@Tag(name = "HomeStatisticsController", description = "首页统计数据")
@RequestMapping("/api/home/statistics")
public class HomeStatisticsController {
    @Autowired
    private HomeStatisticsService homeStatisticsService;
    
    @Autowired
    private BusinessStatisticsService businessStatisticsService;
    
    @Autowired
    private MemberStatisticsService memberStatisticsService;
    
    @Autowired
    private ProductStatisticsService productStatisticsService;

    @Operation(summary = "获取首页统计数据")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeStatisticsDTO> getHomeStatistics() {
        HomeStatisticsDTO statistics = homeStatisticsService.getHomeStatistics();
        return CommonResult.success(statistics);
    }

    @Operation(summary = "获取订单统计数据")
    @RequestMapping(value = "/orderStatistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeStatisticsDTO> getOrderStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        HomeStatisticsDTO statistics = homeStatisticsService.getOrderStatistics(startDate, endDate);
        return CommonResult.success(statistics);
    }

    @Operation(summary = "刷新统计数据缓存")
    @RequestMapping(value = "/refreshCache", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshStatisticsCache() {
        homeStatisticsService.refreshStatisticsCache();
        return CommonResult.success(null);
    }
    
    @Operation(summary = "获取营业数据统计")
    @RequestMapping(value = "/business", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<BusinessStatisticsVO> getBusinessStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long storeId) {
        
        BusinessStatisticsQuery query = new BusinessStatisticsQuery();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setSchoolId(schoolId);
        query.setStoreId(storeId);
        
        BusinessStatisticsVO statistics = businessStatisticsService.getBusinessStatistics(query);
        return CommonResult.success(statistics);
    }
    
    @Operation(summary = "获取会员数据统计")
    @RequestMapping(value = "/member", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<MemberStatisticsVO> getMemberStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false, defaultValue = "10") Integer topLimit) {
        
        MemberStatisticsQuery query = new MemberStatisticsQuery();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setSchoolId(schoolId);
        query.setStoreId(storeId);
        query.setTopLimit(topLimit);
        
        MemberStatisticsVO statistics = memberStatisticsService.getMemberStatistics(query);
        return CommonResult.success(statistics);
    }
    
    @Operation(summary = "获取商品销售数据统计")
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<ProductStatisticsVO> getProductStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false, defaultValue = "10") Integer topLimit,
            @RequestParam(required = false, defaultValue = "amount") String rankBy) {
        
        ProductStatisticsQuery query = new ProductStatisticsQuery();
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setSchoolId(schoolId);
        query.setStoreId(storeId);
        query.setTopLimit(topLimit);
        query.setRankBy(rankBy);
        
        ProductStatisticsVO statistics = productStatisticsService.getProductStatistics(query);
        return CommonResult.success(statistics);
    }
    
} 