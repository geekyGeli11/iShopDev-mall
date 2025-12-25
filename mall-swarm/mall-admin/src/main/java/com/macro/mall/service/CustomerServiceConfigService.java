package com.macro.mall.service;

import com.macro.mall.model.CustomerServiceConfig;

/**
 * 客服配置服务接口
 * Created by macro on 2025/11/28.
 */
public interface CustomerServiceConfigService {
    
    /**
     * 获取客服配置
     * @return 客服配置信息
     */
    CustomerServiceConfig getConfig();
    
    /**
     * 更新客服配置
     * @param config 客服配置信息
     * @return 更新结果
     */
    int updateConfig(CustomerServiceConfig config);
}
