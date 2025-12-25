package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.SmsFreightCalculateParam;
import com.macro.mall.portal.dto.SmsFreightCalculateResult;

/**
 * 运费计算服务
 * Created by AI Assistant
 */
public interface FreightCalculateService {
    
    /**
     * 计算运费
     * @param param 运费计算参数
     * @return 运费计算结果
     */
    SmsFreightCalculateResult calculateFreight(SmsFreightCalculateParam param);
} 