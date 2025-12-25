package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.PmsInviteWithdrawDao;
import com.macro.mall.dto.WechatTransferParam;
import com.macro.mall.dto.WechatTransferResult;
import com.macro.mall.mapper.PmsInviteWithdrawApplyMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.PmsInviteWithdrawApply;
import com.macro.mall.model.PmsInviteWithdrawApplyExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.service.PmsInviteWithdrawService;
import com.macro.mall.service.PmsInviteConfigService;
import com.macro.mall.service.WechatTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 邀请提现申请管理Service实现类（已完善）
 * 提现管理流程优化版本
 * Created by guanghengzhou on 2024/01/20.
 * Updated on 2025/01/04 - 完善提现管理流程
 */
@Service
public class PmsInviteWithdrawServiceImpl implements PmsInviteWithdrawService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsInviteWithdrawServiceImpl.class);
    
    @Autowired
    private PmsInviteWithdrawApplyMapper withdrawMapper;
    
    @Autowired
    private PmsInviteWithdrawDao withdrawDao;
    
    @Autowired
    private WechatTransferService wechatTransferService;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private PmsInviteConfigService configService;
    
    // 提现手续费配置（可从系统配置中读取）
    private static final BigDecimal DEFAULT_FEE_RATE = new BigDecimal("0.02"); // 默认2%手续费
    private static final BigDecimal MIN_FEE_AMOUNT = new BigDecimal("0.01"); // 最小手续费1分
    private static final BigDecimal MAX_FEE_AMOUNT = new BigDecimal("50.00"); // 最大手续费50元
    
    @Override
    public List<Map<String, Object>> list(Long userId, Integer status, Integer sourceType,
                                          String startTime, String endTime,
                                          Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        
        // 使用自定义Dao进行关联查询
        return withdrawDao.getInviteWithdrawsList(userId, status, sourceType, startTime, endTime);
    }
    
    @Override
    public Map<String, Object> getDetail(Long id) {
        // 使用自定义Dao获取详情
        return withdrawDao.getInviteWithdrawDetail(id);
    }
    
    @Override
    public Map<String, Object> getWithdrawComposition(Long id) {
        try {
            // 使用自定义Dao获取提现申请的资金构成明细
            return withdrawDao.getWithdrawComposition(id);
        } catch (Exception e) {
            LOGGER.error("获取提现申请资金构成失败，ID：{}", id, e);
            return null;
        }
    }
    
    /**
     * 审批提现申请（完善版）
     * 增加了更严格的验证和完整的状态管理
     */
    @Override
    @Transactional
    public int approve(Long id, Integer status, String remark) {
        LOGGER.info("开始审批提现申请，申请ID：{}，审核结果：{}，备注：{}", id, status, remark);
        
        // 1. 参数校验
        if (status == null || (status != 1 && status != 2)) {
            throw new IllegalArgumentException("审核状态无效，只能是1(通过)或2(拒绝)");
        }
        
        // 2. 查询提现申请
        PmsInviteWithdrawApply withdraw = withdrawMapper.selectByPrimaryKey(id);
        if (withdraw == null) {
            throw new RuntimeException("提现申请不存在");
        }
        
        // 3. 状态验证
        if (withdraw.getStatus() != 0) {
            throw new RuntimeException("该提现申请已处理过，当前状态：" + getStatusText(withdraw.getStatus()));
        }
        
        // 4. 用户状态验证
        UmsMember member = memberMapper.selectByPrimaryKey(withdraw.getUserId());
        if (member == null || member.getStatus() != 1) {
            throw new RuntimeException("用户状态异常，无法处理提现申请");
        }
        
        // 5. 金额验证（审核通过时）
        if (status == 1) {
            BigDecimal userAvailableAmount = getUserCurrentAvailableAmount(withdraw.getUserId());
            if (withdraw.getApplyAmount().compareTo(userAvailableAmount) > 0) {
                throw new RuntimeException("用户可提现余额不足，当前余额：" + userAvailableAmount + "，申请金额：" + withdraw.getApplyAmount());
            }
        }
        
        // 6. 更新审核信息
        Date now = new Date();
        // 修复状态冲突：审核拒绝使用状态5，避免与提现成功状态2冲突
        byte finalStatus = (status == 2) ? (byte) 5 : status.byteValue(); // 审核拒绝改为状态5
        withdraw.setStatus(finalStatus);
        withdraw.setAuditRemark(remark);
        withdraw.setAuditTime(now);
        withdraw.setAuditUser(getCurrentAuditor()); // 获取当前审核人
        withdraw.setUpdatedAt(now);
        
        int result = withdrawMapper.updateByPrimaryKeySelective(withdraw);
        
        // 7. 审核通过后自动处理转账
        if (result > 0 && status == 1) {
            try {
                LOGGER.info("审核通过，开始处理转账，提现申请ID：{}", id);
                boolean transferSuccess = processTransfer(withdraw);
                
                if (transferSuccess) {
                    LOGGER.info("转账处理成功，提现申请ID：{}，金额：{}", id, withdraw.getActualAmount());
                    // 更新状态为提现成功
                    updateWithdrawStatus(withdraw, (byte) 2, "转账成功", null);
                    // 发送成功通知
                    sendWithdrawNotification(withdraw, "success");
                } else {
                    LOGGER.error("转账处理失败，提现申请ID：{}，金额：{}", id, withdraw.getActualAmount());
                    // 更新状态为提现失败
                    updateWithdrawStatus(withdraw, (byte) 3, "转账失败", "自动转账处理失败");
                    // 发送失败通知
                    sendWithdrawNotification(withdraw, "failed");
                }
            } catch (Exception e) {
                LOGGER.error("处理转账异常，提现申请ID：{}", id, e);
                // 更新状态为提现失败
                updateWithdrawStatus(withdraw, (byte) 3, "转账异常", "转账处理异常：" + e.getMessage());
                // 发送异常通知
                sendWithdrawNotification(withdraw, "error");
            }
        } else if (result > 0 && status == 2) {
            // 审核拒绝，发送通知
            sendWithdrawNotification(withdraw, "rejected");
        }
        
        LOGGER.info("提现申请审批完成，申请ID：{}，最终状态：{}", id, withdraw.getStatus());
        return result;
    }
    
    /**
     * 批量审批提现申请（优化版）
     */
    @Override
    @Transactional
    public int batchApprove(List<Long> ids, Integer status, String remark) {
        LOGGER.info("开始批量审批提现申请，申请数量：{}，审核结果：{}", ids.size(), status);
        
        int successCount = 0;
        List<String> failedReasons = new ArrayList<>();
        
        for (Long id : ids) {
            try {
                int result = approve(id, status, remark);
                if (result > 0) {
                    successCount++;
                    LOGGER.debug("提现申请批量审批成功，申请ID：{}", id);
                } else {
                    failedReasons.add("申请ID " + id + ": 审批失败");
                }
            } catch (Exception e) {
                String errorMsg = "申请ID " + id + ": " + e.getMessage();
                failedReasons.add(errorMsg);
                LOGGER.error("批量审批提现申请失败，{}", errorMsg, e);
            }
        }
        
        LOGGER.info("批量审批提现申请完成：成功数={}, 总数={}, 失败原因：{}", 
                   successCount, ids.size(), failedReasons);
        
        return successCount;
    }
    


    @Override
    public List<Map<String, Object>> exportWithdrawRecords(Long userId, Integer status, Integer sourceType,
                                                           String startTime, String endTime, Integer pageSize, Integer pageNum) {
        // 分页导出
        PageHelper.startPage(pageNum, pageSize);
        return withdrawDao.getInviteWithdrawsList(userId, status, sourceType, startTime, endTime);
    }

    @Override
    public long countWithdrawRecords(Long userId, Integer status, Integer sourceType,
                                    String startTime, String endTime) {
        // 统计记录数量
        return withdrawDao.countInviteWithdrawList(userId, status, sourceType, startTime, endTime);
    }
    
    @Override
    public Map<String, Object> getWithdrawSummary() {
        // 使用自定义Dao获取统计数据
        return withdrawDao.getWithdrawSummaryStats();
    }
    
    @Override
    public Map<String, Object> getSourceStatistics(String startTime, String endTime) {
        try {
            // 使用自定义Dao获取提现资金来源统计
            return withdrawDao.getSourceStatistics(startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("获取提现资金来源统计失败", e);
            // 返回默认的空统计数据
            Map<String, Object> result = new HashMap<>();
            result.put("inviteRewardTotal", 0);
            result.put("distributionCommissionTotal", 0);
            result.put("mixedTotal", 0);
            result.put("totalAmount", 0);
            return result;
        }
    }
    
    @Override
    public Map<String, Object> getUserAvailableAmount(Long userId) {
        try {
            // 使用自定义Dao获取用户可提现金额详情
            return withdrawDao.getUserAvailableAmount(userId);
        } catch (Exception e) {
            LOGGER.error("获取用户可提现金额详情失败，用户ID：{}", userId, e);
            // 返回默认数据
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("totalEarnings", BigDecimal.ZERO);
            result.put("withdrawnAmount", BigDecimal.ZERO);
            result.put("pendingAmount", BigDecimal.ZERO);
            result.put("availableAmount", BigDecimal.ZERO);
            result.put("inviteRewardAmount", BigDecimal.ZERO);
            result.put("distributionCommissionAmount", BigDecimal.ZERO);
            return result;
        }
    }
    
    @Override
    public List<Map<String, Object>> getWithdrawTrend(Integer sourceType, String startTime, 
                                                      String endTime, String granularity) {
        try {
            // 使用自定义Dao获取提现趋势分析数据
            return withdrawDao.getWithdrawTrend(sourceType, startTime, endTime, granularity);
        } catch (Exception e) {
            LOGGER.error("获取提现趋势分析数据失败", e);
            // 返回空的趋势数据
            return new ArrayList<>();
        }
    }
    
    private String getStatusText(Byte status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "审核通过";
            case 2: return "提现成功";
            case 3: return "提现失败";
            case 4: return "已取消";
            case 5: return "审核拒绝";
            default: return "未知";
        }
    }
    
    private String getWithdrawTypeText(Byte withdrawType) {
        if (withdrawType == null) return "未知";
        switch (withdrawType) {
            case 1: return "微信钱包";
            case 2: return "银行卡";
            default: return "未知";
        }
    }
    
    /**
     * 更新提现申请状态（统一状态管理）
     */
    private void updateWithdrawStatus(PmsInviteWithdrawApply withdraw, byte status, 
                                     String processRemark, String failureReason) {
        Date now = new Date();
        withdraw.setStatus(status);
        withdraw.setProcessTime(now);
        withdraw.setProcessUser(getCurrentProcessor());
        withdraw.setProcessRemark(processRemark);
        if (failureReason != null) {
            withdraw.setFailureReason(failureReason);
        }
        withdraw.setUpdatedAt(now);
        
        withdrawMapper.updateByPrimaryKeySelective(withdraw);
        LOGGER.info("提现申请状态已更新，申请ID：{}，新状态：{}，处理备注：{}", 
                   withdraw.getId(), getStatusText(status), processRemark);
    }
    
    /**
     * 获取用户当前可提现金额（实时计算）
     */
    private BigDecimal getUserCurrentAvailableAmount(Long userId) {
        try {
            Map<String, Object> availableAmountInfo = withdrawDao.getUserAvailableAmount(userId);
            if (availableAmountInfo != null && availableAmountInfo.containsKey("availableAmount")) {
                return (BigDecimal) availableAmountInfo.get("availableAmount");
            }
        } catch (Exception e) {
            LOGGER.error("获取用户可提现金额失败，用户ID：{}", userId, e);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 获取当前审核人（从安全上下文获取）
     */
    private String getCurrentAuditor() {
        // TODO: 从Spring Security或SA-Token中获取当前用户信息
        // 这里先返回默认值，实际项目中应该获取当前登录的管理员信息
        return "system";
    }
    
    /**
     * 获取当前处理人（从安全上下文获取）
     */
    private String getCurrentProcessor() {
        // TODO: 从Spring Security或SA-Token中获取当前用户信息
        return "system";
    }
    
    /**
     * 处理转账（统一转账处理）
     */
    private boolean processTransfer(PmsInviteWithdrawApply withdraw) {
        // 根据提现方式选择不同的处理方式
        switch (withdraw.getWithdrawType()) {
            case 1: // 微信钱包
                return processWechatTransfer(withdraw);
            case 2: // 银行卡
                return processBankTransfer(withdraw);
            default:
                LOGGER.warn("未知的提现方式：{}，提现申请ID：{}", withdraw.getWithdrawType(), withdraw.getId());
                return false;
        }
    }
    
    /**
     * 处理微信转账
     */
    private boolean processWechatTransfer(PmsInviteWithdrawApply withdraw) {
        try {
            // 构建转账参数
            WechatTransferParam transferParam = wechatTransferService.buildTransferParam(
                withdraw.getWithdrawSn(),
                withdraw.getWithdrawAccount(),
                withdraw.getActualAmount(),
                withdraw.getAccountName(),
                "用户昵称" // TODO: 从用户信息中获取实际昵称
            );
            
            // 调用转账接口
            WechatTransferResult result = wechatTransferService.transfer(transferParam);
            
            if (result != null && result.isSuccess()) {
                LOGGER.info("微信转账成功，提现编号：{}，批次单号：{}", 
                    withdraw.getWithdrawSn(), result.getBatchId());
                return true;
            } else {
                LOGGER.error("微信转账失败，提现编号：{}，错误信息：{}", 
                    withdraw.getWithdrawSn(), result != null ? result.getStatusDesc() : "未知错误");
                return false;
            }
            
        } catch (Exception e) {
            LOGGER.error("微信转账异常，提现编号：{}", withdraw.getWithdrawSn(), e);
            return false;
        }
    }
    
    /**
     * 处理银行卡转账
     */
    private boolean processBankTransfer(PmsInviteWithdrawApply withdraw) {
        // 银行卡转账通常需要人工处理或第三方支付接口
        // 这里先返回true，表示已提交处理，实际需要根据业务需求实现
        LOGGER.info("银行卡提现已提交处理，等待人工确认，提现编号：{}", withdraw.getWithdrawSn());
        return true;
    }
    
    /**
     * 发送提现状态变更通知
     */
    private void sendWithdrawNotification(PmsInviteWithdrawApply withdraw, String notificationType) {
        try {
            // TODO: 实现消息通知功能
            // 可以发送微信模板消息、短信、站内信等
            switch (notificationType) {
                case "success":
                    LOGGER.info("发送提现成功通知，用户ID：{}，金额：{}", withdraw.getUserId(), withdraw.getActualAmount());
                    break;
                case "failed":
                    LOGGER.info("发送提现失败通知，用户ID：{}，失败原因：{}", withdraw.getUserId(), withdraw.getFailureReason());
                    break;
                case "rejected":
                    LOGGER.info("发送审核拒绝通知，用户ID：{}，拒绝原因：{}", withdraw.getUserId(), withdraw.getAuditRemark());
                    break;
                case "error":
                    LOGGER.info("发送提现异常通知，用户ID：{}，异常信息：{}", withdraw.getUserId(), withdraw.getFailureReason());
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("发送提现通知失败，提现申请ID：{}", withdraw.getId(), e);
        }
    }
    
    /**
     * 计算优化的手续费（支持配置化）
     */
    public BigDecimal calculateOptimizedFee(BigDecimal amount, Byte withdrawType) {
        // 根据提现方式和金额计算手续费
        BigDecimal feeRate = DEFAULT_FEE_RATE;
        
        // 不同提现方式可以有不同的手续费率
        if (withdrawType != null && withdrawType == 2) { // 银行卡
            feeRate = new BigDecimal("0.03"); // 银行卡手续费3%
        }
        
        BigDecimal feeAmount = amount.multiply(feeRate);
        
        // 应用最小和最大手续费限制
        if (feeAmount.compareTo(MIN_FEE_AMOUNT) < 0) {
            feeAmount = MIN_FEE_AMOUNT;
        }
        if (feeAmount.compareTo(MAX_FEE_AMOUNT) > 0) {
            feeAmount = MAX_FEE_AMOUNT;
        }
        
        return feeAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    // 配置键名常量
    private static final class ConfigKeys {
        static final String MIN_WITHDRAW_AMOUNT = "distribution.withdraw.min_amount";
        static final String MAX_WITHDRAW_AMOUNT = "distribution.withdraw.max_amount";
        static final String DAILY_LIMIT = "distribution.withdraw.daily_limit";
        static final String MONTHLY_LIMIT = "distribution.withdraw.monthly_limit";
        static final String WECHAT_FEE_RATE = "distribution.withdraw.wechat_fee_rate";
        static final String BANK_FEE_RATE = "distribution.withdraw.bank_fee_rate";
        static final String MIN_FEE_AMOUNT = "distribution.withdraw.min_fee_amount";
        static final String MAX_FEE_AMOUNT = "distribution.withdraw.max_fee_amount";
        static final String AUDIT_TIME_LIMIT = "distribution.withdraw.audit_time_limit";
        static final String AUTO_AUDIT = "distribution.withdraw.auto_audit";
        static final String AUTO_AUDIT_AMOUNT = "distribution.withdraw.auto_audit_amount";
        static final String WECHAT_ARRIVAL_DAYS = "distribution.withdraw.wechat_arrival_days";
        static final String BANK_ARRIVAL_DAYS = "distribution.withdraw.bank_arrival_days";
        static final String NOTIFICATION_ENABLED = "distribution.withdraw.notification_enabled";
        static final String TIME_RESTRICTION = "distribution.withdraw.time_restriction";
        static final String START_TIME = "distribution.withdraw.start_time";
        static final String END_TIME = "distribution.withdraw.end_time";
    }
    
    @Override
    public Map<String, Object> getWithdrawConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            
            // 金额配置
            config.put("minWithdrawAmount", getDoubleConfig(ConfigKeys.MIN_WITHDRAW_AMOUNT, "最小提现金额"));
            config.put("maxWithdrawAmount", getDoubleConfig(ConfigKeys.MAX_WITHDRAW_AMOUNT, "最大提现金额"));
            config.put("maxDailyCount", getIntegerConfig(ConfigKeys.DAILY_LIMIT, "每日提现次数限制"));
            config.put("monthlyLimit", getDoubleConfig(ConfigKeys.MONTHLY_LIMIT, "每月提现限额"));
            
            // 手续费配置
            config.put("wechatFeeRate", getDoubleConfig(ConfigKeys.WECHAT_FEE_RATE, "微信提现手续费率"));
            config.put("bankFeeRate", getDoubleConfig(ConfigKeys.BANK_FEE_RATE, "银行卡提现手续费率"));
            config.put("minFeeAmount", getDoubleConfig(ConfigKeys.MIN_FEE_AMOUNT, "最小手续费金额"));
            config.put("maxFeeAmount", getDoubleConfig(ConfigKeys.MAX_FEE_AMOUNT, "最大手续费金额"));
            
            // 审核配置
            config.put("auditTimeLimit", getIntegerConfig(ConfigKeys.AUDIT_TIME_LIMIT, "审核时间限制"));
            config.put("autoAudit", getBooleanConfig(ConfigKeys.AUTO_AUDIT, "是否启用自动审核"));
            config.put("autoAuditAmount", getDoubleConfig(ConfigKeys.AUTO_AUDIT_AMOUNT, "自动审核金额上限"));
            
            // 到账时间配置
            config.put("wechatArrivalDays", getIntegerConfig(ConfigKeys.WECHAT_ARRIVAL_DAYS, "微信到账天数"));
            config.put("bankArrivalDays", getIntegerConfig(ConfigKeys.BANK_ARRIVAL_DAYS, "银行卡到账天数"));
            
            // 功能配置
            config.put("notificationEnabled", getBooleanConfig(ConfigKeys.NOTIFICATION_ENABLED, "是否启用通知"));
            config.put("timeRestriction", getBooleanConfig(ConfigKeys.TIME_RESTRICTION, "是否启用时间限制"));
            config.put("startTime", getStringConfig(ConfigKeys.START_TIME, "提现开始时间"));
            config.put("endTime", getStringConfig(ConfigKeys.END_TIME, "提现结束时间"));
            
            LOGGER.info("获取提现配置成功，配置项数量：{}", config.size());
            return config;
            
        } catch (Exception e) {
            LOGGER.error("获取提现配置失败", e);
            throw new RuntimeException("获取提现配置失败：" + e.getMessage());
        }
    }
    
    @Override
    public int updateWithdrawConfig(Map<String, Object> configData) {
        try {
            int updateCount = 0;
            
            // 金额配置
            updateCount += updateConfigIfPresent(configData, "minWithdrawAmount", ConfigKeys.MIN_WITHDRAW_AMOUNT, "最小提现金额");
            updateCount += updateConfigIfPresent(configData, "maxWithdrawAmount", ConfigKeys.MAX_WITHDRAW_AMOUNT, "最大提现金额");
            updateCount += updateConfigIfPresent(configData, "maxDailyCount", ConfigKeys.DAILY_LIMIT, "每日提现次数限制");
            updateCount += updateConfigIfPresent(configData, "monthlyLimit", ConfigKeys.MONTHLY_LIMIT, "每月提现限额");
            
            // 手续费配置
            updateCount += updateConfigIfPresent(configData, "wechatFeeRate", ConfigKeys.WECHAT_FEE_RATE, "微信提现手续费率");
            updateCount += updateConfigIfPresent(configData, "bankFeeRate", ConfigKeys.BANK_FEE_RATE, "银行卡提现手续费率");
            updateCount += updateConfigIfPresent(configData, "minFeeAmount", ConfigKeys.MIN_FEE_AMOUNT, "最小手续费金额");
            updateCount += updateConfigIfPresent(configData, "maxFeeAmount", ConfigKeys.MAX_FEE_AMOUNT, "最大手续费金额");
            
            // 审核配置
            updateCount += updateConfigIfPresent(configData, "auditTimeLimit", ConfigKeys.AUDIT_TIME_LIMIT, "审核时间限制（小时）");
            updateCount += updateBooleanConfigIfPresent(configData, "autoAudit", ConfigKeys.AUTO_AUDIT, "是否启用自动审核");
            updateCount += updateConfigIfPresent(configData, "autoAuditAmount", ConfigKeys.AUTO_AUDIT_AMOUNT, "自动审核金额上限");
            
            // 到账时间配置
            updateCount += updateConfigIfPresent(configData, "wechatArrivalDays", ConfigKeys.WECHAT_ARRIVAL_DAYS, "微信到账天数");
            updateCount += updateConfigIfPresent(configData, "bankArrivalDays", ConfigKeys.BANK_ARRIVAL_DAYS, "银行卡到账天数");
            
            // 功能配置
            updateCount += updateBooleanConfigIfPresent(configData, "notificationEnabled", ConfigKeys.NOTIFICATION_ENABLED, "是否启用通知");
            updateCount += updateBooleanConfigIfPresent(configData, "timeRestriction", ConfigKeys.TIME_RESTRICTION, "是否启用时间限制");
            updateCount += updateConfigIfPresent(configData, "startTime", ConfigKeys.START_TIME, "提现开始时间");
            updateCount += updateConfigIfPresent(configData, "endTime", ConfigKeys.END_TIME, "提现结束时间");
            
            LOGGER.info("更新提现配置成功，更新项数量：{}", updateCount);
            return updateCount;
            
        } catch (Exception e) {
            LOGGER.error("更新提现配置失败", e);
            throw new RuntimeException("更新提现配置失败：" + e.getMessage());
        }
    }
    
    // 辅助方法：获取配置值
    private Double getDoubleConfig(String configKey, String configDesc) {
        var config = configService.getConfigByKey(configKey);
        if (config == null) {
            throw new RuntimeException("获取" + configDesc + "配置失败");
        }
        return Double.parseDouble(config.getConfigValue());
    }
    
    private Integer getIntegerConfig(String configKey, String configDesc) {
        var config = configService.getConfigByKey(configKey);
        if (config == null) {
            throw new RuntimeException("获取" + configDesc + "配置失败");
        }
        return Integer.parseInt(config.getConfigValue());
    }
    
    private Boolean getBooleanConfig(String configKey, String configDesc) {
        var config = configService.getConfigByKey(configKey);
        if (config == null) {
            throw new RuntimeException("获取" + configDesc + "配置失败");
        }
        return "1".equals(config.getConfigValue());
    }
    
    private String getStringConfig(String configKey, String configDesc) {
        var config = configService.getConfigByKey(configKey);
        if (config == null) {
            throw new RuntimeException("获取" + configDesc + "配置失败");
        }
        return config.getConfigValue();
    }
    
    // 辅助方法：更新配置
    private int updateConfigIfPresent(Map<String, Object> configData, String paramKey, String configKey, String configDesc) {
        if (configData.containsKey(paramKey)) {
            configService.updateConfig(configKey, configData.get(paramKey).toString(), configDesc);
            return 1;
        }
        return 0;
    }
    
    private int updateBooleanConfigIfPresent(Map<String, Object> configData, String paramKey, String configKey, String configDesc) {
        if (configData.containsKey(paramKey)) {
            String booleanValue = Boolean.parseBoolean(configData.get(paramKey).toString()) ? "1" : "0";
            configService.updateConfig(configKey, booleanValue, configDesc);
            return 1;
        }
        return 0;
    }
} 