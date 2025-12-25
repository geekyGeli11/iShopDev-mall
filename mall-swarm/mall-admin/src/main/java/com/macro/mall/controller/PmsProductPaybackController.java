package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.PmsPaybackBatch;
import com.macro.mall.model.PmsProductPaybackAnalysis;
import com.macro.mall.service.PaybackBatchService;
import com.macro.mall.service.PaybackSalesStatisticsService;
import com.macro.mall.service.PmsProductPaybackService;
import com.macro.mall.service.impl.PaybackBatchServiceImpl;
import com.macro.mall.util.CsvExportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 商品回本分析Controller
 * Created by guanghengzhou on 2024/01/01.
 */
@Controller
@Tag(name = "PmsProductPaybackController", description = "商品回本分析管理")
@RequestMapping("/payback")
public class PmsProductPaybackController {
    
    @Autowired
    private PmsProductPaybackService paybackService;

    @Autowired
    private PaybackBatchService batchService;

    @Autowired
    private PaybackSalesStatisticsService salesStatisticsService;

    // ==================== 新版批次管理接口 ====================

    @Operation(summary = "创建补货批次")
    @RequestMapping(value = "/batch/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Long> createBatch(@Valid @RequestBody PaybackBatchCreateRequest request) {
        try {
            Long batchId = batchService.createBatch(request);
            return CommonResult.success(batchId, "创建批次成功");
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "更新补货批次")
    @RequestMapping(value = "/batch/{batchId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult<Integer> updateBatch(
            @Parameter(description = "批次ID", required = true) @PathVariable Long batchId,
            @Valid @RequestBody PaybackBatchUpdateRequest request) {
        int count = batchService.updateBatch(batchId, request);
        if (count > 0) {
            return CommonResult.success(count, "更新批次成功");
        } else {
            return CommonResult.failed("更新批次失败，批次不存在");
        }
    }

    @Operation(summary = "删除补货批次")
    @RequestMapping(value = "/batch/{batchId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult<Integer> deleteBatch(
            @Parameter(description = "批次ID", required = true) @PathVariable Long batchId) {
        // 检查是否有销售记录
        boolean hasSales = batchService.hasSalesRecords(batchId);
        int count = batchService.deleteBatch(batchId);
        if (count > 0) {
            String message = hasSales ? "删除批次成功（该批次有销售记录）" : "删除批次成功";
            return CommonResult.success(count, message);
        } else {
            return CommonResult.failed("删除批次失败，批次不存在");
        }
    }

    @Operation(summary = "强制启动批次")
    @RequestMapping(value = "/batch/{batchId}/forceStart", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> forceStartBatch(
            @Parameter(description = "批次ID", required = true) @PathVariable Long batchId) {
        try {
            int count = batchService.forceStartBatch(batchId);
            if (count > 0) {
                return CommonResult.success(count, "强制启动批次成功");
            } else {
                return CommonResult.failed("强制启动批次失败");
            }
        } catch (RuntimeException e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "获取批次列表")
    @RequestMapping(value = "/batch/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PaybackBatchVO>> listBatches(PaybackBatchQueryRequest request) {
        List<PmsPaybackBatch> batches = batchService.listBatches(request);
        long total = batchService.countBatches(request);

        // 批量获取销售统计数据
        List<Long> batchIds = new ArrayList<>();
        for (PmsPaybackBatch batch : batches) {
            batchIds.add(batch.getId());
        }
        Map<Long, BatchSalesStatistics> statsMap = salesStatisticsService.calculateBatchSalesBatch(batchIds);

        // 转换为VO并填充实时统计数据
        List<PaybackBatchVO> voList = new ArrayList<>();
        for (PmsPaybackBatch batch : batches) {
            PaybackBatchVO vo = convertToVO(batch);
            // 填充实时统计数据
            BatchSalesStatistics stats = statsMap.get(batch.getId());
            if (stats != null) {
                vo.setCurrentSoldQuantity(stats.getTotalQuantity());
                vo.setCurrentSoldAmount(stats.getTotalAmount());
                vo.setPaybackProgress(stats.getPaybackProgress());
                vo.setProfitAmount(stats.getProfitAmount());
                vo.setProfitRate(stats.getProfitRate());
            }
            voList.add(vo);
        }

        // 构建分页结果
        CommonPage<PaybackBatchVO> result = new CommonPage<>();
        result.setPageNum(request.getPageNum());
        result.setPageSize(request.getPageSize());
        result.setTotalPage((int) Math.ceil((double) total / request.getPageSize()));
        result.setTotal(total);
        result.setList(voList);

        return CommonResult.success(result);
    }

    @Operation(summary = "获取批次详情")
    @RequestMapping(value = "/batch/{batchId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PaybackBatchDetailVO> getBatchDetail(
            @Parameter(description = "批次ID", required = true) @PathVariable Long batchId) {
        PmsPaybackBatch batch = batchService.getBatchById(batchId);
        if (batch == null) {
            return CommonResult.failed("批次不存在");
        }

        // 获取实时销售统计
        BatchSalesStatistics stats = salesStatisticsService.calculateBatchSales(batchId);

        // 转换为详情VO
        PaybackBatchDetailVO detailVO = new PaybackBatchDetailVO();
        BeanUtils.copyProperties(convertToVO(batch), detailVO);
        
        // 填充实时统计数据
        detailVO.setCurrentSoldQuantity(stats.getTotalQuantity());
        detailVO.setCurrentSoldAmount(stats.getTotalAmount());
        detailVO.setPaybackProgress(stats.getPaybackProgress());
        detailVO.setProfitAmount(stats.getProfitAmount());
        detailVO.setProfitRate(stats.getProfitRate());
        detailVO.setChannelSummary(stats.getChannelData());

        // 填充销售订单明细
        detailVO.setOrderDetails(salesStatisticsService.getOrderSalesDetails(batchId));
        detailVO.setNonSystemSaleDetails(salesStatisticsService.getNonSystemSalesDetails(batchId));

        return CommonResult.success(detailVO);
    }

    @Operation(summary = "导出批次销售明细为Excel")
    @RequestMapping(value = "/batch/{batchId}/exportExcel", method = RequestMethod.GET)
    public void exportBatchSalesToExcel(
            @Parameter(description = "批次ID", required = true) @PathVariable Long batchId,
            HttpServletResponse response) {
        try {
            salesStatisticsService.exportBatchSalesToExcel(batchId, response);
        } catch (Exception e) {
            try {
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("导出失败：" + e.getMessage());
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    @Operation(summary = "刷新批次销售数据")
    @RequestMapping(value = "/batch/{batchId}/refresh", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<PaybackBatchVO> refreshBatchSales(
            @Parameter(description = "批次ID", required = true) @PathVariable Long batchId) {
        PmsPaybackBatch batch = salesStatisticsService.refreshBatchSales(batchId);
        if (batch != null) {
            return CommonResult.success(convertToVO(batch), "刷新成功");
        } else {
            return CommonResult.failed("刷新失败，批次不存在");
        }
    }

    @Operation(summary = "批量刷新所有活跃批次")
    @RequestMapping(value = "/batch/refreshAll", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> refreshAllBatches() {
        int count = salesStatisticsService.refreshAllActiveBatches();
        return CommonResult.success(count, "批量刷新完成，成功刷新 " + count + " 个批次");
    }

    @Operation(summary = "导出批次数据")
    @RequestMapping(value = "/batch/export", method = RequestMethod.GET)
    public void exportBatches(PaybackBatchQueryRequest request, HttpServletResponse response) throws IOException {
        // 获取所有符合条件的批次
        request.setPageNum(1);
        request.setPageSize(Integer.MAX_VALUE);
        List<PmsPaybackBatch> batches = batchService.listBatches(request);
        
        // 批量获取销售统计数据
        List<Long> batchIds = new ArrayList<>();
        for (PmsPaybackBatch batch : batches) {
            batchIds.add(batch.getId());
        }
        Map<Long, BatchSalesStatistics> statsMap = salesStatisticsService.calculateBatchSalesBatch(batchIds);
        
        // 构建导出数据
        List<Map<String, Object>> exportData = new ArrayList<>();
        for (PmsPaybackBatch batch : batches) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("productName", batch.getProductName());
            row.put("productSn", batch.getProductSn());
            row.put("batchNo", batch.getBatchNo());
            row.put("replenishmentQuantity", batch.getReplenishmentQuantity());
            row.put("replenishmentAmount", batch.getReplenishmentAmount());
            row.put("targetAmount", batch.getTargetAmount());
            row.put("replenishmentDate", batch.getReplenishmentDate());
            
            BatchSalesStatistics stats = statsMap.get(batch.getId());
            if (stats != null) {
                row.put("currentSoldQuantity", stats.getTotalQuantity());
                row.put("currentSoldAmount", stats.getTotalAmount());
                row.put("paybackProgress", stats.getPaybackProgress());
                row.put("profitAmount", stats.getProfitAmount());
                row.put("profitRate", stats.getProfitRate());
            } else {
                row.put("currentSoldQuantity", 0);
                row.put("currentSoldAmount", BigDecimal.ZERO);
                row.put("paybackProgress", BigDecimal.ZERO);
                row.put("profitAmount", BigDecimal.ZERO);
                row.put("profitRate", BigDecimal.ZERO);
            }
            
            row.put("batchStatus", getBatchStatusText(batch.getBatchStatus()));
            row.put("startDate", batch.getStartDate());
            row.put("completedDate", batch.getCompletedDate());
            exportData.add(row);
        }
        
        // 表头映射
        LinkedHashMap<String, String> headerMap = buildBatchExportHeader();
        
        // 导出CSV
        CsvExportUtil.exportCsv("回本分析批次数据", headerMap, exportData, () -> null, response);
    }
    
    /**
     * 构建批次导出表头映射
     */
    private LinkedHashMap<String, String> buildBatchExportHeader() {
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productName", "商品名称");
        headerMap.put("productSn", "商品货号");
        headerMap.put("batchNo", "批次序号");
        headerMap.put("replenishmentQuantity", "补货数量");
        headerMap.put("replenishmentAmount", "补货金额");
        headerMap.put("targetAmount", "回本目标");
        headerMap.put("replenishmentDate", "补货日期");
        headerMap.put("currentSoldQuantity", "已售数量");
        headerMap.put("currentSoldAmount", "已售金额");
        headerMap.put("paybackProgress", "回本进度(%)");
        headerMap.put("profitAmount", "利润金额");
        headerMap.put("profitRate", "利润率(%)");
        headerMap.put("batchStatus", "批次状态");
        headerMap.put("startDate", "开始日期");
        headerMap.put("completedDate", "完成日期");
        return headerMap;
    }
    
    /**
     * 获取批次状态文本
     */
    private String getBatchStatusText(Byte status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待启动";
            case 1: return "活跃";
            case 2: return "已回本";
            case 3: return "提前结束";
            default: return "未知";
        }
    }

    @Operation(summary = "获取批次统计信息")
    @RequestMapping(value = "/batch/statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PaybackStatisticsVO> getBatchStatistics() {
        PaybackStatisticsVO stats = new PaybackStatisticsVO();

        // 统计各状态批次数量
        PaybackBatchQueryRequest allQuery = new PaybackBatchQueryRequest();
        allQuery.setPageNum(1);
        allQuery.setPageSize(Integer.MAX_VALUE);
        stats.setTotalBatchCount((int) batchService.countBatches(allQuery));

        PaybackBatchQueryRequest activeQuery = new PaybackBatchQueryRequest();
        activeQuery.setBatchStatus((int) PaybackBatchServiceImpl.BATCH_STATUS_ACTIVE);
        activeQuery.setPageNum(1);
        activeQuery.setPageSize(Integer.MAX_VALUE);
        stats.setActiveBatchCount((int) batchService.countBatches(activeQuery));

        PaybackBatchQueryRequest completedQuery = new PaybackBatchQueryRequest();
        completedQuery.setBatchStatus((int) PaybackBatchServiceImpl.BATCH_STATUS_COMPLETED);
        completedQuery.setPageNum(1);
        completedQuery.setPageSize(Integer.MAX_VALUE);
        stats.setCompletedBatchCount((int) batchService.countBatches(completedQuery));

        PaybackBatchQueryRequest pendingQuery = new PaybackBatchQueryRequest();
        pendingQuery.setBatchStatus((int) PaybackBatchServiceImpl.BATCH_STATUS_PENDING);
        pendingQuery.setPageNum(1);
        pendingQuery.setPageSize(Integer.MAX_VALUE);
        stats.setPendingBatchCount((int) batchService.countBatches(pendingQuery));

        PaybackBatchQueryRequest earlyEndQuery = new PaybackBatchQueryRequest();
        earlyEndQuery.setBatchStatus((int) PaybackBatchServiceImpl.BATCH_STATUS_EARLY_END);
        earlyEndQuery.setPageNum(1);
        earlyEndQuery.setPageSize(Integer.MAX_VALUE);
        stats.setEarlyEndBatchCount((int) batchService.countBatches(earlyEndQuery));

        // 计算总销售金额和利润
        List<PmsPaybackBatch> allBatches = batchService.listBatches(allQuery);
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalReplenishment = BigDecimal.ZERO;
        int profitBatchCount = 0;
        BigDecimal totalProfitRate = BigDecimal.ZERO;

        for (PmsPaybackBatch batch : allBatches) {
            if (batch.getCurrentSoldAmount() != null) {
                totalSales = totalSales.add(batch.getCurrentSoldAmount());
            }
            if (batch.getProfitAmount() != null) {
                totalProfit = totalProfit.add(batch.getProfitAmount());
            }
            if (batch.getReplenishmentAmount() != null) {
                totalReplenishment = totalReplenishment.add(batch.getReplenishmentAmount());
            }
            if (batch.getProfitRate() != null && batch.getProfitRate().compareTo(BigDecimal.ZERO) != 0) {
                totalProfitRate = totalProfitRate.add(batch.getProfitRate());
                profitBatchCount++;
            }
        }

        stats.setTotalSalesAmount(totalSales);
        stats.setTotalProfitAmount(totalProfit);
        stats.setTotalReplenishmentAmount(totalReplenishment);
        stats.setAvgProfitRate(profitBatchCount > 0 
                ? totalProfitRate.divide(new BigDecimal(profitBatchCount), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO);

        return CommonResult.success(stats);
    }

    /**
     * 将批次实体转换为VO
     */
    private PaybackBatchVO convertToVO(PmsPaybackBatch batch) {
        PaybackBatchVO vo = new PaybackBatchVO();
        vo.setId(batch.getId());
        vo.setProductId(batch.getProductId());
        vo.setProductName(batch.getProductName());
        vo.setProductSn(batch.getProductSn());
        vo.setProductPic(batch.getProductPic());
        vo.setBatchNo(batch.getBatchNo());
        vo.setReplenishmentQuantity(batch.getReplenishmentQuantity());
        vo.setReplenishmentAmount(batch.getReplenishmentAmount());
        vo.setTargetAmount(batch.getTargetAmount());
        vo.setReplenishmentDate(batch.getReplenishmentDate());
        vo.setCurrentSoldQuantity(batch.getCurrentSoldQuantity());
        vo.setCurrentSoldAmount(batch.getCurrentSoldAmount());
        vo.setPaybackProgress(batch.getPaybackProgress());
        vo.setProfitAmount(batch.getProfitAmount());
        vo.setProfitRate(batch.getProfitRate());
        vo.setBatchStatus(batch.getBatchStatus() != null ? batch.getBatchStatus().intValue() : null);
        vo.setStartDate(batch.getStartDate());
        vo.setCompletedDate(batch.getCompletedDate());
        vo.setCreatedAt(batch.getCreatedAt());
        vo.setUpdatedAt(batch.getUpdatedAt());
        return vo;
    }

    // ==================== 旧版接口（保留兼容） ====================

    @Operation(summary = "设置商品回本目标")
    @RequestMapping(value = "/setTarget", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> setPaybackTarget(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "回本目标销售数量") @RequestParam(required = false) Integer targetQuantity,
            @Parameter(description = "回本目标金额") @RequestParam(required = false) BigDecimal targetAmount,
            @Parameter(description = "回本开始日期") @RequestParam String startDate) {
        int count = paybackService.setPaybackTarget(productId, targetQuantity, targetAmount, startDate);
        if (count > 0) {
            return CommonResult.success(count, "设置回本目标成功");
        } else {
            return CommonResult.failed("设置回本目标失败");
        }
    }

    @Operation(summary = "获取回本分析数据列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsProductPaybackAnalysis>> listPaybackAnalysis(
            @Parameter(description = "商品名称关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "回本状态：0-未开始，1-回本中，2-已回本，3-销售缓慢，4-已下架") @RequestParam(required = false) Byte paybackStatus,
            @Parameter(description = "页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @Parameter(description = "页大小") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<PmsProductPaybackAnalysis> analysisList = paybackService.listPaybackAnalysis(keyword, paybackStatus, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(analysisList));
    }

    @Operation(summary = "获取单个商品的回本分析详情")
    @RequestMapping(value = "/detail/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<PmsProductPaybackAnalysis> getPaybackAnalysis(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {
        PmsProductPaybackAnalysis analysis = paybackService.getPaybackAnalysis(productId);
        if (analysis != null) {
            return CommonResult.success(analysis);
        } else {
            return CommonResult.failed("该商品未启用回本分析或数据不存在");
        }
    }

    @Operation(summary = "手动刷新商品回本分析数据")
    @RequestMapping(value = "/refresh/{productId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> refreshPaybackAnalysis(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {
        int count = paybackService.refreshPaybackAnalysis(productId);
        if (count > 0) {
            return CommonResult.success(count, "刷新回本分析数据成功");
        } else {
            return CommonResult.failed("刷新回本分析数据失败");
        }
    }

    @Operation(summary = "批量刷新回本分析数据")
    @RequestMapping(value = "/refreshAll", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> refreshAllPaybackAnalysis() {
        int count = paybackService.refreshAllPaybackAnalysis();
        return CommonResult.success(count, "批量刷新完成，成功刷新 " + count + " 个商品的回本分析数据");
    }

    @Operation(summary = "删除商品回本分析数据")
    @RequestMapping(value = "/delete/{productId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> deletePaybackAnalysis(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {
        int count = paybackService.deletePaybackAnalysis(productId);
        if (count > 0) {
            return CommonResult.success(count, "删除回本分析数据成功");
        } else {
            return CommonResult.failed("删除回本分析数据失败");
        }
    }

    @Operation(summary = "获取回本分析统计信息")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getPaybackStatistics() {
        Map<String, Object> statistics = paybackService.getPaybackStatistics();
        return CommonResult.success(statistics);
    }

    @Operation(summary = "订单支付成功时更新回本分析")
    @RequestMapping(value = "/updateOnPayment", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> updateOnPaymentSuccess(
            @Parameter(description = "订单ID", required = true) @RequestParam Long orderId,
            @Parameter(description = "订单编号", required = true) @RequestParam String orderSn) {
        int count = paybackService.updateOnPaymentSuccess(orderId, orderSn);
        if (count > 0) {
            return CommonResult.success(count, "更新回本分析成功");
        } else {
            return CommonResult.failed("更新回本分析失败");
        }
    }

    @Operation(summary = "订单退款时更新回本分析")
    @RequestMapping(value = "/updateOnRefund", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> updateOnRefund(
            @Parameter(description = "订单ID", required = true) @RequestParam Long orderId,
            @Parameter(description = "订单编号", required = true) @RequestParam String orderSn) {
        int count = paybackService.updateOnRefund(orderId, orderSn);
        if (count > 0) {
            return CommonResult.success(count, "更新回本分析成功");
        } else {
            return CommonResult.failed("更新回本分析失败");
        }
    }

    @Operation(summary = "导出回本分析数据")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public CommonResult<?> exportPaybackAnalysis(
            @Parameter(description = "商品名称关键词") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "回本状态") @RequestParam(value = "paybackStatus", required = false) Byte paybackStatus,
            HttpServletResponse response) throws IOException {
        
        // 判断数据规模
        long total = paybackService.countPaybackRecords(keyword, paybackStatus);
        final int PAGE_SIZE = 3000;
        long threshold = 100_000; // 10万行以内同步导出
        
        // 表头映射
        LinkedHashMap<String, String> headerMap = buildPaybackExportHeader();
        
        if (total <= threshold) {
            // 同步导出：分页拉取并直接流式写出 CSV
            AtomicInteger pageNo = new AtomicInteger(1);
            List<Map<String, Object>> firstBatch = paybackService.exportPaybackRecords(keyword, 
                    paybackStatus, PAGE_SIZE, pageNo.get());
            
            CsvExportUtil.exportCsv("商品回本分析", headerMap, firstBatch, () -> {
                int next = pageNo.incrementAndGet();
                List<Map<String, Object>> batch = paybackService.exportPaybackRecords(keyword, 
                        paybackStatus, PAGE_SIZE, next);
                return batch.isEmpty() ? null : batch;
            }, response);
            return null;
        } else {
            // 数据量过大，提示异步导出
            return CommonResult.success(null, "数据量过大，已转入后台异步导出，请稍后在导出列表查看");
        }
    }

    /**
     * 构建回本分析导出表头映射
     */
    private LinkedHashMap<String, String> buildPaybackExportHeader() {
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("productName", "商品名称");
        headerMap.put("productSn", "商品货号");
        headerMap.put("targetQuantity", "目标数量");
        headerMap.put("targetAmount", "目标金额");
        headerMap.put("actualQuantity", "实际数量");
        headerMap.put("actualAmount", "实际金额");
        headerMap.put("paybackProgress", "完成进度(%)");
        headerMap.put("paybackStatus", "回本状态");
        headerMap.put("startDate", "开始时间");
        headerMap.put("estimatedCompletionTime", "预计完成时间");
        headerMap.put("updateTime", "最后更新时间");
        return headerMap;
    }
} 