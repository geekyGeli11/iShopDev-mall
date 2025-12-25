package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.DistributionStatsResult;
import com.macro.mall.portal.domain.DistributorApplyResult;
import com.macro.mall.portal.domain.CustomerRecord;
import com.macro.mall.portal.dto.DistributorApplyParam;
import com.macro.mall.portal.service.DistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分销服务实现类
 */
@Service
public class DistributionServiceImpl implements DistributionService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionServiceImpl.class);
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Autowired
    private UmsDistributorApplyMapper distributorApplyMapper;
    
    @Autowired
    private PmsInviteRelationMapper inviteRelationMapper;
    
    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;
    
    @Autowired
    private PmsInviteStatisticsMapper inviteStatisticsMapper;
    
    @Autowired
    private PmsInviteParamLogMapper inviteParamLogMapper;
    
    @Autowired
    private PmsSystemConfigMapper systemConfigMapper;

    @Override
    public DistributorApplyResult getDistributorApplyStatus(Long userId) {
        try {
            UmsMember member = memberMapper.selectByPrimaryKey(userId);
            if (member == null) {
                return null;
            }
            
            DistributorApplyResult result = new DistributorApplyResult();
            result.setIsDistributor(member.getIsDistributor() != null && member.getIsDistributor() == 1);
            result.setDistributorLevel(member.getDistributorLevel() != null ? member.getDistributorLevel().intValue() : null);
            
            // 查询申请记录
            UmsDistributorApplyExample example = new UmsDistributorApplyExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<UmsDistributorApply> applies = distributorApplyMapper.selectByExample(example);
            
            if (!applies.isEmpty()) {
                UmsDistributorApply apply = applies.get(0);
                // 转换状态为字符串
                String statusString = "";
                switch (apply.getStatus()) {
                    case 0:
                        statusString = "pending";
                        break;
                    case 1:
                        statusString = "approved";
                        break;
                    case 2:
                        statusString = "rejected";
                        break;
                    default:
                        statusString = "none";
                        break;
                }
                result.setApplyStatus(statusString);
                result.setApplyTime(apply.getApplyTime());
                result.setApprovedTime(apply.getReviewTime());
                result.setRejectReason(apply.getReviewComment());
            } else {
                result.setApplyStatus("none"); // 未申请
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.error("获取分销商申请状态失败, userId: {}", userId, e);
            return null;
        }
    }

    @Override
    @Transactional
    public Boolean submitDistributorApply(Long userId, DistributorApplyParam param) {
        try {
            // 检查是否已经是分销商
            UmsMember member = memberMapper.selectByPrimaryKey(userId);
            if (member == null) {
                throw new RuntimeException("用户不存在");
            }
            
            if (member.getIsDistributor() != null && member.getIsDistributor() == 1) {
                throw new RuntimeException("您已经是分销商，无需重复申请");
            }
            
            // 检查是否已有申请记录
            UmsDistributorApplyExample example = new UmsDistributorApplyExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<UmsDistributorApply> existingApplies = distributorApplyMapper.selectByExample(example);
            
            if (!existingApplies.isEmpty()) {
                UmsDistributorApply existingApply = existingApplies.get(0);
                if (existingApply.getStatus() == 0) {
                    throw new RuntimeException("您已提交申请，请等待审核");
                } else if (existingApply.getStatus() == 1) {
                    throw new RuntimeException("您的申请已通过，无需重复申请");
                }
                // 如果是被拒绝的申请，可以重新申请，更新现有记录
                existingApply.setRealName(member.getNickname() != null ? member.getNickname() : "分销申请用户");
                existingApply.setIdCard("待完善");
                existingApply.setPhone(param.getPhone());
                existingApply.setWechat(param.getPhone());
                existingApply.setApplyReason(param.getSelfIntroduction() != null ? param.getSelfIntroduction() : "申请成为分销商");
                existingApply.setExperience(param.getSelfIntroduction());
                existingApply.setSelfIntroduction(param.getSelfIntroduction());
                existingApply.setInfluenceScreenshots(param.getInfluenceScreenshots());
                existingApply.setIdCardFrontImg(param.getIdCardFront());
                existingApply.setIdCardBackImg(param.getIdCardBack());
                existingApply.setOtherCertificates(param.getOtherCertificates());
                existingApply.setStatus((byte) 0);
                existingApply.setApplyTime(new Date());
                existingApply.setReviewTime(null);
                existingApply.setReviewerId(null);
                existingApply.setReviewComment(null);
                existingApply.setUpdatedAt(new Date());
                
                distributorApplyMapper.updateByPrimaryKey(existingApply);
            } else {
                // 创建新的申请记录
                UmsDistributorApply apply = new UmsDistributorApply();
                apply.setUserId(userId);
                // 从用户信息获取真实姓名，如果没有则使用默认值
                apply.setRealName(member.getNickname() != null ? member.getNickname() : "分销申请用户");
                apply.setIdCard("待完善"); // 身份证号暂时使用占位符，后续可从身份证图片中提取
                apply.setPhone(param.getPhone());
                apply.setWechat(param.getPhone()); // 使用手机号作为微信号
                apply.setApplyReason(param.getSelfIntroduction() != null ? param.getSelfIntroduction() : "申请成为分销商");
                apply.setExperience(param.getSelfIntroduction());
                apply.setSelfIntroduction(param.getSelfIntroduction());
                apply.setInfluenceScreenshots(param.getInfluenceScreenshots());
                apply.setIdCardFrontImg(param.getIdCardFront());
                apply.setIdCardBackImg(param.getIdCardBack());
                apply.setOtherCertificates(param.getOtherCertificates());
                apply.setStatus((byte) 0); // 待审核
                apply.setApplyTime(new Date());
                apply.setCreatedAt(new Date());
                apply.setUpdatedAt(new Date());
                
                distributorApplyMapper.insert(apply);
            }
            
            // 更新会员申请时间
            UmsMember updateMember = new UmsMember();
            updateMember.setId(userId);
            updateMember.setApplyTime(new Date());
            memberMapper.updateByPrimaryKeySelective(updateMember);
            
            return true;
        } catch (Exception e) {
            LOGGER.error("提交分销商申请失败, userId: {}", userId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getDistributorApplyDetail(Long userId) {
        Map<String, Object> detail = new HashMap<>();
        
        try {
            // 查询申请记录
            UmsDistributorApplyExample example = new UmsDistributorApplyExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<UmsDistributorApply> applies = distributorApplyMapper.selectByExampleWithBLOBs(example);
            
            if (!applies.isEmpty()) {
                UmsDistributorApply apply = applies.get(0);
                detail.put("realName", apply.getRealName());
                detail.put("idCard", apply.getIdCard());
                detail.put("phone", apply.getPhone());
                detail.put("wechat", apply.getWechat());
                detail.put("applyReason", apply.getApplyReason());
                detail.put("experience", apply.getExperience());
                detail.put("status", apply.getStatus());
                detail.put("applyTime", apply.getApplyTime());
                detail.put("reviewTime", apply.getReviewTime());
                detail.put("reviewComment", apply.getReviewComment());
                
                // 状态描述
                String statusDesc = "";
                switch (apply.getStatus()) {
                    case 0:
                        statusDesc = "待审核";
                        break;
                    case 1:
                        statusDesc = "已通过";
                        break;
                    case 2:
                        statusDesc = "已拒绝";
                        break;
                }
                detail.put("statusDesc", statusDesc);
            }
            
            return detail;
        } catch (Exception e) {
            LOGGER.error("获取分销商申请详情失败, userId: {}", userId, e);
            return detail;
        }
    }

    @Override
    public Map<String, Object> checkApplyEligibility(Long userId) {
        Map<String, Object> eligibility = new HashMap<>();

        try {
            UmsMember member = memberMapper.selectByPrimaryKey(userId);
            if (member == null) {
                eligibility.put("isMember", false);
                eligibility.put("hasPhone", false);
                eligibility.put("hasEnoughPoints", false);
                return eligibility;
            }

            // 检查是否已注册成为会员
            boolean isMember = member.getStatus() != null && member.getStatus() == 1;

            // 检查是否绑定手机号
            boolean hasPhone = member.getPhone() != null && !member.getPhone().trim().isEmpty();

            // 检查积分是否满200
            boolean hasEnoughPoints = member.getIntegration() != null && member.getIntegration() >= 200;

            // 检查是否已经是分销商
            boolean isDistributor = member.getIsDistributor() != null && member.getIsDistributor() == 1;

            // 检查是否有待审核的申请
            UmsDistributorApplyExample example = new UmsDistributorApplyExample();
            example.createCriteria().andUserIdEqualTo(userId).andStatusEqualTo((byte) 0);
            List<UmsDistributorApply> pendingApplies = distributorApplyMapper.selectByExample(example);
            boolean hasPendingApply = !pendingApplies.isEmpty();

            eligibility.put("isMember", isMember);
            eligibility.put("hasPhone", hasPhone);
            eligibility.put("hasEnoughPoints", hasEnoughPoints);
            eligibility.put("isDistributor", isDistributor);
            eligibility.put("hasPendingApply", hasPendingApply);
            eligibility.put("currentPoints", member.getIntegration() != null ? member.getIntegration() : 0);
            eligibility.put("phone", member.getPhone());

            // 如果有待审核申请，返回申请ID
            if (hasPendingApply) {
                eligibility.put("applyId", pendingApplies.get(0).getId());
            }

            return eligibility;
        } catch (Exception e) {
            LOGGER.error("检查申请资格失败, userId: {}", userId, e);
            eligibility.put("isMember", false);
            eligibility.put("hasPhone", false);
            eligibility.put("hasEnoughPoints", false);
            return eligibility;
        }
    }

    @Override
    @Transactional
    public Boolean supplementApplyInfo(Long applyId, String workScreenshots) {
        try {
            // 查找申请记录
            UmsDistributorApply apply = distributorApplyMapper.selectByPrimaryKey(applyId);
            if (apply == null) {
                throw new RuntimeException("申请记录不存在");
            }

            // 检查申请状态，只有待审核状态才能补充信息
            if (apply.getStatus() != 0) {
                throw new RuntimeException("当前申请状态不允许补充信息");
            }

            // 更新工作截图信息
            apply.setWorkScreenshots(workScreenshots);
            apply.setUpdatedAt(new Date());

            int result = distributorApplyMapper.updateByPrimaryKeyWithBLOBs(apply);
            return result > 0;
        } catch (Exception e) {
            LOGGER.error("补充申请信息失败, applyId: {}", applyId, e);
            throw new RuntimeException("补充申请信息失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getDistributorRequirements() {
        Map<String, Object> requirements = new HashMap<>();
        
        requirements.put("title", "分销商申请要求");
        requirements.put("description", "成为我们的分销商，开启您的创业之路");
        
        List<Map<String, Object>> items = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("title", "基本要求");
        item1.put("content", Arrays.asList(
            "年满18周岁，具有完全民事行为能力",
            "账户状态正常，注册满7天",
            "提供真实有效的个人信息",
            "同意并遵守分销商服务协议"
        ));
        items.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("title", "推广能力");
        item2.put("content", Arrays.asList(
            "具备一定的社交媒体使用经验",
            "有一定的客户资源或推广渠道",
            "具备良好的沟通和服务意识",
            "愿意投入时间进行产品推广"
        ));
        items.add(item2);
        
        Map<String, Object> item3 = new HashMap<>();
        item3.put("title", "佣金政策");
        item3.put("content", Arrays.asList(
            "一级客户订单佣金：5%",
            "二级客户订单佣金：2%",
            "佣金每月结算一次",
            "满100元即可申请提现"
        ));
        items.add(item3);
        
        requirements.put("items", items);
        
        return requirements;
    }

    @Override
    public Boolean isDistributor(Long userId) {
        try {
            UmsMember member = memberMapper.selectByPrimaryKey(userId);
            return member != null && member.getIsDistributor() != null && member.getIsDistributor() == 1;
        } catch (Exception e) {
            LOGGER.error("检查是否为分销商失败, userId: {}", userId, e);
            return false;
        }
    }

    @Override
    public String generateDistributionCode(Long userId) {
        try {
            // 生成邀请参数
            long timestamp = System.currentTimeMillis();
            String inviteParam = userId + "_" + timestamp;
            
            // 记录邀请参数日志
            PmsInviteParamLog paramLog = new PmsInviteParamLog();
            paramLog.setUserId(userId);
            paramLog.setInviteParam(inviteParam);
            paramLog.setParamTimestamp(timestamp);
            paramLog.setExpireTime(new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L)); // 3天后过期
            paramLog.setUseCount(0);
            paramLog.setMaxUseCount(0); // 0表示无限制
            paramLog.setStatus((byte) 1);
            paramLog.setCreatedAt(new Date());
            paramLog.setUpdatedAt(new Date());
            
            inviteParamLogMapper.insert(paramLog);
            
            return inviteParam;
        } catch (Exception e) {
            LOGGER.error("生成分销码失败, userId: {}", userId, e);
            throw new RuntimeException("生成分销码失败");
        }
    }

    @Override
    public String getMyDistributionCode(Long userId) {
        try {
            // 查询最新的有效邀请参数
            PmsInviteParamLogExample example = new PmsInviteParamLogExample();
            example.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andStatusEqualTo((byte) 1)
                    .andExpireTimeGreaterThan(new Date());
            example.setOrderByClause("created_at DESC");
            
            List<PmsInviteParamLog> logs = inviteParamLogMapper.selectByExample(example);
            if (!logs.isEmpty()) {
                return logs.get(0).getInviteParam();
            }
            
            // 如果没有有效的邀请参数，生成一个新的
            return generateDistributionCode(userId);
        } catch (Exception e) {
            LOGGER.error("获取分销码失败, userId: {}", userId, e);
            return null;
        }
    }

    @Override
    public DistributionStatsResult getMyDistributionStats(Long userId) {
        if (!isDistributor(userId)) {
            throw new RuntimeException("您还不是分销商");
        }
        
        try {
            DistributionStatsResult result = new DistributionStatsResult();
            
            // 查询佣金统计
            PmsInviteRewardExample rewardExample = new PmsInviteRewardExample();
            rewardExample.createCriteria().andUserIdEqualTo(userId);
            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(rewardExample);
            
            // 计算累计佣金
            BigDecimal totalCommission = rewards.stream()
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setTotalCommission(totalCommission);
            
            // 计算已发放佣金（可提现金额）
            BigDecimal availableAmount = rewards.stream()
                .filter(reward -> reward.getStatus() == 1) // 已发放
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setAvailableAmount(availableAmount);
            
            // 计算待结算佣金
            BigDecimal pendingCommission = rewards.stream()
                .filter(reward -> reward.getStatus() == 0) // 待发放
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setPendingCommission(pendingCommission);
            
            // 查询客户统计
            PmsInviteRelationExample relationExample = new PmsInviteRelationExample();
            relationExample.createCriteria().andInviterIdEqualTo(userId);
            List<PmsInviteRelation> relations = inviteRelationMapper.selectByExample(relationExample);
            
            // 计算一级客户数量
            int level1Count = (int) relations.stream()
                .filter(relation -> relation.getDistributorLevel() == 1)
                .count();
            result.setLevel1CustomerCount(level1Count);
            
            // 计算二级客户数量
            int level2Count = (int) relations.stream()
                .filter(relation -> relation.getDistributorLevel() == 2)
                .count();
            result.setLevel2CustomerCount(level2Count);
            
            // 计算订单统计
            int totalOrderCount = (int) relations.stream()
                .filter(relation -> relation.getFirstOrderTime() != null)
                .count();
            result.setTotalOrderCount(totalOrderCount);
            
            BigDecimal totalOrderAmount = relations.stream()
                .filter(relation -> relation.getFirstOrderAmount() != null)
                .map(PmsInviteRelation::getFirstOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setTotalOrderAmount(totalOrderAmount);
            
            // 计算本月佣金
            Date startOfMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
            BigDecimal monthCommission = rewards.stream()
                .filter(reward -> reward.getCreatedAt() != null && reward.getCreatedAt().after(startOfMonth))
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setMonthCommission(monthCommission);
            
            // 计算昨日佣金
            Date yesterday = DateUtils.addDays(new Date(), -1);
            Date yesterdayStart = DateUtils.truncate(yesterday, Calendar.DAY_OF_MONTH);
            Date yesterdayEnd = DateUtils.addDays(yesterdayStart, 1);
            
            BigDecimal yesterdayCommission = rewards.stream()
                .filter(reward -> reward.getCreatedAt() != null && 
                        reward.getCreatedAt().after(yesterdayStart) && 
                        reward.getCreatedAt().before(yesterdayEnd))
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            result.setYesterdayCommission(yesterdayCommission);
            
            // 获取分销码
            String distributionCode = getMyDistributionCode(userId);
            result.setDistributionCode(distributionCode);
            
            // 已提现金额暂时设为0，需要从提现记录查询
            result.setWithdrawAmount(BigDecimal.ZERO);
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("获取分销统计失败, userId: {}", userId, e);
            throw new RuntimeException("获取分销统计失败");
        }
    }

    @Override
    public Map<String, Object> getMyCustomers(Long memberId, Integer page, Integer size, String level) {
        // 检查是否为分销商
        if (!isDistributor(memberId)) {
            throw new RuntimeException("您还不是分销商");
        }
        
        try {
            // 查询邀请关系
            PmsInviteRelationExample example = new PmsInviteRelationExample();
            PmsInviteRelationExample.Criteria criteria = example.createCriteria().andInviterIdEqualTo(memberId);
            
            if ("1".equals(level)) {
                criteria.andDistributorLevelEqualTo((byte) 1);
            } else if ("2".equals(level)) {
                criteria.andDistributorLevelEqualTo((byte) 2);
            }
            
            example.setOrderByClause("created_at DESC");
            
            // 先查询总数
            long totalCount = inviteRelationMapper.countByExample(example);
            
            // 分页查询
            PageHelper.startPage(page, size);
            List<PmsInviteRelation> relations = inviteRelationMapper.selectByExample(example);
            PageInfo<PmsInviteRelation> pageInfo = new PageInfo<>(relations);
            
            List<CustomerRecord> customerList = relations.stream().map(relation -> {
                CustomerRecord record = new CustomerRecord();
                
                // 查询客户信息
                UmsMember customer = memberMapper.selectByPrimaryKey(relation.getInviteeId());
                if (customer != null) {
                    record.setCustomerId(customer.getId());
                    record.setNickname(customer.getNickname());
                    record.setPhone(customer.getPhone());
                    record.setAvatar(customer.getIcon());
                }
                
                record.setLevel(relation.getDistributorLevel().intValue());
                record.setBindTime(relation.getCreatedAt());
                record.setLastOrderTime(relation.getFirstOrderTime());
                record.setOrderAmount(relation.getFirstOrderAmount());
                record.setBindStatus(relation.getStatus().intValue());
                
                // 查询客户订单统计
                if (customer != null) {
                    // 这里应该查询实际的订单数量和金额
                    record.setOrderCount(0);
                    record.setCommissionAmount(BigDecimal.ZERO);
                }
                
                return record;
            }).collect(Collectors.toList());
            
            // 构造返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", customerList);
            result.put("total", totalCount);
            result.put("page", page);
            result.put("size", size);
            result.put("hasMore", pageInfo.isHasNextPage());
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("获取客户列表失败, memberId: {}", memberId, e);
            return new HashMap<>();
        }
    }
    
    @Override
    public Map<String, Object> getMyCustomerStats(Long memberId) {
        // 检查是否为分销商
        if (!isDistributor(memberId)) {
            throw new RuntimeException("您还不是分销商");
        }
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 查询总客户数
            PmsInviteRelationExample example = new PmsInviteRelationExample();
            example.createCriteria().andInviterIdEqualTo(memberId);
            int totalCustomers = (int) inviteRelationMapper.countByExample(example);
            stats.put("totalCustomers", totalCustomers);
            
            // 查询一级客户数
            PmsInviteRelationExample firstLevelExample = new PmsInviteRelationExample();
            firstLevelExample.createCriteria().andInviterIdEqualTo(memberId).andDistributorLevelEqualTo((byte) 1);
            int firstLevelCustomers = (int) inviteRelationMapper.countByExample(firstLevelExample);
            stats.put("firstLevelCustomers", firstLevelCustomers);
            
            // 查询二级客户数
            PmsInviteRelationExample secondLevelExample = new PmsInviteRelationExample();
            secondLevelExample.createCriteria().andInviterIdEqualTo(memberId).andDistributorLevelEqualTo((byte) 2);
            int secondLevelCustomers = (int) inviteRelationMapper.countByExample(secondLevelExample);
            stats.put("secondLevelCustomers", secondLevelCustomers);
            
            // 查询今日新增客户数
            Date today = new Date();
            Date startOfDay = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);
            PmsInviteRelationExample todayExample = new PmsInviteRelationExample();
            todayExample.createCriteria().andInviterIdEqualTo(memberId).andCreatedAtGreaterThanOrEqualTo(startOfDay);
            int newCustomersToday = (int) inviteRelationMapper.countByExample(todayExample);
            stats.put("newCustomersToday", newCustomersToday);
            
            // 查询本月新增客户数
            Date startOfMonth = DateUtils.truncate(today, Calendar.MONTH);
            PmsInviteRelationExample monthExample = new PmsInviteRelationExample();
            monthExample.createCriteria().andInviterIdEqualTo(memberId).andCreatedAtGreaterThanOrEqualTo(startOfMonth);
            int newCustomersThisMonth = (int) inviteRelationMapper.countByExample(monthExample);
            stats.put("newCustomersThisMonth", newCustomersThisMonth);
            
            // 查询活跃客户数（有订单的客户）
            PmsInviteRelationExample activeExample = new PmsInviteRelationExample();
            activeExample.createCriteria().andInviterIdEqualTo(memberId).andFirstOrderTimeIsNotNull();
            int activeCustomers = (int) inviteRelationMapper.countByExample(activeExample);
            stats.put("activeCustomers", activeCustomers);
            
            // 查询总订单数和总金额、总佣金 - 使用邀请关系表统计
            List<PmsInviteRelation> relations = inviteRelationMapper.selectByExample(example);
            
            int totalOrders = (int) relations.stream()
                .filter(relation -> relation.getFirstOrderTime() != null)
                .count();
            BigDecimal totalAmount = relations.stream()
                .filter(relation -> relation.getFirstOrderAmount() != null)
                .map(PmsInviteRelation::getFirstOrderAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 简单估算佣金 = 订单金额 * 默认比例
            BigDecimal totalCommission = totalAmount.multiply(new BigDecimal("0.05"));
            
            stats.put("totalOrders", totalOrders);
            stats.put("totalAmount", totalAmount);
            stats.put("totalCommission", totalCommission);
            
            return stats;
            
        } catch (Exception e) {
            LOGGER.error("获取客户统计失败, memberId: {}", memberId, e);
            return new HashMap<>();
        }
    }
    
    @Override
    public Map<String, Object> getMyCommissionRecords(Long memberId, Integer page, Integer size, String filter) {
        // 检查是否为分销商
        if (!isDistributor(memberId)) {
            throw new RuntimeException("您还不是分销商");
        }
        
        try {
            // 查询佣金记录 - 根据用户ID查询
            PmsInviteRewardExample example = new PmsInviteRewardExample();
            PmsInviteRewardExample.Criteria criteria = example.createCriteria().andUserIdEqualTo(memberId);
            
            // 根据筛选条件查询
            if ("pending".equals(filter)) {
                criteria.andStatusEqualTo((byte) 0); // 待发放
            } else if ("settled".equals(filter)) {
                criteria.andStatusEqualTo((byte) 1); // 已发放
            }
            
            example.setOrderByClause("created_at DESC");
            
            // 分页查询
            PageHelper.startPage(page, size);
            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(example);
            PageInfo<PmsInviteReward> pageInfo = new PageInfo<>(rewards);
            
            List<Map<String, Object>> records = rewards.stream().map(reward -> {
                Map<String, Object> record = new HashMap<>();
                record.put("id", reward.getId());
                
                // 根据订单ID查询订单号
                if (reward.getOrderId() != null) {
                    record.put("orderSn", "ORDER" + reward.getOrderId());
                } else {
                    record.put("orderSn", "");
                }
                
                record.put("orderAmount", reward.getOrderAmount());
                record.put("commissionAmount", reward.getRewardValue());
                record.put("commissionRate", reward.getCommissionRate());
                record.put("level", reward.getCommissionType() != null ? reward.getCommissionType().toString() : "1");
                record.put("status", reward.getStatus() == 0 ? "pending" : "settled");
                record.put("createTime", reward.getCreatedAt());
                record.put("settleTime", reward.getSendTime());
                
                // 查询客户信息 - 通过关系表查询
                if (reward.getRelationId() != null) {
                    PmsInviteRelation relation = inviteRelationMapper.selectByPrimaryKey(reward.getRelationId());
                    if (relation != null && relation.getInviteeId() != null) {
                        UmsMember customer = memberMapper.selectByPrimaryKey(relation.getInviteeId());
                        if (customer != null) {
                            record.put("customerName", customer.getNickname());
                        }
                    }
                }
                
                return record;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", records);
            result.put("total", pageInfo.getTotal());
            result.put("page", page);
            result.put("size", size);
            result.put("hasMore", pageInfo.isHasNextPage());
            
            return result;
            
        } catch (Exception e) {
            LOGGER.error("获取佣金记录失败, memberId: {}", memberId, e);
            return new HashMap<>();
        }
    }
    
    @Override
    public Map<String, Object> getMyCommissionSummary(Long memberId, String period) {
        // 检查是否为分销商
        if (!isDistributor(memberId)) {
            throw new RuntimeException("您还不是分销商");
        }
        
        try {
            Map<String, Object> summary = new HashMap<>();
            
            // 查询总佣金
            PmsInviteRewardExample totalExample = new PmsInviteRewardExample();
            totalExample.createCriteria().andUserIdEqualTo(memberId);
            List<PmsInviteReward> allRewards = inviteRewardMapper.selectByExample(totalExample);
            
            BigDecimal totalCommission = allRewards.stream()
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.put("totalCommission", totalCommission);
            
            // 查询已发放佣金
            BigDecimal settledCommission = allRewards.stream()
                .filter(reward -> reward.getStatus() == 1)
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.put("settledCommission", settledCommission);
            
            // 查询待发放佣金
            BigDecimal pendingCommission = allRewards.stream()
                .filter(reward -> reward.getStatus() == 0)
                .map(PmsInviteReward::getRewardValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            summary.put("pendingCommission", pendingCommission);
            
            // 查询已提现佣金 - 这里需要从提现表查询
            BigDecimal withdrawnCommission = BigDecimal.ZERO;
            
            // 可提现佣金 = 已发放佣金 - 已提现佣金
            BigDecimal availableCommission = settledCommission.subtract(withdrawnCommission);
            summary.put("withdrawnCommission", withdrawnCommission);
            summary.put("availableCommission", availableCommission);
            
            // 根据时期筛选佣金数据
            Date now = new Date();
            final Date startDate;
            
            if ("today".equals(period)) {
                startDate = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
            } else if ("week".equals(period)) {
                startDate = DateUtils.addDays(now, -7);
            } else if ("month".equals(period)) {
                startDate = DateUtils.truncate(now, Calendar.MONTH);
            } else {
                startDate = null;
            }
            
            if (startDate != null) {
                List<PmsInviteReward> periodRewards = allRewards.stream()
                    .filter(reward -> reward.getCreatedAt() != null && reward.getCreatedAt().after(startDate))
                    .collect(Collectors.toList());
                
                BigDecimal periodCommission = periodRewards.stream()
                    .map(PmsInviteReward::getRewardValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                summary.put("periodCommission", periodCommission);
                summary.put("periodOrders", periodRewards.size());
            } else {
                summary.put("periodCommission", totalCommission);
                summary.put("periodOrders", allRewards.size());
            }
            
            // 查询佣金趋势数据（最近7天）
            List<Map<String, Object>> trendData = new ArrayList<>();
            for (int i = 6; i >= 0; i--) {
                Date date = DateUtils.addDays(now, -i);
                Date dayStart = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
                Date dayEnd = DateUtils.addDays(dayStart, 1);
                
                BigDecimal dayCommission = allRewards.stream()
                    .filter(reward -> reward.getCreatedAt() != null && 
                            reward.getCreatedAt().after(dayStart) && 
                            reward.getCreatedAt().before(dayEnd))
                    .map(PmsInviteReward::getRewardValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                Map<String, Object> trend = new HashMap<>();
                trend.put("date", date);
                trend.put("amount", dayCommission);
                trendData.add(trend);
            }
            summary.put("trendData", trendData);
            
            return summary;
            
        } catch (Exception e) {
            LOGGER.error("获取佣金汇总失败, memberId: {}", memberId, e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public Boolean bindDistributionRelation(String distributionCode, Long customerId) {
        try {
            // 解析分销码获取邀请者ID
            String[] parts = distributionCode.split("_");
            if (parts.length != 2) {
                throw new RuntimeException("无效的分销码格式");
            }
            
            Long inviterId = Long.parseLong(parts[0]);
            Long timestamp = Long.parseLong(parts[1]);
            
            // 检查分销码是否有效
            PmsInviteParamLogExample example = new PmsInviteParamLogExample();
            example.createCriteria()
                    .andInviteParamEqualTo(distributionCode)
                    .andStatusEqualTo((byte) 1)
                    .andExpireTimeGreaterThan(new Date());
            
            List<PmsInviteParamLog> logs = inviteParamLogMapper.selectByExample(example);
            if (logs.isEmpty()) {
                throw new RuntimeException("分销码已过期或无效");
            }
            
            // 检查是否自己邀请自己
            if (inviterId.equals(customerId)) {
                throw new RuntimeException("不能绑定自己的分销码");
            }
            
            // 检查是否已有绑定关系
            PmsInviteRelationExample relationExample = new PmsInviteRelationExample();
            relationExample.createCriteria().andInviteeIdEqualTo(customerId);
            List<PmsInviteRelation> existingRelations = inviteRelationMapper.selectByExample(relationExample);
            
            if (!existingRelations.isEmpty()) {
                throw new RuntimeException("您已绑定过分销关系，无法重复绑定");
            }
            
            // 创建邀请关系
            PmsInviteRelation relation = new PmsInviteRelation();
            relation.setInviterId(inviterId);
            relation.setInviteeId(customerId);
            relation.setInviteParam(distributionCode);
            relation.setInviteTime(new Date(timestamp));
            relation.setRegisterTime(new Date());
            relation.setStatus((byte) 1); // 已注册
            relation.setRewardStatus((byte) 0);
            relation.setRelationType((byte) 2); // 分销关系
            relation.setDistributorLevel((byte) 1); // 一级分销
            relation.setBindSource((byte) 2); // 下单绑定
            relation.setIsPermanent((byte) 1);
            relation.setCreatedAt(new Date());
            relation.setUpdatedAt(new Date());
            
            inviteRelationMapper.insert(relation);
            
            // 更新邀请参数使用次数
            PmsInviteParamLog paramLog = logs.get(0);
            paramLog.setUseCount(paramLog.getUseCount() + 1);
            paramLog.setLastUsedTime(new Date());
            paramLog.setUpdatedAt(new Date());
            inviteParamLogMapper.updateByPrimaryKey(paramLog);
            
            return true;
        } catch (Exception e) {
            LOGGER.error("绑定分销关系失败, distributionCode: {}, customerId: {}", distributionCode, customerId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void processOrderCommission(Long orderId) {
        // 这个方法由CommissionServiceImpl处理
        LOGGER.info("订单佣金处理, orderId: {}", orderId);
    }

    @Override
    public void updateDistributionStats(Long userId) {
        // 更新分销统计数据的实现
        LOGGER.info("更新分销统计数据, userId: {}", userId);
    }
} 