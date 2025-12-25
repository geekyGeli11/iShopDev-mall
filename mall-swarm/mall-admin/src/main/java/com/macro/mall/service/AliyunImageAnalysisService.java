package com.macro.mall.service;

/**
 * 阿里云图片分析服务接口
 * Created by macro on 2024/12/20.
 */
public interface AliyunImageAnalysisService {
    
    /**
     * 提取图片的风格介绍文字
     * @param imageUrl 图片URL
     * @return 风格介绍文字
     */
    String extractStyleDescription(String imageUrl);
    
    /**
     * 检查API服务状态
     * @return 服务是否可用
     */
    boolean isServiceAvailable();
}

