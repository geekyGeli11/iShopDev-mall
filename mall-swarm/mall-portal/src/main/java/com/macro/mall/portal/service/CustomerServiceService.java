package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.CustomerServiceInfoDTO;

/**
 * 客服信息服务接口
 * Created by macro on 2025/11/28.
 */
public interface CustomerServiceService {
    
    /**
     * 获取客服信息
     * @return 客服信息
     */
    CustomerServiceInfoDTO getCustomerServiceInfo();
}
