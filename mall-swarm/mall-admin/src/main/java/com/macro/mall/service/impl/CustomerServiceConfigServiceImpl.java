package com.macro.mall.service.impl;

import com.macro.mall.mapper.CustomerServiceConfigMapper;
import com.macro.mall.model.CustomerServiceConfig;
import com.macro.mall.model.CustomerServiceConfigExample;
import com.macro.mall.service.CustomerServiceConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 客服配置服务实现类
 * Created by macro on 2025/11/28.
 */
@Service
public class CustomerServiceConfigServiceImpl implements CustomerServiceConfigService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceConfigServiceImpl.class);
    
    @Autowired
    private CustomerServiceConfigMapper customerServiceConfigMapper;
    
    @Override
    public CustomerServiceConfig getConfig() {
        CustomerServiceConfigExample example = new CustomerServiceConfigExample();
        List<CustomerServiceConfig> configList = customerServiceConfigMapper.selectByExample(example);
        
        if (configList != null && !configList.isEmpty()) {
            return configList.get(0);
        }
        
        // 如果没有配置，返回空对象
        return new CustomerServiceConfig();
    }
    
    @Override
    @Transactional
    public int updateConfig(CustomerServiceConfig config) {
        if (config == null) {
            throw new RuntimeException("客服配置不能为空");
        }
        
        // 获取现有配置
        CustomerServiceConfig existConfig = getConfig();
        
        if (existConfig != null && existConfig.getId() != null) {
            // 更新现有配置
            config.setId(existConfig.getId());
            config.setUpdatedTime(new Date());
            int count = customerServiceConfigMapper.updateByPrimaryKeySelective(config);
            LOGGER.info("更新客服配置成功: {}", config.getId());
            return count;
        } else {
            // 创建新配置
            config.setCreatedTime(new Date());
            config.setUpdatedTime(new Date());
            int count = customerServiceConfigMapper.insertSelective(config);
            LOGGER.info("创建客服配置成功: {}", config.getId());
            return count;
        }
    }
}
