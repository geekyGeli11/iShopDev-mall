package com.macro.mall.selfcheck.service;

/**
 * 自助结算安全管理服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckSecurityService {

    /**
     * 检查登录频率限制
     * @param phone 手机号
     * @return 是否被限制
     */
    boolean isLoginRateLimited(String phone);

    /**
     * 记录登录尝试
     * @param phone 手机号
     * @param success 是否成功
     * @param ip 登录IP
     */
    void recordLoginAttempt(String phone, boolean success, String ip);

    /**
     * 获取登录失败次数
     * @param phone 手机号
     * @return 失败次数
     */
    int getFailedLoginCount(String phone);

    /**
     * 清除登录失败记录
     * @param phone 手机号
     */
    void clearFailedLoginRecord(String phone);

    /**
     * 检查IP是否被锁定
     * @param ip IP地址
     * @return 是否被锁定
     */
    boolean isIpBlocked(String ip);

    /**
     * 锁定IP
     * @param ip IP地址
     * @param reason 锁定原因
     * @param duration 锁定时长（秒）
     */
    void blockIp(String ip, String reason, long duration);

    /**
     * 检查设备是否可信
     * @param deviceId 设备ID
     * @return 是否可信
     */
    boolean isDeviceTrusted(String deviceId);

    /**
     * 标记设备为可信
     * @param deviceId 设备ID
     * @param memberId 会员ID
     */
    void trustDevice(String deviceId, Long memberId);

    /**
     * 检测异常登录
     * @param phone 手机号
     * @param ip 登录IP
     * @param deviceInfo 设备信息
     * @return 是否异常
     */
    boolean detectAbnormalLogin(String phone, String ip, String deviceInfo);

    /**
     * 获取登录锁定剩余时间（秒）
     * @param phone 手机号
     * @return 剩余时间
     */
    long getLoginLockTtl(String phone);
} 