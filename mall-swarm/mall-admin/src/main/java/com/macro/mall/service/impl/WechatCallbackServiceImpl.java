package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.dto.WechatMessage;
import com.macro.mall.service.WechatCallbackService;
import com.macro.mall.service.WechatServiceAccountService;
import com.macro.mall.utils.WechatMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信回调处理服务实现类
 */
@Service
public class WechatCallbackServiceImpl implements WechatCallbackService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatCallbackServiceImpl.class);
    
    @Autowired
    private WechatServiceAccountService wechatServiceAccountService;
    
    @Override
    public String handleEvent(String xmlMessage) {
        if (StrUtil.isEmpty(xmlMessage)) {
            LOGGER.warn("收到空的微信消息");
            return "success";
        }
        
        LOGGER.info("收到微信消息: {}", xmlMessage);
        
        WechatMessage message = WechatMessageUtil.parseXml(xmlMessage);
        if (message == null) {
            LOGGER.error("解析微信消息失败");
            return "success";
        }
        
        String openId = message.getFromUserName();
        String toUserName = message.getToUserName();
        
        // 处理关注事件（带参数二维码）
        if (message.isSubscribeEvent()) {
            return handleSubscribeEvent(message, openId, toUserName);
        }
        
        // 处理扫码事件（已关注用户扫码）
        if (message.isScanEvent()) {
            return handleScanEvent(message, openId, toUserName);
        }
        
        // 处理取消关注事件
        if (message.isUnsubscribeEvent()) {
            LOGGER.info("用户取消关注: openId={}", openId);
            // 可以在这里处理解绑逻辑，但通常不自动解绑
            return "success";
        }
        
        // 其他消息类型返回success
        return "success";
    }
    
    /**
     * 处理关注事件
     */
    private String handleSubscribeEvent(WechatMessage message, String openId, String toUserName) {
        String sceneValue = message.getSceneValue();
        LOGGER.info("用户关注事件: openId={}, sceneValue={}", openId, sceneValue);
        
        // 如果有场景值，说明是扫描带参数二维码关注
        if (StrUtil.isNotEmpty(sceneValue)) {
            try {
                Long adminId = Long.parseLong(sceneValue);
                // 执行绑定
                wechatServiceAccountService.bindWechat(adminId, openId);
                
                // 发送绑定成功消息
                String replyContent = "绑定成功！您已成功绑定管理员账号，后续将通过此公众号接收系统通知。";
                return WechatMessageUtil.buildTextReplyXml(openId, toUserName, replyContent);
                
            } catch (NumberFormatException e) {
                LOGGER.error("场景值解析失败，非有效的管理员ID: {}", sceneValue);
            } catch (Exception e) {
                LOGGER.error("绑定微信失败: adminId={}, openId={}", sceneValue, openId, e);
                String replyContent = "绑定失败，请联系管理员。";
                return WechatMessageUtil.buildTextReplyXml(openId, toUserName, replyContent);
            }
        }
        
        // 普通关注，发送欢迎消息
        String welcomeMsg = "欢迎关注！如需绑定管理员账号，请在管理后台扫描绑定二维码。";
        return WechatMessageUtil.buildTextReplyXml(openId, toUserName, welcomeMsg);
    }
    
    /**
     * 处理扫码事件（已关注用户扫码）
     */
    private String handleScanEvent(WechatMessage message, String openId, String toUserName) {
        String sceneValue = message.getSceneValue();
        LOGGER.info("用户扫码事件: openId={}, sceneValue={}", openId, sceneValue);
        
        if (StrUtil.isEmpty(sceneValue)) {
            return "success";
        }
        
        try {
            Long adminId = Long.parseLong(sceneValue);
            // 执行绑定
            wechatServiceAccountService.bindWechat(adminId, openId);
            
            // 发送绑定成功消息
            String replyContent = "绑定成功！您已成功绑定管理员账号，后续将通过此公众号接收系统通知。";
            return WechatMessageUtil.buildTextReplyXml(openId, toUserName, replyContent);
            
        } catch (NumberFormatException e) {
            LOGGER.error("场景值解析失败，非有效的管理员ID: {}", sceneValue);
        } catch (Exception e) {
            LOGGER.error("绑定微信失败: adminId={}, openId={}", sceneValue, openId, e);
            String replyContent = "绑定失败，请联系管理员。";
            return WechatMessageUtil.buildTextReplyXml(openId, toUserName, replyContent);
        }
        
        return "success";
    }
}
