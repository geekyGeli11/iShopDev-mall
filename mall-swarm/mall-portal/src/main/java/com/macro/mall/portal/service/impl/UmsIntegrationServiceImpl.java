package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.SmsCouponHistoryMapper;
import com.macro.mall.mapper.SmsCouponMapper;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.service.UmsIntegrationService;
import com.macro.mall.portal.service.UmsMemberCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 会员积分管理统一服务实现类
 * Created by macro on 2025/09/28.
 */
@Service
public class UmsIntegrationServiceImpl implements UmsIntegrationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsIntegrationServiceImpl.class);
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    
    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;
    
    @Autowired
    private SmsCouponMapper couponMapper;
    
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    
    @Autowired
    private UmsMemberCacheService memberCacheService;
    
    @Override
    @Transactional
    public boolean updateIntegrationAndCheckLevel(Long memberId, Integer points, String reason, Integer sourceType) {
        if (memberId == null || points == null || points == 0) {
            LOGGER.warn("积分更新参数无效: memberId={}, points={}", memberId, points);
            return false;
        }
        
        try {
            // 1. 获取用户当前信息
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                LOGGER.warn("用户不存在: memberId={}", memberId);
                return false;
            }
            
            // 2. 计算新的积分
            int currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
            int historyIntegration = member.getHistoryIntegration() != null ? member.getHistoryIntegration() : 0;
            
            // 如果是扣减积分，检查余额是否足够
            if (points < 0 && currentIntegration < Math.abs(points)) {
                LOGGER.warn("用户积分不足: memberId={}, currentIntegration={}, deductPoints={}", 
                    memberId, currentIntegration, Math.abs(points));
                return false;
            }
            
            int newIntegration = currentIntegration + points;
            int newHistoryIntegration = points > 0 ? historyIntegration + points : historyIntegration;
            
            // 3. 更新用户积分
            UmsMember updateMember = new UmsMember();
            updateMember.setId(memberId);
            updateMember.setIntegration(newIntegration);
            updateMember.setHistoryIntegration(newHistoryIntegration);
            
            int updateResult = memberMapper.updateByPrimaryKeySelective(updateMember);
            if (updateResult <= 0) {
                LOGGER.error("更新用户积分失败: memberId={}", memberId);
                return false;
            }
            
            // 4. 记录积分变更历史
            recordIntegrationChangeHistory(memberId, points, reason, sourceType);
            
            // 5. 检查等级升级（只有增加积分时才检查）
            if (points > 0) {
                checkAndUpgradeMemberLevel(memberId);
            }
            
            // 6. 清除用户缓存
            memberCacheService.delMember(memberId);
            
            LOGGER.info("积分更新成功: memberId={}, points={}, newIntegration={}", 
                memberId, points, newIntegration);
            return true;
            
        } catch (Exception e) {
            LOGGER.error("积分更新失败: memberId={}, points={}", memberId, points, e);
            throw new RuntimeException("积分更新失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean checkAndUpgradeMemberLevel(Long memberId) {
        try {
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                return false;
            }
            
            // 根据积分计算应该的等级
            Long newLevelId = calculateMemberLevelByIntegration(member.getIntegration());
            if (newLevelId == null) {
                return false;
            }
            
            // 检查是否需要升级
            Long currentLevelId = member.getMemberLevelId();
            if (currentLevelId != null && currentLevelId.equals(newLevelId)) {
                return false; // 等级没有变化
            }
            
            // 获取新等级信息
            UmsMemberLevel newLevel = memberLevelMapper.selectByPrimaryKey(newLevelId);
            if (newLevel == null) {
                return false;
            }
            
            // 更新用户等级
            UmsMember updateMember = new UmsMember();
            updateMember.setId(memberId);
            updateMember.setMemberLevelId(newLevelId);
            
            int updateResult = memberMapper.updateByPrimaryKeySelective(updateMember);
            if (updateResult > 0) {
                LOGGER.info("用户等级升级成功: memberId={}, oldLevelId={}, newLevelId={}, levelName={}", 
                    memberId, currentLevelId, newLevelId, newLevel.getName());
                
                // 发放等级升级优惠券
                grantLevelUpgradeCoupon(memberId, newLevelId);
                
                // 清除用户缓存
                memberCacheService.delMember(memberId);
                return true;
            }
            
        } catch (Exception e) {
            LOGGER.error("检查等级升级失败: memberId={}", memberId, e);
        }
        
        return false;
    }
    
    @Override
    public boolean grantDefaultLevelCoupon(Long memberId) {
        try {
            // 获取默认等级
            UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
            levelExample.createCriteria().andDefaultStatusEqualTo(1);
            List<UmsMemberLevel> defaultLevels = memberLevelMapper.selectByExample(levelExample);
            
            if (CollectionUtils.isEmpty(defaultLevels)) {
                LOGGER.warn("未找到默认会员等级");
                return false;
            }
            
            UmsMemberLevel defaultLevel = defaultLevels.get(0);
            if (defaultLevel.getGiftCouponId() == null) {
                LOGGER.info("默认等级没有关联优惠券: levelId={}", defaultLevel.getId());
                return true; // 没有优惠券也算成功
            }
            
            return grantCouponToMember(memberId, defaultLevel.getGiftCouponId(), "新用户注册赠送");
            
        } catch (Exception e) {
            LOGGER.error("发放默认等级优惠券失败: memberId={}", memberId, e);
            return false;
        }
    }
    
    @Override
    public Long calculateMemberLevelByIntegration(Integer totalIntegration) {
        if (totalIntegration == null || totalIntegration < 0) {
            return null;
        }
        
        try {
            // 查询所有等级，按成长值升序排列
            UmsMemberLevelExample example = new UmsMemberLevelExample();
            example.createCriteria().andDefaultStatusEqualTo(0); // 排除默认等级
            example.setOrderByClause("growth_point ASC");
            List<UmsMemberLevel> levels = memberLevelMapper.selectByExample(example);
            
            if (CollectionUtils.isEmpty(levels)) {
                return null;
            }
            
            // 找到最高的满足条件的等级
            Long targetLevelId = null;
            for (UmsMemberLevel level : levels) {
                if (totalIntegration >= level.getGrowthPoint()) {
                    targetLevelId = level.getId();
                } else {
                    break;
                }
            }
            
            return targetLevelId;
            
        } catch (Exception e) {
            LOGGER.error("计算会员等级失败: totalIntegration={}", totalIntegration, e);
            return null;
        }
    }
    
    @Override
    public boolean grantLevelUpgradeCoupon(Long memberId, Long newLevelId) {
        try {
            UmsMemberLevel level = memberLevelMapper.selectByPrimaryKey(newLevelId);
            if (level == null || level.getGiftCouponId() == null) {
                LOGGER.info("等级没有关联优惠券: levelId={}", newLevelId);
                return true; // 没有优惠券也算成功
            }
            
            return grantCouponToMember(memberId, level.getGiftCouponId(), "等级升级赠送");
            
        } catch (Exception e) {
            LOGGER.error("发放等级升级优惠券失败: memberId={}, levelId={}", memberId, newLevelId, e);
            return false;
        }
    }
    
    /**
     * 记录积分变更历史
     */
    private void recordIntegrationChangeHistory(Long memberId, Integer points, String reason, Integer sourceType) {
        try {
            UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
            history.setMemberId(memberId);
            history.setCreateTime(new Date());
            history.setChangeType(points > 0 ? 0 : 1); // 0->增加；1->减少
            history.setChangeCount(Math.abs(points));
            history.setOperateMan("系统");
            history.setOperateNote(reason != null ? reason : "积分变更");
            history.setSourceType(sourceType != null ? sourceType : 4); // 默认为其他类型
            
            integrationChangeHistoryMapper.insert(history);
            
        } catch (Exception e) {
            LOGGER.error("记录积分变更历史失败: memberId={}, points={}", memberId, points, e);
        }
    }
    
    /**
     * 发放优惠券给用户
     */
    private boolean grantCouponToMember(Long memberId, Long couponId, String reason) {
        try {
            // 验证优惠券存在且有库存
            SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
            if (coupon == null || coupon.getPublishCount() <= 0) {
                LOGGER.warn("优惠券不存在或库存不足: couponId={}", couponId);
                return false;
            }
            
            // 检查用户是否已经领取过该优惠券
            SmsCouponHistoryExample example = new SmsCouponHistoryExample();
            example.createCriteria()
                   .andMemberIdEqualTo(memberId)
                   .andCouponIdEqualTo(couponId);
            long existCount = couponHistoryMapper.countByExample(example);
            
            if (coupon.getPerLimit() != null && existCount >= coupon.getPerLimit()) {
                LOGGER.warn("用户已达到该优惠券的领取上限: memberId={}, couponId={}, limit={}", 
                    memberId, couponId, coupon.getPerLimit());
                return false;
            }
            
            // 创建优惠券历史记录
            SmsCouponHistory couponHistory = new SmsCouponHistory();
            couponHistory.setCouponId(couponId);
            couponHistory.setMemberId(memberId);
            couponHistory.setCouponCode(generateCouponCode());
            couponHistory.setMemberNickname(""); // 可以后续完善
            couponHistory.setGetType(0); // 0->后台赠送；1->主动获取
            couponHistory.setCreateTime(new Date());
            couponHistory.setUseStatus(0); // 0->未使用；1->已使用；2->已过期
            couponHistory.setOrderId(null);
            couponHistory.setOrderSn(null);
            // SmsCouponHistory没有note字段，使用memberNickname字段记录原因
            if (reason != null && reason.length() > 0) {
                couponHistory.setMemberNickname(reason);
            }
            
            int insertResult = couponHistoryMapper.insert(couponHistory);
            if (insertResult > 0) {
                // 减少优惠券库存
                SmsCoupon updateCoupon = new SmsCoupon();
                updateCoupon.setId(couponId);
                updateCoupon.setPublishCount(coupon.getPublishCount() - 1);
                updateCoupon.setReceiveCount(coupon.getReceiveCount() + 1);
                couponMapper.updateByPrimaryKeySelective(updateCoupon);
                
                LOGGER.info("优惠券发放成功: memberId={}, couponId={}, reason={}", 
                    memberId, couponId, reason);
                return true;
            }
            
        } catch (Exception e) {
            LOGGER.error("发放优惠券失败: memberId={}, couponId={}", memberId, couponId, e);
        }
        
        return false;
    }
    
    @Override
    public Integer calculateRemainingPointsToNextLevel(Long memberId) {
        try {
            // 1. 获取用户当前积分
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                LOGGER.warn("用户不存在: memberId={}", memberId);
                return -1;
            }

            Integer currentIntegration = member.getIntegration();
            if (currentIntegration == null) {
                currentIntegration = 0;
            }

            // 2. 查询所有等级，按成长值升序排列
            UmsMemberLevelExample example = new UmsMemberLevelExample();
            example.createCriteria().andDefaultStatusEqualTo(0); // 排除默认等级
            example.setOrderByClause("growth_point ASC");
            List<UmsMemberLevel> levels = memberLevelMapper.selectByExample(example);

            if (CollectionUtils.isEmpty(levels)) {
                LOGGER.warn("没有找到会员等级配置");
                return 0;
            }

            // 3. 找到下一个等级
            UmsMemberLevel nextLevel = null;
            for (UmsMemberLevel level : levels) {
                if (currentIntegration < level.getGrowthPoint()) {
                    nextLevel = level;
                    break;
                }
            }

            // 4. 计算剩余积分
            if (nextLevel == null) {
                // 已经是最高等级
                LOGGER.info("用户已达到最高等级: memberId={}, currentIntegration={}",
                    memberId, currentIntegration);
                return 0;
            } else {
                int remainingPoints = nextLevel.getGrowthPoint() - currentIntegration;
                LOGGER.info("用户距离下一等级还需积分: memberId={}, currentIntegration={}, nextLevelPoints={}, remaining={}",
                    memberId, currentIntegration, nextLevel.getGrowthPoint(), remainingPoints);
                return remainingPoints;
            }

        } catch (Exception e) {
            LOGGER.error("计算剩余积分失败: memberId={}", memberId, e);
            return -1;
        }
    }

    /**
     * 生成优惠券码
     */
    private String generateCouponCode() {
        return "CPN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}
