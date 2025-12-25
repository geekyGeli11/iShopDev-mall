package com.macro.mall.selfcheck.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 会员号码管理工具类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Component
public class MemberCodeUtil {

    private static final String REDIS_KEY_MEMBER_CODE = "selfcheck:member:code:";
    private static final String REDIS_KEY_CODE_MEMBER = "selfcheck:code:member:";
    private static final String MEMBER_CODE_PREFIX = "M";
    private static final long MEMBER_CODE_EXPIRE = 365 * 24 * 3600; // 1年过期

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 为会员生成唯一的会员号码
     * 格式：M + 年月日 + 5位随机数
     * 
     * @param memberId 会员ID
     * @return 会员号码
     */
    public String generateMemberCode(Long memberId) {
        try {
            // 检查是否已有会员号码
            String existingCode = getMemberCode(memberId);
            if (existingCode != null) {
                return existingCode;
            }

            // 生成新的会员号码
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            String memberCode;
            int attempts = 0;
            
            do {
                String randomStr = String.format("%05d", new Random().nextInt(100000));
                memberCode = MEMBER_CODE_PREFIX + dateStr + randomStr;
                attempts++;
                
                if (attempts > 100) {
                    throw new RuntimeException("生成会员号码失败，重试次数过多");
                }
            } while (isCodeExists(memberCode));

            // 建立双向映射
            linkMemberAndCode(memberId, memberCode);

            log.info("为会员生成号码成功，会员ID：{}，会员号码：{}", memberId, memberCode);
            return memberCode;

        } catch (Exception e) {
            log.error("生成会员号码失败，会员ID：{}，错误：{}", memberId, e.getMessage(), e);
            throw new RuntimeException("生成会员号码失败");
        }
    }

    /**
     * 根据会员ID获取会员号码
     * 
     * @param memberId 会员ID
     * @return 会员号码
     */
    public String getMemberCode(Long memberId) {
        if (memberId == null) {
            return null;
        }

        String key = REDIS_KEY_MEMBER_CODE + memberId;
        try {
            Object codeObj = redisTemplate.opsForValue().get(key);
            if (codeObj != null) {
                return String.valueOf(codeObj);
            }
        } catch (Exception e) {
            log.warn("获取会员号码缓存失败，会员ID：{}，错误：{}", memberId, e.getMessage());
        }
        return null;
    }

    /**
     * 根据会员号码获取会员ID
     * 
     * @param memberCode 会员号码
     * @return 会员ID
     */
    public Long getMemberId(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return null;
        }

        String key = REDIS_KEY_CODE_MEMBER + memberCode;
        try {
            Object memberIdObj = redisTemplate.opsForValue().get(key);
            if (memberIdObj != null) {
                if (memberIdObj instanceof Number) {
                    return ((Number) memberIdObj).longValue();
                } else {
                    return Long.valueOf(memberIdObj.toString());
                }
            }
        } catch (Exception e) {
            log.warn("获取会员ID缓存失败，会员号码：{}，错误：{}", memberCode, e.getMessage());
        }
        return null;
    }

    /**
     * 验证会员号码格式
     * 
     * @param memberCode 会员号码
     * @return 是否格式正确
     */
    public boolean isValidFormat(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return false;
        }

        // 格式：M + 6位日期 + 5位数字，总长度12位
        return memberCode.matches("^M\\d{11}$");
    }

    /**
     * 检查会员号码是否存在
     * 
     * @param memberCode 会员号码
     * @return 是否存在
     */
    public boolean isCodeExists(String memberCode) {
        if (memberCode == null || memberCode.trim().isEmpty()) {
            return false;
        }

        String key = REDIS_KEY_CODE_MEMBER + memberCode;
        return redisTemplate.hasKey(key);
    }

    /**
     * 验证会员号码是否有效（格式正确且存在）
     * 
     * @param memberCode 会员号码
     * @return 是否有效
     */
    public boolean isValidCode(String memberCode) {
        return isValidFormat(memberCode) && isCodeExists(memberCode);
    }

    /**
     * 建立会员ID和会员号码的双向映射
     * 
     * @param memberId 会员ID
     * @param memberCode 会员号码
     */
    private void linkMemberAndCode(Long memberId, String memberCode) {
        String memberToCodeKey = REDIS_KEY_MEMBER_CODE + memberId;
        String codeToMemberKey = REDIS_KEY_CODE_MEMBER + memberCode;

        // 设置双向映射，有效期1年
        redisTemplate.opsForValue().set(memberToCodeKey, memberCode, MEMBER_CODE_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(codeToMemberKey, memberId, MEMBER_CODE_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 清除会员号码映射
     * 
     * @param memberId 会员ID
     */
    public void clearMemberCode(Long memberId) {
        String memberCode = getMemberCode(memberId);
        if (memberCode != null) {
            String memberToCodeKey = REDIS_KEY_MEMBER_CODE + memberId;
            String codeToMemberKey = REDIS_KEY_CODE_MEMBER + memberCode;

            redisTemplate.delete(memberToCodeKey);
            redisTemplate.delete(codeToMemberKey);

            log.info("清除会员号码映射成功，会员ID：{}，会员号码：{}", memberId, memberCode);
        }
    }

    /**
     * 刷新会员号码过期时间
     * 
     * @param memberId 会员ID
     */
    public void refreshMemberCode(Long memberId) {
        String memberCode = getMemberCode(memberId);
        if (memberCode != null) {
            linkMemberAndCode(memberId, memberCode);
        }
    }
} 