package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.model.PmsInviteWithdrawApply;
import com.macro.mall.model.UmsMember;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 提现服务接口
 */
public interface WithdrawService {
    
    /**
     * 获取可提现金额
     */
    BigDecimal getAvailableAmount(Long userId);
    
    /**
     * 获取提现配置
     */
    Map<String, Object> getWithdrawConfig();
    
    /**
     * 申请提现
     */
    Boolean applyWithdraw(Long userId, WithdrawApplyParam param);
    
    /**
     * 获取我的提现记录
     */
    CommonPage<Map<String, Object>> getMyWithdrawRecords(Long userId, Integer pageNum, Integer pageSize, Byte status);
    
    /**
     * 获取提现申请详情
     */
    Map<String, Object> getWithdrawDetail(Long withdrawId, Long userId);
    
    /**
     * 取消提现申请
     */
    Boolean cancelWithdraw(Long withdrawId, Long userId);
    
    /**
     * 获取提现统计
     */
    Map<String, Object> getWithdrawStatistics(Long userId);

    /**
     * 处理微信转账
     */
    void processWechatTransfer(PmsInviteWithdrawApply withdrawApply, UmsMember member);

    /**
     * 处理文创余额转存
     */
    void processBalanceTransfer(PmsInviteWithdrawApply withdrawApply, Long userId, BigDecimal actualAmount);

    /**
     * 计算手续费
     */
    BigDecimal calculateFee(BigDecimal amount, Byte withdrawType);
}