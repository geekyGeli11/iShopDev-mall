package com.macro.mall.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.PmsProductPaybackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 商品回本分析服务实现类
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
    public int setPaybackTarget(Long productId, Integer targetQuantity, BigDecimal targetAmount, String startDate) {
        LOGGER.info("设置商品回本目标：productId={}, targetQuantity={}, targetAmount={}, startDate={}", 
                   productId, targetQuantity, targetAmount, startDate);
        
        try {
            // 检查商品是否存在且启用了回本分析
            PmsProduct product = productMapper.selectByPrimaryKey(productId);
            if (product == null) {
                LOGGER.warn("商品不存在：productId={}", productId);
                return 0;
            }
            
            if (!Boolean.TRUE.equals(product.getEnablePaybackAnalysis())) {
                LOGGER.warn("商品未启用回本分析：productId={}", productId);
                return 0;
            }
            
            // 查找是否已存在回本分析记录
            PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
            example.createCriteria().andProductIdEqualTo(productId);
            List<PmsProductPaybackAnalysis> existingList = paybackAnalysisMapper.selectByExample(example);
            
            Date startDateParsed = DateUtil.parseDate(startDate);
            Date now = new Date();
            
            if (!existingList.isEmpty()) {
                // 更新现有记录
                PmsProductPaybackAnalysis analysis = existingList.get(0);
                analysis.setTargetQuantity(targetQuantity);
                analysis.setTargetAmount(targetAmount);
                analysis.setStartDate(startDateParsed);
                analysis.setUpdatedAt(now);
                
                // 重新计算回本进度
                calculatePaybackProgress(analysis);
                
                paybackAnalysisMapper.updateByPrimaryKeySelective(analysis);
                LOGGER.info("更新回本目标成功：productId={}", productId);
            } else {
                // 创建新记录
                PmsProductPaybackAnalysis analysis = new PmsProductPaybackAnalysis();
                analysis.setProductId(productId);
                analysis.setProductName(product.getName());
                analysis.setProductSn(product.getProductSn());
                analysis.setTargetQuantity(targetQuantity);
                analysis.setTargetAmount(targetAmount);
                analysis.setCurrentSoldQuantity(0);
                analysis.setCurrentSoldAmount(BigDecimal.ZERO);
                analysis.setPaybackProgress(BigDecimal.ZERO);
                analysis.setPaybackStatus((byte) 0); // 未开始
                analysis.setStartDate(startDateParsed);
                analysis.setPaybackDays(0);
                analysis.setDailyAvgQuantity(BigDecimal.ZERO);
                analysis.setCreatedAt(now);
                analysis.setUpdatedAt(now);
                
                paybackAnalysisMapper.insertSelective(analysis);
                LOGGER.info("创建回本目标成功：productId={}", productId);
            }
            
            return 1;
        } catch (Exception e) {
            LOGGER.error("设置回本目标失败：productId={}", productId, e);
            return 0;
        }
    }

    @Override
    public int updateOnPaymentSuccess(Long orderId, String orderSn) {
        LOGGER.info("处理订单支付成功，更新回本分析：orderId={}, orderSn={}", orderId, orderSn);
        
        try {
            // 查询订单商品信息
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
            
            if (orderItems.isEmpty()) {
                LOGGER.warn("订单商品信息不存在：orderId={}", orderId);
                return 0;
            }
            
            int updateCount = 0;
            Date currentDate = new Date();
            
            for (OmsOrderItem orderItem : orderItems) {
                Long productId = orderItem.getProductId();
                
                // 查找对应的回本分析记录
                PmsProductPaybackAnalysisExample analysisExample = new PmsProductPaybackAnalysisExample();
                analysisExample.createCriteria().andProductIdEqualTo(productId);
                List<PmsProductPaybackAnalysis> analysisList = paybackAnalysisMapper.selectByExample(analysisExample);
                
                if (analysisList.isEmpty()) {
                    LOGGER.info("商品没有回本分析记录，跳过：productId={}", productId);
                    continue;
                }
                
                PmsProductPaybackAnalysis analysis = analysisList.get(0);
                
                // 记录更新前的进度
                BigDecimal progressBefore = analysis.getPaybackProgress();
                
                // 增加销售数量和金额
                int soldQuantity = orderItem.getProductQuantity();
                BigDecimal soldAmount = orderItem.getRealAmount(); // 使用实际支付金额
                
                analysis.setCurrentSoldQuantity(analysis.getCurrentSoldQuantity() + soldQuantity);
                analysis.setCurrentSoldAmount(analysis.getCurrentSoldAmount().add(soldAmount));
                
                // 更新最后一次销售日期
                analysis.setLastOrderDate(currentDate);
                
                // 重新计算回本进度
                calculatePaybackProgress(analysis);
                analysis.setUpdatedAt(currentDate);
                
                // 如果达到回本目标，设置完成日期
                if (analysis.getPaybackProgress().compareTo(new BigDecimal(100)) >= 0 && 
                    analysis.getPaybackCompletedDate() == null) {
                    analysis.setPaybackCompletedDate(currentDate);
                    
                    // 计算回本天数
                    if (analysis.getStartDate() != null) {
                        long paybackDays = DateUtil.betweenDay(analysis.getStartDate(), currentDate, false);
                        analysis.setPaybackDays((int) paybackDays);
                    }
                }
                
                // 更新回本分析记录
                paybackAnalysisMapper.updateByPrimaryKeySelective(analysis);
                
                // 记录销售日志
                PmsProductPaybackLog paybackLog = new PmsProductPaybackLog();
                paybackLog.setProductId(productId);
                paybackLog.setOrderId(orderId);
                paybackLog.setOrderSn(orderSn);
                paybackLog.setSoldQuantity(soldQuantity);
                paybackLog.setSoldAmount(soldAmount);
                paybackLog.setOrderDate(currentDate);
                paybackLog.setCumulativeQuantity(analysis.getCurrentSoldQuantity());
                paybackLog.setCumulativeAmount(analysis.getCurrentSoldAmount());
                paybackLog.setPaybackProgressBefore(progressBefore);
                paybackLog.setPaybackProgressAfter(analysis.getPaybackProgress());
                paybackLog.setCreatedAt(currentDate);
                
                paybackLogMapper.insertSelective(paybackLog);
                
                updateCount++;
                
                LOGGER.info("支付成功回本分析更新成功：productId={}, orderId={}, 销售数量={}, 销售金额={}, 当前进度={}%", 
                           productId, orderId, soldQuantity, soldAmount, analysis.getPaybackProgress());
            }
            
            return updateCount;
        } catch (Exception e) {
            LOGGER.error("处理订单支付成功失败：orderId={}", orderId, e);
            return 0;
        }
    }

    @Override
    public int updateOnRefund(Long orderId, String orderSn) {
        LOGGER.info("处理订单退款，更新回本分析：orderId={}, orderSn={}", orderId, orderSn);
        
        try {
            // 查询订单商品信息
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(orderItemExample);
            
            if (orderItems.isEmpty()) {
                LOGGER.warn("订单商品信息不存在：orderId={}", orderId);
                return 0;
            }
            
            int updateCount = 0;
            Date currentDate = new Date();
            
            for (OmsOrderItem orderItem : orderItems) {
                Long productId = orderItem.getProductId();
                
                // 查找对应的回本分析记录
                PmsProductPaybackAnalysisExample analysisExample = new PmsProductPaybackAnalysisExample();
                analysisExample.createCriteria().andProductIdEqualTo(productId);
                List<PmsProductPaybackAnalysis> analysisList = paybackAnalysisMapper.selectByExample(analysisExample);
                
                if (analysisList.isEmpty()) {
                    LOGGER.info("商品没有回本分析记录，跳过：productId={}", productId);
                    continue;
                }
                
                PmsProductPaybackAnalysis analysis = analysisList.get(0);
                
                // 记录更新前的进度
                BigDecimal progressBefore = analysis.getPaybackProgress();
                
                // 减少销售数量和金额（退款）
                int refundQuantity = orderItem.getProductQuantity();
                BigDecimal refundAmount = orderItem.getRealAmount(); // 使用实际支付金额
                
                analysis.setCurrentSoldQuantity(Math.max(0, analysis.getCurrentSoldQuantity() - refundQuantity));
                analysis.setCurrentSoldAmount(analysis.getCurrentSoldAmount().subtract(refundAmount));
                
                // 确保不为负数
                if (analysis.getCurrentSoldAmount().compareTo(BigDecimal.ZERO) < 0) {
                    analysis.setCurrentSoldAmount(BigDecimal.ZERO);
                }
                
                // 重新计算回本进度
                calculatePaybackProgress(analysis);
                analysis.setUpdatedAt(currentDate);
                
                // 更新回本分析记录
                paybackAnalysisMapper.updateByPrimaryKeySelective(analysis);
                
                // 记录退款日志（使用负数表示退款）
                PmsProductPaybackLog paybackLog = new PmsProductPaybackLog();
                paybackLog.setProductId(productId);
                paybackLog.setOrderId(orderId);
                paybackLog.setOrderSn(orderSn);
                paybackLog.setSoldQuantity(-refundQuantity); // 负数表示退款
                paybackLog.setSoldAmount(refundAmount.negate()); // 负数表示退款
                paybackLog.setOrderDate(new Date());
                paybackLog.setCumulativeQuantity(analysis.getCurrentSoldQuantity());
                paybackLog.setCumulativeAmount(analysis.getCurrentSoldAmount());
                paybackLog.setPaybackProgressBefore(progressBefore);
                paybackLog.setPaybackProgressAfter(analysis.getPaybackProgress());
                paybackLog.setCreatedAt(currentDate);
                
                paybackLogMapper.insertSelective(paybackLog);
                
                updateCount++;
                
                LOGGER.info("退款回本分析更新成功：productId={}, orderId={}, 退款数量={}, 退款金额={}, 当前进度={}%", 
                           productId, orderId, refundQuantity, refundAmount, analysis.getPaybackProgress());
            }
            
            return updateCount;
        } catch (Exception e) {
            LOGGER.error("处理订单退款失败：orderId={}, orderSn={}", orderId, orderSn, e);
            return 0;
        }
    }

    @Override
    public int refreshPaybackAnalysis(Long productId) {
        return 1; // 简化实现
    }

    @Override
    public int refreshAllPaybackAnalysis() {
        return 1; // 简化实现
    }

    @Override
    public List<PmsProductPaybackAnalysis> listPaybackAnalysis(String keyword, Byte paybackStatus, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        
        PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
        PmsProductPaybackAnalysisExample.Criteria criteria = example.createCriteria();
        
        if (StrUtil.isNotBlank(keyword)) {
            criteria.andProductNameLike("%" + keyword + "%");
        }
        
        if (paybackStatus != null) {
            criteria.andPaybackStatusEqualTo(paybackStatus);
        }
        
        example.setOrderByClause("updated_at DESC");
        
        return paybackAnalysisMapper.selectByExample(example);
    }

    @Override
    public PmsProductPaybackAnalysis getPaybackAnalysis(Long productId) {
        PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
        example.createCriteria().andProductIdEqualTo(productId);
        List<PmsProductPaybackAnalysis> analysisList = paybackAnalysisMapper.selectByExample(example);
        
        return analysisList.isEmpty() ? null : analysisList.get(0);
    }

    @Override
    public int deletePaybackAnalysis(Long productId) {
        LOGGER.info("删除商品回本分析记录：productId={}", productId);
        
        try {
            // 删除回本分析记录
            PmsProductPaybackAnalysisExample analysisExample = new PmsProductPaybackAnalysisExample();
            analysisExample.createCriteria().andProductIdEqualTo(productId);
            int deleteCount = paybackAnalysisMapper.deleteByExample(analysisExample);
            
            // 删除回本日志记录
            PmsProductPaybackLogExample logExample = new PmsProductPaybackLogExample();
            logExample.createCriteria().andProductIdEqualTo(productId);
            paybackLogMapper.deleteByExample(logExample);
            
            LOGGER.info("删除回本分析记录成功：productId={}, 删除记录数={}", productId, deleteCount);
            return deleteCount;
        } catch (Exception e) {
            LOGGER.error("删除回本分析记录失败：productId={}", productId, e);
            return 0;
        }
    }

    @Override
    public Map<String, Object> getPaybackStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 查询所有回本分析记录
            PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
            List<PmsProductPaybackAnalysis> allAnalysis = paybackAnalysisMapper.selectByExample(example);
            
            int totalCount = allAnalysis.size();
            int completedCount = 0;
            int inProgressCount = 0;
            int slowCount = 0;
            
            // 统计各种状态的数量
            for (PmsProductPaybackAnalysis analysis : allAnalysis) {
                if (analysis.getPaybackStatus() == null) {
                    continue;
                }
                
                switch (analysis.getPaybackStatus()) {
                    case 0: // 未开始 - 计入进行中
                        inProgressCount++;
                        break;
                    case 1: // 回本中
                        inProgressCount++;
                        // 判断是否回本缓慢（开始30天后进度小于50%）
                        if (isSlowPayback(analysis)) {
                            slowCount++;
                        }
                        break;
                    case 2: // 已完成
                        completedCount++;
                        break;
                    default:
                        break;
                }
            }
            
            // 计算回本率
            BigDecimal paybackRate = BigDecimal.ZERO;
            if (totalCount > 0) {
                paybackRate = new BigDecimal(completedCount)
                    .divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            }
            
            statistics.put("totalCount", totalCount);
            statistics.put("completedCount", completedCount);
            statistics.put("inProgressCount", inProgressCount);
            statistics.put("slowCount", slowCount);
            statistics.put("paybackRate", paybackRate);
            
            LOGGER.info("回本分析统计信息：totalCount={}, completedCount={}, inProgressCount={}, slowCount={}, paybackRate={}", 
                       totalCount, completedCount, inProgressCount, slowCount, paybackRate);
            
        } catch (Exception e) {
            LOGGER.error("获取回本分析统计信息失败", e);
            // 出错时返回默认值
            statistics.put("totalCount", 0);
            statistics.put("completedCount", 0);
            statistics.put("inProgressCount", 0);
            statistics.put("slowCount", 0);
            statistics.put("paybackRate", BigDecimal.ZERO);
        }
        
        return statistics;
    }

    @Override
    public long countPaybackRecords(String keyword, Byte paybackStatus) {
        LOGGER.info("统计回本分析记录数量：keyword={}, paybackStatus={}", keyword, paybackStatus);
        
        try {
            PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
            PmsProductPaybackAnalysisExample.Criteria criteria = example.createCriteria();
            
            if (StrUtil.isNotBlank(keyword)) {
                criteria.andProductNameLike("%" + keyword + "%");
            }
            
            if (paybackStatus != null) {
                criteria.andPaybackStatusEqualTo(paybackStatus);
            }
            
            long count = paybackAnalysisMapper.countByExample(example);
            LOGGER.info("回本分析记录统计完成：count={}", count);
            return count;
        } catch (Exception e) {
            LOGGER.error("统计回本分析记录数量失败", e);
            return 0;
        }
    }

    @Override
    public List<Map<String, Object>> exportPaybackRecords(String keyword, Byte paybackStatus, Integer pageSize, Integer pageNum) {
        LOGGER.info("分页导出回本分析数据：keyword={}, paybackStatus={}, pageSize={}, pageNum={}", 
                   keyword, paybackStatus, pageSize, pageNum);
        
        try {
            PageHelper.startPage(pageNum, pageSize);
            
            PmsProductPaybackAnalysisExample example = new PmsProductPaybackAnalysisExample();
            PmsProductPaybackAnalysisExample.Criteria criteria = example.createCriteria();
            
            if (StrUtil.isNotBlank(keyword)) {
                criteria.andProductNameLike("%" + keyword + "%");
            }
            
            if (paybackStatus != null) {
                criteria.andPaybackStatusEqualTo(paybackStatus);
            }
            
            example.setOrderByClause("updated_at DESC");
            
            List<PmsProductPaybackAnalysis> analysisList = paybackAnalysisMapper.selectByExample(example);
            List<Map<String, Object>> exportData = new ArrayList<>();
            
            for (PmsProductPaybackAnalysis analysis : analysisList) {
                Map<String, Object> row = new HashMap<>();
                row.put("productName", analysis.getProductName());
                row.put("productSn", analysis.getProductSn());
                row.put("targetQuantity", analysis.getTargetQuantity());
                row.put("targetAmount", analysis.getTargetAmount());
                row.put("actualQuantity", analysis.getCurrentSoldQuantity());
                row.put("actualAmount", analysis.getCurrentSoldAmount());
                row.put("paybackProgress", analysis.getPaybackProgress());
                row.put("paybackStatus", getStatusText(analysis.getPaybackStatus()));
                row.put("startDate", analysis.getStartDate() != null ? DateUtil.formatDate(analysis.getStartDate()) : "");
                row.put("estimatedCompletionTime", analysis.getPredictedCompletionDate() != null ? 
                    DateUtil.formatDateTime(analysis.getPredictedCompletionDate()) : "");
                row.put("updateTime", analysis.getUpdatedAt() != null ? 
                    DateUtil.formatDateTime(analysis.getUpdatedAt()) : "");
                
                exportData.add(row);
            }
            
            LOGGER.info("分页导出回本分析数据完成：导出记录数={}", exportData.size());
            return exportData;
        } catch (Exception e) {
            LOGGER.error("分页导出回本分析数据失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取状态文本
     */
    private String getStatusText(Byte status) {
        if (status == null) {
            return "未知";
        }
        
        switch (status) {
            case 0:
                return "未开始";
            case 1:
                return "回本中";
            case 2:
                return "已回本";
            case 3:
                return "销售缓慢";
            case 4:
                return "已下架";
            default:
                return "未知";
        }
    }
    
    /**
     * 判断是否回本缓慢
     * 规则：开始30天后进度小于50%
     */
    private boolean isSlowPayback(PmsProductPaybackAnalysis analysis) {
        if (analysis.getStartDate() == null || analysis.getPaybackProgress() == null) {
            return false;
        }
        
        // 计算已开始天数
        long daysSinceStart = DateUtil.betweenDay(analysis.getStartDate(), new Date(), false);
        
        // 开始30天后进度小于50%视为缓慢
        return daysSinceStart >= 30 && analysis.getPaybackProgress().compareTo(new BigDecimal(50)) < 0;
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
        
        // 计算日均销售量和预计完成时间
        calculateDailyAvgAndPredictedDate(analysis);
        
        // 更新回本状态
        updatePaybackStatus(analysis);
    }
    
    /**
     * 计算日均销售量和预计完成时间
     */
    private void calculateDailyAvgAndPredictedDate(PmsProductPaybackAnalysis analysis) {
        if (analysis.getStartDate() == null) {
            return;
        }
        
        Date currentDate = new Date();
        
        // 如果已经回本完成，清除预计完成时间
        if (analysis.getPaybackProgress() != null && 
            analysis.getPaybackProgress().compareTo(new BigDecimal(100)) >= 0) {
            analysis.setPredictedCompletionDate(null);
            return;
        }
        
        // 计算已经过去的天数
        long daysPassed = DateUtil.betweenDay(analysis.getStartDate(), currentDate, false);
        if (daysPassed <= 0) {
            daysPassed = 1; // 至少1天
        }
        
        // 计算日均销售量
        BigDecimal dailyAvgQuantity = BigDecimal.ZERO;
        if (analysis.getCurrentSoldQuantity() != null && analysis.getCurrentSoldQuantity() > 0) {
            dailyAvgQuantity = new BigDecimal(analysis.getCurrentSoldQuantity())
                .divide(new BigDecimal(daysPassed), 4, RoundingMode.HALF_UP);
        }
        analysis.setDailyAvgQuantity(dailyAvgQuantity);
        
        // 预计完成时间计算
        Date predictedDate = calculatePredictedCompletionDate(analysis, daysPassed, dailyAvgQuantity);
        analysis.setPredictedCompletionDate(predictedDate);
    }
    
    /**
     * 计算预计完成时间
     */
    private Date calculatePredictedCompletionDate(PmsProductPaybackAnalysis analysis, long daysPassed, BigDecimal dailyAvgQuantity) {
        // 如果日均销售量为0，无法预测
        if (dailyAvgQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        
        // 如果没有销售数据，无法预测
        if (analysis.getCurrentSoldQuantity() == null || analysis.getCurrentSoldQuantity() <= 0) {
            return null;
        }
        
        try {
            // 按数量预测
            Integer remainingQuantity = null;
            if (analysis.getTargetQuantity() != null && analysis.getTargetQuantity() > 0) {
                remainingQuantity = analysis.getTargetQuantity() - analysis.getCurrentSoldQuantity();
            }
            
            // 按金额预测
            BigDecimal remainingAmount = null;
            BigDecimal dailyAvgAmount = BigDecimal.ZERO;
            if (analysis.getTargetAmount() != null && analysis.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
                remainingAmount = analysis.getTargetAmount().subtract(analysis.getCurrentSoldAmount());
                if (analysis.getCurrentSoldAmount().compareTo(BigDecimal.ZERO) > 0) {
                    dailyAvgAmount = analysis.getCurrentSoldAmount()
                        .divide(new BigDecimal(daysPassed), 4, RoundingMode.HALF_UP);
                }
            }
            
            // 计算需要的天数
            Long daysNeededByQuantity = null;
            Long daysNeededByAmount = null;
            
            if (remainingQuantity != null && remainingQuantity > 0) {
                daysNeededByQuantity = new BigDecimal(remainingQuantity)
                    .divide(dailyAvgQuantity, 0, RoundingMode.UP)
                    .longValue();
            }
            
            if (remainingAmount != null && remainingAmount.compareTo(BigDecimal.ZERO) > 0 && 
                dailyAvgAmount.compareTo(BigDecimal.ZERO) > 0) {
                daysNeededByAmount = remainingAmount
                    .divide(dailyAvgAmount, 0, RoundingMode.UP)
                    .longValue();
            }
            
            // 取需要天数的最大值（更保守的估计）
            Long daysNeeded = null;
            if (daysNeededByQuantity != null && daysNeededByAmount != null) {
                daysNeeded = Math.max(daysNeededByQuantity, daysNeededByAmount);
            } else if (daysNeededByQuantity != null) {
                daysNeeded = daysNeededByQuantity;
            } else if (daysNeededByAmount != null) {
                daysNeeded = daysNeededByAmount;
            }
            
            // 如果无法计算所需天数，返回null
            if (daysNeeded == null || daysNeeded <= 0) {
                return null;
            }
            
            // 限制预测时间不超过3年
            if (daysNeeded > 1095) {
                daysNeeded = 1095L;
            }
            
            // 计算预计完成日期
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, daysNeeded.intValue());
            
            Date predictedDate = calendar.getTime();
            
            LOGGER.debug("预计完成时间计算：商品ID={}, 剩余数量={}, 剩余金额={}, 日均销量={}, 日均销售额={}, 预计需要天数={}, 预计完成日期={}", 
                        analysis.getProductId(), remainingQuantity, remainingAmount, 
                        dailyAvgQuantity, dailyAvgAmount, daysNeeded, DateUtil.formatDateTime(predictedDate));
            
            return predictedDate;
            
        } catch (Exception e) {
            LOGGER.warn("计算预计完成时间失败：productId={}", analysis.getProductId(), e);
            return null;
        }
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
} 