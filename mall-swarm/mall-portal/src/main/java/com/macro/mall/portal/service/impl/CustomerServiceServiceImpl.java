package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.dto.CustomerServiceInfoDTO;
import com.macro.mall.portal.service.CustomerServiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.macro.mall.mapper.CustomerServiceConfigMapper;
import com.macro.mall.model.CustomerServiceConfig;
import com.macro.mall.model.CustomerServiceConfigExample;
import java.util.List;

/**
 * 客服信息服务实现类
 * 
 * 从数据库中查询客服配置信息，提供给小程序端使用
 * 
 * Created by macro on 2025/11/28.
 */
@Service
public class CustomerServiceServiceImpl implements CustomerServiceService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceServiceImpl.class);
    
    @Autowired
    private CustomerServiceConfigMapper customerServiceConfigMapper;
    
    @Override
    public CustomerServiceInfoDTO getCustomerServiceInfo() {
        try {
            // 从数据库查询客服配置
            CustomerServiceConfigExample example = new CustomerServiceConfigExample();
            List<CustomerServiceConfig> configList = customerServiceConfigMapper.selectByExample(example);
            
            CustomerServiceInfoDTO infoDTO = new CustomerServiceInfoDTO();
            
            if (configList != null && !configList.isEmpty()) {
                CustomerServiceConfig config = configList.get(0);
                
                // 只返回启用的配置
                if (config.getIsEnabled() != null && config.getIsEnabled().booleanValue()) {
                    infoDTO.setWechatQrcodeUrl(config.getWechatQrcodeUrl());
                    infoDTO.setWechatAccount(config.getWechatAccount());
                    infoDTO.setEnterpriseWechatQrcodeUrl(config.getEnterpriseWechatQrcodeUrl());
                    infoDTO.setServicePhone(config.getServicePhone());
                    infoDTO.setServiceHours(config.getServiceHours());
                    infoDTO.setServiceDescription(config.getServiceDescription());
                    infoDTO.setIsEnabled(config.getIsEnabled() ? 1 : 0);
                    
                    LOGGER.info("获取客服信息成功");
                } else {
                    LOGGER.info("客服配置未启用");
                }
            } else {
                LOGGER.info("未找到客服配置");
            }
            
            return infoDTO;
        } catch (Exception e) {
            LOGGER.error("获取客服信息失败", e);
            return new CustomerServiceInfoDTO();
        }
    }
}
