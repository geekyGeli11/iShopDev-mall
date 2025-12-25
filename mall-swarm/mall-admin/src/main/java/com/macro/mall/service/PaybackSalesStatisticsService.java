package com.macro.mall.service;

import com.macro.mall.dto.BatchSalesStatistics;
import com.macro.mall.dto.ChannelSalesData;
import com.macro.mall.model.PmsPaybackBatch;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 销售数据统计服务接口
 * 提供批次销售数据的实时统计功能
 * Created by guanghengzhou on 2024/12/12.
 */
public interface PaybackSalesStatisticsService {

    /**
     * 实时统计批次的销售数据
     * 调用时机：查看回本分析列表/详情时
     *
     * @param batchId 批次ID
     * @return 销售统计数据（数量、金额、各渠道明细）
     */
    BatchSalesStatistics calculateBatchSales(Long batchId);

    /**
     * 批量统计多个批次的销售数据
     *
     * @param batchIds 批次ID列表
     * @return 批次ID -> 销售统计数据
     */
    Map<Long, BatchSalesStatistics> calculateBatchSalesBatch(List<Long> batchIds);

    /**
     * 刷新批次销售数据并保存到数据库
     * 调用时机：用户点击刷新按钮
     *
     * @param batchId 批次ID
     * @return 更新后的批次数据
     */
    PmsPaybackBatch refreshBatchSales(Long batchId);

    /**
     * 批量刷新所有活跃批次的销售数据
     *
     * @return 刷新的批次数量
     */
    int refreshAllActiveBatches();

    /**
     * 查询小程序和自助售卖机订单销售数据
     * 筛选条件：订单状态=待发货(1)/已发货(2)/已完成(3)
     *
     * @param productId 商品ID
     * @param startDate 开始日期（补货日期）
     * @param endDate   结束日期（回本完成日期，可为空表示到当前）
     * @return 按渠道分组的销售数据
     */
    Map<String, ChannelSalesData> queryOrderSales(Long productId, Date startDate, Date endDate);

    /**
     * 查询非系统销售数据（仅审核通过的）
     * 筛选条件：审核状态=通过(2)
     *
     * @param productId 商品ID
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 非系统销售数据
     */
    ChannelSalesData queryNonSystemSales(Long productId, Date startDate, Date endDate);

    /**
     * 获取批次的系统订单销售详情列表
     *
     * @param batchId 批次ID
     * @return 订单销售详情列表
     */
    List<com.macro.mall.dto.PaybackSalesOrderDTO> getOrderSalesDetails(Long batchId);

    /**
     * 获取批次的非系统销售详情列表
     *
     * @param batchId 批次ID
     * @return 非系统销售详情列表
     */
    List<com.macro.mall.dto.PaybackNonSystemSaleDTO> getNonSystemSalesDetails(Long batchId);

    /**
     * 导出批次销售明细为 Excel
     *
     * @param batchId  批次ID
     * @param response HTTP响应
     */
    void exportBatchSalesToExcel(Long batchId, jakarta.servlet.http.HttpServletResponse response) throws Exception;
}
