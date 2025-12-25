package com.macro.mall.selfcheck.service;

/**
 * 自助结算短信服务接口
 * 
 * @author macro
 * @since 1.0.0
 */
public interface SelfcheckSmsService {

    /**
     * 发送验证码
     * @param phone 手机号
     * @return 验证码
     */
    String sendVerifyCode(String phone);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifyCode(String phone, String code);

    /**
     * 获取验证码剩余有效时间（秒）
     * @param phone 手机号
     * @return 剩余时间
     */
    long getCodeTtl(String phone);

    /**
     * 检查是否可以发送验证码
     * @param phone 手机号
     * @return 是否可以发送
     */
    boolean canSendCode(String phone);

    /**
     * 清除验证码
     * @param phone 手机号
     */
    void clearCode(String phone);

    /**
     * 获取发送频率限制剩余时间（秒）
     * @param phone 手机号
     * @return 剩余时间
     */
    long getRateLimitTtl(String phone);
} 