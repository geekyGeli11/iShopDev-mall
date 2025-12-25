package com.macro.mall.service;

import com.macro.mall.model.PmsSystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 邀请配置管理Service
 * Created by guanghengzhou on 2024/01/20.
 */
public interface PmsInviteConfigService {
    
    /**
     * 获取所有邀请配置
     */
    Map<String, Object> getAllInviteConfig();
    
    /**
     * 批量更新邀请配置
     */
    int batchUpdateConfig(List<PmsSystemConfig> configs, String reason);
    
    /**
     * 根据配置key获取配置
     */
    PmsSystemConfig getConfigByKey(String configKey);
    
    /**
     * 更新单个配置
     */
    int updateConfig(String configKey, String configValue, String desc);
    
    /**
     * 获取配置变更历史
     */
    List<Map<String, Object>> getConfigHistory(String configKey, Integer pageNum, Integer pageSize);
    
    /**
     * 为现有配置创建初始历史记录
     */
    int createInitialHistory();
} 