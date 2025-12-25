package com.macro.mall.service;

import java.util.Map;

/**
 * 微信服务号服务接口
 */
public interface WechatServiceAccountService {
    
    /**
     * 获取服务号access_token
     * @return access_token
     */
    String getAccessToken();
    
    /**
     * 强制刷新access_token
     * @return 新的access_token
     */
    String refreshAccessToken();
    
    /**
     * 生成带参数的临时二维码
     * @param adminId 管理员ID（作为场景值）
     * @param expireSeconds 过期时间（秒），最大30天
     * @return 二维码图片URL
     */
    String generateQRCode(Long adminId, int expireSeconds);
    
    /**
     * 获取微信用户信息
     * @param openId 用户OpenID
     * @return 用户信息Map，包含nickname、headimgurl等
     */
    Map<String, Object> getUserInfo(String openId);
    
    /**
     * 绑定微信用户到管理员
     * @param adminId 管理员ID
     * @param openId 微信OpenID
     */
    void bindWechat(Long adminId, String openId);
    
    /**
     * 解除微信绑定
     * @param adminId 管理员ID
     */
    void unbindWechat(Long adminId);
    
    /**
     * 发送模板消息
     * @param openId 接收者OpenID
     * @param templateId 模板ID
     * @param data 模板数据
     * @param url 跳转链接（可选）
     * @return 消息ID
     */
    Long sendTemplateMessage(String openId, String templateId, Map<String, String> data, String url);
    
    /**
     * 发送文本客服消息
     * @param openId 接收者OpenID
     * @param content 消息内容
     */
    void sendTextMessage(String openId, String content);
    
    /**
     * 验证微信签名
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @return 验证结果
     */
    boolean verifySignature(String signature, String timestamp, String nonce);
}
