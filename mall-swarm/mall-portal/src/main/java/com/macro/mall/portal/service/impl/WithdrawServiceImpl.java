package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.portal.service.WithdrawService;
import com.macro.mall.portal.service.UmsMemberBalanceService;
import com.macro.mall.common.service.WechatTransferService;
import com.macro.mall.dto.WechatTransferParam;
import com.macro.mall.dto.WechatTransferResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 提现服务实现类（完善版）
 * 用户端提现申请和管理功能
 * Updated on 2025/01/04 - 完善提现管理流程
 */
@Service
public class WithdrawServiceImpl implements WithdrawService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawServiceImpl.class);
    
    @Autowired
    private PmsInviteWithdrawApplyMapper withdrawApplyMapper;
    
    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;
    
    @Autowired
    private PmsInviteStatisticsMapper inviteStatisticsMapper;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private PmsSystemConfigMapper systemConfigMapper;

    @Autowired
    private UmsMemberBalanceService memberBalanceService;

    @Autowired
    private WechatTransferService wechatTransferService;

    // 提现配置键名常量（与管理后台保持一致）
    private static final String CONFIG_MIN_WITHDRAW_AMOUNT = "distribution.withdraw.min_amount";
    private static final String CONFIG_MAX_WITHDRAW_AMOUNT = "distribution.withdraw.max_amount";
    private static final String CONFIG_MAX_DAILY_COUNT = "distribution.withdraw.daily_limit";
    private static final String CONFIG_WECHAT_FEE_RATE = "distribution.withdraw.wechat_fee_rate";
    private static final String CONFIG_BANK_FEE_RATE = "distribution.withdraw.bank_fee_rate";
    private static final String CONFIG_MIN_FEE_AMOUNT = "distribution.withdraw.min_fee_amount";
    private static final String CONFIG_MAX_FEE_AMOUNT = "distribution.withdraw.max_fee_amount";
    private static final String CONFIG_WECHAT_ENABLED = "distribution.withdraw.wechat_enabled";
    private static final String CONFIG_BANK_ENABLED = "distribution.withdraw.bank_enabled";
    private static final String CONFIG_BALANCE_ENABLED = "distribution.withdraw.balance_enabled";
    private static final String CONFIG_WECHAT_ARRIVAL_TIME = "distribution.withdraw.wechat_arrival_time";
    private static final String CONFIG_BANK_ARRIVAL_TIME = "distribution.withdraw.bank_arrival_time";
    private static final String CONFIG_BALANCE_ARRIVAL_TIME = "distribution.withdraw.balance_arrival_time";
    private static final String CONFIG_NOTICE_TEXT = "distribution.withdraw.notice_text";

    // 默认配置值（当数据库中没有配置时使用）
    private static final BigDecimal DEFAULT_MIN_WITHDRAW_AMOUNT = new BigDecimal("10.00");
    private static final BigDecimal DEFAULT_MAX_WITHDRAW_AMOUNT = new BigDecimal("5000.00");
    private static final BigDecimal DEFAULT_WECHAT_FEE_RATE = new BigDecimal("0.02");
    private static final BigDecimal DEFAULT_BANK_FEE_RATE = new BigDecimal("0.03");
    private static final BigDecimal DEFAULT_MIN_FEE_AMOUNT = new BigDecimal("0.01");
    private static final BigDecimal DEFAULT_MAX_FEE_AMOUNT = new BigDecimal("50.00");
    private static final int DEFAULT_MAX_DAILY_COUNT = 3;

    @Override
    public BigDecimal getAvailableAmount(Long userId) {
        try {
            // 查询用户的总佣金收入（已结算）
            PmsInviteRewardExample rewardExample = new PmsInviteRewardExample();
            rewardExample.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andStatusEqualTo((byte) 1) // 已发放
                    .andCommissionTypeIn(Arrays.asList((byte) 2, (byte) 3)) // 分销佣金
                    .andSettlementStatusEqualTo((byte) 1); // 已结算
            
            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(rewardExample);
            BigDecimal totalCommission = rewards.stream()
                    .map(PmsInviteReward::getRewardValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 查询已提现金额（审核通过或提现成功的申请）
            PmsInviteWithdrawApplyExample withdrawExample = new PmsInviteWithdrawApplyExample();
            withdrawExample.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andStatusIn(Arrays.asList((byte) 1, (byte) 2)); // 审核通过或提现成功
            
            List<PmsInviteWithdrawApply> withdraws = withdrawApplyMapper.selectByExample(withdrawExample);
            BigDecimal totalWithdraw = withdraws.stream()
                    .map(PmsInviteWithdrawApply::getApplyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 可提现金额 = 总佣金 - 已提现金额
            BigDecimal availableAmount = totalCommission.subtract(totalWithdraw);
            return availableAmount.max(BigDecimal.ZERO);
            
        } catch (Exception e) {
            LOGGER.error("获取可提现金额失败, userId: {}", userId, e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<String, Object> getWithdrawConfig() {
        Map<String, Object> config = new HashMap<>();

        try {
            // 基本配置（从数据库获取）
            config.put("minAmount", getBigDecimalConfig(CONFIG_MIN_WITHDRAW_AMOUNT, DEFAULT_MIN_WITHDRAW_AMOUNT));
            config.put("maxAmount", getBigDecimalConfig(CONFIG_MAX_WITHDRAW_AMOUNT, DEFAULT_MAX_WITHDRAW_AMOUNT));
            config.put("maxDailyCount", getIntegerConfig(CONFIG_MAX_DAILY_COUNT, DEFAULT_MAX_DAILY_COUNT));

            // 手续费配置（从数据库获取，只保留微信相关）
            config.put("wechatFeeRate", getBigDecimalConfig(CONFIG_WECHAT_FEE_RATE, DEFAULT_WECHAT_FEE_RATE));
            config.put("balanceFeeRate", BigDecimal.ZERO); // 文创余额免手续费
            
            // 提现方式配置（只支持微信和文创余额）
            List<Map<String, Object>> withdrawTypes = new ArrayList<>();

            // 微信零钱
            Map<String, Object> wechat = new HashMap<>();
            wechat.put("type", 1);
            wechat.put("name", "微信零钱");
            wechat.put("description", "提现到微信零钱，" + getStringConfig(CONFIG_WECHAT_ARRIVAL_TIME, "1-3个工作日到账"));
            wechat.put("feeRate", getBigDecimalConfig(CONFIG_WECHAT_FEE_RATE, DEFAULT_WECHAT_FEE_RATE));
            wechat.put("enabled", true);
            withdrawTypes.add(wechat);

            // 文创余额
            Map<String, Object> balance = new HashMap<>();
            balance.put("type", 3);
            balance.put("name", "文创余额");
            balance.put("description", "存入文创余额，" + getStringConfig(CONFIG_BALANCE_ARRIVAL_TIME, "立即到账"));
            balance.put("feeRate", BigDecimal.ZERO);
            balance.put("enabled", true);
            withdrawTypes.add(balance);

            config.put("withdrawTypes", withdrawTypes);
            
            // 提现说明（从数据库获取）
            String noticeText = getStringConfig(CONFIG_NOTICE_TEXT, null);
            List<String> notes;
            if (noticeText != null && noticeText.startsWith("[") && noticeText.endsWith("]")) {
                // 尝试解析JSON格式的说明文字
                try {
                    // 简单的JSON数组解析
                    noticeText = noticeText.substring(1, noticeText.length() - 1); // 去掉[]
                    String[] noteArray = noticeText.split("\",\"");
                    notes = new ArrayList<>();
                    for (String note : noteArray) {
                        notes.add(note.replace("\"", "").trim());
                    }
                } catch (Exception e) {
                    LOGGER.warn("解析提现说明配置失败，使用默认配置", e);
                    notes = getDefaultNotes();
                }
            } else {
                notes = getDefaultNotes();
            }
            config.put("notes", notes);
            
        } catch (Exception e) {
            LOGGER.error("获取提现配置失败", e);
        }
        
        return config;
    }

    /**
     * 申请提现（完善版）
     * 增加了更严格的验证和优化的手续费计算
     */
    @Override
    @Transactional
    public Boolean applyWithdraw(Long userId, WithdrawApplyParam param) {
        LOGGER.info("用户申请提现，用户ID: {}, 提现金额: {}, 提现方式: {}", userId, param.getApplyAmount(), param.getWithdrawType());
        
        try {
            // 1. 基础参数验证
            validateWithdrawParam(param);
            
            // 2. 用户状态验证
            UmsMember member = validateUserStatus(userId);
            
            // 3. 提现金额验证
            validateWithdrawAmount(userId, param.getApplyAmount());
            
            // 4. 提现频次验证
            validateWithdrawFrequency(userId);
            
            // 5. 计算手续费
            BigDecimal feeAmount = calculateFee(param.getApplyAmount(), param.getWithdrawType());
            BigDecimal actualAmount = param.getApplyAmount().subtract(feeAmount);
            
            // 6. 创建提现申请记录
            PmsInviteWithdrawApply withdrawApply = createWithdrawApply(userId, param, feeAmount, actualAmount, member);
            
            // 7. 保存提现申请
            int result = withdrawApplyMapper.insertSelective(withdrawApply);

            if (result > 0) {
                // 8. 根据提现方式进行不同处理
                if (param.getWithdrawType() == 3) {
                    // 存入文创余额，立即处理
                    processBalanceTransfer(withdrawApply, userId, actualAmount);
                } else if (param.getWithdrawType() == 1) {
                    // 微信零钱提现，调用微信转账服务
                    processWechatTransfer(withdrawApply, member);
                }
                // 银行卡提现(type=2)保持原有审核流程

                LOGGER.info("提现申请提交成功，申请ID: {}, 用户ID: {}, 申请金额: {}, 手续费: {}, 实际到账: {}",
                          withdrawApply.getId(), userId, param.getApplyAmount(), feeAmount, actualAmount);
                return true;
            } else {
                throw new RuntimeException("提现申请保存失败");
            }
            
        } catch (Exception e) {
            LOGGER.error("提现申请失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("提现申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证提现参数
     */
    private void validateWithdrawParam(WithdrawApplyParam param) {
        if (param.getApplyAmount() == null || param.getApplyAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }
        
        if (param.getWithdrawType() == null || (param.getWithdrawType() != 1 && param.getWithdrawType() != 2 && param.getWithdrawType() != 3)) {
            throw new RuntimeException("提现方式无效");
        }

        // 存入文创余额不需要验证账户信息
        if (param.getWithdrawType() != 3) {
            if (param.getWithdrawAccount() == null || param.getWithdrawAccount().trim().isEmpty()) {
                throw new RuntimeException("提现账户不能为空");
            }

            if (param.getAccountName() == null || param.getAccountName().trim().isEmpty()) {
                throw new RuntimeException("账户名称不能为空");
            }
        }
    }
    
    /**
     * 验证用户状态
     */
    private UmsMember validateUserStatus(Long userId) {
        UmsMember member = memberMapper.selectByPrimaryKey(userId);
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (member.getStatus() != 1) {
            throw new RuntimeException("用户状态异常，无法提现");
        }
        
        return member;
    }
    
    /**
     * 验证提现金额
     */
    private void validateWithdrawAmount(Long userId, BigDecimal applyAmount) {
        // 从数据库获取金额限制配置
        BigDecimal minAmount = getBigDecimalConfig(CONFIG_MIN_WITHDRAW_AMOUNT, DEFAULT_MIN_WITHDRAW_AMOUNT);
        BigDecimal maxAmount = getBigDecimalConfig(CONFIG_MAX_WITHDRAW_AMOUNT, DEFAULT_MAX_WITHDRAW_AMOUNT);

        // 检查金额范围
        if (applyAmount.compareTo(minAmount) < 0) {
            throw new RuntimeException("提现金额不能少于" + minAmount + "元");
        }
        if (applyAmount.compareTo(maxAmount) > 0) {
            throw new RuntimeException("提现金额不能超过" + maxAmount + "元");
        }
        
        // 检查可提现余额
        BigDecimal availableAmount = getAvailableAmount(userId);
        if (applyAmount.compareTo(availableAmount) > 0) {
            throw new RuntimeException("可提现余额不足，当前可提现: " + availableAmount + "元");
        }
    }
    
    /**
     * 验证提现频次
     */
    private void validateWithdrawFrequency(Long userId) throws ParseException {
        // 检查今日提现次数
        LocalDate today = LocalDate.now();
        String todayStart = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
        String todayEnd = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";
        
        PmsInviteWithdrawApplyExample todayExample = new PmsInviteWithdrawApplyExample();
        try {
            Date startDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(todayStart);
            Date endDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(todayEnd);
            
            todayExample.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andCreatedAtGreaterThanOrEqualTo(startDate)
                    .andCreatedAtLessThanOrEqualTo(endDate);
                    
            long todayCount = withdrawApplyMapper.countByExample(todayExample);
            Integer maxDailyCount = getIntegerConfig(CONFIG_MAX_DAILY_COUNT, DEFAULT_MAX_DAILY_COUNT);
            if (todayCount >= maxDailyCount) {
                throw new RuntimeException("今日提现次数已达上限(" + maxDailyCount + "次)，请明日再试");
            }
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            }
            LOGGER.warn("验证提现频次时解析日期失败", e);
        }
        
        // 检查是否有待审核的申请
        PmsInviteWithdrawApplyExample pendingExample = new PmsInviteWithdrawApplyExample();
        pendingExample.createCriteria()
                .andUserIdEqualTo(userId)
                .andStatusEqualTo((byte) 0); // 待审核
        List<PmsInviteWithdrawApply> pendingApplies = withdrawApplyMapper.selectByExample(pendingExample);
        if (!pendingApplies.isEmpty()) {
            throw new RuntimeException("您有待审核的提现申请，请等待处理完成后再申请");
        }
    }
    
    /**
     * 计算手续费（优化版）
     */
    @Override
    public BigDecimal calculateFee(BigDecimal amount, Byte withdrawType) {
        if (withdrawType == 3) {
            // 存入文创余额不收手续费
            return BigDecimal.ZERO;
        }

        // 从数据库获取手续费配置
        BigDecimal feeRate = withdrawType == 1 ?
            getBigDecimalConfig(CONFIG_WECHAT_FEE_RATE, DEFAULT_WECHAT_FEE_RATE) :
            getBigDecimalConfig(CONFIG_BANK_FEE_RATE, DEFAULT_BANK_FEE_RATE);

        BigDecimal minFeeAmount = getBigDecimalConfig(CONFIG_MIN_FEE_AMOUNT, DEFAULT_MIN_FEE_AMOUNT);
        BigDecimal maxFeeAmount = getBigDecimalConfig(CONFIG_MAX_FEE_AMOUNT, DEFAULT_MAX_FEE_AMOUNT);

        BigDecimal feeAmount = amount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);

        // 应用手续费限制
        if (feeAmount.compareTo(minFeeAmount) < 0) {
            feeAmount = minFeeAmount;
        }
        if (feeAmount.compareTo(maxFeeAmount) > 0) {
            feeAmount = maxFeeAmount;
        }

        return feeAmount;
    }
    
    /**
     * 创建提现申请记录
     */
    private PmsInviteWithdrawApply createWithdrawApply(Long userId, WithdrawApplyParam param, 
                                                       BigDecimal feeAmount, BigDecimal actualAmount, 
                                                       UmsMember member) {
        PmsInviteWithdrawApply withdrawApply = new PmsInviteWithdrawApply();
        
        // 基本信息
        withdrawApply.setUserId(userId);
        withdrawApply.setWithdrawSn(generateWithdrawSn());
        withdrawApply.setApplyAmount(param.getApplyAmount());
        withdrawApply.setFeeAmount(feeAmount);
        withdrawApply.setActualAmount(actualAmount);
        
        // 提现方式和账户信息
        withdrawApply.setWithdrawType(param.getWithdrawType());
        withdrawApply.setWithdrawAccount(param.getWithdrawAccount());
        withdrawApply.setAccountName(param.getAccountName());
        
        // 状态和时间
        withdrawApply.setStatus((byte) 0); // 待审核
        withdrawApply.setApplyTime(new Date());
        withdrawApply.setCreatedAt(new Date());
        withdrawApply.setUpdatedAt(new Date());
        
        // 备注信息
        withdrawApply.setRemark(param.getRemark());
        
        // 预计到账时间
        Calendar calendar = Calendar.getInstance();
        if (param.getWithdrawType() == 3) {
            // 存入文创余额立即到账
            withdrawApply.setExpectedArrivalTime(calendar.getTime());
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, param.getWithdrawType() == 1 ? 3 : 5); // 微信3天，银行卡5天
            withdrawApply.setExpectedArrivalTime(calendar.getTime());
        }
        
        return withdrawApply;
    }

    /**
     * 生成提现申请单号（优化版）
     * 格式：WD + yyyyMMdd + HHmmss + 随机3位数
     */
    private String generateWithdrawSn() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = (int) (Math.random() * 900) + 100; // 100-999随机数
        return "WD" + datePart + randomPart;
    }

    @Override
    public CommonPage<Map<String, Object>> getMyWithdrawRecords(Long userId, Integer pageNum, Integer pageSize, Byte status) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            
            PmsInviteWithdrawApplyExample example = new PmsInviteWithdrawApplyExample();
            PmsInviteWithdrawApplyExample.Criteria criteria = example.createCriteria().andUserIdEqualTo(userId);
            
            if (status != null) {
                criteria.andStatusEqualTo(status);
            }
            
            example.setOrderByClause("apply_time DESC");
            
            List<PmsInviteWithdrawApply> withdraws = withdrawApplyMapper.selectByExample(example);
            PageInfo<PmsInviteWithdrawApply> pageInfo = new PageInfo<>(withdraws);
            
            List<Map<String, Object>> records = new ArrayList<>();
            for (PmsInviteWithdrawApply withdraw : withdraws) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", withdraw.getId());
                record.put("withdrawSn", withdraw.getWithdrawSn());
                record.put("applyAmount", withdraw.getApplyAmount());
                record.put("feeAmount", withdraw.getFeeAmount());
                record.put("actualAmount", withdraw.getActualAmount());
                record.put("withdrawType", withdraw.getWithdrawType());
                String withdrawTypeName = "";
                switch (withdraw.getWithdrawType()) {
                    case 1:
                        withdrawTypeName = "微信钱包";
                        break;
                    case 2:
                        withdrawTypeName = "银行卡";
                        break;
                    case 3:
                        withdrawTypeName = "文创余额";
                        break;
                    default:
                        withdrawTypeName = "未知";
                        break;
                }
                record.put("withdrawTypeName", withdrawTypeName);
                record.put("accountName", withdraw.getAccountName());
                record.put("status", withdraw.getStatus());
                record.put("applyTime", withdraw.getApplyTime());
                record.put("auditTime", withdraw.getAuditTime());
                record.put("processTime", withdraw.getProcessTime());
                record.put("actualArrivalTime", withdraw.getActualArrivalTime());
                record.put("auditRemark", withdraw.getAuditRemark());
                record.put("failureReason", withdraw.getFailureReason());
                
                // 状态描述
                String statusDesc = "";
                switch (withdraw.getStatus()) {
                    case 0:
                        statusDesc = "待审核";
                        break;
                    case 1:
                        statusDesc = "审核通过";
                        break;
                    case 2:
                        statusDesc = "提现成功";
                        break;
                    case 3:
                        statusDesc = "提现失败";
                        break;
                    case 4:
                        statusDesc = "已取消";
                        break;
                    case 5:
                        statusDesc = "审核拒绝";
                        break;
                }
                record.put("statusDesc", statusDesc);
                
                records.add(record);
            }
            
            return CommonPage.restPage(records);
        } catch (Exception e) {
            LOGGER.error("获取提现记录失败, userId: {}", userId, e);
            return CommonPage.restPage(new ArrayList<>());
        }
    }

    @Override
    public Map<String, Object> getWithdrawDetail(Long withdrawId, Long userId) {
        try {
            // 查询提现记录
            PmsInviteWithdrawApply withdraw = withdrawApplyMapper.selectByPrimaryKey(withdrawId);
            if (withdraw == null || !withdraw.getUserId().equals(userId)) {
                return null;
            }
            
            Map<String, Object> detail = new HashMap<>();
            detail.put("id", withdraw.getId());
            detail.put("withdrawSn", withdraw.getWithdrawSn());
            detail.put("applyAmount", withdraw.getApplyAmount());
            detail.put("feeAmount", withdraw.getFeeAmount());
            detail.put("actualAmount", withdraw.getActualAmount());
            detail.put("withdrawType", withdraw.getWithdrawType());
            detail.put("withdrawTypeName", withdraw.getWithdrawType() == 1 ? "微信钱包" : "银行卡");
            detail.put("withdrawAccount", withdraw.getWithdrawAccount());
            detail.put("accountName", withdraw.getAccountName());
            detail.put("status", withdraw.getStatus());
            detail.put("applyTime", withdraw.getApplyTime());
            detail.put("auditTime", withdraw.getAuditTime());
            detail.put("processTime", withdraw.getProcessTime());
            detail.put("expectedArrivalTime", withdraw.getExpectedArrivalTime());
            detail.put("actualArrivalTime", withdraw.getActualArrivalTime());
            detail.put("auditUser", withdraw.getAuditUser());
            detail.put("processUser", withdraw.getProcessUser());
            detail.put("remark", withdraw.getRemark());
            detail.put("auditRemark", withdraw.getAuditRemark());
            detail.put("processRemark", withdraw.getProcessRemark());
            detail.put("failureReason", withdraw.getFailureReason());
            
            // 状态描述
            String statusDesc = "";
            switch (withdraw.getStatus()) {
                case 0:
                    statusDesc = "待审核";
                    break;
                case 1:
                    statusDesc = "审核通过";
                    break;
                case 2:
                    statusDesc = "提现成功";
                    break;
                case 3:
                    statusDesc = "提现失败";
                    break;
                case 4:
                    statusDesc = "已取消";
                    break;
                case 5:
                    statusDesc = "审核拒绝";
                    break;
            }
            detail.put("statusDesc", statusDesc);
            
            return detail;
        } catch (Exception e) {
            LOGGER.error("获取提现详情失败, withdrawId: {}, userId: {}", withdrawId, userId, e);
            return null;
        }
    }

    @Override
    @Transactional
    public Boolean cancelWithdraw(Long withdrawId, Long userId) {
        try {
            // 查询提现记录
            PmsInviteWithdrawApply withdraw = withdrawApplyMapper.selectByPrimaryKey(withdrawId);
            if (withdraw == null || !withdraw.getUserId().equals(userId)) {
                throw new RuntimeException("提现记录不存在");
            }
            
            // 只有待审核状态才能取消
            if (withdraw.getStatus() != 0) {
                throw new RuntimeException("当前状态不允许取消");
            }
            
            // 更新状态为已取消
            PmsInviteWithdrawApply update = new PmsInviteWithdrawApply();
            update.setId(withdrawId);
            update.setStatus((byte) 4); // 已取消
            update.setUpdatedAt(new Date());
            
            withdrawApplyMapper.updateByPrimaryKeySelective(update);
            
            return true;
        } catch (Exception e) {
            LOGGER.error("取消提现失败, withdrawId: {}, userId: {}", withdrawId, userId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getWithdrawStatistics(Long userId) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 总申请次数
            PmsInviteWithdrawApplyExample totalExample = new PmsInviteWithdrawApplyExample();
            totalExample.createCriteria().andUserIdEqualTo(userId);
            long totalCount = withdrawApplyMapper.countByExample(totalExample);
            statistics.put("totalCount", totalCount);
            
            // 成功提现次数
            PmsInviteWithdrawApplyExample successExample = new PmsInviteWithdrawApplyExample();
            successExample.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo((byte) 2);
            long successCount = withdrawApplyMapper.countByExample(successExample);
            statistics.put("successCount", successCount);
            
            // 总提现金额
            PmsInviteWithdrawApplyExample amountExample = new PmsInviteWithdrawApplyExample();
            amountExample.createCriteria().andUserIdEqualTo(userId).andStatusIn(Arrays.asList((byte) 1, (byte) 2));
            List<PmsInviteWithdrawApply> withdraws = withdrawApplyMapper.selectByExample(amountExample);
            BigDecimal totalAmount = withdraws.stream()
                    .map(PmsInviteWithdrawApply::getApplyAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.put("totalAmount", totalAmount);
            
            // 手续费总额
            BigDecimal totalFee = withdraws.stream()
                    .map(PmsInviteWithdrawApply::getFeeAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.put("totalFee", totalFee);
            
            // 可提现金额
            BigDecimal availableAmount = getAvailableAmount(userId);
            statistics.put("availableAmount", availableAmount);
            
            // 待审核申请数量
            PmsInviteWithdrawApplyExample pendingExample = new PmsInviteWithdrawApplyExample();
            pendingExample.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo((byte) 0);
            long pendingCount = withdrawApplyMapper.countByExample(pendingExample);
            statistics.put("pendingCount", pendingCount);
            
            return statistics;
        } catch (Exception e) {
            LOGGER.error("获取提现统计失败, userId: {}", userId, e);
            return statistics;
        }
    }

    /**
     * 处理存入文创余额
     */
    @Override
    public void processBalanceTransfer(PmsInviteWithdrawApply withdrawApply, Long userId, BigDecimal actualAmount) {
        try {
            LOGGER.info("开始处理存入文创余额，用户ID: {}, 提现申请ID: {}, 金额: {}",
                       userId, withdrawApply.getId(), actualAmount);

            // 1. 记录余额变动（转入）
            boolean balanceResult = memberBalanceService.recordBalanceChange(
                userId,
                4, // 变动类型：4-转入
                actualAmount,
                "withdraw_to_balance", // 业务类型
                withdrawApply.getWithdrawSn(), // 业务ID
                "邀请奖励转入文创余额"
            );

            if (!balanceResult) {
                throw new RuntimeException("余额转入失败");
            }

            // 2. 更新提现申请状态为成功
            withdrawApply.setStatus((byte) 2); // 提现成功
            withdrawApply.setProcessTime(new Date());
            withdrawApply.setActualArrivalTime(new Date());
            withdrawApply.setAuditRemark("系统自动处理：存入文创余额");
            withdrawApply.setUpdatedAt(new Date());

            int updateResult = withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);
            if (updateResult <= 0) {
                throw new RuntimeException("更新提现申请状态失败");
            }

            LOGGER.info("存入文创余额处理完成，用户ID: {}, 提现申请ID: {}, 金额: {}",
                       userId, withdrawApply.getId(), actualAmount);

        } catch (Exception e) {
            LOGGER.error("处理存入文创余额失败，用户ID: {}, 提现申请ID: {}, 错误: {}",
                        userId, withdrawApply.getId(), e.getMessage(), e);

            // 更新提现申请状态为失败
            withdrawApply.setStatus((byte) 3); // 提现失败
            withdrawApply.setProcessTime(new Date());
            withdrawApply.setFailureReason("存入文创余额失败: " + e.getMessage());
            withdrawApply.setUpdatedAt(new Date());
            withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);

            throw new RuntimeException("存入文创余额失败: " + e.getMessage());
        }
    }   

    /**
     * 处理微信转账
     */
    @Override
    public void processWechatTransfer(PmsInviteWithdrawApply withdrawApply, UmsMember member) {
        try {
            LOGGER.info("开始处理微信转账，提现申请ID: {}, 用户: {}, 金额: {}",
                       withdrawApply.getId(), member.getUsername(), withdrawApply.getActualAmount());

            // 1. 构建转账参数
            WechatTransferParam transferParam = wechatTransferService.buildTransferParam(
                withdrawApply.getWithdrawSn(),
                withdrawApply.getWithdrawAccount(), // openId
                withdrawApply.getActualAmount(),
                withdrawApply.getAccountName(),
                member.getUsername() != null ? member.getUsername() : member.getNickname()
            );

            // 2. 调用微信转账接口
            WechatTransferResult result = wechatTransferService.transfer(transferParam);

            if (result != null && result.isSuccess()) {
                // 转账成功，更新提现申请状态
                withdrawApply.setStatus((byte) 2); // 提现成功
                withdrawApply.setProcessTime(new Date());
                withdrawApply.setActualArrivalTime(new Date());
                withdrawApply.setAuditRemark("微信转账成功，批次单号: " + result.getBatchId());
                withdrawApply.setUpdatedAt(new Date());

                int updateResult = withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);
                if (updateResult <= 0) {
                    LOGGER.error("更新提现申请状态失败，申请ID: {}", withdrawApply.getId());
                }

                LOGGER.info("微信转账处理完成，提现申请ID: {}, 批次单号: {}",
                           withdrawApply.getId(), result.getBatchId());
            } else {
                // 转账失败，更新状态
                withdrawApply.setStatus((byte) 3); // 提现失败
                withdrawApply.setProcessTime(new Date());
                String errorMsg = result != null ? result.getStatusDesc() : "未知错误";
                withdrawApply.setFailureReason("微信转账失败: " + errorMsg);
                withdrawApply.setUpdatedAt(new Date());
                withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);

                LOGGER.error("微信转账失败，提现申请ID: {}, 错误信息: {}",
                           withdrawApply.getId(), errorMsg);

                // 抛出异常，让上层调用者知道转账失败
                throw new RuntimeException("微信转账失败: " + errorMsg);
            }

        } catch (Exception e) {
            LOGGER.error("处理微信转账失败，提现申请ID: {}, 错误: {}",
                        withdrawApply.getId(), e.getMessage(), e);

            // 更新提现申请状态为失败
            withdrawApply.setStatus((byte) 3); // 提现失败
            withdrawApply.setProcessTime(new Date());
            withdrawApply.setFailureReason("微信转账异常: " + e.getMessage());
            withdrawApply.setUpdatedAt(new Date());
            withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);

            // 重新抛出异常，让上层调用者知道转账失败
            throw new RuntimeException("微信转账失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从数据库获取配置值
     */
    private String getConfigValue(String configKey) {
        try {
            PmsSystemConfigExample example = new PmsSystemConfigExample();
            example.createCriteria()
                   .andConfigKeyEqualTo(configKey)
                   .andIsActiveEqualTo((byte) 1);

            List<PmsSystemConfig> configs = systemConfigMapper.selectByExample(example);
            if (configs != null && !configs.isEmpty()) {
                return configs.get(0).getConfigValue();
            }
        } catch (Exception e) {
            LOGGER.warn("获取配置失败，配置键：{}，错误：{}", configKey, e.getMessage());
        }
        return null;
    }

    /**
     * 获取BigDecimal类型配置
     */
    private BigDecimal getBigDecimalConfig(String configKey, BigDecimal defaultValue) {
        String value = getConfigValue(configKey);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("配置值格式错误，配置键：{}，值：{}，使用默认值：{}", configKey, value, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * 获取Integer类型配置
     */
    private Integer getIntegerConfig(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                LOGGER.warn("配置值格式错误，配置键：{}，值：{}，使用默认值：{}", configKey, value, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * 获取Boolean类型配置
     */
    private Boolean getBooleanConfig(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (value != null && !value.trim().isEmpty()) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return defaultValue;
    }

    /**
     * 获取String类型配置
     */
    private String getStringConfig(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null && !value.trim().isEmpty() ? value : defaultValue;
    }

    /**
     * 获取默认提现说明
     */
    private List<String> getDefaultNotes() {
        Integer maxDailyCount = getIntegerConfig(CONFIG_MAX_DAILY_COUNT, DEFAULT_MAX_DAILY_COUNT);
        return Arrays.asList(
            "提现申请提交后，我们会在1-2个工作日内完成审核",
            "审核通过后，资金将在指定时间内到账",
            "请确保提现账户信息准确，错误信息可能导致提现失败",
            "每笔提现会收取相应手续费，具体费用在申请时显示",
            "每日最多可申请" + maxDailyCount + "次提现"
        );
    }
}