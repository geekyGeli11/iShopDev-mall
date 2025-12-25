package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.InviteGenerateResult;
import com.macro.mall.portal.domain.InviteRewardRecord;
import com.macro.mall.portal.domain.InviteStatisticsResult;
import com.macro.mall.portal.domain.RewardSummaryResult;
import com.macro.mall.portal.dto.InviteGenerateParam;
import com.macro.mall.portal.dto.InviteVerifyParam;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.portal.domain.WithdrawApplyResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 邀请服务接口
 */
public interface InviteService {
    
    /**
     * 生成邀请参数
     * @param param 生成参数
     * @return 邀请结果
     */
    InviteGenerateResult generateInviteParam(InviteGenerateParam param);
    
    /**
     * 验证邀请参数
     * @param param 验证参数
     * @return 验证结果
     */
    boolean verifyInviteParam(InviteVerifyParam param);
    
    /**
     * 建立邀请关系
     * @param inviterUserId 邀请者ID
     * @param inviteeUserId 被邀请者ID
     * @return 是否成功
     */
    Boolean establishInviteRelation(Long inviterUserId, Long inviteeUserId);

    /**
     * 根据邀请参数建立邀请关系
     * @param inviteParam 邀请参数
     * @param inviteeUserId 被邀请者ID
     * @return 是否成功
     */
    Boolean establishInviteRelation(String inviteParam, Long inviteeUserId);
    
    /**
     * 获取我的邀请统计
     * @param userId 用户ID
     * @return 统计信息
     */
    InviteStatisticsResult getMyInviteStatistics(Long userId);
    
    /**
     * 获取我的邀请奖励记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 页大小
     * @return 奖励记录列表
     */
    List<InviteRewardRecord> getMyInviteRewards(Long userId, Integer page, Integer size);
    
    /**
     * 获取奖励汇总
     * @param userId 用户ID
     * @param year 年份
     * @return 奖励汇总信息
     */
    RewardSummaryResult getRewardSummary(Long userId, Integer year);
    
    /**
     * 生成小程序码
     * @param inviteParam 邀请参数
     * @return 小程序码URL
     */
    String generateMiniProgramQrCode(String inviteParam);
    
    /**
     * 生成带参数小程序码（返回完整信息）
     * @param inviteParam 邀请参数
     * @return 小程序码信息
     */
    CommonResult<Map<String, Object>> generateQrCode(String inviteParam);
    
    /**
     * 申请提现
     * @param userId 用户ID
     * @param param 提现申请参数
     * @return 提现申请结果
     */
    WithdrawApplyResult applyWithdraw(Long userId, WithdrawApplyParam param);

    /**
     * 获取用户的邀请者ID
     * @param inviteeUserId 被邀请者用户ID
     * @return 邀请者用户ID，如果没有邀请关系则返回null
     */
    Long getInviterUserId(Long inviteeUserId);

    /**
     * 获取邀请配置
     * @return 邀请配置信息
     */
    Map<String, Object> getInviteConfig();

    /**
     * 获取邀请关系建立时间
     * @param inviterUserId 邀请者用户ID
     * @param inviteeUserId 被邀请者用户ID
     * @return 邀请关系建立时间
     */
    Date getInviteDate(Long inviterUserId, Long inviteeUserId);

    /**
     * 获取提现配置
     * @return 提现配置信息
     */
    Map<String, Object> getWithdrawConfig();
}