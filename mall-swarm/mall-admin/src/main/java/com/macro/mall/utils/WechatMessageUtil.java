package com.macro.mall.utils;

import com.macro.mall.dto.WechatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信消息工具类
 * 用于解析和生成微信XML消息
 */
public class WechatMessageUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WechatMessageUtil.class);
    
    /**
     * 验证微信签名
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param token 服务号配置的Token
     * @return 验证结果
     */
    public static boolean verifySignature(String signature, String timestamp, String nonce, String token) {
        if (signature == null || timestamp == null || nonce == null || token == null) {
            LOGGER.warn("签名验证参数为空: signature={}, timestamp={}, nonce={}, token={}", 
                signature != null, timestamp != null, nonce != null, token != null);
            return false;
        }
        
        String[] arr = {token, timestamp, nonce};
        Arrays.sort(arr);
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s);
        }
        
        String sha1 = sha1(sb.toString());
        LOGGER.info("签名验证: token={}, 排序后字符串={}, 计算签名={}, 微信签名={}, 匹配={}", 
            token, sb.toString(), sha1, signature, signature.equals(sha1));
        return signature.equals(sha1);
    }
    
    /**
     * SHA1加密
     */
    private static String sha1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(str.getBytes());
            StringBuilder hexStr = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    hexStr.append("0");
                }
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (Exception e) {
            LOGGER.error("SHA1加密失败", e);
            return "";
        }
    }
    
    /**
     * 解析微信XML消息
     * @param xmlStr XML字符串
     * @return WechatMessage对象
     */
    public static WechatMessage parseXml(String xmlStr) {
        if (xmlStr == null || xmlStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            Map<String, String> map = xmlToMap(xmlStr);
            WechatMessage message = new WechatMessage();
            
            message.setToUserName(map.get("ToUserName"));
            message.setFromUserName(map.get("FromUserName"));
            message.setMsgType(map.get("MsgType"));
            message.setEvent(map.get("Event"));
            message.setEventKey(map.get("EventKey"));
            message.setTicket(map.get("Ticket"));
            message.setContent(map.get("Content"));
            message.setPicUrl(map.get("PicUrl"));
            message.setMediaId(map.get("MediaId"));
            message.setLabel(map.get("Label"));
            
            // 解析数值类型
            String createTime = map.get("CreateTime");
            if (createTime != null && !createTime.isEmpty()) {
                message.setCreateTime(Long.parseLong(createTime));
            }
            
            String msgId = map.get("MsgId");
            if (msgId != null && !msgId.isEmpty()) {
                message.setMsgId(Long.parseLong(msgId));
            }
            
            String latitude = map.get("Latitude");
            if (latitude != null && !latitude.isEmpty()) {
                message.setLatitude(Double.parseDouble(latitude));
            }
            
            String longitude = map.get("Longitude");
            if (longitude != null && !longitude.isEmpty()) {
                message.setLongitude(Double.parseDouble(longitude));
            }
            
            String scale = map.get("Scale");
            if (scale != null && !scale.isEmpty()) {
                message.setScale(Double.parseDouble(scale));
            }
            
            return message;
        } catch (Exception e) {
            LOGGER.error("解析微信XML消息失败: {}", xmlStr, e);
            return null;
        }
    }
    
    /**
     * XML字符串转Map
     */
    private static Map<String, String> xmlToMap(String xmlStr) throws Exception {
        Map<String, String> map = new HashMap<>();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 防止XXE攻击
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlStr)));
        
        Element root = document.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                map.put(node.getNodeName(), node.getTextContent());
            }
        }
        
        return map;
    }
    
    /**
     * 将WechatMessage对象转换为XML字符串
     * @param message WechatMessage对象
     * @return XML字符串
     */
    public static String toXml(WechatMessage message) {
        if (message == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        
        appendCDataElement(sb, "ToUserName", message.getToUserName());
        appendCDataElement(sb, "FromUserName", message.getFromUserName());
        appendElement(sb, "CreateTime", message.getCreateTime());
        appendCDataElement(sb, "MsgType", message.getMsgType());
        
        if (message.getEvent() != null) {
            appendCDataElement(sb, "Event", message.getEvent());
        }
        if (message.getEventKey() != null) {
            appendCDataElement(sb, "EventKey", message.getEventKey());
        }
        if (message.getTicket() != null) {
            appendCDataElement(sb, "Ticket", message.getTicket());
        }
        if (message.getContent() != null) {
            appendCDataElement(sb, "Content", message.getContent());
        }
        if (message.getMsgId() != null) {
            appendElement(sb, "MsgId", message.getMsgId());
        }
        if (message.getPicUrl() != null) {
            appendCDataElement(sb, "PicUrl", message.getPicUrl());
        }
        if (message.getMediaId() != null) {
            appendCDataElement(sb, "MediaId", message.getMediaId());
        }
        if (message.getLatitude() != null) {
            appendElement(sb, "Latitude", message.getLatitude());
        }
        if (message.getLongitude() != null) {
            appendElement(sb, "Longitude", message.getLongitude());
        }
        if (message.getScale() != null) {
            appendElement(sb, "Scale", message.getScale());
        }
        if (message.getLabel() != null) {
            appendCDataElement(sb, "Label", message.getLabel());
        }
        
        sb.append("</xml>");
        return sb.toString();
    }
    
    /**
     * 生成文本回复消息XML
     * @param toUser 接收方OpenID
     * @param fromUser 发送方（公众号）
     * @param content 回复内容
     * @return XML字符串
     */
    public static String buildTextReplyXml(String toUser, String fromUser, String content) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        appendCDataElement(sb, "ToUserName", toUser);
        appendCDataElement(sb, "FromUserName", fromUser);
        appendElement(sb, "CreateTime", System.currentTimeMillis() / 1000);
        appendCDataElement(sb, "MsgType", "text");
        appendCDataElement(sb, "Content", content);
        sb.append("</xml>");
        return sb.toString();
    }
    
    private static void appendCDataElement(StringBuilder sb, String name, String value) {
        if (value != null) {
            sb.append("<").append(name).append("><![CDATA[").append(value).append("]]></").append(name).append(">");
        }
    }
    
    private static void appendElement(StringBuilder sb, String name, Object value) {
        if (value != null) {
            sb.append("<").append(name).append(">").append(value).append("</").append(name).append(">");
        }
    }
}
