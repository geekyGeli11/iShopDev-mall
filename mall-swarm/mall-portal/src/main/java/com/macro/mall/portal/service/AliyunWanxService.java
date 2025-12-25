package com.macro.mall.portal.service;

/**
 * 阿里云万相API服务接口
 * Created by macro on 2024/12/20.
 */
public interface AliyunWanxService {
    
    /**
     * 图片风格化处理
     * @param imageUrl 原图URL
     * @param fullPrompt 完整的提示词（包含风格描述和用户提示词）
     * @param functionType API功能类型（如 stylization_all, stylization_local, description_edit 等）
     * @return 风格化后的图片URL
     */
    String stylizeImage(String imageUrl, String fullPrompt, String functionType);
    
    /**
     * 检查API服务状态
     * @return 服务是否可用
     */
    boolean isServiceAvailable();
}
