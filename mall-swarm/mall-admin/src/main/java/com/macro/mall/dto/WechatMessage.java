package com.macro.mall.dto;

import lombok.Data;

/**
 * 微信消息对象
 * 用于解析微信服务号推送的XML消息
 */
@Data
public class WechatMessage {
    /**
     * 开发者微信号
     */
    private String toUserName;
    
    /**
     * 发送方帐号（OpenID）
     */
    private String fromUserName;
    
    /**
     * 消息创建时间（整型）
     */
    private Long createTime;
    
    /**
     * 消息类型：text/image/voice/video/shortvideo/location/link/event
     */
    private String msgType;
    
    /**
     * 事件类型：subscribe/unsubscribe/SCAN/LOCATION/CLICK/VIEW
     */
    private String event;
    
    /**
     * 事件KEY值
     * 扫描带参数二维码：qrscene_为前缀，后面为二维码的参数值
     * 已关注用户扫码：二维码的参数值
     */
    private String eventKey;
    
    /**
     * 二维码的ticket，可用来换取二维码图片
     */
    private String ticket;
    
    /**
     * 文本消息内容
     */
    private String content;
    
    /**
     * 消息id，64位整型
     */
    private Long msgId;
    
    /**
     * 图片链接（由系统生成）
     */
    private String picUrl;
    
    /**
     * 图片消息媒体id
     */
    private String mediaId;
    
    /**
     * 地理位置纬度
     */
    private Double latitude;
    
    /**
     * 地理位置经度
     */
    private Double longitude;
    
    /**
     * 地图缩放大小
     */
    private Double scale;
    
    /**
     * 地理位置信息
     */
    private String label;
    
    /**
     * 判断是否为关注事件
     */
    public boolean isSubscribeEvent() {
        return "event".equals(msgType) && "subscribe".equals(event);
    }
    
    /**
     * 判断是否为扫码事件（已关注用户扫码）
     */
    public boolean isScanEvent() {
        return "event".equals(msgType) && "SCAN".equals(event);
    }
    
    /**
     * 判断是否为取消关注事件
     */
    public boolean isUnsubscribeEvent() {
        return "event".equals(msgType) && "unsubscribe".equals(event);
    }
    
    /**
     * 获取场景值（管理员ID）
     * 关注事件：eventKey格式为 qrscene_123
     * 扫码事件：eventKey格式为 123
     */
    public String getSceneValue() {
        if (eventKey == null) {
            return null;
        }
        if (eventKey.startsWith("qrscene_")) {
            return eventKey.substring(8);
        }
        return eventKey;
    }
}
