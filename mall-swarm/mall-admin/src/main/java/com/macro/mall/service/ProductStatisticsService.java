package com.macro.mall.service;

import com.macro.mall.dto.ProductStatisticsQuery;
import com.macro.mall.dto.ProductStatisticsVO;

/**
 * 商品销售数据统计Service
 * Created by macro on 2025/11/28.
 */
public interface ProductStatisticsService {
    
    /**
     * 获取商品销售数据统计
     * @param query 查询参数
     * @return 商品销售数据统计结果
     */
    ProductStatisticsVO getProductStatistics(ProductStatisticsQuery query);
}
