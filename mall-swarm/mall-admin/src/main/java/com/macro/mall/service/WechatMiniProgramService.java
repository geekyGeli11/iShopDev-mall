package com.macro.mall.service;

/**
 * 微信小程序API服务
 * Created by macro on 2024/12/30.
 */
public interface WechatMiniProgramService {
    
    /**
     * 获取小程序访问令牌
     * @return access_token
     */
    String getAccessToken();
    
    /**
     * 生成小程序URL Link（短链接）
     * @param path 页面路径
     * @param query 页面参数
     * @return URL Link
     */
    String generateUrlLink(String path, String query);
    
    /**
     * 生成小程序码
     * @param scene 场景值
     * @param page 页面路径
     * @param width 宽度（默认430）
     * @return 小程序码图片的本地路径或URL
     */
    String generateMiniProgramCode(String scene, String page, Integer width);
    
    /**
     * 检查小程序配置是否可用
     * @return 是否可用
     */
    boolean isConfigAvailable();
    
    /**
     * 强制刷新access_token
     */
    void refreshAccessToken();
}
