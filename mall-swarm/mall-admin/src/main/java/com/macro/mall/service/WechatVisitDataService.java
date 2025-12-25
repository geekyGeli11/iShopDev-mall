package com.macro.mall.service;

import com.macro.mall.dto.WechatVisitDataVO;

import java.time.LocalDate;

/**
 * 微信小程序访问数据Service
 * Created by macro on 2025/11/28.
 */
public interface WechatVisitDataService {
    
    /**
     * 获取小程序访问数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 访问数据
     */
    WechatVisitDataVO getWechatVisitData(LocalDate startDate, LocalDate endDate);
    
    /**
     * 刷新小程序访问数据缓存
     */
    void refreshWechatVisitDataCache();
}
