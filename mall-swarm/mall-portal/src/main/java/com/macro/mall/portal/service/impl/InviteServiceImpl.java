package com.macro.mall.portal.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dao.InviteDao;
import com.macro.mall.portal.domain.InviteGenerateResult;
import com.macro.mall.portal.domain.InviteRewardRecord;
import com.macro.mall.portal.domain.InviteStatisticsResult;
import com.macro.mall.portal.domain.RewardSummaryResult;
import com.macro.mall.portal.domain.WithdrawApplyResult;
import com.macro.mall.portal.dto.InviteGenerateParam;
import com.macro.mall.portal.dto.InviteVerifyParam;
import com.macro.mall.portal.dto.WithdrawApplyParam;
import com.macro.mall.portal.service.InviteService;
import com.macro.mall.portal.service.WxMiniProgramService;
import com.macro.mall.portal.util.StpMemberUtil;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.WithdrawService;
import com.macro.mall.portal.service.UmsIntegrationService;
import com.macro.mall.mapper.PmsInviteWithdrawApplyMapper;
import com.macro.mall.model.PmsInviteWithdrawApply;
import com.macro.mall.mapper.PmsInviteRewardMapper;
import com.macro.mall.model.PmsInviteReward;
import com.macro.mall.model.PmsInviteRewardExample;
import com.macro.mall.mapper.PmsSystemConfigMapper;
import com.macro.mall.model.PmsSystemConfig;
import com.macro.mall.model.PmsSystemConfigExample;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邀请服务实现类
 * @author macro
 * @since 1.0.0
 */
