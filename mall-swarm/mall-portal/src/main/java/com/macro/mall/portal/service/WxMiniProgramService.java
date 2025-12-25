package com.macro.mall.portal.service;

import java.io.InputStream;

/**
 * 微信小程序服务
 * Created on 2023/11/17.
 */
public interface WxMiniProgramService {
    
    /**
     * 生成小程序码
     * @param page 小程序页面路径
     * @param scene 场景参数
     * @return 小程序码图片输入流
     */
    InputStream generateMiniProgramCode(String page, String scene);

    /**
     * 生成小程序码并返回字节数组
     * @param page 小程序页面路径
     * @param scene 场景参数
     * @return 小程序码字节数组
     */
    byte[] generateMiniProgramCodeBytes(String page, String scene);
} 