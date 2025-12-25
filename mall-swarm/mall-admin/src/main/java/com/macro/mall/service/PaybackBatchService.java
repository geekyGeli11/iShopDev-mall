package com.macro.mall.service;

import com.macro.mall.dto.PaybackBatchCreateRequest;
import com.macro.mall.dto.PaybackBatchQueryRequest;
import com.macro.mall.dto.PaybackBatchUpdateRequest;
import com.macro.mall.model.PmsPaybackBatch;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 补货批次服务接口
 * 提供批次创建、更新、删除、状态管理等功能
 * Created by guanghengzhou on 2024/12/12.
 */
public interface PaybackBatchService {

    /**
     * 创建补货批次
     * 如果商品没有活跃批次，新批次状态为活跃(1)
     * 如果商品已有活跃批次，新批次状态为待启动(0)
     *
     * @param request 批次创建请求
     * @return 新创建的批次ID
     */
    @Transactional
    Long createBatch(PaybackBatchCreateRequest request);

    /**
     * 更新补货批次
     *
     * @param batchId 批次ID
     * @param request 批次更新请求
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int updateBatch(Long batchId, PaybackBatchUpdateRequest request);

    /**
     * 删除补货批次
     * 如果删除的是活跃批次，自动将下一个待启动批次设为活跃
     *
     * @param batchId 批次ID
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int deleteBatch(Long batchId);

    /**
     * 强制启动批次
     * 将当前活跃批次标记为"提前结束"，将指定的待启动批次设为活跃
     *
     * @param batchId 要启动的批次ID
     * @return 操作结果：1-成功，0-失败
     */
    @Transactional
    int forceStartBatch(Long batchId);

    /**
     * 获取商品的活跃批次
     *
     * @param productId 商品ID
     * @return 活跃批次，如果没有返回null
     */
    PmsPaybackBatch getActiveBatch(Long productId);

    /**
     * 获取商品的下一个待启动批次（按批次序号排序）
     *
     * @param productId 商品ID
     * @return 下一个待启动批次，如果没有返回null
     */
    PmsPaybackBatch getNextPendingBatch(Long productId);

    /**
     * 批次回本完成处理
     * 将批次状态更新为已回本，并自动启动下一个待启动批次
     *
     * @param batchId 批次ID
     */
    @Transactional
    void onBatchCompleted(Long batchId);

    /**
     * 获取批次详情
     *
     * @param batchId 批次ID
     * @return 批次信息
     */
    PmsPaybackBatch getBatchById(Long batchId);

    /**
     * 分页查询批次列表
     *
     * @param request 查询请求
     * @return 批次列表
     */
    List<PmsPaybackBatch> listBatches(PaybackBatchQueryRequest request);

    /**
     * 统计批次数量
     *
     * @param request 查询请求
     * @return 批次数量
     */
    long countBatches(PaybackBatchQueryRequest request);

    /**
     * 获取商品的最大批次序号
     *
     * @param productId 商品ID
     * @return 最大批次序号，如果没有批次返回0
     */
    int getMaxBatchNo(Long productId);

    /**
     * 检查批次是否可以删除
     *
     * @param batchId 批次ID
     * @return true-可以删除，false-不可以删除
     */
    boolean canDeleteBatch(Long batchId);

    /**
     * 检查批次是否有销售记录
     *
     * @param batchId 批次ID
     * @return true-有销售记录，false-没有销售记录
     */
    boolean hasSalesRecords(Long batchId);

    /**
     * 获取商品的最后一个批次（按批次序号排序）
     *
     * @param productId 商品ID
     * @return 最后一个批次，如果没有返回null
     */
    PmsPaybackBatch getLastBatch(Long productId);
}
