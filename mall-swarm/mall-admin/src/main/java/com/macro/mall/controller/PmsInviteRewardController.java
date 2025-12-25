package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.PmsInviteRewardService;
import com.macro.mall.util.CsvExportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 邀请奖励管理Controller
 * Created by guanghengzhou on 2024/01/20.
 */
@Controller
@Tag(name = "邀请奖励管理", description = "邀请奖励相关接口")
@RequestMapping("/invite/reward")
public class PmsInviteRewardController {
    
    @Autowired
    private PmsInviteRewardService rewardService;
    
    @Operation(summary = "分页获取奖励记录列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<Map<String, Object>>> getRewardList(
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false) Long userId,
            @Parameter(description = "用户类型") @RequestParam(value = "userType", required = false) Integer userType,
            @Parameter(description = "奖励类型") @RequestParam(value = "rewardType", required = false) Integer rewardType,
            @Parameter(description = "触发类型") @RequestParam(value = "triggerType", required = false) Integer triggerType,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "佣金类型：1-邀请奖励，2-一级分销佣金，3-二级分销佣金") @RequestParam(value = "commissionType", required = false) Integer commissionType,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        List<Map<String, Object>> rewardList = rewardService.list(userId, userType, rewardType, 
                triggerType, status, commissionType, startTime, endTime, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(rewardList));
    }
    
    @Operation(summary = "获取奖励记录详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getRewardDetail(@PathVariable Long id) {
        Map<String, Object> reward = rewardService.getDetail(id);
        if (reward == null) {
            return CommonResult.failed("奖励记录不存在");
        }
        return CommonResult.success(reward);
    }
    
    @Operation(summary = "更新奖励状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRewardStatus(@PathVariable Long id,
                                          @RequestParam Integer status,
                                          @RequestParam(required = false) String remark) {
        int count = rewardService.updateStatus(id, status, remark);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "重试发放失败的奖励")
    @RequestMapping(value = "/retry/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult retryReward(@PathVariable Long id) {
        int count = rewardService.retrySendReward(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("重试失败");
    }
    
    @Operation(summary = "批量发放奖励")
    @RequestMapping(value = "/batchSend", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult batchSendReward(@RequestParam("ids") List<Long> ids) {
        int count = rewardService.batchSendReward(ids);
        return CommonResult.success(count);
    }
    
    @Operation(summary = "导出奖励记录")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public CommonResult<?> exportRewardRecords(
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false) Long userId,
            @Parameter(description = "用户类型") @RequestParam(value = "userType", required = false) Integer userType,
            @Parameter(description = "奖励类型") @RequestParam(value = "rewardType", required = false) Integer rewardType,
            @Parameter(description = "触发类型") @RequestParam(value = "triggerType", required = false) Integer triggerType,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "佣金类型") @RequestParam(value = "commissionType", required = false) Integer commissionType,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            HttpServletResponse response) throws IOException {
        
        // 判断数据规模
        long total = rewardService.countRewardRecords(userId, userType, rewardType, triggerType, status, commissionType, startTime, endTime);
        final int PAGE_SIZE = 3000;
        long threshold = 100_000; // 10万行以内同步导出
        
        // 表头映射
        LinkedHashMap<String, String> headerMap = buildExportHeader();
        
        if (total <= threshold) {
            // 同步导出：分页拉取并直接流式写出 CSV
            AtomicInteger pageNo = new AtomicInteger(1);
            List<Map<String, Object>> firstBatch = rewardService.exportRewardRecords(userId, userType, 
                    rewardType, triggerType, status, commissionType, startTime, endTime, PAGE_SIZE, pageNo.get());
            List<Map<String, Object>> firstData = convertBatchForExport(firstBatch);
            
            CsvExportUtil.exportCsv("邀请奖励记录", headerMap, firstData, () -> {
                int next = pageNo.incrementAndGet();
                List<Map<String, Object>> batch = rewardService.exportRewardRecords(userId, userType, 
                        rewardType, triggerType, status, commissionType, startTime, endTime, PAGE_SIZE, next);
                return batch.isEmpty() ? null : convertBatchForExport(batch);
            }, response);
            return null;
        } else {
            // 数据量过大，提示异步导出
            return CommonResult.success(null, "数据量过大，已转入后台异步导出，请稍后在导出列表查看");
        }
    }
    
    @Operation(summary = "获取奖励发放汇总")
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getRewardSummary() {
        Map<String, Object> summary = rewardService.getRewardSummary();
        return CommonResult.success(summary);
    }

    @Operation(summary = "获取不同佣金类型的统计数据")
    @RequestMapping(value = "/commission-statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getCommissionStatistics(
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime) {
        Map<String, Object> statistics = rewardService.getCommissionStatistics(startTime, endTime);
        return CommonResult.success(statistics);
    }

    @Operation(summary = "获取佣金趋势数据")
    @RequestMapping(value = "/commission-trend", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getCommissionTrend(
            @Parameter(description = "佣金类型") @RequestParam(value = "commissionType", required = false) Integer commissionType,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @Parameter(description = "时间粒度: day/week/month") @RequestParam(value = "granularity", defaultValue = "day") String granularity) {
        List<Map<String, Object>> trendData = rewardService.getCommissionTrend(commissionType, startTime, endTime, granularity);
        return CommonResult.success(trendData);
    }

    // =================== 导出辅助方法 ===================
    
    /**
     * 构建导出表头映射
     */
    private LinkedHashMap<String, String> buildExportHeader() {
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("id", "奖励ID");
        headerMap.put("userNickname", "用户昵称");
        headerMap.put("userPhone", "用户手机");
        headerMap.put("userTypeText", "用户类型");
        headerMap.put("inviterNickname", "邀请者昵称");
        headerMap.put("inviteeNickname", "被邀请者昵称");
        headerMap.put("rewardTypeText", "奖励类型");
        headerMap.put("rewardValue", "奖励数值");
        headerMap.put("rewardDesc", "奖励描述");
        headerMap.put("triggerTypeText", "触发类型");
        headerMap.put("triggerAmount", "触发金额");
        headerMap.put("statusText", "发放状态");
        headerMap.put("sendTime", "发放时间");
        headerMap.put("sendResult", "发放结果");
        headerMap.put("createdAt", "创建时间");
        headerMap.put("updatedAt", "更新时间");
        return headerMap;
    }

    /**
     * 转换数据批次为导出格式
     */
    private List<Map<String, Object>> convertBatchForExport(List<Map<String, Object>> batch) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 由于数据已经在Dao层处理过，这里主要是格式化日期和处理空值
        return batch.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", row.get("id"));
            map.put("userNickname", row.get("userNickname"));
            map.put("userPhone", row.get("userPhone"));
            map.put("userTypeText", row.get("userTypeText"));
            map.put("inviterNickname", row.get("inviterNickname"));
            map.put("inviteeNickname", row.get("inviteeNickname"));
            map.put("rewardTypeText", row.get("rewardTypeText"));
            map.put("rewardValue", row.get("rewardValue"));
            map.put("rewardDesc", row.get("rewardDesc"));
            map.put("triggerTypeText", row.get("triggerTypeText"));
            map.put("triggerAmount", row.get("triggerAmount"));
            map.put("statusText", row.get("statusText"));
            map.put("sendTime", formatDate(row.get("sendTime"), sdf));
            map.put("sendResult", row.get("sendResult"));
            map.put("createdAt", formatDate(row.get("createdAt"), sdf));
            map.put("updatedAt", formatDate(row.get("updatedAt"), sdf));
            return map;
        }).toList();
    }

    /**
     * 格式化日期
     */
    private String formatDate(Object date, SimpleDateFormat sdf) {
        if (date == null) {
            return "";
        }
        if (date instanceof java.util.Date) {
            return sdf.format((java.util.Date) date);
        }
        if (date instanceof String) {
            try {
                java.util.Date parsedDate = java.sql.Timestamp.valueOf((String) date);
                return sdf.format(parsedDate);
            } catch (Exception e) {
                return date.toString();
            }
        }
        return date.toString();
    }
} 