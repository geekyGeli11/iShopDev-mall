package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsMemberAdminDao;
import com.macro.mall.dto.UmsMemberDetailVO;
import com.macro.mall.dto.UmsMemberListVO;
import com.macro.mall.dto.UmsMemberQueryParam;
import com.macro.mall.dto.UmsMemberUpdateParam;
import com.macro.mall.dto.SmsCouponHistoryDetailVO;
import com.macro.mall.mapper.*;

import java.util.Arrays;
import com.macro.mall.model.*;
import com.macro.mall.service.UmsAdminService;
import com.macro.mall.service.UmsMemberAdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 用户管理Admin Service实现类
 */
@Service
public class UmsMemberAdminServiceImpl implements UmsMemberAdminService {

    private static final Logger log = LoggerFactory.getLogger(UmsMemberAdminServiceImpl.class);

    @Autowired
    private UmsMemberAdminDao memberAdminDao;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    
    @Autowired
    private UmsMemberStatisticsInfoMapper statisticsInfoMapper;
    
    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;
    
    @Autowired
    private UmsMemberLoginLogMapper loginLogMapper;
    
    @Autowired
    private UmsAdminService adminService;
    
    @Autowired
    private SmsCouponMapper couponMapper;
    
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    
    @Autowired
    private OmsOrderMapper orderMapper;
    
    @Autowired
    private PmsInviteRelationMapper inviteRelationMapper;
    
    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;
    
    @Autowired
    private PmsInviteWithdrawApplyMapper inviteWithdrawApplyMapper;
    
    @Autowired
    private PmsInviteStatisticsMapper inviteStatisticsMapper;
    
    @Autowired
    private UmsMemberTagMapper memberTagMapper;
    
    @Autowired
    private UmsMemberTagRelationMapper memberTagRelationMapper;

    @Autowired
    private UmsMemberSigninLogMapper memberSigninLogMapper;

    @Override
    public List<UmsMemberListVO> list(UmsMemberQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberAdminDao.selectMemberList(queryParam);
    }

    @Override
    public UmsMemberDetailVO getDetail(Long id) {
        // 直接使用DAO查询获取包含正确统计信息的用户详情
        UmsMemberDetailVO detailVO = memberAdminDao.selectMemberDetail(id);
        if (detailVO == null) {
            return null;
        }
        
        // 获取基础用户信息用于补充字段
        UmsMember member = memberMapper.selectByPrimaryKey(id);
        if (member != null) {
            // 补充DAO查询中没有的字段
            detailVO.setUsername(member.getUsername());
            detailVO.setBirthday(member.getBirthday());
            detailVO.setJob(member.getJob());
            detailVO.setPersonalizedSignature(member.getPersonalizedSignature());
            detailVO.setOpenid(member.getOpenid());
        }
        
        // 计算需要手动统计的字段
        
        // 已完成订单数 (状态3)
        OmsOrderExample finishedExample = new OmsOrderExample();
        finishedExample.createCriteria().andMemberIdEqualTo(id).andStatusEqualTo(3);
        int finishedOrderCount = (int) orderMapper.countByExample(finishedExample);
        detailVO.setFinishedOrderCount(finishedOrderCount);
        
        // 已取消订单数 (状态4和5)
        OmsOrderExample cancelledExample = new OmsOrderExample();
        cancelledExample.createCriteria().andMemberIdEqualTo(id).andStatusIn(Arrays.asList(4, 5));
        int cancelledOrderCount = (int) orderMapper.countByExample(cancelledExample);
        detailVO.setCancelledOrderCount(cancelledOrderCount);
        
        // 获取邀请统计
        PmsInviteRelationExample inviteExample = new PmsInviteRelationExample();
        inviteExample.createCriteria().andInviterIdEqualTo(id);
        int inviteFriendCount = (int) inviteRelationMapper.countByExample(inviteExample);
        detailVO.setInviteFriendCount(inviteFriendCount);
        
        // 设置其他统计字段的默认值（这些数据在当前系统中可能不存在对应表）
        detailVO.setReturnOrderCount(0);
        detailVO.setCollectProductCount(0);
        detailVO.setCommentCount(0);
        
        // 获取最近登录信息
        UmsMemberLoginLogExample loginLogExample = new UmsMemberLoginLogExample();
        loginLogExample.createCriteria().andMemberIdEqualTo(id);
        loginLogExample.setOrderByClause("create_time desc");
        PageHelper.startPage(1, 1);
        List<UmsMemberLoginLog> loginLogs = loginLogMapper.selectByExample(loginLogExample);
        if (!CollectionUtils.isEmpty(loginLogs)) {
            UmsMemberLoginLog latestLogin = loginLogs.get(0);
            detailVO.setLastLoginTime(latestLogin.getCreateTime());
            detailVO.setLastLoginIp(latestLogin.getIp());
        }
        
        return detailVO;
    }

