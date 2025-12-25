package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.service.CommissionService;
import com.macro.mall.portal.domain.CommissionRecord;
import com.macro.mall.portal.domain.CommissionSummary;
import com.macro.mall.portal.domain.WithdrawRecord;
import com.macro.mall.portal.domain.WithdrawApplyResult;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 佣金服务实现类 - 简化版
 * 专注于支付成功后佣金计算和订单完成后佣金发放
 */
@Service
@Transactional
public class CommissionServiceImpl implements CommissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommissionServiceImpl.class);
    
    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;
    
    @Autowired
    private PmsInviteRelationMapper inviteRelationMapper;
    
    @Autowired
    private PmsCommissionConfigMapper commissionConfigMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    /**
     * 佣金状态枚举
     */
    public static final byte COMMISSION_STATUS_PENDING = 0; // 待结算
    public static final byte COMMISSION_STATUS_SETTLED = 1; // 已结算
    public static final byte COMMISSION_STATUS_FAILED = 2; // 结算失败
    public static final byte COMMISSION_STATUS_EXPIRED = 3; // 已过期
    
    /**
     * 支付成功后计算佣金（状态为待结算）
     */
    @Transactional
    public void calculateCommissionOnPayment(Long orderId) {
        try {
            LOGGER.info("支付成功后计算佣金, orderId: {}", orderId);
            
            // 查询订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null || order.getStatus() != 1) {
                LOGGER.warn("订单状态不正确, orderId: {}, status: {}", orderId, order != null ? order.getStatus() : null);
                return;
            }
            
            // 查询分销关系
            List<DistributorInfo> distributors = findDistributorsByCustomer(order.getMemberId());
            if (CollectionUtils.isEmpty(distributors)) {
                LOGGER.info("客户无分销关系, customerId: {}", order.getMemberId());
                return;
            }
            
            // 查询订单商品
            OmsOrderItemExample itemExample = new OmsOrderItemExample();
            itemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(itemExample);
            
            // 为每个分销商计算佣金（状态为待结算）
            for (DistributorInfo distributor : distributors) {
                calculateCommissionForDistributor(order, orderItems, distributor);
            }
            
        } catch (Exception e) {
            LOGGER.error("支付成功后计算佣金失败, orderId: {}", orderId, e);
        }
    }
    
    /**
     * 订单完成后发放佣金（更新状态为已结算）
     */
    @Transactional
    public void settleCommissionOnOrderComplete(Long orderId) {
        try {
            LOGGER.info("订单完成后发放佣金, orderId: {}", orderId);
            
            // 查询该订单的所有待结算佣金
            PmsInviteRewardExample example = new PmsInviteRewardExample();
            example.createCriteria()
                    .andOrderIdEqualTo(orderId)
                    .andSettlementStatusEqualTo(COMMISSION_STATUS_PENDING);
            List<PmsInviteReward> pendingCommissions = inviteRewardMapper.selectByExample(example);
            
            if (CollectionUtils.isEmpty(pendingCommissions)) {
                LOGGER.info("订单无待结算佣金, orderId: {}", orderId);
                return;
            }
            
            // 更新佣金状态为已结算
            for (PmsInviteReward commission : pendingCommissions) {
                PmsInviteReward updateReward = new PmsInviteReward();
                updateReward.setId(commission.getId());
                updateReward.setSettlementStatus(COMMISSION_STATUS_SETTLED);
                updateReward.setSendTime(new Date());
                updateReward.setStatus((byte) 1); // 已发放
                updateReward.setUpdatedAt(new Date());
                
                inviteRewardMapper.updateByPrimaryKeySelective(updateReward);
            }
            
            LOGGER.info("订单佣金发放完成, orderId: {}, count: {}", orderId, pendingCommissions.size());
            
        } catch (Exception e) {
            LOGGER.error("订单完成后发放佣金失败, orderId: {}", orderId, e);
        }
    }
    
    /**
     * 批量更新佣金结算状态（用于订单取消等场景）
     */
    @Transactional
    public void updateCommissionSettlement(List<Long> commissionIds, Integer status) {
        try {
            LOGGER.info("批量更新佣金结算状态, commissionIds: {}, status: {}", commissionIds, status);
            
            if (CollectionUtils.isEmpty(commissionIds)) {
                return;
            }
            
            for (Long commissionId : commissionIds) {
                PmsInviteReward updateReward = new PmsInviteReward();
                updateReward.setId(commissionId);
                updateReward.setSettlementStatus(status.byteValue());
                updateReward.setUpdatedAt(new Date());
                
                if (status == COMMISSION_STATUS_SETTLED) {
                    updateReward.setSendTime(new Date());
                    updateReward.setStatus((byte) 1); // 已发放
                }
                
                inviteRewardMapper.updateByPrimaryKeySelective(updateReward);
            }
            
            LOGGER.info("佣金结算状态更新完成, count: {}", commissionIds.size());
            
        } catch (Exception e) {
            LOGGER.error("更新佣金结算状态失败, commissionIds: {}", commissionIds, e);
            throw e;
        }
    }
    
    /**
     * 为单个分销商计算佣金
     */
    private void calculateCommissionForDistributor(OmsOrder order, List<OmsOrderItem> orderItems, DistributorInfo distributor) {
        try {
            // 获取佣金配置
            CommissionConfig config = getCommissionConfig(distributor.getLevel());
            if (config == null) {
                LOGGER.warn("未找到佣金配置, distributorId: {}, level: {}", distributor.getDistributorId(), distributor.getLevel());
                return;
            }
            
            // 计算订单总佣金
            BigDecimal totalCommission = calculateOrderCommission(order, orderItems, config, distributor.getLevel());
            
            if (totalCommission.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.info("订单佣金为0, orderId: {}, distributorId: {}", order.getId(), distributor.getDistributorId());
                return;
            }
            
            // 创建佣金记录
            PmsInviteReward commissionRecord = new PmsInviteReward();
            commissionRecord.setUserId(distributor.getDistributorId());
            commissionRecord.setRewardType((byte) 1); // 现金红包
            commissionRecord.setRewardValue(totalCommission);
            commissionRecord.setRewardDesc(String.format("%d级分销佣金", distributor.getLevel()));
            commissionRecord.setTriggerType((byte) 2); // 订单完成触发
            commissionRecord.setTriggerAmount(order.getPayAmount());
            commissionRecord.setStatus((byte) 0); // 待发放
            commissionRecord.setCommissionType(distributor.getLevel() == 1 ? (byte) 2 : (byte) 3); // 一级或二级分销佣金
            commissionRecord.setOrderId(order.getId());
            commissionRecord.setOrderAmount(order.getPayAmount());
            commissionRecord.setCommissionRate(config.getRate());
            commissionRecord.setSettlementStatus(COMMISSION_STATUS_PENDING); // 待结算
            commissionRecord.setCreatedAt(new Date());
            commissionRecord.setUpdatedAt(new Date());
            
            // 如果有关联的邀请关系，设置relation_id
            if (distributor.getRelationId() != null) {
                commissionRecord.setRelationId(distributor.getRelationId());
            }
            
            int insertResult = inviteRewardMapper.insert(commissionRecord);
            if (insertResult > 0) {
                LOGGER.info("佣金记录创建成功, orderId: {}, distributorId: {}, amount: {}", 
                           order.getId(), distributor.getDistributorId(), totalCommission);
            } else {
                LOGGER.error("佣金记录创建失败, orderId: {}, distributorId: {}", order.getId(), distributor.getDistributorId());
            }
            
        } catch (Exception e) {
            LOGGER.error("为分销商计算佣金失败, distributorId: {}", distributor.getDistributorId(), e);
        }
    }
    
    /**
     * 计算订单佣金
     */
    private BigDecimal calculateOrderCommission(OmsOrder order, List<OmsOrderItem> orderItems, CommissionConfig config, Integer level) {
        BigDecimal totalCommission = BigDecimal.ZERO;
        
        // 基础订单金额（用于计算佣金的金额）
        BigDecimal baseAmount = order.getPayAmount();
        
        // 检查最小订单金额限制
        if (config.getMinOrderAmount() != null && baseAmount.compareTo(config.getMinOrderAmount()) < 0) {
            LOGGER.info("订单金额未达到最小佣金计算金额, orderAmount: {}, minAmount: {}", 
                       baseAmount, config.getMinOrderAmount());
            return BigDecimal.ZERO;
        }
        
        // 根据佣金类型计算
        switch (config.getType()) {
            case 1: // 按比例
                totalCommission = baseAmount.multiply(config.getRate()).setScale(2, RoundingMode.HALF_UP);
                break;
            case 2: // 固定金额
                totalCommission = config.getFixedAmount();
                break;
            case 3: // 比例+固定
                BigDecimal percentCommission = baseAmount.multiply(config.getRate()).setScale(2, RoundingMode.HALF_UP);
                totalCommission = percentCommission.add(config.getFixedAmount());
                break;
            default:
                LOGGER.warn("未知的佣金类型: {}", config.getType());
                return BigDecimal.ZERO;
        }
        
        // 检查最大佣金限制
        if (config.getMaxCommission() != null && totalCommission.compareTo(config.getMaxCommission()) > 0) {
            totalCommission = config.getMaxCommission();
        }
        
        return totalCommission;
    }
    
    /**
     * 根据客户ID查找分销商信息
     */
    private List<DistributorInfo> findDistributorsByCustomer(Long customerId) {
        List<DistributorInfo> distributors = new ArrayList<>();
        
        try {
            // 查询分销关系
            PmsInviteRelationExample example = new PmsInviteRelationExample();
            example.createCriteria().andInviteeIdEqualTo(customerId);
            List<PmsInviteRelation> relations = inviteRelationMapper.selectByExample(example);
            
            for (PmsInviteRelation relation : relations) {
                // 检查邀请者是否为分销商
                UmsMember distributor = memberMapper.selectByPrimaryKey(relation.getInviterId());
                if (distributor != null && distributor.getIsDistributor() != null && distributor.getIsDistributor() == 1) {
                    DistributorInfo info = new DistributorInfo();
                    info.setDistributorId(relation.getInviterId());
                    info.setLevel(relation.getDistributorLevel() != null ? relation.getDistributorLevel().intValue() : 1);
                    info.setRelationId(relation.getId());
                    distributors.add(info);
                }
            }
            
        } catch (Exception e) {
            LOGGER.error("查找分销商信息失败, customerId: {}", customerId, e);
        }
        
        return distributors;
    }
    
    /**
     * 获取佣金配置
     */
    private CommissionConfig getCommissionConfig(Integer level) {
        try {
            // 查询全局佣金配置
            PmsCommissionConfigExample example = new PmsCommissionConfigExample();
            example.createCriteria()
                    .andIsActiveEqualTo((byte) 1)
                    .andProductCategoryIdIsNull(); // 全局配置
            example.setOrderByClause("id DESC");
            List<PmsCommissionConfig> configs = commissionConfigMapper.selectByExample(example);
            
            if (!CollectionUtils.isEmpty(configs)) {
                PmsCommissionConfig config = configs.get(0);
                
                CommissionConfig result = new CommissionConfig();
                result.setType(config.getCommissionType().intValue());
                
                if (level == 1) {
                    result.setRate(config.getLevel1Rate());
                    result.setFixedAmount(config.getLevel1Amount());
                } else if (level == 2) {
                    result.setRate(config.getLevel2Rate());
                    result.setFixedAmount(config.getLevel2Amount());
                } else {
                    return null;
                }
                
                result.setMinOrderAmount(config.getMinOrderAmount());
                result.setMaxCommission(config.getMaxCommission());
                
                return result;
            }
            
        } catch (Exception e) {
            LOGGER.error("获取佣金配置失败, level: {}", level, e);
        }
        
        return null;
    }
    
    /**
     * 获取我的佣金记录
     */
    @Override
    public List<CommissionRecord> getMyCommissionRecords(Long userId, Integer page, Integer size, 
                                                        String type, String startDate, String endDate) {
        try {
            // 查询佣金记录
            PmsInviteRewardExample example = new PmsInviteRewardExample();
            PmsInviteRewardExample.Criteria criteria = example.createCriteria().andUserIdEqualTo(userId);
            
            // 根据类型筛选
            if ("order".equals(type)) {
                criteria.andCommissionTypeEqualTo((byte) 2).andCommissionTypeEqualTo((byte) 3); // 2=一级分销佣金，3=二级分销佣金
            } else if ("invite".equals(type)) {
                criteria.andCommissionTypeEqualTo((byte) 1); // 1=邀请奖励
            }
            
            example.setOrderByClause("created_at DESC");
            
            // 分页查询
            PageHelper.startPage(page, size);
            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(example);
            
            // 转换为前端需要的格式
            List<CommissionRecord> records = new ArrayList<>();
            for (PmsInviteReward reward : rewards) {
                CommissionRecord record = new CommissionRecord();
                record.setId(reward.getId());
                record.setCommissionAmount(reward.getRewardValue());
                record.setCommissionType(reward.getCommissionType().intValue());
                record.setOrderId(reward.getOrderId());
                record.setCreateTime(reward.getCreatedAt());
                record.setSettlementStatus(reward.getSettlementStatus().intValue());
                records.add(record);
            }
            
            return records;
        } catch (Exception e) {
            LOGGER.error("获取佣金记录失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取我的佣金汇总
     */
    @Override
    public CommissionSummary getMyCommissionSummary(Long userId) {
        try {
            CommissionSummary summary = new CommissionSummary();
            
            // 查询所有佣金记录
            PmsInviteRewardExample example = new PmsInviteRewardExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(example);
            
            BigDecimal totalEarned = BigDecimal.ZERO;
            BigDecimal availableAmount = BigDecimal.ZERO;
            BigDecimal pendingAmount = BigDecimal.ZERO;
            
            for (PmsInviteReward reward : rewards) {
                BigDecimal amount = reward.getRewardValue();
                totalEarned = totalEarned.add(amount);
                
                if (reward.getStatus() == 1) { // 1=已发放
                    availableAmount = availableAmount.add(amount);
                } else if (reward.getStatus() == 0) { // 0=待发放
                    pendingAmount = pendingAmount.add(amount);
                }
            }
            
            summary.setTotalCommission(totalEarned);
            summary.setAvailableAmount(availableAmount);
            summary.setPendingAmount(pendingAmount);
            
            return summary;
        } catch (Exception e) {
            LOGGER.error("获取佣金汇总失败: userId={}", userId, e);
            return new CommissionSummary();
        }
    }
    
    /**
     * 申请提现
     */
    @Override
    public WithdrawApplyResult applyWithdraw(Long userId, WithdrawApplyParam param) {
        // 这个方法由WithdrawServiceImpl处理，这里只是为了接口完整性
        throw new RuntimeException("请使用WithdrawService进行提现申请");
    }
    
    /**
     * 获取提现历史
     */
    @Override
    public List<WithdrawRecord> getWithdrawHistory(Long userId, Integer page, Integer size, 
                                                  Integer status, String timeRange) {
        // 这个方法由WithdrawServiceImpl处理，这里只是为了接口完整性
        throw new RuntimeException("请使用WithdrawService获取提现历史");
    }
    
    /**
     * 获取提现统计
     */
    @Override
    public Map<String, Object> getWithdrawStats(Long userId) {
        // 这个方法由WithdrawServiceImpl处理，这里只是为了接口完整性
        throw new RuntimeException("请使用WithdrawService获取提现统计");
    }
    
    /**
     * 取消提现申请
     */
    @Override
    public Boolean cancelWithdraw(Long userId, Long withdrawId) {
        // 这个方法由WithdrawServiceImpl处理，这里只是为了接口完整性
        throw new RuntimeException("请使用WithdrawService取消提现申请");
    }
    
    /**
     * 计算并发放佣金
     */
    @Override
    public void calculateAndGrantCommission(Long orderId, Long customerId, Long distributorId, Integer level) {
        try {
            LOGGER.info("开始计算佣金: orderId={}, customerId={}, distributorId={}, level={}", 
                       orderId, customerId, distributorId, level);
            
            // 查询订单信息
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                LOGGER.warn("订单不存在: orderId={}", orderId);
                return;
            }
            
            // 查询佣金配置
            PmsCommissionConfigExample configExample = new PmsCommissionConfigExample();
            configExample.createCriteria().andIsActiveEqualTo((byte) 1);
            List<PmsCommissionConfig> configs = commissionConfigMapper.selectByExample(configExample);
            
            if (configs.isEmpty()) {
                LOGGER.warn("未找到有效的佣金配置");
                return;
            }
            
            PmsCommissionConfig config = configs.get(0);
            BigDecimal commissionRate = level == 1 ? config.getLevel1Rate() : config.getLevel2Rate();
            
            // 计算佣金金额
            BigDecimal commissionAmount = order.getTotalAmount().multiply(commissionRate);
            
            // 创建佣金记录
            PmsInviteReward reward = new PmsInviteReward();
            reward.setUserId(distributorId);
            reward.setOrderId(orderId);
            reward.setRewardValue(commissionAmount);
            reward.setCommissionType(level == 1 ? (byte) 2 : (byte) 3); // 2=一级分销佣金，3=二级分销佣金
            reward.setStatus((byte) 0); // 0=待发放
            reward.setCommissionRate(commissionRate);
            reward.setOrderAmount(order.getTotalAmount());
            reward.setCreatedAt(new Date());
            
            inviteRewardMapper.insert(reward);
            
            LOGGER.info("佣金计算完成: distributorId={}, amount={}", distributorId, commissionAmount);
            
        } catch (Exception e) {
            LOGGER.error("计算佣金失败: orderId={}, distributorId={}", orderId, distributorId, e);
            throw new RuntimeException("计算佣金失败");
        }
    }
    
    /**
     * 分销商信息内部类
     */
    private static class DistributorInfo {
        private Long distributorId;
        private Integer level;
        private Long relationId;
        
        public Long getDistributorId() { return distributorId; }
        public void setDistributorId(Long distributorId) { this.distributorId = distributorId; }
        
        public Integer getLevel() { return level; }
        public void setLevel(Integer level) { this.level = level; }
        
        public Long getRelationId() { return relationId; }
        public void setRelationId(Long relationId) { this.relationId = relationId; }
    }
    
    /**
     * 佣金配置内部类
     */
    private static class CommissionConfig {
        private Integer type;
        private BigDecimal rate;
        private BigDecimal fixedAmount;
        private BigDecimal minOrderAmount;
        private BigDecimal maxCommission;
        
        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        
        public BigDecimal getRate() { return rate; }
        public void setRate(BigDecimal rate) { this.rate = rate; }
        
        public BigDecimal getFixedAmount() { return fixedAmount; }
        public void setFixedAmount(BigDecimal fixedAmount) { this.fixedAmount = fixedAmount; }
        
        public BigDecimal getMinOrderAmount() { return minOrderAmount; }
        public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }
        
        public BigDecimal getMaxCommission() { return maxCommission; }
        public void setMaxCommission(BigDecimal maxCommission) { this.maxCommission = maxCommission; }
    }
}
 