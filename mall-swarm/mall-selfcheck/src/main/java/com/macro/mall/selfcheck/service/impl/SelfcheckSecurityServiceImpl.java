package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.selfcheck.config.SelfcheckSecurityConfig;
import com.macro.mall.selfcheck.service.SelfcheckSecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 自助结算安全管理服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class SelfcheckSecurityServiceImpl implements SelfcheckSecurityService {

    private static final String REDIS_KEY_LOGIN_ATTEMPT = "selfcheck:security:login_attempt:";
    private static final String REDIS_KEY_LOGIN_FAIL = "selfcheck:security:login_fail:";
    private static final String REDIS_KEY_IP_BLOCK = "selfcheck:security:ip_block:";
    private static final String REDIS_KEY_DEVICE_TRUST = "selfcheck:security:device_trust:";
    private static final String REDIS_KEY_LOGIN_HISTORY = "selfcheck:security:login_history:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SelfcheckSecurityConfig securityConfig;

    @Override
    public boolean isLoginRateLimited(String phone) {
        String key = REDIS_KEY_LOGIN_ATTEMPT + phone;
        String countStr = (String) redisTemplate.opsForValue().get(key);
        
        if (countStr == null) {
            return false;
        }

        int count = Integer.parseInt(countStr);
        return count >= securityConfig.getMaxAttemptsPerWindow();
    }

    @Override
    public void recordLoginAttempt(String phone, boolean success, String ip) {
        // 记录登录尝试次数
        String attemptKey = REDIS_KEY_LOGIN_ATTEMPT + phone;
        Long count = redisTemplate.opsForValue().increment(attemptKey);
        if (count == 1) {
            redisTemplate.expire(attemptKey, securityConfig.getRateLimitWindow(), TimeUnit.SECONDS);
        }

        if (success) {
            // 登录成功，清除失败记录
            clearFailedLoginRecord(phone);
            log.info("登录成功 - 手机号：{}，IP：{}", phone, ip);
        } else {
            // 登录失败，记录失败次数
            String failKey = REDIS_KEY_LOGIN_FAIL + phone;
            Long failCount = redisTemplate.opsForValue().increment(failKey);
            if (failCount == 1) {
                redisTemplate.expire(failKey, securityConfig.getLoginLockDuration(), TimeUnit.SECONDS);
            }

            log.warn("登录失败 - 手机号：{}，IP：{}，连续失败次数：{}", phone, ip, failCount);

            // 达到最大失败次数，锁定账号
            if (failCount >= securityConfig.getMaxLoginAttempts()) {
                log.warn("账号被锁定 - 手机号：{}，IP：{}，失败次数：{}", phone, ip, failCount);
            }

            // 检查IP异常行为
            checkIpAbnormalBehavior(ip);
        }

        // 记录登录历史
        recordLoginHistory(phone, ip, success);
    }

    @Override
    public int getFailedLoginCount(String phone) {
        String failKey = REDIS_KEY_LOGIN_FAIL + phone;
        String countStr = (String) redisTemplate.opsForValue().get(failKey);
        return countStr != null ? Integer.parseInt(countStr) : 0;
    }

    @Override
    public void clearFailedLoginRecord(String phone) {
        String failKey = REDIS_KEY_LOGIN_FAIL + phone;
        String attemptKey = REDIS_KEY_LOGIN_ATTEMPT + phone;
        
        redisTemplate.delete(failKey);
        redisTemplate.delete(attemptKey);
    }

    @Override
    public boolean isIpBlocked(String ip) {
        String blockKey = REDIS_KEY_IP_BLOCK + ip;
        return redisTemplate.hasKey(blockKey);
    }

    @Override
    public void blockIp(String ip, String reason, long duration) {
        String blockKey = REDIS_KEY_IP_BLOCK + ip;
        String blockInfo = String.format("{\"reason\":\"%s\",\"blockTime\":\"%s\"}", 
                reason, new Date().toString());
        
        redisTemplate.opsForValue().set(blockKey, blockInfo, duration, TimeUnit.SECONDS);
        
        log.warn("IP被锁定 - IP：{}，原因：{}，锁定时长：{}秒", ip, reason, duration);
    }

    @Override
    public boolean isDeviceTrusted(String deviceId) {
        String trustKey = REDIS_KEY_DEVICE_TRUST + deviceId;
        return redisTemplate.hasKey(trustKey);
    }

    @Override
    public void trustDevice(String deviceId, Long memberId) {
        String trustKey = REDIS_KEY_DEVICE_TRUST + deviceId;
        String trustInfo = String.format("{\"memberId\":%d,\"trustTime\":\"%s\"}", 
                memberId, new Date().toString());
        
        // 设备信任期使用配置
        redisTemplate.opsForValue().set(trustKey, trustInfo, securityConfig.getDeviceTrustDays(), TimeUnit.DAYS);
        
        log.info("设备被标记为可信 - 设备ID：{}，会员ID：{}", deviceId, memberId);
    }

    @Override
    public boolean detectAbnormalLogin(String phone, String ip, String deviceInfo) {
        // 检查登录频率
        if (isLoginRateLimited(phone)) {
            log.warn("检测到异常登录：登录频率过高 - 手机号：{}，IP：{}", phone, ip);
            return true;
        }

        // 检查IP是否被锁定
        if (isIpBlocked(ip)) {
            log.warn("检测到异常登录：IP被锁定 - 手机号：{}，IP：{}", phone, ip);
            return true;
        }

        // 检查失败次数
        int failedCount = getFailedLoginCount(phone);
        if (failedCount >= securityConfig.getMaxLoginAttempts()) {
            log.warn("检测到异常登录：账号被锁定 - 手机号：{}，失败次数：{}", phone, failedCount);
            return true;
        }

        // 检查地理位置异常（这里简化处理，实际可以集成IP地理位置服务）
        if (detectGeoLocationAnomaly(phone, ip)) {
            log.warn("检测到异常登录：地理位置异常 - 手机号：{}，IP：{}", phone, ip);
            return true;
        }

        return false;
    }

    @Override
    public long getLoginLockTtl(String phone) {
        String failKey = REDIS_KEY_LOGIN_FAIL + phone;
        Long ttl = redisTemplate.getExpire(failKey, TimeUnit.SECONDS);
        
        int failedCount = getFailedLoginCount(phone);
        if (failedCount >= securityConfig.getMaxLoginAttempts() && ttl != null && ttl > 0) {
            return ttl;
        }
        
        return 0;
    }

    /**
     * 检查IP异常行为
     */
    private void checkIpAbnormalBehavior(String ip) {
        String ipAttemptKey = "selfcheck:security:ip_attempt:" + ip;
        Long count = redisTemplate.opsForValue().increment(ipAttemptKey);
        
        if (count == 1) {
            redisTemplate.expire(ipAttemptKey, 300, TimeUnit.SECONDS); // 5分钟窗口
        }

        // 5分钟内失败次数超过10次，锁定IP
        if (count > 10) {
            blockIp(ip, "短时间内大量登录失败", securityConfig.getIpBlockDuration());
        }
    }

    /**
     * 记录登录历史
     */
    private void recordLoginHistory(String phone, String ip, boolean success) {
        try {
            String historyKey = REDIS_KEY_LOGIN_HISTORY + phone;
            String historyRecord = String.format("{\"ip\":\"%s\",\"success\":%b,\"time\":\"%s\"}", 
                    ip, success, new Date().toString());
            
            // 保留最近50条记录
            redisTemplate.opsForList().leftPush(historyKey, historyRecord);
            redisTemplate.opsForList().trim(historyKey, 0, 49);
            redisTemplate.expire(historyKey, 7, TimeUnit.DAYS); // 保留7天
            
        } catch (Exception e) {
            log.error("记录登录历史失败：", e);
        }
    }

    /**
     * 检测地理位置异常
     * TODO: 可以集成IP地理位置服务进行更精确的检测
     */
    private boolean detectGeoLocationAnomaly(String phone, String ip) {
        // 这里简化处理，实际环境可以：
        // 1. 调用IP地理位置服务获取当前IP的地理位置
        // 2. 对比用户历史登录的地理位置
        // 3. 如果距离过远且时间间隔较短，则认为异常
        
        return false; // 暂时返回false
    }
} 