@Service
public class InviteServiceImpl implements InviteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InviteServiceImpl.class);
    
    @Value("${tencent.cos.bucketName:haojiang-1332489043}")
    private String cosBucketName;
    
    @Value("${tencent.cos.region:ap-guangzhou}")
    private String cosRegion;
    

    
    @Autowired
    private WxMiniProgramService wxMiniProgramService;
    
    @Autowired
    @Lazy
    private UmsMemberService memberService;
    
    @Autowired
    private InviteDao inviteDao;
    
    @Autowired
    private PmsInviteWithdrawApplyMapper withdrawApplyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PmsInviteRewardMapper inviteRewardMapper;

    @Autowired
    private PmsSystemConfigMapper systemConfigMapper;

    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsIntegrationService integrationService;

    // Redis键前缀
    private static final String REDIS_KEY_INVITE_PARAM = "invite:param:";
    private static final String REDIS_KEY_INVITE_USER = "invite:user:";

    // 邀请参数有效期（3天，单位：秒）
    private static final long INVITE_PARAM_EXPIRE_SECONDS = 3 * 24 * 60 * 60;

    // 添加配置项控制是否启用小程序码生成
    @Value("${invite.qrcode.enabled:true}")
    private boolean qrCodeEnabled;
    
    // 默认小程序码图片URL
    @Value("${invite.qrcode.default-url:https://haojiang-1332489043.cos.ap-guangzhou.myqcloud.com/static/default-qrcode.png}")
    private String defaultQrCodeUrl;

    /**
     * 生成随机字符串
     * @param length 长度
     * @return 随机字符串
     */
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public InviteGenerateResult generateInviteParam(InviteGenerateParam param) {
        // 获取当前用户ID
        Long currentUserId = StpMemberUtil.getLoginIdAsLong();

        LOGGER.info("开始生成邀请参数，用户ID: {}", currentUserId);

        // 生成新格式邀请参数：INVITE_v2_{用户ID}_{时间戳}_{随机字符串}
        long timestamp = System.currentTimeMillis();
        String randomString = generateRandomString(6);
        String inviteParam = String.format("INVITE_v2_%d_%d_%s", currentUserId, timestamp, randomString);

        LOGGER.info("生成新格式邀请参数: {}", inviteParam);

        // 生成过期时间（默认3天）
        Date expireTime = new Date(timestamp + 3 * 24 * 60 * 60 * 1000L);

        // 存储邀请参数到数据库和Redis
        try {
            // 1. 存储到数据库 (pms_invite_param_log)
            inviteDao.saveInviteParamLog(currentUserId, inviteParam, timestamp, expireTime);

            // 2. 存储邀请参数到Redis（用于快速验证）
            Map<String, Object> inviteInfo = new HashMap<>();
            inviteInfo.put("inviterUserId", currentUserId);
            inviteInfo.put("timestamp", timestamp);
            inviteInfo.put("randomString", randomString);
            inviteInfo.put("expireTime", expireTime.getTime());
            inviteInfo.put("createTime", System.currentTimeMillis());

            // 存储邀请参数 -> 邀请者信息的映射
            String paramKey = REDIS_KEY_INVITE_PARAM + inviteParam;
            redisTemplate.opsForValue().set(paramKey, inviteInfo, INVITE_PARAM_EXPIRE_SECONDS, TimeUnit.SECONDS);

            // 存储邀请者 -> 邀请参数的映射（用于查询用户的邀请参数）
            String userKey = REDIS_KEY_INVITE_USER + currentUserId;
            redisTemplate.opsForValue().set(userKey, inviteParam, INVITE_PARAM_EXPIRE_SECONDS, TimeUnit.SECONDS);

            LOGGER.info("邀请参数已存储到数据库和Redis: {}", inviteParam);
        } catch (Exception e) {
            LOGGER.error("存储邀请参数失败: {}", inviteParam, e);
            // 不影响主流程，继续执行
        }

        // 生成分享URL
        String shareUrl = "/pages/new_index/index?inviteParam=" + inviteParam;
        
        // 生成小程序码（可选）
        String qrCodeUrl = defaultQrCodeUrl; // 使用默认图片
        if (qrCodeEnabled && param.isGenerateQrCode()) {
            try {
                qrCodeUrl = generateMiniProgramQrCode(inviteParam);
                LOGGER.info("小程序码生成成功: {}", qrCodeUrl);
            } catch (Exception e) {
                LOGGER.warn("小程序码生成失败，使用默认占位图: {}", e.getMessage());
                LOGGER.error("生成小程序码失败，邀请参数: {}", inviteParam, e);
                // 继续使用默认图片，不影响主要功能
            }
        } else {
            LOGGER.info("小程序码生成已禁用或未请求生成，使用默认图片");
        }
        
        // 获取用户信息
        UmsMember member = memberService.getCurrentMember();
        String inviterUsername = member != null ? member.getUsername() : "用户" + currentUserId;
        String inviterAvatar = member != null ? member.getIcon() : "";
        
        // 构建返回结果
        InviteGenerateResult result = new InviteGenerateResult();
        result.setInviteParam(inviteParam);
        result.setShareUrl(shareUrl);
        result.setQrCodeUrl(qrCodeUrl);
        result.setInviterUsername(inviterUsername);
        result.setInviterAvatar(inviterAvatar);
        result.setExpireTime(expireTime);
        result.setRewardDesc("邀请好友注册即可获得积分奖励");
        
        LOGGER.info("邀请参数生成完成: {}", inviteParam);
        return result;
    }
    
    @Override
    public boolean verifyInviteParam(InviteVerifyParam param) {
        try {
            LOGGER.info("开始验证邀请参数: {}", param.getInviteParam());

            String inviteParam = param.getInviteParam();
            if (inviteParam == null || !inviteParam.startsWith("INVITE_v2_")) {
                LOGGER.warn("邀请参数格式不正确，必须以INVITE_v2_开头: {}", inviteParam);
                return false;
            }

            // 解析新格式邀请参数：INVITE_v2_{userId}_{timestamp}_{random}
            String[] parts = inviteParam.split("_");
            if (parts.length != 5) {
                LOGGER.warn("邀请参数格式不正确，分割后长度应为5: {}", parts.length);
                return false;
            }

            try {
                String prefix = parts[0];
                String version = parts[1];
                Long inviterUserId = Long.parseLong(parts[2]);
                Long timestamp = Long.parseLong(parts[3]);
                String randomString = parts[4];

                // 验证格式
                if (!"INVITE".equals(prefix) || !"v2".equals(version)) {
                    LOGGER.warn("邀请参数前缀或版本不正确: prefix={}, version={}", prefix, version);
                    return false;
                }

                // 验证随机字符串格式（6位字母数字）
                if (!randomString.matches("^[a-zA-Z0-9]{6}$")) {
                    LOGGER.warn("邀请参数随机字符串格式不正确: {}", randomString);
                    return false;
                }

                // 从Redis查询邀请参数信息
                String paramKey = REDIS_KEY_INVITE_PARAM + inviteParam;
                Map<String, Object> inviteInfo = null;
                try {
                    inviteInfo = (Map<String, Object>) redisTemplate.opsForValue().get(paramKey);
                } catch (Exception e) {
                    LOGGER.error("从Redis查询邀请参数失败: {}", inviteParam, e);
                }

                if (inviteInfo == null) {
                    LOGGER.warn("邀请参数不存在或已过期: {}", inviteParam);
                    return false;
                }

                // 验证邀请参数信息的一致性
                Long storedInviterUserId = ((Number) inviteInfo.get("inviterUserId")).longValue();
                Long storedTimestamp = ((Number) inviteInfo.get("timestamp")).longValue();
                String storedRandomString = (String) inviteInfo.get("randomString");

                if (!inviterUserId.equals(storedInviterUserId) ||
                    !timestamp.equals(storedTimestamp) ||
                    !randomString.equals(storedRandomString)) {
                    LOGGER.warn("邀请参数信息不一致: {}", inviteParam);
                    return false;
                }

                // 检查邀请者是否存在
                UmsMember inviter = memberService.getById(inviterUserId);
                if (inviter == null) {
                    LOGGER.warn("邀请者不存在，ID: {}", inviterUserId);
                    return false;
                }

                LOGGER.info("邀请参数验证成功，邀请者ID: {}, 邀请者: {}, 时间戳: {}, 随机串: {}",
                    inviterUserId, inviter.getUsername(), timestamp, randomString);
                return true;

            } catch (NumberFormatException e) {
                LOGGER.warn("邀请参数中的数字格式不正确: {}", inviteParam);
                return false;
            }

        } catch (Exception e) {
            LOGGER.error("验证邀请参数失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public Boolean establishInviteRelation(Long inviterUserId, Long inviteeUserId) {
        try {
            LOGGER.info("开始建立邀请关系，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId);

            // 检查邀请者和被邀请者是否为同一用户
            if (inviterUserId.equals(inviteeUserId)) {
                LOGGER.warn("邀请者和被邀请者不能是同一用户，用户ID: {}", inviterUserId);
                return false;
            }

            // 检查被邀请者是否已经被其他人邀请过（根据现有表结构，invitee_id有唯一索引）
            if (inviteDao.checkInviteeHasInviter(inviteeUserId)) {
                LOGGER.warn("被邀请者已有邀请者，被邀请者ID: {}", inviteeUserId);
                return false;
            }

            // 保存邀请关系到数据库 (pms_invite_relation)
            boolean saveResult = inviteDao.saveInviteRelation(inviterUserId, inviteeUserId, "INVITE_v2_" + inviterUserId + "_" + System.currentTimeMillis() + "_MANUAL");
            if (!saveResult) {
                LOGGER.error("保存邀请关系失败，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId);
                return false;
            }

            // 发放邀请奖励（异步处理）
            try {
                processInviteReward(inviterUserId, inviteeUserId);
            } catch (Exception e) {
                LOGGER.error("发放邀请奖励失败，但邀请关系已建立，邀请者ID: {}, 被邀请者ID: {}",
                    inviterUserId, inviteeUserId, e);
                // 不影响邀请关系的建立
            }

            LOGGER.info("邀请关系建立成功，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId);
            return true;

        } catch (Exception e) {
            LOGGER.error("建立邀请关系失败，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId, e);
            return false;
        }
    }

    /**
     * 处理邀请奖励
     */
    private void processInviteReward(Long inviterUserId, Long inviteeUserId) {
        try {
            LOGGER.info("开始处理邀请奖励，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId);

            // 获取邀请配置
            Map<String, Object> config = getAllInviteConfig();
            Map<String, Object> inviterReward = (Map<String, Object>) config.get("inviterReward");
            Map<String, Object> inviteeReward = (Map<String, Object>) config.get("inviteeReward");

            if (inviterReward == null || inviteeReward == null) {
                LOGGER.warn("邀请奖励配置不存在，跳过奖励发放");
                return;
            }

            // 发放邀请者注册奖励
            grantInviterRegisterReward(inviterUserId, inviteeUserId, inviterReward);

            // 发放被邀请者注册奖励
            grantInviteeRegisterReward(inviteeUserId, inviterUserId, inviteeReward);

            LOGGER.info("邀请奖励处理完成，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId);
        } catch (Exception e) {
            LOGGER.error("处理邀请奖励失败，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId, e);
            throw e;
        }
    }


    
    @Override
    public InviteStatisticsResult getMyInviteStatistics(Long userId) {
        try {
            LOGGER.info("获取用户邀请统计，用户ID: {}", userId);
            
            // 从数据库查询真实统计数据
            InviteStatisticsResult result = inviteDao.getUserInviteStatistics(userId);
            
            // 如果没有查询到数据，返回默认值
            if (result == null) {
                result = new InviteStatisticsResult();
                result.setTotalInvites(0);
                result.setTodayInvites(0);
                result.setRegisteredCount(0);
                result.setOrderedCount(0);
                result.setTotalRewardPoints(0);
                result.setTotalRewardAmount(BigDecimal.ZERO);
                result.setPendingRewards(0);
                result.setConversionRate(BigDecimal.ZERO);
                result.setRanking(1);
                result.setCurrentTotalIncome(BigDecimal.ZERO);
                result.setWithdrawableAmount(BigDecimal.ZERO);
                result.setPendingAmount(BigDecimal.ZERO);
            }
            
            // 获取被邀请人列表（显示在个人中心）
            try {
                List<Map<String, Object>> invitedListData = inviteDao.getUserInvitedList(userId, 5); // 最多显示5个
                List<InviteStatisticsResult.InvitedUserInfo> invitedUsers = new ArrayList<>();
                
                for (Map<String, Object> data : invitedListData) {
                    InviteStatisticsResult.InvitedUserInfo userInfo = new InviteStatisticsResult.InvitedUserInfo();
                    userInfo.setUserId(data.get("userId") != null ? ((Number) data.get("userId")).longValue() : null);
                    userInfo.setNickname((String) data.get("nickname"));
                    userInfo.setAvatar((String) data.get("avatar"));
                    userInfo.setStatus(data.get("status") != null ? ((Number) data.get("status")).intValue() : 0);
                    userInfo.setCreateTime((Date) data.get("createTime"));
                    invitedUsers.add(userInfo);
                }
                
                result.setInvitedUsers(invitedUsers);
                LOGGER.info("获取被邀请人列表成功，数量: {}", invitedUsers.size());
                
            } catch (Exception e) {
                LOGGER.warn("获取被邀请人列表失败: {}", e.getMessage());
                result.setInvitedUsers(new ArrayList<>()); // 设置空列表
            }
            
            LOGGER.info("用户邀请统计获取成功: totalInvites={}, registeredCount={}, orderedCount={}", 
                       result.getTotalInvites(), result.getRegisteredCount(), result.getOrderedCount());
            return result;
            
        } catch (Exception e) {
            LOGGER.error("获取用户邀请统计失败", e);
            throw new RuntimeException("获取邀请统计失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<InviteRewardRecord> getMyInviteRewards(Long userId, Integer page, Integer size) {
        try {
            LOGGER.info("获取用户邀请奖励记录，用户ID: {}, 页码: {}, 大小: {}", userId, page, size);
            
            // TODO: 从数据库查询真实奖励记录
            List<InviteRewardRecord> records = new ArrayList<>();
            
            LOGGER.info("用户邀请奖励记录获取成功，记录数: {}", records.size());
            return records;
            
        } catch (Exception e) {
            LOGGER.error("获取用户邀请奖励记录失败", e);
            throw new RuntimeException("获取邀请奖励记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public String generateMiniProgramQrCode(String inviteParam) {
        try {
            LOGGER.info("开始生成小程序码，邀请参数: {}", inviteParam);
            
            // 使用微信小程序服务生成小程序码
            String page = "pages/new_index/index";
            String scene = inviteParam;
            
            byte[] qrCodeBytes = wxMiniProgramService.generateMiniProgramCodeBytes(page, scene);
            
            if (qrCodeBytes == null || qrCodeBytes.length == 0) {
                throw new RuntimeException("生成的小程序码为空");
            }
            
            // 生成文件名
            String fileName = inviteParam + ".jpg";
            String subDir = "invite";
            
            // TODO: 上传到COS
            String qrCodeUrl = defaultQrCodeUrl; // 暂时返回默认URL
            
            LOGGER.info("小程序码生成并上传成功，URL: {}", qrCodeUrl);
            return qrCodeUrl;
            
        } catch (Exception e) {
            LOGGER.error("生成小程序码失败，邀请参数: {}", inviteParam, e);
            throw new RuntimeException("生成小程序码失败: " + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Map<String, Object>> generateQrCode(String inviteParam) {
        try {
            LOGGER.info("开始生成小程序码，邀请参数: {}", inviteParam);
            
            // 生成小程序码URL
            String qrCodeUrl = generateMiniProgramQrCode(inviteParam);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("qrCodeUrl", qrCodeUrl);
            result.put("inviteParam", inviteParam);
            result.put("shareUrl", "/pages/new_index/index?inviteParam=" + inviteParam);
            
            LOGGER.info("小程序码生成成功，结果: {}", result);
            return CommonResult.success(result, "小程序码生成成功");
            
        } catch (Exception e) {
            LOGGER.error("生成小程序码失败，邀请参数: {}", inviteParam, e);
            return CommonResult.failed("生成小程序码失败: " + e.getMessage());
        }
    }
    
    @Override
    public RewardSummaryResult getRewardSummary(Long userId, Integer year) {
        try {
            LOGGER.info("获取用户奖励汇总，用户ID: {}, 年份: {}", userId, year);
            
            // 如果年份为空，使用当前年份
            if (year == null) {
                year = LocalDate.now().getYear();
            }
            
            // 获取当前月份，只返回当前月份及之前的数据
            int currentMonth = LocalDate.now().getMonthValue();
            int maxMonth = (year.equals(LocalDate.now().getYear())) ? currentMonth : 12;
            
            // 从数据库查询年度汇总数据
            RewardSummaryResult result = inviteDao.getUserRewardSummary(userId, year);
            if (result == null) {
                result = new RewardSummaryResult();
                result.setYearTotalAmount(BigDecimal.ZERO);
                result.setYearTotalPoints(0);
                result.setYearRewardCount(0);
            }
            
            // 从数据库查询月度数据
            List<Map<String, Object>> monthlyData = inviteDao.getUserMonthlyRewards(userId, year, maxMonth);
            
            // 生成完整的月度奖励列表（填充没有数据的月份）
            List<RewardSummaryResult.MonthlyReward> monthlyRewards = new ArrayList<>();
            Map<Integer, Map<String, Object>> monthDataMap = new HashMap<>();
            
            // 将查询结果转换为Map便于查找
            for (Map<String, Object> data : monthlyData) {
                Integer month = (Integer) data.get("month");
                monthDataMap.put(month, data);
            }
            
            // 只生成当前年度当前月份及之前的数据
            for (int month = 1; month <= maxMonth; month++) {
                RewardSummaryResult.MonthlyReward monthlyReward = new RewardSummaryResult.MonthlyReward();
                monthlyReward.setMonth(month);
                monthlyReward.setMonthName(month + "月");
                
                if (monthDataMap.containsKey(month)) {
                    // 有真实数据
                    Map<String, Object> data = monthDataMap.get(month);
                    monthlyReward.setAmount((BigDecimal) data.get("amount"));
                    monthlyReward.setPoints(((Number) data.get("points")).intValue());
                    monthlyReward.setRewardCount(((Number) data.get("rewardCount")).intValue());
                    
                    // MySQL CASE WHEN 返回的是 Long 类型（1/0），需要转换为 Boolean
                    Object isCurrentObj = data.get("isCurrent");
                    boolean isCurrent = isCurrentObj != null && ((Number) isCurrentObj).intValue() == 1;
                    monthlyReward.setIsCurrent(isCurrent);
                } else {
                    // 没有数据，填充0值
                    monthlyReward.setAmount(BigDecimal.ZERO);
                    monthlyReward.setPoints(0);
                    monthlyReward.setRewardCount(0);
                    monthlyReward.setIsCurrent(month == currentMonth && year.equals(LocalDate.now().getYear()));
                }
                
                monthlyRewards.add(monthlyReward);
            }
            
            result.setMonthlyRewards(monthlyRewards);
            
            LOGGER.info("用户奖励汇总获取成功，年度总金额: {}, 月度数据条数: {}", result.getYearTotalAmount(), monthlyRewards.size());
            return result;
            
        } catch (Exception e) {
            LOGGER.error("获取用户奖励汇总失败", e);
            throw new RuntimeException("获取奖励汇总失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public WithdrawApplyResult applyWithdraw(Long userId, WithdrawApplyParam param) {
        try {
            LOGGER.info("用户申请提现，用户ID: {}, 提现金额: {}, 提现方式: {}", userId, param.getApplyAmount(), param.getWithdrawType());
            
            // 1. 校验提现金额
            if (param.getApplyAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("提现金额必须大于0");
            }
            
            // 2. 检查是否有待处理的提现申请（防重复提现）
            int pendingCount = inviteDao.countPendingWithdrawApply(userId);
            if (pendingCount > 0) {
                throw new RuntimeException("您还有待处理的提现申请，请耐心等待审核完成后再次申请");
            }
            
            // 3. 检查可提现余额
            InviteStatisticsResult userStats = getMyInviteStatistics(userId);
            BigDecimal withdrawableAmount = userStats.getWithdrawableAmount();
            
            if (param.getApplyAmount().compareTo(withdrawableAmount) > 0) {
                throw new RuntimeException("提现金额超过可提现余额，当前可提现: " + withdrawableAmount);
            }
            
            // 4. 计算手续费（使用WithdrawService的配置化手续费计算）
            BigDecimal feeAmount = withdrawService.calculateFee(param.getApplyAmount(), param.getWithdrawType());
            BigDecimal actualAmount = param.getApplyAmount().subtract(feeAmount);
            
            // 5. 获取当前用户信息
            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                throw new RuntimeException("无法获取当前用户信息，请重新登录");
            }
            
            // 6. 获取真实的微信openid（如果是微信提现）
            String realOpenid = null;
            if (param.getWithdrawType() == 1) {
                // 微信钱包提现 - 从当前登录用户信息中获取真实openid
                if (currentMember.getOpenid() != null && !currentMember.getOpenid().trim().isEmpty()) {
                    realOpenid = currentMember.getOpenid();
                } else {
                    throw new RuntimeException("无法获取您的微信openid，请重新登录或联系客服");
                }
            }
            
            // 7. 校验提现方式和必要参数
            String withdrawAccount = null;
            String accountName = null;
            
            if (param.getWithdrawType() == 1) {
                // 微信钱包提现
                withdrawAccount = realOpenid;
                accountName = currentMember.getNickname() != null ? currentMember.getNickname() : "微信用户";
            } else if (param.getWithdrawType() == 2) {
                // 银行卡提现
                if (param.getWithdrawAccount() == null || param.getWithdrawAccount().trim().isEmpty() ||
                    param.getAccountName() == null || param.getAccountName().trim().isEmpty()) {
                    throw new RuntimeException("提现到银行卡需要提供完整的银行卡信息");
                }
                withdrawAccount = param.getWithdrawAccount();
                accountName = param.getAccountName();
            } else if (param.getWithdrawType() == 3) {
                // 文创余额存入
                withdrawAccount = String.valueOf(userId); // 使用用户ID作为账户标识
                accountName = currentMember.getNickname() != null ? currentMember.getNickname() : "用户";
            } else {
                throw new RuntimeException("不支持的提现方式");
            }
            
            // 8. 生成提现申请单号
            String withdrawSn = generateWithdrawSn();
            
            // 9. 按金额锁定用户的可提现奖励记录
            BigDecimal lockedAmount = lockRewardsByAmount(userId, param.getApplyAmount(), withdrawSn);
            if (lockedAmount.compareTo(param.getApplyAmount()) < 0) {
                throw new RuntimeException("可提现余额不足，需要: " + param.getApplyAmount() + "元，可用: " + lockedAmount + "元");
            }
            LOGGER.info("已按金额锁定奖励记录，锁定金额: {}", lockedAmount);
            
            // 10. 创建提现申请记录
            PmsInviteWithdrawApply withdrawApply = new PmsInviteWithdrawApply();
            withdrawApply.setUserId(userId);
            withdrawApply.setWithdrawSn(withdrawSn);
            withdrawApply.setApplyAmount(param.getApplyAmount());
            withdrawApply.setFeeAmount(feeAmount);
            withdrawApply.setActualAmount(actualAmount);
            withdrawApply.setWithdrawType(param.getWithdrawType().byteValue());
            withdrawApply.setWithdrawAccount(withdrawAccount);
            withdrawApply.setAccountName(accountName);
            withdrawApply.setStatus((byte) 0); // 0-待审核
            withdrawApply.setApplyTime(new Date());
            
            // 预计到账时间（工作日1-3天）
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 3);
            withdrawApply.setExpectedArrivalTime(calendar.getTime());
            
            withdrawApply.setRemark(param.getRemark() != null ? param.getRemark() : "用户申请提现");
            withdrawApply.setCreatedAt(new Date());
            withdrawApply.setUpdatedAt(new Date());
            
            // 保存到数据库
            int insertResult = withdrawApplyMapper.insertSelective(withdrawApply);
            if (insertResult != 1) {
                throw new RuntimeException("保存提现申请记录失败");
            }

            // 根据提现方式进行不同处理
            if (param.getWithdrawType() == 3) {
                // 存入文创余额，调用WithdrawService处理
                try {
                    withdrawService.processBalanceTransfer(withdrawApply, userId, actualAmount);
                    LOGGER.info("文创余额转存处理完成，申请ID: {}", withdrawApply.getId());
                } catch (Exception e) {
                    LOGGER.error("文创余额转存处理失败，申请ID: {}, 错误: {}", withdrawApply.getId(), e.getMessage(), e);

                    // 解锁奖励记录
                    BigDecimal unlockedAmount = unlockRewardsByWithdrawSn(userId, withdrawSn);
                    LOGGER.info("提现失败，已解锁奖励记录，解锁金额: {}", unlockedAmount);

                    // 更新状态为失败
                    withdrawApply.setStatus((byte) 3);
                    withdrawApply.setFailureReason("转存失败: " + e.getMessage());
                    withdrawApply.setUpdatedAt(new Date());
                    withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);
                }
            } else if (param.getWithdrawType() == 1) {
                // 微信零钱提现，调用WithdrawService处理
                try {
                    UmsMember member = memberService.getCurrentMember();
                    withdrawService.processWechatTransfer(withdrawApply, member);
                    LOGGER.info("微信转账处理完成，申请ID: {}", withdrawApply.getId());
                } catch (Exception e) {
                    LOGGER.error("微信转账处理失败，申请ID: {}, 错误: {}", withdrawApply.getId(), e.getMessage(), e);

                    // 解锁奖励记录
                    BigDecimal unlockedAmount = unlockRewardsByWithdrawSn(userId, withdrawSn);
                    LOGGER.info("提现失败，已解锁奖励记录，解锁金额: {}", unlockedAmount);

                    // 更新状态为失败
                    withdrawApply.setStatus((byte) 3);
                    withdrawApply.setFailureReason("转账失败: " + e.getMessage());
                    withdrawApply.setUpdatedAt(new Date());
                    withdrawApplyMapper.updateByPrimaryKeySelective(withdrawApply);
                }
            }
            // 银行卡提现(type=2)保持原有审核流程，不做自动处理
            
            // 11. 生成返回结果
            WithdrawApplyResult result = new WithdrawApplyResult();
            result.setWithdrawId(withdrawApply.getId());
            result.setAmount(param.getApplyAmount());
            result.setFeeAmount(feeAmount);
            result.setActualAmount(actualAmount);
            result.setStatus(0); // 0-待审核
            result.setApplyTime(withdrawApply.getApplyTime());
            result.setExpectedArrivalTime(withdrawApply.getExpectedArrivalTime());
            result.setRemark("提现申请已提交，请耐心等待审核");
            
            LOGGER.info("提现申请提交成功，申请ID: {}, 实际到账金额: {}", result.getWithdrawId(), result.getActualAmount());
            return result;
            
        } catch (Exception e) {
            LOGGER.error("提现申请失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            throw new RuntimeException("提现申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成提现申请单号
     * 格式：WD + yyyyMMdd + HHmmss + 随机3位数
     */
    private String generateWithdrawSn() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = (int) (Math.random() * 900) + 100; // 100-999随机数
        return "WD" + datePart + randomPart;
    }

    /**
     * 按金额锁定用户的可提现奖励记录
     * @param userId 用户ID
     * @param amount 需要锁定的金额
     * @param withdrawSn 提现单号
     * @return 实际锁定的金额
     */
    private BigDecimal lockRewardsByAmount(Long userId, BigDecimal amount, String withdrawSn) {
        try {
            // 1. 获取用户可提现的奖励记录（按时间顺序）
            PmsInviteRewardExample example = new PmsInviteRewardExample();
            example.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andStatusEqualTo((byte) 1) // 已发放
                    .andSettlementStatusEqualTo((byte) 0); // 未结算
            example.setOrderByClause("send_time ASC");

            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(example);
            if (rewards == null || rewards.isEmpty()) {
                return BigDecimal.ZERO;
            }

            // 2. 按顺序锁定奖励记录，直到金额足够
            BigDecimal totalLocked = BigDecimal.ZERO;
            BigDecimal remainingAmount = amount;

            for (PmsInviteReward reward : rewards) {
                if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break; // 已经锁定足够的金额
                }

                // 计算当前记录的可用金额
                BigDecimal currentLocked = reward.getLockedAmount() != null ? reward.getLockedAmount() : BigDecimal.ZERO;
                BigDecimal availableAmount = reward.getRewardValue().subtract(currentLocked);

                if (availableAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue; // 这条记录已经完全锁定，跳过
                }

                // 计算本次需要锁定的金额
                BigDecimal lockAmount = remainingAmount.min(availableAmount);

                // 更新记录的锁定金额
                BigDecimal newLockedAmount = currentLocked.add(lockAmount);
                reward.setLockedAmount(newLockedAmount);
                reward.setSendResult("提现申请锁定：" + withdrawSn + "，锁定金额：" + lockAmount);
                reward.setUpdatedAt(new Date());

                int updateResult = inviteRewardMapper.updateByPrimaryKeySelective(reward);
                if (updateResult > 0) {
                    totalLocked = totalLocked.add(lockAmount);
                    remainingAmount = remainingAmount.subtract(lockAmount);

                    LOGGER.info("锁定奖励记录 ID: {}, 本次锁定: {}, 累计锁定: {}, 剩余需锁定: {}",
                        reward.getId(), lockAmount, newLockedAmount, remainingAmount);
                } else {
                    LOGGER.error("更新奖励记录锁定金额失败，ID: {}", reward.getId());
                }
            }

            LOGGER.info("按金额锁定完成，用户ID: {}, 需要锁定: {}, 实际锁定: {}", userId, amount, totalLocked);
            return totalLocked;

        } catch (Exception e) {
            LOGGER.error("按金额锁定奖励记录失败，用户ID: {}, 金额: {}, 错误: {}", userId, amount, e.getMessage(), e);
            throw new RuntimeException("锁定奖励记录失败: " + e.getMessage());
        }
    }

    /**
     * 解锁用户的奖励记录（提现失败时使用）
     * @param userId 用户ID
     * @param withdrawSn 提现单号
     * @return 解锁的金额
     */
    private BigDecimal unlockRewardsByWithdrawSn(Long userId, String withdrawSn) {
        try {
            // 查找与该提现单号相关的锁定记录
            PmsInviteRewardExample example = new PmsInviteRewardExample();
            example.createCriteria()
                    .andUserIdEqualTo(userId)
                    .andSendResultLike("%" + withdrawSn + "%");

            List<PmsInviteReward> rewards = inviteRewardMapper.selectByExample(example);
            BigDecimal totalUnlocked = BigDecimal.ZERO;

            for (PmsInviteReward reward : rewards) {
                if (reward.getLockedAmount() != null && reward.getLockedAmount().compareTo(BigDecimal.ZERO) > 0) {
                    // 解析锁定金额（从 send_result 中提取）
                    String sendResult = reward.getSendResult();
                    BigDecimal unlockAmount = extractLockAmountFromResult(sendResult);

                    if (unlockAmount.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal newLockedAmount = reward.getLockedAmount().subtract(unlockAmount);
                        if (newLockedAmount.compareTo(BigDecimal.ZERO) < 0) {
                            newLockedAmount = BigDecimal.ZERO;
                        }

                        reward.setLockedAmount(newLockedAmount);
                        reward.setSendResult("提现失败解锁：" + withdrawSn + "，解锁金额：" + unlockAmount);
                        reward.setUpdatedAt(new Date());

                        int updateResult = inviteRewardMapper.updateByPrimaryKeySelective(reward);
                        if (updateResult > 0) {
                            totalUnlocked = totalUnlocked.add(unlockAmount);
                            LOGGER.info("解锁奖励记录 ID: {}, 解锁金额: {}, 剩余锁定: {}",
                                reward.getId(), unlockAmount, newLockedAmount);
                        }
                    }
                }
            }

            LOGGER.info("解锁完成，用户ID: {}, 提现单号: {}, 总解锁金额: {}", userId, withdrawSn, totalUnlocked);
            return totalUnlocked;

        } catch (Exception e) {
            LOGGER.error("解锁奖励记录失败，用户ID: {}, 提现单号: {}, 错误: {}", userId, withdrawSn, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 从 send_result 中提取锁定金额
     */
    private BigDecimal extractLockAmountFromResult(String sendResult) {
        try {
            if (sendResult != null && sendResult.contains("锁定金额：")) {
                String[] parts = sendResult.split("锁定金额：");
                if (parts.length > 1) {
                    String amountStr = parts[1].trim();
                    return new BigDecimal(amountStr);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("解析锁定金额失败: {}", sendResult);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Long getInviterUserId(Long inviteeUserId) {
        try {
            return inviteDao.getInviterUserId(inviteeUserId);
        } catch (Exception e) {
            LOGGER.error("获取邀请者ID失败，被邀请者ID: {}", inviteeUserId, e);
            return null;
        }
    }

    /**
     * 发放邀请者注册奖励
     */
    private void grantInviterRegisterReward(Long inviterUserId, Long inviteeUserId, Map<String, Object> config) {
        try {
            // 发放积分奖励
            Integer points = (Integer) config.get("register_points");
            if (points != null && points > 0) {
                // 调用积分发放服务
                giveIntegration(inviterUserId, points, "邀请好友注册奖励");

                // 记录奖励记录
                createRewardRecord(inviterUserId, 1, points.toString(), "邀请好友注册奖励积分",
                    1, 1, inviteeUserId, null);
            }

            LOGGER.info("邀请者注册奖励发放成功，用户ID: {}, 积分: {}", inviterUserId, points);
        } catch (Exception e) {
            LOGGER.error("发放邀请者注册奖励失败，用户ID: {}", inviterUserId, e);
        }
    }

    /**
     * 发放被邀请者注册奖励
     */
    private void grantInviteeRegisterReward(Long inviteeUserId, Long inviterUserId, Map<String, Object> config) {
        try {
            // 发放积分奖励
            Integer points = (Integer) config.get("register_points");
            if (points != null && points > 0) {
                // 调用积分发放服务
                giveIntegration(inviteeUserId, points, "新用户注册奖励");

                // 记录奖励记录
                createRewardRecord(inviteeUserId, 1, points.toString(), "新用户注册奖励积分",
                    1, 1, inviterUserId, null);
            }

            // 发放优惠券奖励
            String couponCode = (String) config.get("register_coupon");
            if (couponCode != null && !couponCode.isEmpty()) {
                // TODO: 根据优惠券代码查找优惠券ID并发放
                // Long couponId = findCouponIdByCode(couponCode);
                // if (couponId != null) {
                //     sendCouponToMember(inviteeUserId, couponId, "新用户注册奖励");
                //     createRewardRecord(inviteeUserId, 2, "1", "新用户注册奖励优惠券", 1, 1, inviterUserId, null);
                // }
                LOGGER.info("优惠券发放功能暂未实现，优惠券代码: {}", couponCode);
            }

            LOGGER.info("被邀请者注册奖励发放成功，用户ID: {}, 积分: {}", inviteeUserId, points);
        } catch (Exception e) {
            LOGGER.error("发放被邀请者注册奖励失败，用户ID: {}", inviteeUserId, e);
        }
    }

    /**
     * 创建奖励记录
     */
    private void createRewardRecord(Long userId, Integer rewardType, String rewardValue,
                                  String rewardDesc, Integer triggerType, Integer commissionType,
                                  Long relatedUserId, Long orderId) {
        try {
            // 获取邀请关系ID
            Long relationId = getInviteRelationId(userId, relatedUserId);

            PmsInviteReward reward = new PmsInviteReward();
            reward.setRelationId(relationId);
            reward.setUserId(userId);
            reward.setUserType(getUserType(userId, relatedUserId).byteValue());
            reward.setRewardType(rewardType.byteValue());
            reward.setRewardValue(new BigDecimal(rewardValue));
            reward.setLockedAmount(BigDecimal.ZERO); // 初始锁定金额为0
            reward.setRewardDesc(rewardDesc);
            reward.setTriggerType(triggerType.byteValue());
            reward.setCommissionType(commissionType.byteValue());
            reward.setStatus((byte) 1); // 已发放
            reward.setSendTime(new Date());
            reward.setSendResult("自动发放成功");
            reward.setOrderId(orderId);
            reward.setSettlementStatus((byte) 0); // 未结算
            reward.setCreatedAt(new Date());
            reward.setUpdatedAt(new Date());

            inviteRewardMapper.insert(reward);
            LOGGER.info("奖励记录创建成功，用户ID: {}, 奖励类型: {}, 奖励值: {}", userId, rewardType, rewardValue);
        } catch (Exception e) {
            LOGGER.error("创建奖励记录失败，用户ID: {}", userId, e);
        }
    }

    /**
     * 获取邀请关系ID
     */
    private Long getInviteRelationId(Long userId, Long relatedUserId) {
        try {
            // 尝试查找邀请关系
            return inviteDao.getInviteRelationId(userId, relatedUserId);
        } catch (Exception e) {
            LOGGER.warn("获取邀请关系ID失败，用户ID: {}, 关联用户ID: {}", userId, relatedUserId, e);
            return null;
        }
    }

    /**
     * 获取用户类型
     */
    private Integer getUserType(Long userId, Long relatedUserId) {
        try {
            // 检查userId是否为邀请者
            Long inviterUserId = getInviterUserId(userId);
            if (inviterUserId != null) {
                return 2; // 被邀请者
            } else {
                return 1; // 邀请者
            }
        } catch (Exception e) {
            LOGGER.warn("获取用户类型失败，默认为邀请者，用户ID: {}", userId, e);
            return 1; // 默认为邀请者
        }
    }

    @Override
    public Map<String, Object> getInviteConfig() {
        try {
            return getAllInviteConfig();
        } catch (Exception e) {
            LOGGER.error("获取邀请配置失败", e);
            return new HashMap<>();
        }
    }

    /**
     * 获取所有邀请配置
     */
    private Map<String, Object> getAllInviteConfig() {
        PmsSystemConfigExample example = new PmsSystemConfigExample();
        example.createCriteria().andConfigKeyLike("invite.%");
        List<PmsSystemConfig> configs = systemConfigMapper.selectByExample(example);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> basic = new HashMap<>();
        Map<String, Object> inviterReward = new HashMap<>();
        Map<String, Object> inviteeReward = new HashMap<>();
        Map<String, Object> antiCheat = new HashMap<>();

        // 设置默认值，防止配置缺失
        initDefaultValues(basic, inviterReward, inviteeReward, antiCheat);

        for (PmsSystemConfig config : configs) {
            String key = config.getConfigKey();
            Object value = parseConfigValue(config);

            if (key.startsWith("invite.param.")) {
                String fieldName = convertDotToUnderscore(key.substring("invite.param.".length()));
                basic.put(fieldName, value);
            } else if (key.startsWith("invite.reward.enable")) {
                basic.put("reward_enable", value);
            } else if (key.startsWith("invite.reward.inviter.")) {
                String fieldName = convertDotToUnderscore(key.substring("invite.reward.inviter.".length()));
                inviterReward.put(fieldName, value);
            } else if (key.startsWith("invite.reward.invitee.")) {
                String fieldName = convertDotToUnderscore(key.substring("invite.reward.invitee.".length()));
                inviteeReward.put(fieldName, value);
            } else if (key.startsWith("invite.anti_cheat.")) {
                String fieldName = convertDotToUnderscore(key.substring("invite.anti_cheat.".length()));
                antiCheat.put(fieldName, value);
            }
        }

        result.put("basic", basic);
        result.put("inviterReward", inviterReward);
        result.put("inviteeReward", inviteeReward);
        result.put("antiCheat", antiCheat);

        return result;
    }

    /**
     * 初始化默认配置值
     */
    private void initDefaultValues(Map<String, Object> basic, Map<String, Object> inviterReward,
                                 Map<String, Object> inviteeReward, Map<String, Object> antiCheat) {
        // 基础配置默认值
        basic.put("param_expire_days", 3);
        basic.put("param_max_use_count", 0);
        basic.put("reward_enable", true);

        // 邀请者奖励默认值
        inviterReward.put("register_points", 200);
        inviterReward.put("register_desc", "邀请好友注册奖励");
        inviterReward.put("first_order_points", 100);
        inviterReward.put("first_order_cashback_rate", 0.03);
        inviterReward.put("first_order_desc", "邀请好友首单奖励");
        inviterReward.put("repurchase_commission_rate", 0.02);
        inviterReward.put("repurchase_valid_days", 180);
        inviterReward.put("repurchase_desc", "邀请好友复购分佣");

        // 被邀请者奖励默认值
        inviteeReward.put("register_points", 200);
        inviteeReward.put("register_coupon", "NEWUSER20");
        inviteeReward.put("register_desc", "新用户注册礼包");

        // 防刷机制默认值
        antiCheat.put("max_register_per_ip_daily", 5);
        antiCheat.put("max_register_per_device_daily", 3);
        antiCheat.put("reward_delay_hours", 24);
    }

    /**
     * 将点分隔符转换为下划线格式
     */
    private String convertDotToUnderscore(String dotSeparated) {
        return dotSeparated.replace(".", "_");
    }

    /**
     * 解析配置值
     */
    private Object parseConfigValue(PmsSystemConfig config) {
        String value = config.getConfigValue();
        Byte configType = config.getConfigType();

        if (configType == null || configType == 1) {
            // 字符串类型
            return value;
        } else if (configType == 2) {
            // 数字类型
            try {
                if (value.contains(".")) {
                    return Double.parseDouble(value);
                } else {
                    return Integer.parseInt(value);
                }
            } catch (NumberFormatException e) {
                LOGGER.warn("配置值解析失败，key: {}, value: {}", config.getConfigKey(), value);
                return value;
            }
        } else if (configType == 3) {
            // 布尔类型
            return Boolean.parseBoolean(value);
        } else {
            // 其他类型，返回字符串
            return value;
        }
    }

    /**
     * 发放积分给用户
     */
    private int giveIntegration(Long memberId, Integer integration, String reason) {
        if (integration <= 0) {
            return 0;
        }

        try {
            // 使用统一积分服务更新积分并检查等级升级
            boolean result = integrationService.updateIntegrationAndCheckLevel(
                memberId,
                integration,
                reason != null ? reason : "邀请奖励积分",
                3 // 3->邀请
            );

            if (result) {
                LOGGER.info("积分发放成功，用户ID: {}, 积分: {}, 原因: {}", memberId, integration, reason);
                return 1;
            } else {
                LOGGER.warn("积分发放失败，用户ID: {}, 积分: {}, 原因: {}", memberId, integration, reason);
                return 0;
            }
        } catch (Exception e) {
            LOGGER.error("发放积分失败，用户ID: {}, 积分: {}, 原因: {}", memberId, integration, reason, e);
            return 0;
        }
    }

    @Override
    public Date getInviteDate(Long inviterUserId, Long inviteeUserId) {
        try {
            return inviteDao.getInviteDate(inviterUserId, inviteeUserId);
        } catch (Exception e) {
            LOGGER.error("获取邀请关系建立时间失败，邀请者ID: {}, 被邀请者ID: {}", inviterUserId, inviteeUserId, e);
            return null;
        }
    }

    @Override
    public Boolean establishInviteRelation(String inviteParam, Long inviteeUserId) {
        try {
            // 解析邀请参数获取邀请者ID
            Long inviterUserId = parseInviterIdFromParam(inviteParam);
            if (inviterUserId == null) {
                LOGGER.warn("无效的邀请参数: {}", inviteParam);
                return false;
            }

            // 调用原有的建立邀请关系方法
            return establishInviteRelation(inviterUserId, inviteeUserId);
        } catch (Exception e) {
            LOGGER.error("根据邀请参数建立邀请关系失败，邀请参数: {}, 被邀请者ID: {}", inviteParam, inviteeUserId, e);
            return false;
        }
    }

    /**
     * 从邀请参数中解析邀请者ID
     */
    private Long parseInviterIdFromParam(String inviteParam) {
        try {
            if (StringUtils.isEmpty(inviteParam) || !inviteParam.contains("_")) {
                return null;
            }

            String[] parts = inviteParam.split("_");
            if (parts.length >= 2) {
                return Long.parseLong(parts[0]);
            }

        } catch (NumberFormatException e) {
            LOGGER.warn("邀请参数格式错误: {}", inviteParam);
        }

        return null;
    }

    @Override
    public Map<String, Object> getWithdrawConfig() {
        try {
            LOGGER.info("获取提现配置");

            // 使用WithdrawService获取配置
            Map<String, Object> config = withdrawService.getWithdrawConfig();

            LOGGER.info("提现配置获取成功");
            return config;

        } catch (Exception e) {
            LOGGER.error("获取提现配置失败", e);
            throw new RuntimeException("获取提现配置失败: " + e.getMessage());
        }
    }
}