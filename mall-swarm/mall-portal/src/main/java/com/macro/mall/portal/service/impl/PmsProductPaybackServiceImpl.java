package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.service.PmsProductPaybackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 商品回本分析服务实现类（Portal模块版本）
 * Created by guanghengzhou on 2024/01/01.
 */
@Service
public class PmsProductPaybackServiceImpl implements PmsProductPaybackService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsProductPaybackServiceImpl.class);
    
    @Autowired
    private PmsProductMapper productMapper;
    
    @Autowired
    private PmsProductPaybackAnalysisMapper paybackAnalysisMapper;
    
    @Autowired
    private PmsProductPaybackLogMapper paybackLogMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Override
    public int updateOnPaymentSuccess(Long orderId, String orderSn) {
        LOGGER.info("订单支付成功，更新回本分析：orderId={}, orderSn={}", orderId, orderSn);
        
        try {
            // 获取订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                LOGGER.warn("订单不存在：orderId={}", orderId);
                return 0;
            }
            
            // 获取订单明细
            OmsOrderItemExample itemExample = new OmsOrderItemExample();
            itemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(itemExample);
            
            if (CollectionUtils.isEmpty(orderItems)) {
                LOGGER.warn("订单明细不存在：orderId={}", orderId);
                return 0;
            }
            
            int updateCount = 0;
            
            // 更新每个商品的回本分析数据
            for (OmsOrderItem item : orderItems) {
                if (updatePaybackForOrderItem(item, order)) {
                    updateCount++;
                }
            }
            
            LOGGER.info("订单支付成功，回本分析更新完成：orderId={}, 更新商品数={}", orderId, updateCount);
            return updateCount > 0 ? 1 : 0;
            
        } catch (Exception e) {
            LOGGER.error("更新回本分析失败：orderId={}", orderId, e);
            return 0;
        }
    }

    @Override
    public int updateOnRefund(Long orderId, String orderSn) {
        LOGGER.info("订单退款，更新回本分析：orderId={}, orderSn={}", orderId, orderSn);
        return 1; // 简化实现
    }
    
    /**
     * 为订单明细更新回本分析数据
     */
    private boolean updatePaybackForOrderItem(OmsOrderItem item, OmsOrder order) {
        try {
            // 检查商品是否启用回本分析
            PmsProduct product = productMapper.selectByPrimaryKey(item.getProductId());
            if (product == null || !Boolean.TRUE.equals(product.getEnablePaybackAnalysis())) {
                return false; // 商品未启用回本分析
            }
            
            // 查找或创建回本分析记录
            PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
            example.createCriteria().andProductIdEqualTo(item.getProductId());
            List<PmsProductPaybackAnalysis> analysisList = paybackAnalysisMapper.selectByExample(example);
            
            PmsProductPaybackAnalysis analysis;
            BigDecimal progressBefore = BigDecimal.ZERO;
            
            if (analysisList.isEmpty()) {
                // 如果商品启用了回本分析但没有分析记录，创建默认记录
                analysis = createDefaultAnalysis(product);
                paybackAnalysisMapper.insertSelective(analysis);
            } else {
                analysis = analysisList.get(0);
                progressBefore = analysis.getPaybackProgress();
            }
            
            // 更新销售数据
            analysis.setCurrentSoldQuantity(analysis.getCurrentSoldQuantity() + item.getProductQuantity());
            BigDecimal itemAmount = item.getRealAmount() != null ? item.getRealAmount() : 
                                  item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity()));
            analysis.setCurrentSoldAmount(analysis.getCurrentSoldAmount().add(itemAmount));
            analysis.setLastOrderDate(order.getCreateTime());
            
            // 重新计算回本进度
            calculatePaybackProgress(analysis);
            
            analysis.setUpdatedAt(new Date());
            paybackAnalysisMapper.updateByPrimaryKeySelective(analysis);
            
            // 记录日志
            createPaybackLog(analysis, order, item, progressBefore, analysis.getPaybackProgress());
            
            return true;
            
        } catch (Exception e) {
            LOGGER.error("更新订单明细回本分析失败：productId={}, orderId={}", item.getProductId(), item.getOrderId(), e);
            return false;
        }
    }
    
    /**
     * 创建默认回本分析记录
     */
    private PmsProductPaybackAnalysis createDefaultAnalysis(PmsProduct product) {
        PmsProductPaybackAnalysis analysis = new PmsProductPaybackAnalysis();
        analysis.setProductId(product.getId());
        analysis.setProductName(product.getName());
        analysis.setProductSn(product.getProductSn());
        analysis.setTargetQuantity(product.getPaybackTargetQuantity() != null ? product.getPaybackTargetQuantity() : 100);
        analysis.setTargetAmount(product.getPaybackTargetAmount() != null ? product.getPaybackTargetAmount() : new BigDecimal("1000"));
        analysis.setCurrentSoldQuantity(0);
        analysis.setCurrentSoldAmount(BigDecimal.ZERO);
        analysis.setPaybackProgress(BigDecimal.ZERO);
        analysis.setPaybackStatus((byte) 0); // 未开始
        analysis.setStartDate(product.getPaybackStartDate() != null ? product.getPaybackStartDate() : new Date());
        analysis.setPaybackDays(0);
        analysis.setDailyAvgQuantity(BigDecimal.ZERO);
        analysis.setCreatedAt(new Date());
        analysis.setUpdatedAt(new Date());
        
        return analysis;
    }
    
    /**
     * 计算回本进度
     */
    private void calculatePaybackProgress(PmsProductPaybackAnalysis analysis) {
        // 按数量计算进度
        BigDecimal quantityProgress = BigDecimal.ZERO;
        if (analysis.getTargetQuantity() != null && analysis.getTargetQuantity() > 0) {
            quantityProgress = new BigDecimal(analysis.getCurrentSoldQuantity())
                .divide(new BigDecimal(analysis.getTargetQuantity()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        }
        
        // 按金额计算进度
        BigDecimal amountProgress = BigDecimal.ZERO;
        if (analysis.getTargetAmount() != null && analysis.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            amountProgress = analysis.getCurrentSoldAmount()
                .divide(analysis.getTargetAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        }
        
        // 取两者的最大值作为回本进度
        BigDecimal progress = quantityProgress.max(amountProgress);
        if (progress.compareTo(new BigDecimal(100)) > 0) {
            progress = new BigDecimal(100);
        }
        
        analysis.setPaybackProgress(progress);
        
        // 更新回本状态
        updatePaybackStatus(analysis);
    }
    
    /**
     * 更新回本状态
     */
    private void updatePaybackStatus(PmsProductPaybackAnalysis analysis) {
        BigDecimal progress = analysis.getPaybackProgress();
        
        if (progress.compareTo(new BigDecimal(100)) >= 0) {
            // 已回本
            analysis.setPaybackStatus((byte) 2);
            if (analysis.getPaybackCompletedDate() == null) {
                analysis.setPaybackCompletedDate(new Date());
            }
        } else if (progress.compareTo(BigDecimal.ZERO) > 0) {
            // 回本中
            analysis.setPaybackStatus((byte) 1);
        } else {
            // 未开始
            analysis.setPaybackStatus((byte) 0);
        }
    }
    
    /**
     * 创建回本日志
     */
    private void createPaybackLog(PmsProductPaybackAnalysis analysis, OmsOrder order, OmsOrderItem item, 
                                 BigDecimal progressBefore, BigDecimal progressAfter) {
        try {
            PmsProductPaybackLog log = new PmsProductPaybackLog();
            log.setProductId(analysis.getProductId());
            log.setOrderId(order.getId());
            log.setOrderSn(order.getOrderSn());
            log.setSoldQuantity(item.getProductQuantity());
            log.setSoldAmount(item.getRealAmount() != null ? item.getRealAmount() : 
                            item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
            log.setOrderDate(order.getCreateTime());
            log.setCumulativeQuantity(analysis.getCurrentSoldQuantity());
            log.setCumulativeAmount(analysis.getCurrentSoldAmount());
            log.setPaybackProgressBefore(progressBefore);
            log.setPaybackProgressAfter(progressAfter);
            log.setCreatedAt(new Date());
            
            paybackLogMapper.insertSelective(log);
            
        } catch (Exception e) {
            LOGGER.error("创建回本日志失败：productId={}, orderId={}", analysis.getProductId(), order.getId(), e);
        }
    }
} 