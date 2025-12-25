package com.macro.mall.portal.service;

public interface WechatLoginService {
    String getOpenId(String code);
    
    /**
     * 获取微信手机号
     * @param code 手机号获取凭证
     * @return 解密后的手机号
     */
    String getPhoneNumber(String code);
}