package com.macro.mall.portal.service;

import com.macro.mall.model.UmsAiStylizationRecord;

/**
 * AI风格化服务接口
 * Created by macro on 2024/12/20.
 */
public interface AiStylizationService {
    
    /**
     * AI风格化处理
     * @param imageUrl 原图URL
     * @param style 风格类型
     * @param memberId 用户ID
     * @return 风格化后的图片URL
     */
    String processStylization(String imageUrl, String style, Long memberId);
    
    /**
     * 获取支持的风格列表
     * @return 风格列表
     */
    java.util.List<String> getSupportedStyles();
    
    /**
     * 保存AI风格化记录
     * @param record 记录信息
     * @return 保存结果
     */
    int saveStylizationRecord(UmsAiStylizationRecord record);
    
    /**
     * 检查风格化任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    String checkTaskStatus(String taskId);
}
