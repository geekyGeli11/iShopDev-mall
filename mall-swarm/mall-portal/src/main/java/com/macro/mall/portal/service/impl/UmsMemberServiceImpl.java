package com.macro.mall.portal.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.macro.mall.common.constant.AuthConstant;
import com.macro.mall.common.dto.UserDto;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.portal.service.UmsMemberCacheService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.InviteService;
import com.macro.mall.portal.domain.MemberInfoResult;
import com.macro.mall.portal.service.DistributionService;
import com.macro.mall.portal.service.UmsIntegrationService;
import com.macro.mall.portal.util.StpMemberUtil;
import com.macro.mall.portal.controller.UmsMemberController;
import com.macro.mall.common.util.MemberCodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员管理Service实现类
 * Created by macro on 2018/8/3.
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private UmsMemberCacheService memberCacheService;

    @Autowired
    private InviteService inviteService;
    @Autowired
    private DistributionService distributionService;
    @Autowired
    private UmsIntegrationService integrationService;
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${redis.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;

    @Override
    public UmsMember getByUsername(String username) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            return memberList.get(0);
        }
        return null;
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public void register(String username, String password, String telephone, String authCode) {
        //验证验证码
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        //查询是否已有该用户
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        example.or(example.createCriteria().andPhoneEqualTo(telephone));
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(umsMembers)) {
            Asserts.fail("该用户已经存在");
        }
        //没有该用户进行添加操作
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setPassword(BCrypt.hashpw(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        umsMember.setSourceType(1); // 设置用户来源类型为1
        
        // 生成唯一的会员码
        String memberCode = generateUniqueMemberCode();
        umsMember.setMemberCode(memberCode);
        
        //获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            umsMember.setMemberLevelId(memberLevelList.get(0).getId());
        }
        memberMapper.insert(umsMember);
        umsMember.setPassword(null);
    }

    @Override
    public String generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        memberCacheService.setAuthCode(telephone,sb.toString());
        return sb.toString();
    }

    @Override
    public void updatePassword(String telephone, String password, String authCode) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(memberList)){
            Asserts.fail("该账号不存在");
        }
        //验证验证码
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        UmsMember umsMember = memberList.get(0);
        umsMember.setPassword(BCrypt.hashpw(password));
        memberMapper.updateByPrimaryKeySelective(umsMember);
        memberCacheService.delMember(umsMember.getId());
    }

    @Override
    public UmsMember getCurrentMember() {
        UserDto userDto = (UserDto) StpMemberUtil.getSession().get(AuthConstant.STP_MEMBER_INFO);
        // 直接从数据库获取最新数据，确保积分、余额等信息的实时性
        UmsMember member = getById(userDto.getId());
        // 更新缓存为最新数据
        if(member != null) {
            memberCacheService.setMember(member);
        }
        return member;
    }

    @Override
    public MemberInfoResult getCurrentMemberInfo() {
        UmsMember member = getCurrentMember();
        if (member == null) {
            return null;
        }

        // 转换为增强的用户信息
        MemberInfoResult memberInfo = MemberInfoResult.fromUmsMember(member);

        try {
            // 计算剩余积分
            Integer remainingPoints = integrationService.calculateRemainingPointsToNextLevel(member.getId());
            memberInfo.setRemainingPointsToNextLevel(remainingPoints);

            // 设置是否已达到最高等级
            memberInfo.setIsMaxLevel(remainingPoints != null && remainingPoints == 0);

            // 获取当前等级信息
            if (member.getMemberLevelId() != null) {
                UmsMemberLevel currentLevel = memberLevelMapper.selectByPrimaryKey(member.getMemberLevelId());
                if (currentLevel != null) {
                    memberInfo.setCurrentLevelName(currentLevel.getName());
                }
            }

            // 获取下一等级信息
            if (remainingPoints != null && remainingPoints > 0) {
                Integer currentIntegration = member.getIntegration() != null ? member.getIntegration() : 0;
                Integer nextLevelPoints = currentIntegration + remainingPoints;

                // 查找下一等级
                UmsMemberLevelExample example = new UmsMemberLevelExample();
                example.createCriteria()
                    .andDefaultStatusEqualTo(0)
                    .andGrowthPointEqualTo(nextLevelPoints);
                List<UmsMemberLevel> nextLevels = memberLevelMapper.selectByExample(example);

                if (!CollectionUtils.isEmpty(nextLevels)) {
                    UmsMemberLevel nextLevel = nextLevels.get(0);
                    memberInfo.setNextLevelName(nextLevel.getName());
                    memberInfo.setNextLevelRequiredPoints(nextLevel.getGrowthPoint());
                }
            }

        } catch (Exception e) {
            LOGGER.error("获取用户扩展信息失败: memberId={}", member.getId(), e);
            // 即使扩展信息获取失败，也返回基础用户信息
        }

        return memberInfo;
    }

    @Override
    public void updateIntegration(Long id, Integer integration) {
        // 获取当前积分，计算变化量
        UmsMember currentMember = memberMapper.selectByPrimaryKey(id);
        if (currentMember == null) {
            LOGGER.warn("用户不存在，无法更新积分: memberId={}", id);
            return;
        }

        int currentIntegration = currentMember.getIntegration() != null ? currentMember.getIntegration() : 0;
        int pointsChange = integration - currentIntegration;

        if (pointsChange != 0) {
            // 使用统一积分服务更新积分
            integrationService.updateIntegrationAndCheckLevel(id, pointsChange, "积分更新", 4);
        }
    }

    @Override
    public SaTokenInfo login(String username, String password) {
        if(StrUtil.isEmpty(username)||StrUtil.isEmpty(password)){
            Asserts.fail("用户名或密码不能为空！");
        }
        UmsMember member = getByUsername(username);
        if(member==null){
            Asserts.fail("找不到该用户！");
        }
        if (!BCrypt.checkpw(password, member.getPassword())) {
            Asserts.fail("密码不正确！");
        }
        if(member.getStatus()!=1){
            Asserts.fail("该账号已被禁用！");
        }
        // 登录校验成功后，一行代码实现登录
        StpMemberUtil.login(member.getId());
        UserDto userDto = new UserDto();
        userDto.setId(member.getId());
        userDto.setUsername(member.getUsername());
        userDto.setClientId(AuthConstant.PORTAL_CLIENT_ID);
        // 将用户信息存储到Session中
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO,userDto);
        // 获取当前登录用户Token信息
        return StpUtil.getTokenInfo();
    }

    @Override
    public void logout() {
        //先清空缓存
        UserDto userDto = (UserDto) StpMemberUtil.getSession().get(AuthConstant.STP_MEMBER_INFO);
        memberCacheService.delMember(userDto.getId());
        //再调用sa-token的登出方法
        StpMemberUtil.logout();
    }

    //对输入的验证码进行校验
    private boolean verifyAuthCode(String authCode, String telephone){
        if(StringUtils.isEmpty(authCode)){
            return false;
        }
        String realAuthCode = memberCacheService.getAuthCode(telephone);
        return authCode.equals(realAuthCode);
    }

    //根据 openid 获取用户
    @Override
    public UmsMember getByOpenId(String openId) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andOpenidEqualTo(openId);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        return CollectionUtils.isEmpty(memberList) ? null : memberList.get(0);
    }

    //使用 openid 注册用户
    @Override
    public UmsMember registerByOpenIdAndInfo(String openId, String nickname, String icon) {
        UmsMember member = new UmsMember();
        member.setOpenid(openId);
        member.setNickname(nickname); // 保存昵称
        member.setIcon(icon);         // 保存头像
        member.setCreateTime(new Date());
        member.setStatus(1);          // 设置用户状态为激活
        member.setSourceType(1);      // 设置用户来源类型为1

        // 生成唯一的会员码
        String memberCode = generateUniqueMemberCode();
        member.setMemberCode(memberCode);

        //获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            member.setMemberLevelId(memberLevelList.get(0).getId());
        }

        memberMapper.insert(member);
        return member;
    }

    //使用 openid 登录
    @Override
    public SaTokenInfo loginByOpenId(String openId) {
        UmsMember member = getByOpenId(openId);
        if (member == null) {
            Asserts.fail("用户不存在");
        }
        StpMemberUtil.login(member.getId());
        UserDto userDto = new UserDto();
        userDto.setId(member.getId());
        userDto.setUsername(member.getUsername());
        userDto.setClientId(AuthConstant.PORTAL_CLIENT_ID);
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO, userDto);
        return StpUtil.getTokenInfo();
    }

    // 根据手机号获取用户
    @Override
    public UmsMember getByPhone(String phone) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(phone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        return CollectionUtils.isEmpty(memberList) ? null : memberList.get(0);
    }
    
    // 根据openId和手机号更新或创建用户
    @Override
    public UmsMember registerByOpenIdAndPhone(String openId, String phone, String nickname,
                                              String icon, Integer gender, String birthday) {
        // 先尝试通过 openid 查找用户
        UmsMember member = getByOpenId(openId);
        boolean isNewUser = false;

        if (member == null) {
            // 如果通过openId找不到用户，再尝试通过手机号查找
            UmsMember existingMemberByPhone = getByPhone(phone);

            if (existingMemberByPhone != null) {
                // 手机号已存在，说明是同一个用户但openId发生了变化
                // 更新现有用户的openId实现账号合并
                LOGGER.info("检测到用户openId变化，更新用户ID: {}, 旧openId: {}, 新openId: {}",
                           existingMemberByPhone.getId(), existingMemberByPhone.getOpenid(), openId);

                existingMemberByPhone.setOpenid(openId);

                // 同时更新其他信息
                boolean needUpdate = true; // openId已经需要更新

                if (!StringUtils.isEmpty(nickname) && (StringUtils.isEmpty(existingMemberByPhone.getNickname())
                        || !nickname.equals(existingMemberByPhone.getNickname()))) {
                    existingMemberByPhone.setNickname(nickname);
                }

                if (!StringUtils.isEmpty(icon) && (StringUtils.isEmpty(existingMemberByPhone.getIcon())
                        || !icon.equals(existingMemberByPhone.getIcon()))) {
                    existingMemberByPhone.setIcon(icon);
                }

                if (gender != null && (existingMemberByPhone.getGender() == null
                        || !gender.equals(existingMemberByPhone.getGender()))) {
                    existingMemberByPhone.setGender(gender);
                }

                if (!StringUtils.isEmpty(birthday) && !"undefined".equals(birthday) && !birthday.startsWith("undefined")) {
                    try {
                        String birthdayStr = birthday + "-01";
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date birthdayDate = formatter.parse(birthdayStr);

                        if (existingMemberByPhone.getBirthday() == null || !birthdayDate.equals(existingMemberByPhone.getBirthday())) {
                            existingMemberByPhone.setBirthday(birthdayDate);
                        }
                    } catch (Exception e) {
                        LOGGER.error("生日格式转换错误: birthday={}, error={}", birthday, e.getMessage());
                    }
                }

                // 更新数据库
                memberMapper.updateByPrimaryKeySelective(existingMemberByPhone);

                // 设置为已存在用户
                existingMemberByPhone.setSourceType(0);

                LOGGER.info("账号合并完成，用户ID: {}, 手机号: {}, 新openId: {}",
                           existingMemberByPhone.getId(), phone, openId);

                return existingMemberByPhone;
            }

            // 如果手机号也不存在，则创建新用户
            member = new UmsMember();
            member.setOpenid(openId);
            member.setPhone(phone);
            member.setNickname(nickname);
            
            // 设置可选信息
            if (!StringUtils.isEmpty(icon)) {
                member.setIcon(icon);
            }
            
            if (gender != null) {
                member.setGender(gender);
            }
            
            if (!StringUtils.isEmpty(birthday) && !"undefined".equals(birthday) && !birthday.startsWith("undefined")) {
                try {
                    // 将yyyy-MM格式转换为日期，日固定为1
                    String birthdayStr = birthday + "-01";
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date birthdayDate = formatter.parse(birthdayStr);
                    member.setBirthday(birthdayDate);
                } catch (Exception e) {
                    LOGGER.error("生日格式转换错误: birthday={}, error={}", birthday, e.getMessage());
                }
            }
            
            member.setCreateTime(new Date());
            member.setStatus(1);  // 设置用户状态为激活
            member.setSourceType(1); // 设置用户来源类型为1
            
            // 生成唯一的会员码
            String memberCode = generateUniqueMemberCode();
            member.setMemberCode(memberCode);
            
            // 获取默认会员等级并设置
            UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
            levelExample.createCriteria().andDefaultStatusEqualTo(1);
            List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
            if (!CollectionUtils.isEmpty(memberLevelList)) {
                member.setMemberLevelId(memberLevelList.get(0).getId());
            }
            
            memberMapper.insert(member);
            isNewUser = true;

            // 新用户注册后，处理邀请关系
            handleInviteRelationForNewUser(member.getId());

            // 新用户注册后，发放默认等级优惠券
            try {
                boolean couponResult = integrationService.grantDefaultLevelCoupon(member.getId());
                if (couponResult) {
                    LOGGER.info("新用户注册优惠券发放成功: memberId={}", member.getId());
                } else {
                    LOGGER.warn("新用户注册优惠券发放失败: memberId={}", member.getId());
                }
            } catch (Exception e) {
                LOGGER.error("新用户注册优惠券发放异常: memberId={}", member.getId(), e);
                // 优惠券发放失败不影响用户注册流程
            }
        } else {
            // 如果用户存在，更新用户信息
            boolean needUpdate = false;
            
            // 更新手机号（如果提供了且与现有不同）
            if (!StringUtils.isEmpty(phone) && (StringUtils.isEmpty(member.getPhone()) 
                    || !phone.equals(member.getPhone()))) {
                member.setPhone(phone);
                needUpdate = true;
            }
            
            // 更新昵称（如果提供了且与现有不同）
            if (!StringUtils.isEmpty(nickname) && (StringUtils.isEmpty(member.getNickname()) 
                    || !nickname.equals(member.getNickname()))) {
                member.setNickname(nickname);
                needUpdate = true;
            }
            
            // 更新头像（如果提供了且与现有不同）
            if (!StringUtils.isEmpty(icon) && (StringUtils.isEmpty(member.getIcon()) 
                    || !icon.equals(member.getIcon()))) {
                member.setIcon(icon);
                needUpdate = true;
            }
            
            // 更新性别（如果提供了且与现有不同）
            if (gender != null && (member.getGender() == null 
                    || !gender.equals(member.getGender()))) {
                member.setGender(gender);
                needUpdate = true;
            }
            
            // 更新生日（如果提供了且与现有不同）
            if (!StringUtils.isEmpty(birthday) && !"undefined".equals(birthday) && !birthday.startsWith("undefined")) {
                try {
                    String birthdayStr = birthday + "-01";
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date birthdayDate = formatter.parse(birthdayStr);

                    if (member.getBirthday() == null || !birthdayDate.equals(member.getBirthday())) {
                        member.setBirthday(birthdayDate);
                        needUpdate = true;
                    }
                } catch (Exception e) {
                    LOGGER.error("生日格式转换错误: birthday={}, error={}", birthday, e.getMessage());
                }
            }
            
            // 如果有信息更新，则保存到数据库
            if (needUpdate) {
                memberMapper.updateByPrimaryKeySelective(member);
            }
        }
        
        // 设置新注册标识
        member.setSourceType(isNewUser ? 1 : 0);  // 1表示新注册用户，0表示已存在用户
        
        // 如果是新用户，尝试绑定分销关系
        if (isNewUser) {
            try {
                bindDistributionRelationIfExists(member.getId());
            } catch (Exception e) {
                LOGGER.error("绑定分销关系失败，用户ID: {}, 错误: {}", member.getId(), e.getMessage());
                // 分销关系绑定失败不影响用户注册
            }
        }
        
        return member;
    }
    
    // 使用手机号登录
    @Override
    public SaTokenInfo loginByPhone(String phone) {
        UmsMember member = getByPhone(phone);
        if (member == null) {
            Asserts.fail("用户不存在");
        }
        StpMemberUtil.login(member.getId());
        UserDto userDto = new UserDto();
        userDto.setId(member.getId());
        userDto.setUsername(member.getUsername());
        userDto.setClientId(AuthConstant.PORTAL_CLIENT_ID);
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO, userDto);
        return StpUtil.getTokenInfo();
    }

    // 根据openId和手机号注册或更新用户（原始方法）
    @Override
    public UmsMember registerByOpenIdAndPhone(String openId, String phone, String nickname, String icon) {
        // 重用更完整的方法，不传递可选参数
        return registerByOpenIdAndPhone(openId, phone, nickname, icon, null, null);
    }
    
    /**
     * 生成唯一的会员码
     * 确保生成的会员码在数据库中不存在
     * 
     * @return 唯一的会员码
     */
    private String generateUniqueMemberCode() {
        String memberCode;
        int maxAttempts = 10; // 最多尝试10次
        int attempts = 0;
        
        do {
            memberCode = MemberCodeGenerator.generateMemberCode();
            attempts++;
            
            // 检查是否已存在
            UmsMemberExample example = new UmsMemberExample();
            example.createCriteria().andMemberCodeEqualTo(memberCode);
            List<UmsMember> existingMembers = memberMapper.selectByExample(example);
            
            if (CollectionUtils.isEmpty(existingMembers)) {
                break; // 找到唯一的会员码
            }
            
            if (attempts >= maxAttempts) {
                LOGGER.error("生成唯一会员码失败，已尝试{}次", attempts);
                throw new RuntimeException("生成会员码失败，请重试");
            }
            
        } while (true);
        
        LOGGER.info("成功生成会员码：{}", memberCode);
        return memberCode;
    }
    
    /**
     * 尝试绑定分销关系
     * 从当前会话或请求中获取邀请参数并绑定分销关系
     */
    private void bindDistributionRelationIfExists(Long newUserId) {
        try {
            // 从线程本地存储或Session中获取邀请参数
            String inviteParam = getInviteParamFromContext();
            if (StringUtils.isEmpty(inviteParam)) {
                LOGGER.debug("未找到邀请参数，跳过分销关系绑定");
                return;
            }
            
            // 解析邀请参数获取邀请者ID
            Long inviterUserId = parseInviterIdFromParam(inviteParam);
            if (inviterUserId == null) {
                LOGGER.warn("邀请参数解析失败: {}", inviteParam);
                return;
            }
            
            // 绑定分销关系（这里需要根据实际的分销码来绑定）
            // 暂时使用用户ID作为分销码的占位符，实际实现时需要从邀请参数中解析分销码
            String distributionCode = inviterUserId.toString(); // 临时实现
            boolean success = distributionService.bindDistributionRelation(distributionCode, newUserId);
            if (success) {
                LOGGER.info("分销关系绑定成功，邀请者ID: {}, 新用户ID: {}", inviterUserId, newUserId);
            } else {
                LOGGER.warn("分销关系绑定失败，邀请者ID: {}, 新用户ID: {}", inviterUserId, newUserId);
            }
            
        } catch (Exception e) {
            LOGGER.error("绑定分销关系异常", e);
        }
    }
    
    /**
     * 从上下文中获取邀请参数
     * 这里需要根据实际情况从请求参数、Session或线程本地存储中获取
     */
    private String getInviteParamFromContext() {
        try {
            // 先尝试从ThreadLocal中获取（用于新用户注册时）
            String inviteParam = UmsMemberController.getInviteParamFromThreadLocal();
            if (inviteParam != null && !inviteParam.trim().isEmpty()) {
                // 使用后清除ThreadLocal
                UmsMemberController.clearInviteParamFromThreadLocal();
                return inviteParam;
            }
            
            // 再尝试从Session中获取（用于已登录用户）
            try {
                Object sessionInviteParam = StpMemberUtil.getSession().get("inviteParam");
                if (sessionInviteParam != null) {
                    return sessionInviteParam.toString();
                }
            } catch (Exception sessionEx) {
                LOGGER.debug("从Session获取邀请参数失败: {}", sessionEx.getMessage());
            }
            
            return null;
        } catch (Exception e) {
            LOGGER.debug("获取邀请参数失败: {}", e.getMessage());
            return null;
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

    /**
     * 处理新用户的邀请关系
     */
    private void handleInviteRelationForNewUser(Long userId) {
        try {
            // 从上下文中获取邀请参数（支持ThreadLocal和Session）
            String inviteParam = getInviteParamFromContext();
            if (inviteParam != null && !inviteParam.trim().isEmpty()) {
                LOGGER.info("处理新用户邀请关系，用户ID: {}, 邀请参数: {}", userId, inviteParam);

                // 建立邀请关系并发放奖励
                boolean success = inviteService.establishInviteRelation(inviteParam, userId);
                if (success) {
                    LOGGER.info("邀请关系建立成功，用户ID: {}", userId);
                    // 尝试清除Session中的邀请参数（如果用户已登录）
                    try {
                        StpMemberUtil.getSession().delete("inviteParam");
                    } catch (Exception sessionEx) {
                        LOGGER.debug("清除Session邀请参数失败（用户可能未登录）: {}", sessionEx.getMessage());
                    }
                } else {
                    LOGGER.warn("邀请关系建立失败，用户ID: {}, 邀请参数: {}", userId, inviteParam);
                }
            } else {
                LOGGER.debug("未找到邀请参数，跳过邀请关系处理，用户ID: {}", userId);
            }
        } catch (Exception e) {
            LOGGER.error("处理新用户邀请关系失败，用户ID: {}", userId, e);
        }
    }
}
