package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.PmsInviteWithdrawService;
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
 * 邀请提现申请管理Controller
 * Created by guanghengzhou on 2024/01/20.
 */
@Controller
@Tag(name = "邀请提现申请管理", description = "邀请提现申请相关接口")
@RequestMapping("/invite/withdraw")
public class PmsInviteWithdrawController {
    
    @Autowired
    private PmsInviteWithdrawService withdrawService;
    
    @Operation(summary = "获取提现配置")
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getWithdrawConfig() {
        try {
            Map<String, Object> config = withdrawService.getWithdrawConfig();
            return CommonResult.success(config);
        } catch (Exception e) {
            return CommonResult.failed("获取提现配置失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "更新提现配置")
    @RequestMapping(value = "/config", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateWithdrawConfig(@RequestBody Map<String, Object> configData) {
        try {
            int updateCount = withdrawService.updateWithdrawConfig(configData);
            return CommonResult.success("配置更新成功，更新了" + updateCount + "项配置");
        } catch (Exception e) {
            return CommonResult.failed("配置更新失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "分页获取提现申请列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<Map<String, Object>>> getWithdrawList(
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false) Long userId,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "资金来源类型：1-邀请奖励，2-分销佣金，3-混合") @RequestParam(value = "sourceType", required = false) Integer sourceType,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        List<Map<String, Object>> withdrawList = withdrawService.list(userId, status, sourceType,
                startTime, endTime, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(withdrawList));
    }
    
    @Operation(summary = "获取提现申请详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getWithdrawDetail(@PathVariable Long id) {
        Map<String, Object> withdraw = withdrawService.getDetail(id);
        if (withdraw == null) {
            return CommonResult.failed("提现申请不存在");
        }
        return CommonResult.success(withdraw);
    }

    @Operation(summary = "获取提现申请的资金构成明细")
    @RequestMapping(value = "/{id}/composition", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getWithdrawComposition(@PathVariable Long id) {
        Map<String, Object> composition = withdrawService.getWithdrawComposition(id);
        if (composition == null) {
            return CommonResult.failed("提现申请不存在或无法获取资金构成");
        }
        return CommonResult.success(composition);
    }
    
    @Operation(summary = "审批提现申请")
    @RequestMapping(value = "/approve/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult approveWithdraw(@PathVariable Long id,
                                       @RequestParam Integer status,
                                       @RequestParam(required = false) String remark) {
        int count = withdrawService.approve(id, status, remark);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "批量审批提现申请")
    @RequestMapping(value = "/batchApprove", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult batchApproveWithdraw(@RequestParam("ids") List<Long> ids,
                                            @RequestParam Integer status,
                                            @RequestParam(required = false) String remark) {
        int count = withdrawService.batchApprove(ids, status, remark);
        return CommonResult.success(count);
    }
    
    @Operation(summary = "导出提现申请记录")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public CommonResult<?> exportWithdrawRecords(
            @Parameter(description = "用户ID") @RequestParam(value = "userId", required = false) Long userId,
            @Parameter(description = "状态") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "资金来源类型") @RequestParam(value = "sourceType", required = false) Integer sourceType,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            HttpServletResponse response) throws IOException {
        
        // 判断数据规模
        long total = withdrawService.countWithdrawRecords(userId, status, sourceType, startTime, endTime);
        final int PAGE_SIZE = 3000;
        long threshold = 100_000; // 10万行以内同步导出
        
        // 表头映射
        LinkedHashMap<String, String> headerMap = buildWithdrawExportHeader();
        
        if (total <= threshold) {
            // 同步导出：分页拉取并直接流式写出 CSV
            AtomicInteger pageNo = new AtomicInteger(1);
            List<Map<String, Object>> firstBatch = withdrawService.exportWithdrawRecords(userId, 
                    status, sourceType, startTime, endTime, PAGE_SIZE, pageNo.get());
            List<Map<String, Object>> firstData = convertWithdrawBatchForExport(firstBatch);
            
            CsvExportUtil.exportCsv("提现申请记录", headerMap, firstData, () -> {
                int next = pageNo.incrementAndGet();
                List<Map<String, Object>> batch = withdrawService.exportWithdrawRecords(userId, 
                        status, sourceType, startTime, endTime, PAGE_SIZE, next);
                return batch.isEmpty() ? null : convertWithdrawBatchForExport(batch);
            }, response);
            return null;
        } else {
            // 数据量过大，提示异步导出
            return CommonResult.success(null, "数据量过大，已转入后台异步导出，请稍后在导出列表查看");
        }
    }
    
    @Operation(summary = "获取提现申请汇总")
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getWithdrawSummary() {
        Map<String, Object> summary = withdrawService.getWithdrawSummary();
        return CommonResult.success(summary);
    }

    @Operation(summary = "获取提现资金来源统计")
    @RequestMapping(value = "/source-statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getSourceStatistics(
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime) {
        Map<String, Object> statistics = withdrawService.getSourceStatistics(startTime, endTime);
        return CommonResult.success(statistics);
    }

    @Operation(summary = "获取用户可提现金额详情")
    @RequestMapping(value = "/available-amount/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getUserAvailableAmount(@PathVariable Long userId) {
        Map<String, Object> availableAmount = withdrawService.getUserAvailableAmount(userId);
        return CommonResult.success(availableAmount);
    }

    @Operation(summary = "获取提现趋势分析")
    @RequestMapping(value = "/trend-analysis", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getWithdrawTrend(
            @Parameter(description = "资金来源类型") @RequestParam(value = "sourceType", required = false) Integer sourceType,
            @Parameter(description = "开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @Parameter(description = "时间粒度: day/week/month") @RequestParam(value = "granularity", defaultValue = "day") String granularity) {
        List<Map<String, Object>> trendData = withdrawService.getWithdrawTrend(sourceType, startTime, endTime, granularity);
        return CommonResult.success(trendData);
    }

    // =================== 导出辅助方法 ===================
    
    /**
     * 构建提现申请导出表头映射
     */
    private LinkedHashMap<String, String> buildWithdrawExportHeader() {
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("id", "申请ID");
        headerMap.put("withdrawSn", "提现单号");
        headerMap.put("userNickname", "用户昵称");
        headerMap.put("userPhone", "用户手机");
        headerMap.put("applyAmount", "申请金额");
        headerMap.put("feeAmount", "手续费");
        headerMap.put("actualAmount", "实际到账金额");
        headerMap.put("withdrawTypeText", "提现方式");
        headerMap.put("withdrawAccount", "提现账户");
        headerMap.put("accountName", "账户姓名");
        headerMap.put("statusText", "申请状态");
        headerMap.put("applyTime", "申请时间");
        headerMap.put("auditTime", "审核时间");
        headerMap.put("auditUser", "审核人");
        headerMap.put("auditRemark", "审核备注");
        headerMap.put("processTime", "处理时间");
        headerMap.put("processUser", "处理人");
        headerMap.put("processRemark", "处理备注");
        headerMap.put("failureReason", "失败原因");
        headerMap.put("expectedArrivalTime", "预计到账时间");
        headerMap.put("actualArrivalTime", "实际到账时间");
        headerMap.put("remark", "申请备注");
        return headerMap;
    }

    /**
     * 转换提现申请数据批次为导出格式
     */
    private List<Map<String, Object>> convertWithdrawBatchForExport(List<Map<String, Object>> batch) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        // 由于数据已经在Dao层处理过，这里主要是格式化日期和处理空值
        return batch.stream().map(row -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", row.get("id"));
            map.put("withdrawSn", row.get("withdrawSn"));
            map.put("userNickname", row.get("userNickname"));
            map.put("userPhone", row.get("userPhone"));
            map.put("applyAmount", row.get("applyAmount"));
            map.put("feeAmount", row.get("feeAmount"));
            map.put("actualAmount", row.get("actualAmount"));
            map.put("withdrawTypeText", row.get("withdrawTypeText"));
            map.put("withdrawAccount", row.get("withdrawAccount"));
            map.put("accountName", row.get("accountName"));
            map.put("statusText", row.get("statusText"));
            map.put("applyTime", formatWithdrawDate(row.get("applyTime"), sdf));
            map.put("auditTime", formatWithdrawDate(row.get("auditTime"), sdf));
            map.put("auditUser", row.get("auditUser"));
            map.put("auditRemark", row.get("auditRemark"));
            map.put("processTime", formatWithdrawDate(row.get("processTime"), sdf));
            map.put("processUser", row.get("processUser"));
            map.put("processRemark", row.get("processRemark"));
            map.put("failureReason", row.get("failureReason"));
            map.put("expectedArrivalTime", formatWithdrawDate(row.get("expectedArrivalTime"), sdf));
            map.put("actualArrivalTime", formatWithdrawDate(row.get("actualArrivalTime"), sdf));
            map.put("remark", row.get("remark"));
            return map;
        }).toList();
    }

    /**
     * 格式化提现申请日期
     */
    private String formatWithdrawDate(Object date, SimpleDateFormat sdf) {
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