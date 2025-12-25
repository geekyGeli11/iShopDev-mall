package com.macro.mall.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * 请求工具类
 * 
 * @author macro
 * @since 1.0.0
 */
public class RequestUtil {

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HttpServletRequest
     * @return 客户端IP地址
     */
    public static String getRequestIp(HttpServletRequest request) {
        String ip = null;
        
        // 1. 优先从X-Forwarded-For获取（代理服务器会添加此头）
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            ip = xForwardedFor.split(",")[0].trim();
        }
        
        // 2. 从X-Real-IP获取（Nginx反向代理会添加此头）
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        // 3. 从Proxy-Client-IP获取（Apache服务器会添加此头）
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        
        // 4. 从WL-Proxy-Client-IP获取（WebLogic服务器会添加此头）
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        
        // 5. 从HTTP_CLIENT_IP获取
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        
        // 6. 从HTTP_X_FORWARDED_FOR获取
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        
        // 7. 最后从getRemoteAddr获取
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 8. 处理IPv6本地地址
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        
        return ip;
    }

    /**
     * 获取用户代理字符串
     * 
     * @param request HttpServletRequest
     * @return 用户代理字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 判断是否为Ajax请求
     * 
     * @param request HttpServletRequest
     * @return 是否为Ajax请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String xRequestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(xRequestedWith);
    }

    /**
     * 获取请求来源URL
     * 
     * @param request HttpServletRequest
     * @return 请求来源URL
     */
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("Referer");
    }
} 