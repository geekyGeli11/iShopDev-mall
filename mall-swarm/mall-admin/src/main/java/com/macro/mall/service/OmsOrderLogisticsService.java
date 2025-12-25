package com.macro.mall.service;
import com.macro.mall.dto.OmsOrderLogisticsInfo;

public interface OmsOrderLogisticsService {
    OmsOrderLogisticsInfo getLogisticsInfo(Long orderId) throws Exception;
}