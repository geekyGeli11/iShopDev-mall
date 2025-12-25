package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.util.MemberCodeGenerator;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.mapper.UmsMemberLoginLogMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.*;
import com.macro.mall.selfcheck.dto.MemberCodeLoginParam;
import com.macro.mall.selfcheck.dto.MemberLoginParam;
import com.macro.mall.selfcheck.service.SelfcheckMemberService;
import com.macro.mall.selfcheck.service.SelfcheckSecurityService;
import com.macro.mall.selfcheck.service.SelfcheckSmsService;
import com.macro.mall.selfcheck.util.MemberCodeUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 自助结算会员服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckMemberServiceImpl implements SelfcheckMemberService {

    private static final String REDIS_KEY_SESSION = "selfcheck:session:member:";
    private static final String REDIS_KEY_MEMBER = "selfcheck:member:";
    private static final long SESSION_TIMEOUT = 3600; // 1小时

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Autowired
    private UmsMemberLoginLogMapper loginLogMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SelfcheckSmsService smsService;

    @Autowired
    private SelfcheckSecurityService securityService;

    @Autowired
    private MemberCodeUtil memberCodeUtil;

    @Override
    public UmsMember authenticateMember(MemberLoginParam loginParam) {
        String phone = loginParam.getTelephone();
        String verifyCode = loginParam.getVerifyCode();

        // 验证手机号格式
        if (!StringUtils.hasText(phone) || !phone.matches("^1[3-9]\\d{9}$")) {
            Asserts.fail("手机号格式不正确");
        }

        // 兼容处理验证码类型（支持字符串和数字）
        String processedVerifyCode = processVerifyCode(verifyCode);

        // 验证验证码格式 - 支持1-6位数字，自动补齐前导0
        if (!StringUtils.hasText(processedVerifyCode) || !processedVerifyCode.matches("^\\d{1,6}$")) {
            Asserts.fail("验证码格式不正确");
        }

        // 自动补齐验证码到6位（补齐前导0）
        processedVerifyCode = String.format("%06d", Integer.parseInt(processedVerifyCode));

        // 验证验证码
        if (!smsService.verifyCode(phone, processedVerifyCode)) {
            Asserts.fail("验证码错误或已过期");
        }

        // 获取会员信息
        UmsMember member = getMemberByPhone(phone);
        if (member == null) {
            // 如果会员不存在，自动注册
            member = autoRegisterMember(phone);
        }

        // 检查会员状态
        if (member.getStatus() != 1) {
            Asserts.fail("账号已被禁用，请联系客服");
        }

        // 清除验证码
        smsService.clearCode(phone);

        return member;
    }

    @Override
    public UmsMember authenticateMemberByCode(MemberCodeLoginParam loginParam) {
        String memberCode = loginParam.getMemberCode();

        // 验证会员号码格式
        if (memberCode == null || memberCode.trim().isEmpty()) {
            Asserts.fail("会员码不能为空");
        }

        // 格式化会员码（自动添加M前缀如果需要）
        String formattedCode = formatMemberCode(memberCode.trim());
        if (!isValidMemberCodeFormat(formattedCode)) {
            Asserts.fail("会员码格式不正确，应为M开头的12位码");
        }

        // 根据会员码查找会员
        UmsMember member = getMemberByCode(formattedCode);
        if (member == null) {
            Asserts.fail("会员码不存在，请确认后重试");
        }

        // 检查会员状态
        if (member.getStatus() != 1) {
            Asserts.fail("账号已被禁用，请联系客服");
        }

        log.info("会员号码登录验证成功，会员号码：{}，会员ID：{}", formattedCode, member.getId());
        return member;
    }

    @Override
    public UmsMember getMemberByPhone(String phone) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(phone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(memberList)) {
            return null;
        }

        return memberList.get(0);
    }

    @Override
    public UmsMember getMemberById(Long memberId) {
        // 先从缓存获取
        String cacheKey = REDIS_KEY_MEMBER + memberId;
        try {
            Object cachedObj = redisTemplate.opsForValue().get(cacheKey);
            if (cachedObj != null) {
                if (cachedObj instanceof UmsMember) {
                    return (UmsMember) cachedObj;
                } else {
                    // 缓存类型不匹配，清除缓存
                    log.warn("会员缓存类型不匹配，清除缓存，会员ID：{}，缓存类型：{}", 
                            memberId, cachedObj.getClass().getSimpleName());
                    redisTemplate.delete(cacheKey);
                }
            }
        } catch (Exception e) {
            log.warn("获取会员缓存失败，会员ID：{}，错误：{}", memberId, e.getMessage());
            // 清除可能损坏的缓存
            redisTemplate.delete(cacheKey);
        }

        // 从数据库获取
        UmsMember member = memberMapper.selectByPrimaryKey(memberId);
        if (member == null) {
            return null;
        }

        // 存入缓存
        try {
            redisTemplate.opsForValue().set(cacheKey, member, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("保存会员缓存失败，会员ID：{}，错误：{}", memberId, e.getMessage());
        }

        return member;
    }

    @Override
    public UmsMemberSession createMemberSession(UmsMember member, String deviceInfo, String loginIp) {
        UmsMemberSession session = new UmsMemberSession();
        session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        session.setMemberId(member.getId());
        session.setPhone(member.getPhone());
        session.setLoginTime(new Date());
        session.setLastActiveTime(new Date());
        session.setLoginIp(loginIp);
        session.setDeviceInfo(deviceInfo);
        session.setOrderInProgress(false);
        session.setStatus(1); // 1表示有效
        session.setExpireTime(new Date(System.currentTimeMillis() + SESSION_TIMEOUT * 1000));

        // 存储会话信息
        String sessionKey = REDIS_KEY_SESSION + member.getId();
        redisTemplate.opsForValue().set(sessionKey, session, SESSION_TIMEOUT, TimeUnit.SECONDS);

        log.info("创建会员会话成功，会员ID：{}，会话ID：{}", member.getId(), session.getSessionId());

        return session;
    }

    @Override
    public void updateMemberActivity(Long memberId) {
        String sessionKey = REDIS_KEY_SESSION + memberId;
        try {
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
            if (sessionObj instanceof UmsMemberSession) {
                UmsMemberSession session = (UmsMemberSession) sessionObj;
                session.setLastActiveTime(new Date());
                redisTemplate.opsForValue().set(sessionKey, session, SESSION_TIMEOUT, TimeUnit.SECONDS);
            } else if (sessionObj != null) {
                log.warn("会员会话缓存类型不匹配，清除缓存，会员ID：{}，缓存类型：{}", 
                        memberId, sessionObj.getClass().getSimpleName());
                redisTemplate.delete(sessionKey);
            }
        } catch (Exception e) {
            log.warn("更新会员活跃时间失败，会员ID：{}，错误：{}", memberId, e.getMessage());
        }
    }

    @Override
    public UmsMemberSession getMemberSession(Long memberId) {
        String sessionKey = REDIS_KEY_SESSION + memberId;
        try {
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
            if (sessionObj instanceof UmsMemberSession) {
                return (UmsMemberSession) sessionObj;
            } else if (sessionObj != null) {
                log.warn("会员会话缓存类型不匹配，清除缓存，会员ID：{}，缓存类型：{}", 
                        memberId, sessionObj.getClass().getSimpleName());
                redisTemplate.delete(sessionKey);
            }
        } catch (Exception e) {
            log.warn("获取会员会话失败，会员ID：{}，错误：{}", memberId, e.getMessage());
        }
        return null;
    }

    @Override
    public void clearMemberSession(Long memberId) {
        String sessionKey = REDIS_KEY_SESSION + memberId;
        redisTemplate.delete(sessionKey);

        // 清除会员缓存
        String memberKey = REDIS_KEY_MEMBER + memberId;
        redisTemplate.delete(memberKey);

        log.info("清除会员会话成功，会员ID：{}", memberId);
    }

    @Override
    public boolean isSessionValid(Long memberId) {
        UmsMemberSession session = getMemberSession(memberId);
        if (session == null) {
            return false;
        }

        // 检查会话状态
        if (session.getStatus() != 1) {
            return false;
        }

        // 检查是否过期
        if (session.getExpireTime() != null && session.getExpireTime().before(new Date())) {
            clearMemberSession(memberId);
            return false;
        }

        return true;
    }

    @Override
    public void recordLoginLog(UmsMember member, String loginIp, String deviceInfo) {
        try {
            UmsMemberLoginLog loginLog = new UmsMemberLoginLog();
            loginLog.setMemberId(member.getId());
            loginLog.setCreateTime(new Date());
            loginLog.setIp(loginIp);
            loginLog.setLoginType(4); // 4表示自助结算终端
            
            // 简单的地理位置处理（实际项目中可以集成IP地址库）
            loginLog.setCity("未知");
            loginLog.setProvince("未知");

            loginLogMapper.insert(loginLog);

            log.info("记录会员登录日志成功，会员ID：{}，IP：{}", member.getId(), loginIp);

        } catch (Exception e) {
            log.error("记录会员登录日志失败，会员ID：{}，错误：{}", member.getId(), e.getMessage(), e);
        }
    }

    /**
     * 自动注册会员
     */
    private UmsMember autoRegisterMember(String phone) {
        try {
            // 获取默认会员等级
            UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
            levelExample.createCriteria().andDefaultStatusEqualTo(1);
            List<UmsMemberLevel> levelList = memberLevelMapper.selectByExample(levelExample);
            
            Long memberLevelId = 1L; // 默认等级ID
            if (!CollectionUtils.isEmpty(levelList)) {
                memberLevelId = levelList.get(0).getId();
            }

            // 创建新会员
            UmsMember newMember = new UmsMember();
            newMember.setMemberLevelId(memberLevelId);
            newMember.setPhone(phone);
            newMember.setUsername(phone); // 使用手机号作为用户名
            newMember.setNickname("会员" + phone.substring(phone.length() - 4)); // 生成默认昵称
            newMember.setStatus(1); // 启用状态
            newMember.setCreateTime(new Date());
            newMember.setSourceType(4); // 来源：自助结算终端
            newMember.setIntegration(0); // 初始积分
            newMember.setGrowth(0); // 初始成长值
            
            // 生成唯一的会员码
            String memberCode = generateUniqueMemberCode();
            newMember.setMemberCode(memberCode);

            memberMapper.insert(newMember);

            log.info("自动注册会员成功，手机号：{}，会员ID：{}", phone, newMember.getId());

            return newMember;

        } catch (Exception e) {
            log.error("自动注册会员失败，手机号：{}，错误：{}", phone, e.getMessage(), e);
            throw new RuntimeException("注册会员失败：" + e.getMessage());
        }
    }

    @Override
    public UmsMember getMemberByCode(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return null;
        }

        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andMemberCodeEqualTo(memberCode.trim());
        List<UmsMember> memberList = memberMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(memberList)) {
            log.warn("根据会员码查找会员失败，会员码：{}", memberCode);
            return null;
        }

        UmsMember member = memberList.get(0);
        log.info("根据会员码查找会员成功，会员码：{}，会员ID：{}", memberCode, member.getId());
        return member;
    }

    @Override
    public String generateMemberCode(Long memberId) {
        // 注意：在新的架构中，会员码应该在注册时就生成
        // 这个方法主要用于兼容性或特殊情况
        UmsMember member = getMemberById(memberId);
        if (member == null) {
            return null;
        }
        
        // 如果会员已经有会员码，直接返回
        if (member.getMemberCode() != null && !member.getMemberCode().trim().isEmpty()) {
            return member.getMemberCode();
        }
        
        // 为了兼容性，如果没有会员码，生成一个并更新到数据库
        // 但这种情况在正常流程中不应该发生
        log.warn("会员ID:{}没有会员码，正在生成...", memberId);
        
        String memberCode = generateUniqueMemberCode();
        
        // 更新会员记录
        UmsMember updateMember = new UmsMember();
        updateMember.setId(memberId);
        updateMember.setMemberCode(memberCode);
        memberMapper.updateByPrimaryKeySelective(updateMember);
        
        // 清除缓存
        String cacheKey = REDIS_KEY_MEMBER + memberId;
        redisTemplate.delete(cacheKey);
        
        log.info("为会员ID:{}生成并保存会员码：{}", memberId, memberCode);
        return memberCode;
    }

    @Override
    public String getMemberCode(Long memberId) {
        UmsMember member = getMemberById(memberId);
        if (member == null) {
            return null;
        }
        return member.getMemberCode();
    }

    /**
     * 格式化会员码
     * 如果用户输入11位数字，自动添加M前缀
     */
    private String formatMemberCode(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = input.trim().toUpperCase();
        
        // 如果用户输入的是纯11位数字，自动添加M前缀
        if (trimmed.matches("^\\d{11}$")) {
            trimmed = "M" + trimmed;
        }
        
        return trimmed;
    }

    /**
     * 验证会员码格式是否正确
     */
    private boolean isValidMemberCodeFormat(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return false;
        }
        return memberCode.matches("^M\\d{11}$");
    }

    /**
     * 处理验证码类型兼容（支持字符串和数字类型）
     * @param verifyCode 原始验证码
     * @return 处理后的验证码字符串
     */
    private String processVerifyCode(Object verifyCode) {
        if (verifyCode == null) {
            return null;
        }

        // 如果是字符串类型，直接返回
        if (verifyCode instanceof String) {
            return (String) verifyCode;
        }

        // 如果是数字类型，转换为字符串
        if (verifyCode instanceof Number) {
            return String.valueOf(verifyCode);
        }

        // 其他类型，尝试转换为字符串
        return String.valueOf(verifyCode);
    }

    /**
     * 生成唯一的会员码
     * 确保生成的会员码在数据库中不存在
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
                log.error("生成唯一会员码失败，已尝试{}次", attempts);
                throw new RuntimeException("生成会员码失败，请重试");
            }

        } while (true);

        log.info("成功生成会员码：{}", memberCode);
        return memberCode;
    }

    @Override
    public boolean bindMemberSchool(Long memberId, Long schoolId) {
        try {
            UmsMember record = new UmsMember();
            record.setId(memberId);
            record.setSchoolId(schoolId);
            int result = memberMapper.updateByPrimaryKeySelective(record);

            if (result > 0) {
                // 清除缓存
                redisTemplate.delete(REDIS_KEY_MEMBER + memberId);
                log.info("会员{}绑定学校{}成功", memberId, schoolId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("会员{}绑定学校{}失败", memberId, schoolId, e);
            return false;
        }
    }

    @Override
    public Long getMemberSchoolId(Long memberId) {
        UmsMember member = getMemberById(memberId);
        return member != null ? member.getSchoolId() : null;
    }

    @Override
    public UmsMember authenticateMemberWithStoreBinding(MemberLoginParam loginParam) {
        // 先进行正常的登录验证
        UmsMember member = authenticateMember(loginParam);

        if (member != null && loginParam.getSchoolId() != null) {
            // 检查用户是否已经绑定学校
            if (member.getSchoolId() == null) {
                // 用户还没有绑定学校，进行绑定
                log.info("会员{}还未绑定学校，开始绑定学校ID：{}", member.getId(), loginParam.getSchoolId());
                boolean bindSuccess = bindMemberSchool(member.getId(), loginParam.getSchoolId());
                if (bindSuccess) {
                    // 更新member对象中的学校ID
                    member.setSchoolId(loginParam.getSchoolId());
                    log.info("会员{}绑定学校{}成功", member.getId(), loginParam.getSchoolId());
                } else {
                    log.warn("会员{}绑定学校{}失败", member.getId(), loginParam.getSchoolId());
                }
            } else {
                log.info("会员{}已绑定学校{}，跳过绑定", member.getId(), member.getSchoolId());
            }
        }

        return member;
    }

    @Override
    public UmsMember authenticateMemberByCodeWithStoreBinding(MemberCodeLoginParam loginParam) {
        // 先进行正常的会员号码登录验证
        UmsMember member = authenticateMemberByCode(loginParam);

        if (member != null && loginParam.getSchoolId() != null) {
            // 检查用户是否已经绑定学校
            if (member.getSchoolId() == null) {
                // 用户还没有绑定学校，进行绑定
                log.info("会员{}还未绑定学校，开始绑定学校ID：{}", member.getId(), loginParam.getSchoolId());
                boolean bindSuccess = bindMemberSchool(member.getId(), loginParam.getSchoolId());
                if (bindSuccess) {
                    // 更新member对象中的学校ID
                    member.setSchoolId(loginParam.getSchoolId());
                    log.info("会员{}绑定学校{}成功", member.getId(), loginParam.getSchoolId());
                } else {
                    log.warn("会员{}绑定学校{}失败", member.getId(), loginParam.getSchoolId());
                }
            } else {
                log.info("会员{}已绑定学校{}，跳过绑定", member.getId(), member.getSchoolId());
            }
        }

        return member;
    }
}