    @Override
    @Transactional
    public int updateMember(Long id, UmsMemberUpdateParam updateParam) {
        UmsMember member = new UmsMember();
        member.setId(id);
        BeanUtils.copyProperties(updateParam, member);
        return memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    @Transactional
    public int updateStatus(Long id, Integer status) {
        UmsMember member = new UmsMember();
        member.setId(id);
        member.setStatus(status);
        return memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    @Transactional
    public int deleteMember(Long id) {
        return memberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int getTotalMemberCount() {
        UmsMemberQueryParam param = new UmsMemberQueryParam();
        param.setSourceType(1); // 只统计小程序用户
        return memberAdminDao.countMemberTotal(param);
    }

    @Override
    @Transactional
    public int giveIntegration(Long memberId, Integer integration, String reason) {
        if (integration <= 0) {
            return 0;
        }
        
        // 获取用户当前积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return 0;
        }
        
        // 更新用户积分 - 正确处理NULL值
        int currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
        int historyIntegration = member.getHistoryIntegration() != null ? member.getHistoryIntegration() : 0;
        
        UmsMember updateMember = new UmsMember();
        updateMember.setId(memberId);
        updateMember.setIntegration(currentIntegration + integration);
        updateMember.setHistoryIntegration(historyIntegration + integration);
        
        int result = memberMapper.updateByPrimaryKeySelective(updateMember);

        if (result > 0) {
            // 记录积分变更历史
            UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
            history.setMemberId(memberId);
            history.setCreateTime(new Date());
            history.setChangeType(0); // 0->增加；1->减少
            history.setChangeCount(integration);

            // 获取当前管理员信息
            UmsAdmin currentAdmin = adminService.getCurrentAdmin();
            history.setOperateMan(currentAdmin != null ? currentAdmin.getUsername() : "系统");
            history.setOperateNote(StrUtil.isNotBlank(reason) ? reason : "管理员发放积分");
            history.setSourceType(1); // 1->管理员修改

            integrationChangeHistoryMapper.insert(history);

            // 检查并升级会员等级
            try {
                checkAndUpgradeMemberLevel(memberId);
            } catch (Exception e) {
                log.error("检查会员等级升级失败: memberId={}", memberId, e);
                // 等级升级失败不影响积分发放
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public int deductIntegration(Long memberId, Integer integration, String reason) {
        if (integration <= 0) {
            return 0;
        }
        
        // 获取用户当前积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return 0;
        }
        
        int currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
        if (currentIntegration < integration) {
            // 积分不足，不能扣减
            return 0;
        }
        
        // 更新用户积分
        UmsMember updateMember = new UmsMember();
        updateMember.setId(memberId);
        updateMember.setIntegration(currentIntegration - integration);
        
        int result = memberMapper.updateByPrimaryKeySelective(updateMember);
        
        if (result > 0) {
            // 记录积分变更历史
            UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
            history.setMemberId(memberId);
            history.setCreateTime(new Date());
            history.setChangeType(1); // 0->增加；1->减少
            history.setChangeCount(integration);
            
            // 获取当前管理员信息
            UmsAdmin currentAdmin = adminService.getCurrentAdmin();
            history.setOperateMan(currentAdmin != null ? currentAdmin.getUsername() : "系统");
            history.setOperateNote(StrUtil.isNotBlank(reason) ? reason : "管理员扣减积分");
            history.setSourceType(1); // 1->管理员修改
            
            integrationChangeHistoryMapper.insert(history);
        }
        
        return result;
    }

    @Override
    public List<UmsIntegrationChangeHistory> getIntegrationHistory(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("create_time desc");
        return integrationChangeHistoryMapper.selectByExample(example);
    }

    @Override
    public Map<String, Object> getIntegrationSummary(Long memberId) {
        Map<String, Object> summary = new HashMap<>();
        
        // 获取用户当前积分
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member != null) {
            summary.put("currentIntegration", member.getIntegration() != null ? member.getIntegration() : 0);
            summary.put("historyIntegration", member.getHistoryIntegration() != null ? member.getHistoryIntegration() : 0);
        } else {
            summary.put("currentIntegration", 0);
            summary.put("historyIntegration", 0);
        }
        
        // 统计积分变更次数
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        long totalChangeCount = integrationChangeHistoryMapper.countByExample(example);
        summary.put("totalChangeCount", totalChangeCount);
        
        // 统计增加积分次数
        UmsIntegrationChangeHistoryExample addExample = new UmsIntegrationChangeHistoryExample();
        addExample.createCriteria().andMemberIdEqualTo(memberId).andChangeTypeEqualTo(0);
        long addCount = integrationChangeHistoryMapper.countByExample(addExample);
        summary.put("addCount", addCount);
        
        // 统计减少积分次数
        UmsIntegrationChangeHistoryExample deductExample = new UmsIntegrationChangeHistoryExample();
        deductExample.createCriteria().andMemberIdEqualTo(memberId).andChangeTypeEqualTo(1);
        long deductCount = integrationChangeHistoryMapper.countByExample(deductExample);
        summary.put("deductCount", deductCount);
        
        return summary;
    }

    @Override
    public List<SmsCouponHistory> getCouponHistory(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("create_time desc");
        return couponHistoryMapper.selectByExample(example);
    }

    @Override
    public List<SmsCouponHistoryDetailVO> getCouponHistoryDetail(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberAdminDao.selectCouponHistoryDetail(memberId);
    }

    @Override
    @Transactional
    public int sendCouponToMember(Long memberId, Long couponId, String reason) {
        // 验证用户存在
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return 0;
        }
        
        // 验证优惠券存在且有库存
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if (coupon == null || coupon.getPublishCount() <= 0) {
            return 0;
        }
        
        // 检查用户是否已经领取过该优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria()
               .andMemberIdEqualTo(memberId)
               .andCouponIdEqualTo(couponId);
        long existCount = couponHistoryMapper.countByExample(example);
        
        if (coupon.getPerLimit() != null && existCount >= coupon.getPerLimit()) {
            // 用户已达到该优惠券的领取上限
            return 0;
        }
        
        // 创建优惠券历史记录
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setMemberId(memberId);
        couponHistory.setMemberNickname(member.getNickname());
        couponHistory.setGetType(0); // 0-后台赠送
        couponHistory.setCreateTime(new Date());
        couponHistory.setUseStatus(0); // 0-未使用
        
        // 生成唯一券码
        String couponCode = "ADMIN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
        couponHistory.setCouponCode(couponCode);
        
        int result = couponHistoryMapper.insert(couponHistory);
        
        if (result > 0) {
            // 更新优惠券统计
            SmsCoupon updateCoupon = new SmsCoupon();
            updateCoupon.setId(couponId);
            updateCoupon.setReceiveCount((coupon.getReceiveCount() != null ? coupon.getReceiveCount() : 0) + 1);
            couponMapper.updateByPrimaryKeySelective(updateCoupon);
        }
        
        return result;
    }

    @Override
    public List<SmsCoupon> getAvailableCoupons(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        SmsCouponExample example = new SmsCouponExample();
        example.createCriteria()
               .andPublishCountGreaterThan(0) // 还有库存
               .andEndTimeGreaterThan(new Date()); // 还未过期
        example.setOrderByClause("enable_time desc");
        
        List<SmsCoupon> allCoupons = couponMapper.selectByExample(example);
        
        // 如果指定了用户ID，需要过滤掉用户已领取且有限制的优惠券
        if (memberId != null) {
            List<SmsCoupon> filteredCoupons = new ArrayList<>();
            for (SmsCoupon coupon : allCoupons) {
                // 检查用户是否已经领取过该优惠券
                SmsCouponHistoryExample historyExample = new SmsCouponHistoryExample();
                historyExample.createCriteria()
                              .andMemberIdEqualTo(memberId)
                              .andCouponIdEqualTo(coupon.getId());
                long existCount = couponHistoryMapper.countByExample(historyExample);
                
                // 如果用户未领取过，或者优惠券没有限制，或者领取次数未达到限制，则可以发放
                if (existCount == 0 || coupon.getPerLimit() == null || existCount < coupon.getPerLimit()) {
                    filteredCoupons.add(coupon);
                }
            }
            return filteredCoupons;
        }
        
        return allCoupons;
    }

    @Override
    public List<OmsOrder> getMemberOrders(Long memberId, Integer orderType, Integer status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        OmsOrderExample example = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = example.createCriteria().andMemberIdEqualTo(memberId);
        
        // 根据订单类型筛选
        if (orderType != null) {
            if (orderType == 0) {
                // 正常订单：is_gift = false 或 null，且 order_type = 0
                criteria.andOrderTypeEqualTo(0);
                // 处理 is_gift 为 null 或 false 的情况
                OmsOrderExample example2 = new OmsOrderExample();
                OmsOrderExample.Criteria criteria1 = example2.createCriteria()
                    .andMemberIdEqualTo(memberId)
                    .andOrderTypeEqualTo(0)
                    .andIsGiftEqualTo(Boolean.FALSE);
                OmsOrderExample.Criteria criteria2 = example2.createCriteria()
                    .andMemberIdEqualTo(memberId)
                    .andOrderTypeEqualTo(0)
                    .andIsGiftIsNull();
                example2.or(criteria1);
                example2.or(criteria2);
                
                // 添加状态筛选
                if (status != null) {
                    criteria1.andStatusEqualTo(status);
                    criteria2.andStatusEqualTo(status);
                }
                
                example2.setOrderByClause("create_time desc");
                return orderMapper.selectByExample(example2);
                
            } else if (orderType == 1) {
                // 秒杀订单：order_type = 1
                criteria.andOrderTypeEqualTo(1);
            } else if (orderType == 2) {
                // 送礼订单：is_gift = true
                criteria.andIsGiftEqualTo(Boolean.TRUE);
            }
        }
        
        // 根据订单状态筛选
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        
        example.setOrderByClause("create_time desc");
        return orderMapper.selectByExample(example);
    }

    @Override
    public Map<String, Object> getMemberOrderStatistics(Long memberId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 有效订单总数 (排除未支付、已关闭和无效订单)
        OmsOrderExample totalExample = new OmsOrderExample();
        totalExample.createCriteria().andMemberIdEqualTo(memberId).andStatusIn(Arrays.asList(1, 2, 3));
        long totalCount = orderMapper.countByExample(totalExample);
        statistics.put("totalCount", totalCount);
        
        // 待付款订单数 (状态0)
        OmsOrderExample pendingExample = new OmsOrderExample();
        pendingExample.createCriteria().andMemberIdEqualTo(memberId).andStatusEqualTo(0);
        long pendingCount = orderMapper.countByExample(pendingExample);
        statistics.put("pendingCount", pendingCount);
        
        // 待发货订单数 (状态1)
        OmsOrderExample toShipExample = new OmsOrderExample();
        toShipExample.createCriteria().andMemberIdEqualTo(memberId).andStatusEqualTo(1);
        long toShipCount = orderMapper.countByExample(toShipExample);
        statistics.put("toShipCount", toShipCount);
        
        // 待收货订单数 (状态2)
        OmsOrderExample shippedExample = new OmsOrderExample();
        shippedExample.createCriteria().andMemberIdEqualTo(memberId).andStatusEqualTo(2);
        long shippedCount = orderMapper.countByExample(shippedExample);
        statistics.put("shippedCount", shippedCount);
        
        // 已完成订单数 (状态3)
        OmsOrderExample completedExample = new OmsOrderExample();
        completedExample.createCriteria().andMemberIdEqualTo(memberId).andStatusEqualTo(3);
        long completedCount = orderMapper.countByExample(completedExample);
        statistics.put("completedCount", completedCount);
        
        // 已取消订单数 (状态4-已关闭，状态5-无效订单)
        OmsOrderExample cancelledExample = new OmsOrderExample();
        cancelledExample.createCriteria().andMemberIdEqualTo(memberId).andStatusIn(Arrays.asList(4, 5));
        long cancelledCount = orderMapper.countByExample(cancelledExample);
        statistics.put("cancelledCount", cancelledCount);
        
        // 送礼订单数 (只统计有效订单)
        OmsOrderExample giftExample = new OmsOrderExample();
        giftExample.createCriteria().andMemberIdEqualTo(memberId).andIsGiftEqualTo(Boolean.TRUE).andStatusIn(Arrays.asList(1, 2, 3));
        long giftCount = orderMapper.countByExample(giftExample);
        statistics.put("giftCount", giftCount);
        
        // 计算总消费金额 (只计算有效订单：待发货、已发货、已完成)
        OmsOrderExample paidAmountExample = new OmsOrderExample();
        paidAmountExample.createCriteria().andMemberIdEqualTo(memberId).andStatusIn(Arrays.asList(1, 2, 3));
        List<OmsOrder> paidOrders = orderMapper.selectByExample(paidAmountExample);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OmsOrder order : paidOrders) {
            if (order.getTotalAmount() != null) {
                totalAmount = totalAmount.add(order.getTotalAmount());
            }
        }
        statistics.put("totalAmount", totalAmount);
        
        // 计算平均客单价 (基于有效订单数)
        BigDecimal avgAmount = BigDecimal.ZERO;
        if (totalCount > 0) {
            avgAmount = totalAmount.divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
        }
        statistics.put("avgAmount", avgAmount);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getInviteSummary(Long memberId) {
        Map<String, Object> summary = new HashMap<>();
        
        // 直接从邀请关系表统计（实时统计）
        PmsInviteRelationExample relationExample = new PmsInviteRelationExample();
        relationExample.createCriteria().andInviterIdEqualTo(memberId);
        List<PmsInviteRelation> relations = inviteRelationMapper.selectByExample(relationExample);
        
        int totalInviteCount = relations.size();
        int totalRegisterCount = 0;
        int totalFirstOrderCount = 0;
        int validInviteCount = 0;
        
        // 统计注册和首单情况
        for (PmsInviteRelation relation : relations) {
            if (relation.getStatus() != null && relation.getStatus() >= 1) {
                totalRegisterCount++;
                validInviteCount++;
            }
            if (relation.getFirstOrderTime() != null) {
                totalFirstOrderCount++;
            }
        }
        
        // 从奖励表统计奖励金额
        PmsInviteRewardExample rewardExample = new PmsInviteRewardExample();
        rewardExample.createCriteria().andUserIdEqualTo(memberId);
        List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(rewardExample);
        
        BigDecimal totalRewardAmount = BigDecimal.ZERO;
        int totalRewardPoints = 0;
        
        for (PmsInviteReward reward : rewards) {
            if (reward.getRewardValue() != null) {
                totalRewardAmount = totalRewardAmount.add(reward.getRewardValue());
            }
        }
        
        // 从提现申请表统计实际提现金额（只统计状态为2-提现成功的记录）
        PmsInviteWithdrawApplyExample withdrawExample = new PmsInviteWithdrawApplyExample();
        withdrawExample.createCriteria().andUserIdEqualTo(memberId).andStatusEqualTo((byte) 2); // 只统计提现成功的记录
        List<PmsInviteWithdrawApply> withdrawApplies = inviteWithdrawApplyMapper.selectByExample(withdrawExample);
        
        BigDecimal totalWithdrawAmount = BigDecimal.ZERO;
        for (PmsInviteWithdrawApply apply : withdrawApplies) {
            if (apply.getApplyAmount() != null) {
                totalWithdrawAmount = totalWithdrawAmount.add(apply.getApplyAmount());
            }
        }
        
        summary.put("totalInviteCount", totalInviteCount);
        summary.put("validInviteCount", validInviteCount);
        summary.put("totalRegisterCount", totalRegisterCount);
        summary.put("totalFirstOrderCount", totalFirstOrderCount);
        summary.put("totalRewardAmount", totalRewardAmount);
        summary.put("totalWithdrawAmount", totalWithdrawAmount);
        summary.put("totalRewardPoints", totalRewardPoints);
        
        // 计算转化率
        double registerRate = totalInviteCount > 0 ? (double) totalRegisterCount / totalInviteCount * 100 : 0;
        double orderRate = totalRegisterCount > 0 ? (double) totalFirstOrderCount / totalRegisterCount * 100 : 0;
        
        summary.put("registerConversionRate", Math.round(registerRate * 100.0) / 100.0);
        summary.put("orderConversionRate", Math.round(orderRate * 100.0) / 100.0);
        
        return summary;
    }

    @Override
    public List<com.macro.mall.dto.PmsInviteRelationDetailVO> getInviteRelations(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberAdminDao.selectInviteRelationsWithDetail(memberId);
    }

    @Override
    public List<com.macro.mall.dto.PmsInviteRewardDetailVO> getInviteRewards(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberAdminDao.selectInviteRewardsWithDetail(memberId);
    }

    @Override
    public List<PmsInviteWithdrawApply> getWithdrawApplies(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PmsInviteWithdrawApplyExample example = new PmsInviteWithdrawApplyExample();
        example.createCriteria().andUserIdEqualTo(memberId);
        example.setOrderByClause("apply_time desc");
        return inviteWithdrawApplyMapper.selectByExample(example);
    }

    @Override
    public Map<String, Object> getMemberProfile(Long memberId) {
        Map<String, Object> profile = new HashMap<>();
        
        // 获取用户基本信息
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return profile;
        }
        
        // 获取用户统计信息
        UmsMemberStatisticsInfoExample statsExample = new UmsMemberStatisticsInfoExample();
        statsExample.createCriteria().andMemberIdEqualTo(memberId);
        List<UmsMemberStatisticsInfo> statsList = statisticsInfoMapper.selectByExample(statsExample);
        UmsMemberStatisticsInfo stats = !statsList.isEmpty() ? statsList.get(0) : null;
        
        // 计算RFM模型
        Map<String, Object> rfmScore = calculateRFMScore(memberId, stats);
        profile.put("rfmScore", rfmScore);
        
        // 生成用户标签（系统自动标签）
        List<String> userTags = generateUserTags(memberId, member, stats);
        profile.put("userTags", userTags);
        
        // 获取用户绑定的标签（手动绑定的标签）
        List<UmsMemberTag> memberTags = getMemberTags(memberId);
        profile.put("memberTags", memberTags);
        
        // 用户活跃度分析
        Map<String, Object> activityAnalysis = analyzeUserActivity(memberId, member);
        profile.put("activityAnalysis", activityAnalysis);
        
        // 消费能力分析
        Map<String, Object> spendingAnalysis = analyzeSpendingCapability(stats);
        profile.put("spendingAnalysis", spendingAnalysis);
        
        // 购买偏好分析
        Map<String, Object> preferences = analyzeBuyingPreferences(memberId, stats);
        profile.put("preferences", preferences);
        
        profile.put("lastUpdated", new Date());
        
        return profile;
    }

    @Override
    public Map<String, Object> generateMemberProfile(Long memberId) {
        // 重新生成用户画像，逻辑与getMemberProfile相同
        return getMemberProfile(memberId);
    }

    @Override
    public List<UmsMemberTag> getMemberTags(Long memberId) {
        // 查询用户的标签关联关系
        UmsMemberTagRelationExample relationExample = new UmsMemberTagRelationExample();
        relationExample.createCriteria().andMemberIdEqualTo(memberId);
        List<UmsMemberTagRelation> relations = memberTagRelationMapper.selectByExample(relationExample);
        
        if (CollectionUtils.isEmpty(relations)) {
            return new ArrayList<>();
        }
        
        // 获取标签ID列表
        List<Long> tagIds = relations.stream()
                .map(UmsMemberTagRelation::getTagId)
                .collect(java.util.stream.Collectors.toList());
        
        // 查询标签详情
        UmsMemberTagExample tagExample = new UmsMemberTagExample();
        tagExample.createCriteria().andIdIn(tagIds);
        return memberTagMapper.selectByExample(tagExample);
    }

    @Override
    @Transactional
    public int updateMemberTags(Long memberId, List<Long> tagIds) {
        // 先删除用户原有的标签关联
        UmsMemberTagRelationExample deleteExample = new UmsMemberTagRelationExample();
        deleteExample.createCriteria().andMemberIdEqualTo(memberId);
        memberTagRelationMapper.deleteByExample(deleteExample);
        
        if (CollectionUtils.isEmpty(tagIds)) {
            return 1; // 如果标签列表为空，只需删除原有关联即可
        }
        
        // 添加新的标签关联
        int successCount = 0;
        for (Long tagId : tagIds) {
            UmsMemberTagRelation relation = new UmsMemberTagRelation();
            relation.setMemberId(memberId);
            relation.setTagId(tagId);
            relation.setCreateTime(new Date());
            
            int result = memberTagRelationMapper.insert(relation);
            if (result > 0) {
                successCount++;
            }
        }
        
                return successCount > 0 ? 1 : 0;
    }
    
    @Override
    public List<UmsMemberTag> getAllTags() {
        UmsMemberTagExample example = new UmsMemberTagExample();
        example.setOrderByClause("id ASC");
        return memberTagMapper.selectByExample(example);
    }
    
    @Override
    @Transactional
    public int createTag(String name, String description, Boolean tagType, String color) {
        UmsMemberTag tag = new UmsMemberTag();
        tag.setName(name);
        tag.setDescription(description);
        tag.setTagType(tagType != null ? tagType : false); // false=自定义标签，true=系统标签
        tag.setColor(color);
        tag.setStatus(true); // 默认启用
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        return memberTagMapper.insertSelective(tag);
    }
    
    @Override
    @Transactional
    public int updateTag(Long tagId, String name, String description, String color) {
        UmsMemberTag tag = new UmsMemberTag();
        tag.setId(tagId);
        tag.setName(name);
        tag.setDescription(description);
        tag.setColor(color);
        tag.setUpdateTime(new Date());
        return memberTagMapper.updateByPrimaryKeySelective(tag);
    }
    
    @Override
    @Transactional
    public int updateTagStatus(Long tagId, Boolean status) {
        UmsMemberTag tag = new UmsMemberTag();
        tag.setId(tagId);
        tag.setStatus(status);
        tag.setUpdateTime(new Date());
        return memberTagMapper.updateByPrimaryKeySelective(tag);
    }
    
    @Override
    @Transactional
    public int deleteTag(Long tagId) {
        // 先删除用户与该标签的关联关系
        UmsMemberTagRelationExample relationExample = new UmsMemberTagRelationExample();
        relationExample.createCriteria().andTagIdEqualTo(tagId);
        memberTagRelationMapper.deleteByExample(relationExample);
        
        // 删除标签
        return memberTagMapper.deleteByPrimaryKey(tagId);
    }
    
    @Override
    @Transactional
    public int bindMemberTag(Long memberId, Long tagId) {
        // 检查是否已存在关联
        UmsMemberTagRelationExample example = new UmsMemberTagRelationExample();
        example.createCriteria().andMemberIdEqualTo(memberId).andTagIdEqualTo(tagId);
        List<UmsMemberTagRelation> existing = memberTagRelationMapper.selectByExample(example);
        
        if (!CollectionUtils.isEmpty(existing)) {
            return 1; // 已存在关联，返回成功
        }
        
        // 创建新的关联
        UmsMemberTagRelation relation = new UmsMemberTagRelation();
        relation.setMemberId(memberId);
        relation.setTagId(tagId);
        relation.setCreateTime(new Date());
        return memberTagRelationMapper.insert(relation);
    }
    
    @Override
    @Transactional
    public int unbindMemberTag(Long memberId, Long tagId) {
        UmsMemberTagRelationExample example = new UmsMemberTagRelationExample();
        example.createCriteria().andMemberIdEqualTo(memberId).andTagIdEqualTo(tagId);
        return memberTagRelationMapper.deleteByExample(example);
    }
    
    @Override
    @Transactional
    public int batchBindTags(List<Long> memberIds, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(memberIds) || CollectionUtils.isEmpty(tagIds)) {
            return 0;
        }
        
        int successCount = 0;
        for (Long memberId : memberIds) {
            for (Long tagId : tagIds) {
                try {
                    int result = bindMemberTag(memberId, tagId);
                    if (result > 0) {
                        successCount++;
                    }
                } catch (Exception e) {
                    // 忽略错误，继续处理其他标签
                    System.err.println("批量绑定标签失败，用户ID: " + memberId + ", 标签ID: " + tagId + ", 错误: " + e.getMessage());
                }
            }
        }
        
        return successCount > 0 ? 1 : 0;
    }
    
