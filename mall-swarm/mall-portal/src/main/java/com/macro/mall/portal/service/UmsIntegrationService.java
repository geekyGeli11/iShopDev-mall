package com.macro.mall.portal.service;

import com.macro.mall.model.UmsMember;

/**
 * 会员积分管理统一服务
 * 提供积分更新、等级检查、优惠券发放等核心功能
 * Created by macro on 2025/09/28.
 */
public interface UmsIntegrationService {
    
    /**
     * 更新会员积分并检查等级升级
     * @param memberId 会员ID
     * @param points 积分变化量（正数为增加，负数为减少）
     * @param reason 积分变化原因
     * @param sourceType 积分来源类型：0->购物；1->管理员修改；2->签到；3->邀请；4->其他
     * @return 更新结果：true成功，false失败
     */
    boolean updateIntegrationAndCheckLevel(Long memberId, Integer points, String reason, Integer sourceType);
    
    /**
     * 检查并升级会员等级
     * @param memberId 会员ID
     * @return 是否发生了等级升级
     */
    boolean checkAndUpgradeMemberLevel(Long memberId);
    
    /**
     * 为新注册用户发放默认等级优惠券
     * @param memberId 会员ID
     * @return 发放结果：true成功，false失败
     */
    boolean grantDefaultLevelCoupon(Long memberId);
    
    /**
     * 根据积分计算应该的会员等级
     * @param totalIntegration 总积分
     * @return 应该的会员等级ID，如果没有匹配的等级返回null
     */
    Long calculateMemberLevelByIntegration(Integer totalIntegration);
    
    /**
     * 发放等级升级优惠券
     * @param memberId 会员ID
     * @param newLevelId 新等级ID
     * @return 发放结果：true成功，false失败
     */
    boolean grantLevelUpgradeCoupon(Long memberId, Long newLevelId);

    /**
     * 计算用户达到下一个等级所需的剩余积分
     * @param memberId 会员ID
     * @return 剩余积分数量，如果已是最高等级返回0，如果用户不存在返回-1
     */
    Integer calculateRemainingPointsToNextLevel(Long memberId);
}
