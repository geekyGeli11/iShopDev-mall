package com.macro.mall.service;

import com.macro.mall.model.PmsProductPaybackAnalysis;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品回本分析服务
 * 提供商品回本目标设置、销售数据统计、回本进度分析等功能
 * Created by guanghengzhou on 2024/01/01.
 */
public interface PmsProductPaybackService {

    /**
     * 设置商品回本目标
     * 当商品启用回本分析时，初始化或更新回本目标数据
     * 
     * @param productId 商品ID
     * @param targetQuantity 回本目标销售数量
     * @param targetAmount 回本目标金额
     * @param startDate 回本开始日期
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int setPaybackTarget(Long productId, Integer targetQuantity, BigDecimal targetAmount, String startDate);

    /**
     * 订单支付成功时更新回本分析数据
     * 在订单支付完成后调用，更新商品的销售数据和回本进度
     * 
     * @param orderId 订单ID
     * @param orderSn 订单编号
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int updateOnPaymentSuccess(Long orderId, String orderSn);

    /**
     * 订单退款时更新回本分析数据
     * 在订单退款完成后调用，减少商品的销售数据并重新计算回本进度
     * 
     * @param orderId 订单ID
     * @param orderSn 订单编号
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int updateOnRefund(Long orderId, String orderSn);

    /**
     * 手动刷新商品回本分析数据
     * 重新统计指定商品的销售数据并更新回本进度
     * 
     * @param productId 商品ID
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int refreshPaybackAnalysis(Long productId);

    /**
     * 批量刷新回本分析数据
     * 重新统计所有启用回本分析的商品销售数据
     * 
     * @return 成功刷新的商品数量
     */
    @Transactional
    int refreshAllPaybackAnalysis();

    /**
     * 获取回本分析数据列表
     * 支持按商品名称、回本状态等条件查询
     * 
     * @param keyword 商品名称关键词
     * @param paybackStatus 回本状态：0-未开始，1-回本中，2-已回本，3-销售缓慢，4-已下架
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 回本分析数据列表
     */
    List<PmsProductPaybackAnalysis> listPaybackAnalysis(String keyword, Byte paybackStatus, Integer pageNum, Integer pageSize);

    /**
     * 获取单个商品的回本分析详情
     * 
     * @param productId 商品ID
     * @return 回本分析数据，如果商品未启用回本分析返回null
     */
    PmsProductPaybackAnalysis getPaybackAnalysis(Long productId);

    /**
     * 删除商品回本分析数据
     * 当商品被删除或禁用回本分析时调用
     * 
     * @param productId 商品ID
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int deletePaybackAnalysis(Long productId);

    /**
     * 获取回本分析统计信息
     * 包括总商品数、已回本商品数、回本中商品数等统计数据
     * 
     * @return 统计信息Map，包含各种计数数据
     */
    java.util.Map<String, Object> getPaybackStatistics();

    /**
     * 统计符合条件的回本分析记录数量
     * 用于导出前判断数据规模
     * 
     * @param keyword 商品名称关键词
     * @param paybackStatus 回本状态
     * @return 记录总数
     */
    long countPaybackRecords(String keyword, Byte paybackStatus);

    /**
     * 分页导出回本分析数据
     * 用于大批量数据的流式导出
     * 
     * @param keyword 商品名称关键词
     * @param paybackStatus 回本状态
     * @param pageSize 每页大小
     * @param pageNum 页码
     * @return 导出数据列表
     */
    List<Map<String, Object>> exportPaybackRecords(String keyword, Byte paybackStatus, Integer pageSize, Integer pageNum);
} 