    @Override
    @Transactional
    public int batchAdjustIntegration(List<Long> memberIds, Integer integration, Integer operationType, String reason) {
        if (memberIds == null || memberIds.isEmpty() || integration <= 0) {
            return 0;
        }
        
        int successCount = 0;
        for (Long memberId : memberIds) {
            try {
                int result;
                if (operationType == 0) {
                    // 增加积分
                    result = giveIntegration(memberId, integration, reason);
                } else {
                    // 扣减积分
                    result = deductIntegration(memberId, integration, reason);
                }
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他用户
                System.err.println("批量调整积分失败，用户ID: " + memberId + ", 错误: " + e.getMessage());
            }
        }
        
        return successCount;
    }

    @Override
    @Transactional
    public int batchSendCoupon(List<Long> memberIds, Long couponId, String reason) {
        if (memberIds == null || memberIds.isEmpty() || couponId == null) {
            return 0;
        }
        
        int successCount = 0;
        for (Long memberId : memberIds) {
            try {
                int result = sendCouponToMember(memberId, couponId, reason);
                if (result > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录错误但继续处理其他用户
                System.err.println("批量发放优惠券失败，用户ID: " + memberId + ", 错误: " + e.getMessage());
            }
        }
        
        return successCount;
    }

    @Override
    public List<UmsMemberListVO> exportMemberData(UmsMemberQueryParam queryParam) {
        // 不使用分页，导出所有符合条件的数据
        return memberAdminDao.selectMemberList(queryParam);
    }

    /**
     * 计算RFM模型得分 - 直接从订单表查询真实数据
     */
    private Map<String, Object> calculateRFMScore(Long memberId, UmsMemberStatisticsInfo stats) {
        Map<String, Object> rfm = new HashMap<>();
        
        // 原始数据初始化
        long recencyDays = 999; // 默认很久没购买
        int frequency = 0;      // 默认购买次数0
        BigDecimal monetary = BigDecimal.ZERO; // 默认消费金额0
        
        try {
            // 直接从订单表查询真实数据
            OmsOrderExample orderExample = new OmsOrderExample();
            // 只统计有效订单：待发货(1)、已发货(2)、已完成(3)
            orderExample.createCriteria().andMemberIdEqualTo(memberId).andStatusIn(Arrays.asList(1, 2, 3));
            orderExample.setOrderByClause("create_time desc");
            
            List<OmsOrder> validOrders = orderMapper.selectByExample(orderExample);
            
            if (!CollectionUtils.isEmpty(validOrders)) {
                // R (Recency) - 最近购买时间：基于最新订单的创建时间
                Date latestOrderDate = validOrders.get(0).getCreateTime();
                if (latestOrderDate != null) {
                    recencyDays = (System.currentTimeMillis() - latestOrderDate.getTime()) / (1000 * 60 * 60 * 24);
                }
                
                // F (Frequency) - 购买频次：有效订单数量
                frequency = validOrders.size();
                
                // M (Monetary) - 消费金额：所有有效订单的总金额
                for (OmsOrder order : validOrders) {
                    if (order.getTotalAmount() != null) {
                        monetary = monetary.add(order.getTotalAmount());
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("计算RFM模型时出错，用户ID：{}", memberId, e);
            // 如果出错，回退到使用统计表数据
            if (stats != null) {
                if (stats.getRecentOrderTime() != null) {
                    recencyDays = (System.currentTimeMillis() - stats.getRecentOrderTime().getTime()) / (1000 * 60 * 60 * 24);
                }
                if (stats.getOrderCount() != null) {
                    frequency = stats.getOrderCount();
                }
                if (stats.getConsumeAmount() != null) {
                    monetary = stats.getConsumeAmount();
                }
            }
        }
        
        // R (Recency) - 最近购买时间评分
        int recencyScore = 1; // 默认最低分
        if (recencyDays <= 30) {
            recencyScore = 5; // 高
        } else if (recencyDays <= 90) {
            recencyScore = 4;
        } else if (recencyDays <= 180) {
            recencyScore = 3;
        } else if (recencyDays <= 365) {
            recencyScore = 2;
        } else {
            recencyScore = 1; // 低
        }
        
        // F (Frequency) - 购买频次评分
        int frequencyScore = 1;
        if (frequency >= 20) {
            frequencyScore = 5;
        } else if (frequency >= 10) {
            frequencyScore = 4;
        } else if (frequency >= 5) {
            frequencyScore = 3;
        } else if (frequency >= 2) {
            frequencyScore = 2;
        }
        
        // M (Monetary) - 消费金额评分
        int monetaryScore = 1;
        if (monetary.compareTo(new BigDecimal("5000")) >= 0) {
            monetaryScore = 5;
        } else if (monetary.compareTo(new BigDecimal("2000")) >= 0) {
            monetaryScore = 4;
        } else if (monetary.compareTo(new BigDecimal("500")) >= 0) {
            monetaryScore = 3;
        } else if (monetary.compareTo(new BigDecimal("100")) >= 0) {
            monetaryScore = 2;
        }
        
        // 返回评分
        rfm.put("recencyScore", recencyScore);
        rfm.put("frequencyScore", frequencyScore);
        rfm.put("monetaryScore", monetaryScore);
        rfm.put("totalScore", recencyScore + frequencyScore + monetaryScore);
        
        // 返回原始真实数据
        rfm.put("recencyDays", recencyDays);
        rfm.put("frequency", frequency);
        rfm.put("monetary", monetary);
        
        // 用户等级
        String userLevel;
        int totalScore = recencyScore + frequencyScore + monetaryScore;
        if (totalScore >= 12) {
            userLevel = "重要价值客户";
        } else if (totalScore >= 9) {
            userLevel = "重要发展客户";
        } else if (totalScore >= 6) {
            userLevel = "重要保持客户";
        } else {
            userLevel = "一般客户";
        }
        rfm.put("userLevel", userLevel);
        
        return rfm;
    }

    /**
     * 生成用户标签
     */
    private List<String> generateUserTags(Long memberId, UmsMember member, UmsMemberStatisticsInfo stats) {
        List<String> tags = new ArrayList<>();
        
        // 活跃度标签
        if (stats != null && stats.getRecentOrderTime() != null) {
            long daysSinceLastOrder = (System.currentTimeMillis() - stats.getRecentOrderTime().getTime()) / (1000 * 60 * 60 * 24);
            if (daysSinceLastOrder <= 30) {
                tags.add("活跃用户");
            } else if (daysSinceLastOrder > 180) {
                tags.add("沉睡用户");
            }
        }
        
        // 消费能力标签
        if (stats != null && stats.getConsumeAmount() != null) {
            BigDecimal consumeAmount = stats.getConsumeAmount();
            if (consumeAmount.compareTo(new BigDecimal("2000")) >= 0) {
                tags.add("高消费用户");
            } else if (consumeAmount.compareTo(new BigDecimal("500")) >= 0) {
                tags.add("中等消费用户");
            } else {
                tags.add("低消费用户");
            }
        }
        
        // 购买频次标签
        if (stats != null && stats.getOrderCount() != null) {
            if (stats.getOrderCount() >= 10) {
                tags.add("忠实客户");
            } else if (stats.getOrderCount() >= 3) {
                tags.add("回头客");
            } else if (stats.getOrderCount() == 1) {
                tags.add("新客户");
            }
        }
        
        // 邀请行为标签
        if (stats != null && stats.getInviteFriendCount() != null && stats.getInviteFriendCount() > 0) {
            if (stats.getInviteFriendCount() >= 5) {
                tags.add("邀请达人");
            } else {
                tags.add("有邀请行为");
            }
        }
        
        // 年龄标签（基于生日）
        if (member.getBirthday() != null) {
            int age = calculateAge(member.getBirthday());
            if (age >= 18 && age <= 25) {
                tags.add("年轻用户");
            } else if (age >= 26 && age <= 35) {
                tags.add("青年用户");
            } else if (age >= 36 && age <= 50) {
                tags.add("中年用户");
            } else if (age > 50) {
                tags.add("中老年用户");
            }
        }
        
        // 性别标签
        if (member.getGender() != null) {
            if (member.getGender() == 1) {
                tags.add("男性用户");
            } else if (member.getGender() == 2) {
                tags.add("女性用户");
            }
        }
        
        return tags;
    }

    /**
     * 分析用户活跃度
     */
    private Map<String, Object> analyzeUserActivity(Long memberId, UmsMember member) {
        Map<String, Object> activity = new HashMap<>();
        
        // 注册天数
        if (member.getCreateTime() != null) {
            long registerDays = (System.currentTimeMillis() - member.getCreateTime().getTime()) / (1000 * 60 * 60 * 24);
            activity.put("registerDays", registerDays);
        }
        
        // 最近登录次数
        UmsMemberLoginLogExample loginExample = new UmsMemberLoginLogExample();
        loginExample.createCriteria().andMemberIdEqualTo(memberId);
        long totalLoginCount = loginLogMapper.countByExample(loginExample);
        activity.put("totalLoginCount", totalLoginCount);
        
        // 近30天登录次数
        Date thirtyDaysAgo = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        UmsMemberLoginLogExample recentLoginExample = new UmsMemberLoginLogExample();
        recentLoginExample.createCriteria().andMemberIdEqualTo(memberId).andCreateTimeGreaterThan(thirtyDaysAgo);
        long recentLoginCount = loginLogMapper.countByExample(recentLoginExample);
        activity.put("recent30DaysLoginCount", recentLoginCount);
        
        return activity;
    }

    /**
     * 分析消费能力
     */
    private Map<String, Object> analyzeSpendingCapability(UmsMemberStatisticsInfo stats) {
        Map<String, Object> spending = new HashMap<>();
        
        if (stats != null) {
            // 总消费金额
            BigDecimal totalAmount = stats.getConsumeAmount() != null ? stats.getConsumeAmount() : BigDecimal.ZERO;
            spending.put("totalAmount", totalAmount);
            
            // 总订单数
            int totalOrders = stats.getOrderCount() != null ? stats.getOrderCount() : 0;
            spending.put("totalOrders", totalOrders);
            
            // 平均订单金额
            BigDecimal avgOrderAmount = BigDecimal.ZERO;
            if (totalOrders > 0) {
                avgOrderAmount = totalAmount.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
            }
            spending.put("avgOrderAmount", avgOrderAmount);
            
            // 消费能力等级
            String spendingLevel;
            if (totalAmount.compareTo(new BigDecimal("5000")) >= 0) {
                spendingLevel = "高消费能力";
            } else if (totalAmount.compareTo(new BigDecimal("1000")) >= 0) {
                spendingLevel = "中等消费能力";
            } else if (totalAmount.compareTo(new BigDecimal("100")) >= 0) {
                spendingLevel = "低消费能力";
            } else {
                spendingLevel = "待激活用户";
            }
            spending.put("spendingLevel", spendingLevel);
        }
        
        return spending;
    }

    /**
     * 分析购买偏好
     */
    private Map<String, Object> analyzeBuyingPreferences(Long memberId, UmsMemberStatisticsInfo stats) {
        Map<String, Object> preferences = new HashMap<>();
        
        // 默认值
        preferences.put("categories", new ArrayList<>());
        preferences.put("paymentMethod", 1); // 默认支付宝
        preferences.put("buyingTimeSlot", "全天");
        preferences.put("avgOrderAmount", BigDecimal.ZERO);
        preferences.put("couponUsageRate", 0);
        
        try {
            // 获取用户订单进行分析
            OmsOrderExample orderExample = new OmsOrderExample();
            orderExample.createCriteria().andMemberIdEqualTo(memberId).andStatusIn(Arrays.asList(1, 2, 3));
            orderExample.setOrderByClause("create_time desc");
            
            List<OmsOrder> orders = orderMapper.selectByExample(orderExample);
            
            if (!CollectionUtils.isEmpty(orders)) {
                // 计算平均订单金额
                BigDecimal totalAmount = BigDecimal.ZERO;
                Map<Integer, Integer> paymentMethodCount = new HashMap<>();
                int couponUsedCount = 0;
                
                for (OmsOrder order : orders) {
                    if (order.getTotalAmount() != null) {
                        totalAmount = totalAmount.add(order.getTotalAmount());
                    }
                    
                    // 统计支付方式
                    Integer paymentType = order.getPayType();
                    if (paymentType != null) {
                        paymentMethodCount.put(paymentType, paymentMethodCount.getOrDefault(paymentType, 0) + 1);
                    }
                    
                    // 统计优惠券使用
                    if (order.getCouponAmount() != null && order.getCouponAmount().compareTo(BigDecimal.ZERO) > 0) {
                        couponUsedCount++;
                    }
                }
                
                // 平均订单金额
                BigDecimal avgAmount = totalAmount.divide(new BigDecimal(orders.size()), 2, RoundingMode.HALF_UP);
                preferences.put("avgOrderAmount", avgAmount);
                
                // 常用支付方式（取使用最多的）
                Integer mostUsedPayment = paymentMethodCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(1);
                preferences.put("paymentMethod", mostUsedPayment);
                
                // 优惠券使用率
                int couponUsageRate = (int) ((couponUsedCount * 100.0) / orders.size());
                preferences.put("couponUsageRate", couponUsageRate);
                
                // 购买时段分析（简化：根据订单创建时间分析）
                Map<String, Integer> timeSlotCount = new HashMap<>();
                for (OmsOrder order : orders) {
                    if (order.getCreateTime() != null) {
                        int hour = order.getCreateTime().getHours();
                        String timeSlot;
                        if (hour >= 6 && hour < 12) {
                            timeSlot = "上午";
                        } else if (hour >= 12 && hour < 18) {
                            timeSlot = "下午";
                        } else if (hour >= 18 && hour < 24) {
                            timeSlot = "晚上";
                        } else {
                            timeSlot = "深夜";
                        }
                        timeSlotCount.put(timeSlot, timeSlotCount.getOrDefault(timeSlot, 0) + 1);
                    }
                }
                
                String preferredTimeSlot = timeSlotCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("全天");
                preferences.put("buyingTimeSlot", preferredTimeSlot);
            }
            
        } catch (Exception e) {
            log.error("分析用户购买偏好失败，用户ID：{}", memberId, e);
        }
        
        return preferences;
    }
    
    /**
     * 计算年龄
     */
    private int calculateAge(Date birthday) {
        if (birthday == null) return 0;
        
        long ageInMillis = System.currentTimeMillis() - birthday.getTime();
        return (int) (ageInMillis / (365.25 * 24 * 60 * 60 * 1000));
    }
    
    // =============== 签到记录管理实现 ===============
    
    @Override
    public List<UmsMemberSigninLog> getSignInHistory(Long memberId, String startDate, String endDate, Integer status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        
        UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
        UmsMemberSigninLogExample.Criteria criteria = example.createCriteria();
        
        // 必须指定用户ID
        criteria.andMemberIdEqualTo(memberId);
        
        // 日期范围筛选
        if (StrUtil.isNotBlank(startDate)) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                criteria.andSigninDateGreaterThanOrEqualTo(start);
            } catch (Exception e) {
                log.warn("解析开始日期失败: {}", startDate, e);
            }
        }
        
        if (StrUtil.isNotBlank(endDate)) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                Date end = sdf.parse(endDate);
                criteria.andSigninDateLessThanOrEqualTo(end);
            } catch (Exception e) {
                log.warn("解析结束日期失败: {}", endDate, e);
            }
        }
        
        // 签到状态筛选（这里简化处理，实际可以根据业务需求扩展）
        // status参数暂时不做筛选，因为UmsMemberSigninLog模型中没有status字段
        
        // 按签到日期倒序排列
        example.setOrderByClause("signin_date desc, signin_time desc");
        
        try {
            return memberSigninLogMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("获取用户签到记录失败，用户ID：{}", memberId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> getSignInSummary(Long memberId) {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // 获取用户签到记录
            UmsMemberSigninLogExample example = new UmsMemberSigninLogExample();
            example.createCriteria().andMemberIdEqualTo(memberId);
            List<UmsMemberSigninLog> allSigninLogs = memberSigninLogMapper.selectByExample(example);
            
            // 总签到天数
            int totalDays = allSigninLogs.size();
            summary.put("totalDays", totalDays);
            
            // 累计获得积分
            int totalPoints = allSigninLogs.stream()
                .mapToInt(log -> log.getPointsEarned() != null ? log.getPointsEarned() : 0)
                .sum();
            summary.put("totalPoints", totalPoints);
            
            // 连续签到天数（获取最新记录的连续天数）
            int continuousDays = 0;
            if (!allSigninLogs.isEmpty()) {
                // 按日期排序，获取最新的记录
                allSigninLogs.sort((a, b) -> b.getSigninDate().compareTo(a.getSigninDate()));
                UmsMemberSigninLog latestLog = allSigninLogs.get(0);
                continuousDays = latestLog.getContinuousDays() != null ? latestLog.getContinuousDays() : 0;
            }
            summary.put("continuousDays", continuousDays);
            
            // 本月签到天数
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentYear = cal.get(java.util.Calendar.YEAR);
            int currentMonth = cal.get(java.util.Calendar.MONTH) + 1; // Calendar月份从0开始
            String currentMonthStr = String.format("%04d-%02d", currentYear, currentMonth);
            
            int thisMonthDays = (int) allSigninLogs.stream()
                .filter(log -> {
                    if (log.getSigninMonth() != null) {
                        return log.getSigninMonth().equals(currentMonthStr);
                    }
                    // 如果signinMonth为空，从signinDate中提取
                    if (log.getSigninDate() != null) {
                        java.util.Calendar logCal = java.util.Calendar.getInstance();
                        logCal.setTime(log.getSigninDate());
                        int logYear = logCal.get(java.util.Calendar.YEAR);
                        int logMonth = logCal.get(java.util.Calendar.MONTH) + 1;
                        return logYear == currentYear && logMonth == currentMonth;
                    }
                    return false;
                })
                .count();
            summary.put("thisMonthDays", thisMonthDays);
            
            log.info("获取用户签到统计成功，用户ID：{}，统计结果：{}", memberId, summary);
            
        } catch (Exception e) {
            log.error("获取用户签到统计失败，用户ID：{}", memberId, e);
            // 返回默认值
            summary.put("totalDays", 0);
            summary.put("continuousDays", 0);
            summary.put("thisMonthDays", 0);
            summary.put("totalPoints", 0);
        }
        
        return summary;
    }
    
    @Override
    @Transactional
    public int supplementSignIn(Long memberId, String signinDate, String reason) {
        try {
            // 验证用户是否存在
            UmsMember member = memberMapper.selectByPrimaryKey(memberId);
            if (member == null) {
                log.warn("用户不存在，无法补签，用户ID：{}", memberId);
                return 0;
            }
            
            // 解析日期
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            Date targetDate = sdf.parse(signinDate);
            
            // 检查该日期是否已经签到过
            UmsMemberSigninLogExample checkExample = new UmsMemberSigninLogExample();
            checkExample.createCriteria()
                .andMemberIdEqualTo(memberId)
                .andSigninDateEqualTo(targetDate);
            
            List<UmsMemberSigninLog> existingLogs = memberSigninLogMapper.selectByExample(checkExample);
            if (!existingLogs.isEmpty()) {
                log.warn("该日期已经签到过，无法重复补签，用户ID：{}，日期：{}", memberId, signinDate);
                return 0;
            }
            
            // 计算连续签到天数（简化处理：查询用户最新的签到记录）
            UmsMemberSigninLogExample latestExample = new UmsMemberSigninLogExample();
            latestExample.createCriteria().andMemberIdEqualTo(memberId);
            latestExample.setOrderByClause("signin_date desc limit 1");
            List<UmsMemberSigninLog> latestLogs = memberSigninLogMapper.selectByExample(latestExample);
            
            int continuousDays = 1; // 默认为1天
            if (!latestLogs.isEmpty()) {
                UmsMemberSigninLog latestLog = latestLogs.get(0);
                // 如果补签日期是连续的，则增加连续天数
                java.util.Calendar latestCal = java.util.Calendar.getInstance();
                latestCal.setTime(latestLog.getSigninDate());
                java.util.Calendar targetCal = java.util.Calendar.getInstance();
                targetCal.setTime(targetDate);
                
                long daysDiff = (targetDate.getTime() - latestLog.getSigninDate().getTime()) / (1000 * 60 * 60 * 24);
                if (daysDiff == 1) {
                    continuousDays = (latestLog.getContinuousDays() != null ? latestLog.getContinuousDays() : 0) + 1;
                }
            }
            
            // 创建补签记录
            UmsMemberSigninLog signinLog = new UmsMemberSigninLog();
            signinLog.setMemberId(memberId);
            // 使用 nickname，如果为空则使用用户ID作为备选
            String memberUsername = StrUtil.isNotBlank(member.getNickname()) ? 
                member.getNickname() : "用户" + memberId;
            signinLog.setMemberUsername(memberUsername);
            signinLog.setSigninDate(targetDate);
            signinLog.setSigninTime(new Date()); // 补签时间为当前时间
            signinLog.setPointsEarned(5); // 补签给予固定积分，可以根据业务规则调整
            signinLog.setContinuousDays(continuousDays);
            signinLog.setCycleSigninDays(1); // 周期签到天数，简化处理
            signinLog.setIsRewardClaimed((byte) 0); // 未领取奖励
            
            // 设置签到月份
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(targetDate);
            String signinMonth = String.format("%04d-%02d", 
                cal.get(java.util.Calendar.YEAR), 
                cal.get(java.util.Calendar.MONTH) + 1);
            signinLog.setSigninMonth(signinMonth);
            
            // 插入签到记录
            int result = memberSigninLogMapper.insert(signinLog);
            
            if (result > 0) {
                // 更新用户积分
                UmsMember updateMember = new UmsMember();
                updateMember.setId(memberId);
                int currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
                updateMember.setIntegration(currentIntegration + signinLog.getPointsEarned());
                memberMapper.updateByPrimaryKeySelective(updateMember);
                
                // 记录积分变更历史
                UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
                history.setMemberId(memberId);
                history.setChangeType(1); // 增加积分
                history.setChangeCount(signinLog.getPointsEarned());
                history.setOperateMan("系统管理员");
                history.setOperateNote("管理员补签");
                history.setCreateTime(new Date());
                integrationChangeHistoryMapper.insert(history);

                // 检查并升级会员等级
                try {
                    checkAndUpgradeMemberLevel(memberId);
                } catch (Exception e) {
                    log.error("补签后检查会员等级升级失败: memberId={}", memberId, e);
                    // 等级升级失败不影响补签
                }

                log.info("用户补签成功，用户ID：{}，补签日期：{}，获得积分：{}",
                    memberId, signinDate, signinLog.getPointsEarned());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("用户补签失败，用户ID：{}，补签日期：{}", memberId, signinDate, e);
            throw new RuntimeException("补签失败：" + e.getMessage());
        }
    }

    /**
     * 检查并升级会员等级
     */
    private boolean checkAndUpgradeMemberLevel(Long memberId) {
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
                log.info("用户等级升级成功: memberId={}, oldLevelId={}, newLevelId={}, levelName={}",
                    memberId, currentLevelId, newLevelId, newLevel.getName());

                // 发放等级升级优惠券
                grantLevelUpgradeCoupon(memberId, newLevelId);
                return true;
            }

        } catch (Exception e) {
            log.error("检查等级升级失败: memberId={}", memberId, e);
        }

        return false;
    }

    /**
     * 根据积分计算应该的会员等级
     */
    private Long calculateMemberLevelByIntegration(Integer totalIntegration) {
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
            log.error("计算会员等级失败: totalIntegration={}", totalIntegration, e);
            return null;
        }
    }

    /**
     * 发放等级升级优惠券
     */
    private boolean grantLevelUpgradeCoupon(Long memberId, Long newLevelId) {
        try {
            UmsMemberLevel level = memberLevelMapper.selectByPrimaryKey(newLevelId);
            if (level == null || level.getGiftCouponId() == null) {
                log.info("等级没有关联优惠券: levelId={}", newLevelId);
                return true; // 没有优惠券也算成功
            }

            // 调用现有的优惠券发放方法
            int result = sendCouponToMember(memberId, level.getGiftCouponId(), "等级升级赠送");
            return result > 0;

        } catch (Exception e) {
            log.error("发放等级升级优惠券失败: memberId={}, levelId={}", memberId, newLevelId, e);
            return false;
        }
    }
}