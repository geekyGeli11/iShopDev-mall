package com.macro.mall.service;

/**
 * 微信回调处理服务接口
 */
public interface WechatCallbackService {
    
    /**
     * 处理微信推送的事件
     * @param xmlMessage XML格式的消息
     * @return 响应XML
     */
    String handleEvent(String xmlMessage);
}
