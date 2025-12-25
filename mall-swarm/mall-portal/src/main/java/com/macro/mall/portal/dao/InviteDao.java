package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.InviteStatisticsResult;
import com.macro.mall.portal.domain.RewardSummaryResult;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 邀请数据查询Dao
 * @author macro
 * @since 1.0.0
 */
public interface InviteDao {
    
    /**
     * 获取用户邀请统计数据
     * @param userId 用户ID
     * @return 邀请统计结果
     */
    InviteStatisticsResult getUserInviteStatistics(@Param("userId") Long userId);
    
    /**
     * 获取用户奖励汇总数据
     * @param userId 用户ID
     * @param year 年份
     * @return 奖励汇总结果
     */
    RewardSummaryResult getUserRewardSummary(@Param("userId") Long userId, @Param("year") Integer year);
    
    /**
     * 获取用户月度奖励数据
     * @param userId 用户ID
     * @param year 年份
     * @param maxMonth 最大月份（包含）
     * @return 月度奖励列表
     */
    List<Map<String, Object>> getUserMonthlyRewards(@Param("userId") Long userId, @Param("year") Integer year, @Param("maxMonth") Integer maxMonth);
    
    /**
     * 获取用户的被邀请人列表（显示在个人中心的推荐区域）
     * @param userId 用户ID
     * @param limit 最大显示数量
     * @return 被邀请人信息列表
     */
    List<Map<String, Object>> getUserInvitedList(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 获取用户可提现的奖励记录ID列表
     * @param userId 用户ID
     * @param amount 需要的金额
     * @return 奖励记录ID列表
     */
    List<Long> getWithdrawableRewardIds(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 锁定指定的奖励记录
     * @param rewardIds 要锁定的奖励记录ID列表
     * @param withdrawSn 提现申请单号（用于标记锁定记录）
     * @return 锁定的记录数量
     */
    int lockRewardsByIds(@Param("rewardIds") List<Long> rewardIds, @Param("withdrawSn") String withdrawSn);

    /**
     * 锁定用户的可提现奖励记录（旧方法，保持兼容性）
     * @param userId 用户ID
     * @param amount 要锁定的金额
     * @param withdrawSn 提现申请单号（用于标记锁定记录）
     * @return 锁定的记录数量
     */
    int lockWithdrawableRewards(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("withdrawSn") String withdrawSn);
    
    /**
     * 检查用户是否有待处理的提现申请
     * @param userId 用户ID
     * @return 待处理的提现申请数量
     */
    int countPendingWithdrawApply(@Param("userId") Long userId);

    /**
     * 保存邀请参数日志到数据库
     * @param userId 邀请者用户ID
     * @param inviteParam 邀请参数
     * @param timestamp 参数中的时间戳
     * @param expireTime 过期时间
     * @return 是否成功
     */
    boolean saveInviteParamLog(@Param("userId") Long userId, @Param("inviteParam") String inviteParam,
                               @Param("timestamp") Long timestamp, @Param("expireTime") java.util.Date expireTime);

    /**
     * 检查被邀请者是否已有邀请者
     * @param inviteeUserId 被邀请者ID
     * @return 是否已有邀请者
     */
    boolean checkInviteeHasInviter(@Param("inviteeUserId") Long inviteeUserId);

    /**
     * 保存邀请关系
     * @param inviterUserId 邀请者ID
     * @param inviteeUserId 被邀请者ID
     * @param inviteParam 邀请参数
     * @return 是否成功
     */
    boolean saveInviteRelation(@Param("inviterUserId") Long inviterUserId, @Param("inviteeUserId") Long inviteeUserId, @Param("inviteParam") String inviteParam);

    /**
     * 获取用户的邀请者ID
     * @param inviteeUserId 被邀请者用户ID
     * @return 邀请者用户ID，如果没有邀请关系则返回null
     */
    Long getInviterUserId(@Param("inviteeUserId") Long inviteeUserId);

    /**
     * 获取邀请关系建立时间
     * @param inviterUserId 邀请者用户ID
     * @param inviteeUserId 被邀请者用户ID
     * @return 邀请关系建立时间
     */
    java.util.Date getInviteDate(@Param("inviterUserId") Long inviterUserId, @Param("inviteeUserId") Long inviteeUserId);

    /**
     * 获取邀请关系ID
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @return 邀请关系ID
     */
    Long getInviteRelationId(@Param("userId") Long userId, @Param("relatedUserId") Long relatedUserId);
}