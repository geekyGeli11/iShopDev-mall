package com.macro.mall.service.impl;

import com.macro.mall.dto.BatchSalesStatistics;
import com.macro.mall.dto.ChannelSalesData;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.PaybackBatchService;
import com.macro.mall.service.PaybackSalesStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 销售数据统计服务实现类
 * Created by guanghengzhou on 2024/12/12.
 */
@Service
public class PaybackSalesStatisticsServiceImpl implements PaybackSalesStatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaybackSalesStatisticsServiceImpl.class);

    // 订单来源类型
    private static final int SOURCE_TYPE_MINIPROGRAM = 1;  // 小程序
    private static final int SOURCE_TYPE_SELFCHECK = 2;    // 自助设备

    // 订单状态：待发货、已发货、已完成
    private static final List<Integer> VALID_ORDER_STATUS = Arrays.asList(1, 2, 3);

    // 非系统销售状态：已审核
    private static final byte NON_SYSTEM_SALE_APPROVED = 2;

    // 渠道标识
    private static final String CHANNEL_MINIPROGRAM = "miniprogram";
    private static final String CHANNEL_SELFCHECK = "selfcheck";
    private static final String CHANNEL_NON_SYSTEM = "non_system";

    @Autowired
    private PmsPaybackBatchMapper batchMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Autowired
    private PmsNonSystemSaleMapper nonSystemSaleMapper;

    @Autowired
    private PmsNonSystemSaleItemMapper nonSystemSaleItemMapper;

    @Autowired
    private PaybackBatchService batchService;

    @Override
    public BatchSalesStatistics calculateBatchSales(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return new BatchSalesStatistics();
        }

        BatchSalesStatistics stats = new BatchSalesStatistics();

        // 确定统计时间范围
        Date startDate = batch.getReplenishmentDate();
        Date endDate = batch.getCompletedDate(); // 如果未完成，endDate为null，表示到当前

        // 1. 查询订单销售数据
        Map<String, ChannelSalesData> orderSales = queryOrderSales(batch.getProductId(), startDate, endDate);
        for (ChannelSalesData channelData : orderSales.values()) {
            stats.addChannelData(channelData.getChannel(), channelData.getQuantity(), channelData.getAmount());
        }

        // 2. 查询非系统销售数据（始终显示该渠道，即使数量为0）
        ChannelSalesData nonSystemSales = queryNonSystemSales(batch.getProductId(), startDate, endDate);
        if (nonSystemSales != null) {
            stats.addChannelData(nonSystemSales.getChannel(), nonSystemSales.getQuantity(), nonSystemSales.getAmount());
        }

        // 3. 计算利润和进度
        stats.calculateProfitAndProgress(batch.getReplenishmentAmount(), batch.getTargetAmount());

        return stats;
    }

    @Override
    public Map<Long, BatchSalesStatistics> calculateBatchSalesBatch(List<Long> batchIds) {
        Map<Long, BatchSalesStatistics> result = new HashMap<>();
        for (Long batchId : batchIds) {
            result.put(batchId, calculateBatchSales(batchId));
        }
        return result;
    }

    @Override
    public PmsPaybackBatch refreshBatchSales(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return null;
        }

        // 计算销售数据
        BatchSalesStatistics stats = calculateBatchSales(batchId);

        // 更新批次数据
        PmsPaybackBatch updateBatch = new PmsPaybackBatch();
        updateBatch.setId(batchId);
        updateBatch.setCurrentSoldQuantity(stats.getTotalQuantity());
        updateBatch.setCurrentSoldAmount(stats.getTotalAmount());
        updateBatch.setPaybackProgress(stats.getPaybackProgress());
        updateBatch.setProfitAmount(stats.getProfitAmount());
        updateBatch.setProfitRate(stats.getProfitRate());
        updateBatch.setUpdatedAt(new Date());

        batchMapper.updateByPrimaryKeySelective(updateBatch);

        // 检查是否回本完成
        if (stats.getPaybackProgress().compareTo(new BigDecimal("100")) >= 0 
                && batch.getBatchStatus() == PaybackBatchServiceImpl.BATCH_STATUS_ACTIVE) {
            batchService.onBatchCompleted(batchId);
        }

        LOGGER.info("刷新批次销售数据，批次ID: {}, 销售数量: {}, 销售金额: {}, 回本进度: {}%",
                batchId, stats.getTotalQuantity(), stats.getTotalAmount(), stats.getPaybackProgress());

        return batchMapper.selectByPrimaryKey(batchId);
    }

    @Override
    public int refreshAllActiveBatches() {
        // 查询所有活跃批次
        PmsPaybackBatchExample example = new PmsPaybackBatchExample();
        example.createCriteria().andBatchStatusEqualTo(PaybackBatchServiceImpl.BATCH_STATUS_ACTIVE);
        List<PmsPaybackBatch> activeBatches = batchMapper.selectByExample(example);

        int count = 0;
        for (PmsPaybackBatch batch : activeBatches) {
            try {
                refreshBatchSales(batch.getId());
                count++;
            } catch (Exception e) {
                LOGGER.error("刷新批次销售数据失败，批次ID: {}", batch.getId(), e);
            }
        }

        LOGGER.info("批量刷新活跃批次完成，共刷新 {} 个批次", count);
        return count;
    }

    @Override
    public Map<String, ChannelSalesData> queryOrderSales(Long productId, Date startDate, Date endDate) {
        Map<String, ChannelSalesData> result = new HashMap<>();

        // 初始化渠道数据
        result.put(CHANNEL_MINIPROGRAM, new ChannelSalesData(CHANNEL_MINIPROGRAM, "小程序", 0, BigDecimal.ZERO));
        result.put(CHANNEL_SELFCHECK, new ChannelSalesData(CHANNEL_SELFCHECK, "自助售卖机", 0, BigDecimal.ZERO));

        // 查询符合条件的订单
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andStatusIn(VALID_ORDER_STATUS);
        if (startDate != null) {
            criteria.andPaymentTimeGreaterThanOrEqualTo(startDate);
        }
        if (endDate != null) {
            criteria.andPaymentTimeLessThanOrEqualTo(endDate);
        }

        List<OmsOrder> orders = orderMapper.selectByExample(orderExample);
        if (orders.isEmpty()) {
            return result;
        }

        // 获取订单ID列表
        List<Long> orderIds = new ArrayList<>();
        Map<Long, Integer> orderSourceMap = new HashMap<>();
        for (OmsOrder order : orders) {
            orderIds.add(order.getId());
            orderSourceMap.put(order.getId(), order.getSourceType());
        }

        // 查询订单项中包含指定商品的记录
        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria()
                .andOrderIdIn(orderIds)
                .andProductIdEqualTo(productId);
        List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(itemExample);

        // 按渠道汇总
        for (OmsOrderItem item : orderItems) {
            Integer sourceType = orderSourceMap.get(item.getOrderId());
            String channel = getChannelBySourceType(sourceType);
            
            ChannelSalesData channelData = result.get(channel);
            if (channelData != null) {
                channelData.setQuantity(channelData.getQuantity() + item.getProductQuantity());
                BigDecimal amount = item.getRealAmount() != null ? item.getRealAmount() : BigDecimal.ZERO;
                channelData.setAmount(channelData.getAmount().add(amount));
            }
        }

        return result;
    }

    @Override
    public ChannelSalesData queryNonSystemSales(Long productId, Date startDate, Date endDate) {
        ChannelSalesData result = new ChannelSalesData(CHANNEL_NON_SYSTEM, "非系统销售", 0, BigDecimal.ZERO);

        // 查询已审核的非系统销售单
        PmsNonSystemSaleExample saleExample = new PmsNonSystemSaleExample();
        PmsNonSystemSaleExample.Criteria criteria = saleExample.createCriteria();
        criteria.andStatusEqualTo(NON_SYSTEM_SALE_APPROVED);
        if (startDate != null) {
            criteria.andSaleDateGreaterThanOrEqualTo(startDate);
        }
        if (endDate != null) {
            criteria.andSaleDateLessThanOrEqualTo(endDate);
        }

        List<PmsNonSystemSale> sales = nonSystemSaleMapper.selectByExample(saleExample);
        if (sales.isEmpty()) {
            return result;
        }

        // 获取销售单ID列表
        List<Long> saleIds = new ArrayList<>();
        for (PmsNonSystemSale sale : sales) {
            saleIds.add(sale.getId());
        }

        // 查询销售明细中包含指定商品的记录
        PmsNonSystemSaleItemExample itemExample = new PmsNonSystemSaleItemExample();
        itemExample.createCriteria()
                .andSaleIdIn(saleIds)
                .andProductIdEqualTo(productId);
        List<PmsNonSystemSaleItem> saleItems = nonSystemSaleItemMapper.selectByExample(itemExample);

        // 汇总
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PmsNonSystemSaleItem item : saleItems) {
            totalQuantity += item.getQuantity() != null ? item.getQuantity() : 0;
            totalAmount = totalAmount.add(item.getLineAmount() != null ? item.getLineAmount() : BigDecimal.ZERO);
        }

        result.setQuantity(totalQuantity);
        result.setAmount(totalAmount);

        return result;
    }

    /**
     * 根据订单来源类型获取渠道标识
     */
    private String getChannelBySourceType(Integer sourceType) {
        if (sourceType == null) {
            return CHANNEL_MINIPROGRAM;
        }
        switch (sourceType) {
            case SOURCE_TYPE_SELFCHECK:
                return CHANNEL_SELFCHECK;
            case SOURCE_TYPE_MINIPROGRAM:
            default:
                return CHANNEL_MINIPROGRAM;
        }
    }

    @Override
    public List<com.macro.mall.dto.PaybackSalesOrderDTO> getOrderSalesDetails(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return new ArrayList<>();
        }

        Date startDate = batch.getReplenishmentDate();
        Date endDate = batch.getCompletedDate();
        Long productId = batch.getProductId();

        List<com.macro.mall.dto.PaybackSalesOrderDTO> result = new ArrayList<>();

        // 查询符合条件的订单
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andStatusIn(VALID_ORDER_STATUS);
        if (startDate != null) {
            criteria.andPaymentTimeGreaterThanOrEqualTo(startDate);
        }
        if (endDate != null) {
            criteria.andPaymentTimeLessThanOrEqualTo(endDate);
        }
        orderExample.setOrderByClause("payment_time DESC");

        List<OmsOrder> orders = orderMapper.selectByExample(orderExample);
        if (orders.isEmpty()) {
            return result;
        }

        // 获取订单ID列表和订单映射
        List<Long> orderIds = new ArrayList<>();
        Map<Long, OmsOrder> orderMap = new HashMap<>();
        for (OmsOrder order : orders) {
            orderIds.add(order.getId());
            orderMap.put(order.getId(), order);
        }

        // 查询订单项中包含指定商品的记录
        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria()
                .andOrderIdIn(orderIds)
                .andProductIdEqualTo(productId);
        List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(itemExample);

        // 构建结果（按订单分组，每个订单只计算一次）
        Set<Long> processedOrderIds = new HashSet<>();
        for (OmsOrderItem item : orderItems) {
            Long orderId = item.getOrderId();
            if (processedOrderIds.contains(orderId)) {
                continue; // 同一订单只计算一次
            }
            processedOrderIds.add(orderId);

            OmsOrder order = orderMap.get(orderId);
            if (order == null) continue;

            com.macro.mall.dto.PaybackSalesOrderDTO dto = new com.macro.mall.dto.PaybackSalesOrderDTO();
            dto.setOrderId(order.getId());
            dto.setOrderSn(order.getOrderSn());
            dto.setSourceType(order.getSourceType());
            dto.setProductQuantity(item.getProductQuantity());
            dto.setProductAmount(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
            dto.setPayAmount(item.getRealAmount());
            dto.setPaymentTime(order.getPaymentTime());
            dto.setOrderStatus(order.getStatus());
            dto.setReceiverName(order.getReceiverName());
            dto.setReceiverPhone(order.getReceiverPhone());
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<com.macro.mall.dto.PaybackNonSystemSaleDTO> getNonSystemSalesDetails(Long batchId) {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            return new ArrayList<>();
        }

        Date startDate = batch.getReplenishmentDate();
        Date endDate = batch.getCompletedDate();
        Long productId = batch.getProductId();

        List<com.macro.mall.dto.PaybackNonSystemSaleDTO> result = new ArrayList<>();

        // 查询已审核的非系统销售单
        PmsNonSystemSaleExample saleExample = new PmsNonSystemSaleExample();
        PmsNonSystemSaleExample.Criteria criteria = saleExample.createCriteria();
        criteria.andStatusEqualTo(NON_SYSTEM_SALE_APPROVED);
        if (startDate != null) {
            criteria.andSaleDateGreaterThanOrEqualTo(startDate);
        }
        if (endDate != null) {
            criteria.andSaleDateLessThanOrEqualTo(endDate);
        }
        saleExample.setOrderByClause("sale_date DESC");

        List<PmsNonSystemSale> sales = nonSystemSaleMapper.selectByExample(saleExample);
        if (sales.isEmpty()) {
            return result;
        }

        // 获取销售单ID列表和映射
        List<Long> saleIds = new ArrayList<>();
        Map<Long, PmsNonSystemSale> saleMap = new HashMap<>();
        for (PmsNonSystemSale sale : sales) {
            saleIds.add(sale.getId());
            saleMap.put(sale.getId(), sale);
        }

        // 查询销售明细中包含指定商品的记录
        PmsNonSystemSaleItemExample itemExample = new PmsNonSystemSaleItemExample();
        itemExample.createCriteria()
                .andSaleIdIn(saleIds)
                .andProductIdEqualTo(productId);
        List<PmsNonSystemSaleItem> saleItems = nonSystemSaleItemMapper.selectByExample(itemExample);

        // 构建结果（按销售单分组）
        Set<Long> processedSaleIds = new HashSet<>();
        for (PmsNonSystemSaleItem item : saleItems) {
            Long saleId = item.getSaleId();
            if (processedSaleIds.contains(saleId)) {
                continue;
            }
            processedSaleIds.add(saleId);

            PmsNonSystemSale sale = saleMap.get(saleId);
            if (sale == null) continue;

            com.macro.mall.dto.PaybackNonSystemSaleDTO dto = new com.macro.mall.dto.PaybackNonSystemSaleDTO();
            dto.setSaleId(sale.getId());
            dto.setSaleNo(sale.getSaleNo());
            dto.setSaleTypeName(sale.getSaleTypeName());
            dto.setQuantity(item.getQuantity());
            dto.setAmount(item.getLineAmount());
            dto.setSaleDate(sale.getSaleDate());
            dto.setOperatorName(sale.getOperatorName());
            dto.setRemark(sale.getRemark());
            result.add(dto);
        }

        return result;
    }

    @Override
    public void exportBatchSalesToExcel(Long batchId, jakarta.servlet.http.HttpServletResponse response) throws Exception {
        PmsPaybackBatch batch = batchMapper.selectByPrimaryKey(batchId);
        if (batch == null) {
            throw new IllegalArgumentException("批次不存在");
        }

        // 获取销售详情
        List<com.macro.mall.dto.PaybackSalesOrderDTO> orderDetails = getOrderSalesDetails(batchId);
        List<com.macro.mall.dto.PaybackNonSystemSaleDTO> nonSystemDetails = getNonSystemSalesDetails(batchId);

        // 计算统计数据
        BatchSalesStatistics stats = calculateBatchSales(batchId);
        Map<String, ChannelSalesData> channelData = stats.getChannelData();

        // 创建 Excel 工作簿
        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("回本分析销售明细");

        // 设置列宽
        for (int i = 0; i < 8; i++) {
            sheet.setColumnWidth(i, 18 * 256);
        }

        // 创建样式
        org.apache.poi.ss.usermodel.CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);

        org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        int rowNum = 0;

        // 标题
        org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("回本分析销售明细 - " + batch.getProductName() + " (第" + batch.getBatchNo() + "批)");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));

        // 基本信息
        rowNum++;
        org.apache.poi.ss.usermodel.Row infoRow1 = sheet.createRow(rowNum++);
        infoRow1.createCell(0).setCellValue("商品名称：");
        infoRow1.createCell(1).setCellValue(batch.getProductName());
        infoRow1.createCell(2).setCellValue("商品货号：");
        infoRow1.createCell(3).setCellValue(batch.getProductSn());

        org.apache.poi.ss.usermodel.Row infoRow2 = sheet.createRow(rowNum++);
        infoRow2.createCell(0).setCellValue("补货数量：");
        infoRow2.createCell(1).setCellValue(batch.getReplenishmentQuantity());
        infoRow2.createCell(2).setCellValue("补货金额：");
        infoRow2.createCell(3).setCellValue(batch.getReplenishmentAmount() != null ? batch.getReplenishmentAmount().doubleValue() : 0);

        org.apache.poi.ss.usermodel.Row infoRow3 = sheet.createRow(rowNum++);
        infoRow3.createCell(0).setCellValue("回本目标：");
        infoRow3.createCell(1).setCellValue(batch.getTargetAmount() != null ? batch.getTargetAmount().doubleValue() : 0);
        infoRow3.createCell(2).setCellValue("补货日期：");
        infoRow3.createCell(3).setCellValue(formatDateTime(batch.getReplenishmentDate()));

        org.apache.poi.ss.usermodel.Row infoRow4 = sheet.createRow(rowNum++);
        infoRow4.createCell(0).setCellValue("已售数量：");
        infoRow4.createCell(1).setCellValue(stats.getTotalQuantity());
        infoRow4.createCell(2).setCellValue("已售金额：");
        infoRow4.createCell(3).setCellValue(stats.getTotalAmount() != null ? stats.getTotalAmount().doubleValue() : 0);

        org.apache.poi.ss.usermodel.Row infoRow5 = sheet.createRow(rowNum++);
        infoRow5.createCell(0).setCellValue("利润金额：");
        infoRow5.createCell(1).setCellValue(stats.getProfitAmount() != null ? stats.getProfitAmount().doubleValue() : 0);
        infoRow5.createCell(2).setCellValue("利润率：");
        infoRow5.createCell(3).setCellValue((stats.getProfitRate() != null ? stats.getProfitRate().doubleValue() : 0) + "%");

        // 系统订单明细
        rowNum += 2;
        org.apache.poi.ss.usermodel.Row orderTitleRow = sheet.createRow(rowNum++);
        orderTitleRow.createCell(0).setCellValue("【系统订单销售明细】");

        org.apache.poi.ss.usermodel.Row orderHeaderRow = sheet.createRow(rowNum++);
        String[] orderHeaders = {"订单编号", "订单来源", "商品数量", "实付金额", "支付时间", "订单状态", "收货人", "收货电话"};
        for (int i = 0; i < orderHeaders.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = orderHeaderRow.createCell(i);
            cell.setCellValue(orderHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        for (com.macro.mall.dto.PaybackSalesOrderDTO order : orderDetails) {
            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(order.getOrderSn());
            dataRow.createCell(1).setCellValue(order.getSourceTypeName());
            dataRow.createCell(2).setCellValue(order.getProductQuantity() != null ? order.getProductQuantity() : 0);
            dataRow.createCell(3).setCellValue(order.getPayAmount() != null ? order.getPayAmount().doubleValue() : 0);
            dataRow.createCell(4).setCellValue(formatDateTime(order.getPaymentTime()));
            dataRow.createCell(5).setCellValue(order.getOrderStatusName());
            dataRow.createCell(6).setCellValue(order.getReceiverName());
            dataRow.createCell(7).setCellValue(order.getReceiverPhone());
        }

        // 非系统销售明细
        rowNum += 2;
        org.apache.poi.ss.usermodel.Row nonSystemTitleRow = sheet.createRow(rowNum++);
        nonSystemTitleRow.createCell(0).setCellValue("【非系统销售明细】");

        org.apache.poi.ss.usermodel.Row nonSystemHeaderRow = sheet.createRow(rowNum++);
        String[] nonSystemHeaders = {"销售单号", "销售类型", "商品数量", "销售金额", "销售日期", "操作人", "备注", ""};
        for (int i = 0; i < nonSystemHeaders.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = nonSystemHeaderRow.createCell(i);
            cell.setCellValue(nonSystemHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        for (com.macro.mall.dto.PaybackNonSystemSaleDTO sale : nonSystemDetails) {
            org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(sale.getSaleNo());
            dataRow.createCell(1).setCellValue(sale.getSaleTypeName());
            dataRow.createCell(2).setCellValue(sale.getQuantity() != null ? sale.getQuantity() : 0);
            dataRow.createCell(3).setCellValue(sale.getAmount() != null ? sale.getAmount().doubleValue() : 0);
            dataRow.createCell(4).setCellValue(formatDateTime(sale.getSaleDate()));
            dataRow.createCell(5).setCellValue(sale.getOperatorName());
            dataRow.createCell(6).setCellValue(sale.getRemark());
        }

        // 设置响应头
        String fileName = "回本分析_" + batch.getProductName() + "_第" + batch.getBatchNo() + "批.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));

        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private String formatDateTime(Date date) {
        if (date == null) return "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
