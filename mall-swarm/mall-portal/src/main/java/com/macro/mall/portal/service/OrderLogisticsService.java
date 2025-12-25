package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.OrderLogisticsInfo;

public interface OrderLogisticsService {
    OrderLogisticsInfo getLogisticsInfo(Long orderId) throws Exception;
}
