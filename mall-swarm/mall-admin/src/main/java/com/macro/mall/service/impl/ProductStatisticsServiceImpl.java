package com.macro.mall.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.dao.ProductStatisticsDao;
import com.macro.mall.dto.*;
import com.macro.mall.service.ProductStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品销售数据统计Service实现类
 * Created by macro on 2025/11/28.
 */
@Service
public class ProductStatisticsServiceImpl implements ProductStatisticsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductStatisticsServiceImpl.class);
    
    @Autowired
    private ProductStatisticsDao productStatisticsDao;
    
    @Autowired
    private RedisService redisService;
    
    /**
     * Redis缓存key前缀
     */
    private static final String REDIS_KEY_PREFIX = "dashboard:product:";
    
    /**
     * 缓存过期时间：5分钟（秒）
     */
    private static final long CACHE_EXPIRE_TIME = 5 * 60;
    
    @Override
    public ProductStatisticsVO getProductStatistics(ProductStatisticsQuery query) {
        // 构建缓存key
        String cacheKey = buildCacheKey(query);
        
        // 尝试从缓存获取
        ProductStatisticsVO cachedData = (ProductStatisticsVO) redisService.get(cacheKey);
        if (cachedData != null) {
            LOGGER.info("从缓存获取商品销售数据统计: {}", cacheKey);
            return cachedData;
        }
        
        LOGGER.info("查询商品销售数据统计: startDate={}, endDate={}, schoolId={}, rankBy={}", 
                    query.getStartDate(), query.getEndDate(), query.getSchoolId(), query.getRankBy());
        
        ProductStatisticsVO result = new ProductStatisticsVO();
        
        // 0. 获取商品总览数据
        Map<String, Object> overviewData = productStatisticsDao.getProductOverview(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId()
        );
        
        if (overviewData != null) {
            Object totalProductsObj = overviewData.get("totalProducts");
            result.setTotalProducts(totalProductsObj != null ? ((Number) totalProductsObj).intValue() : 0);
            
            BigDecimal totalSalesAmount = (BigDecimal) overviewData.get("totalSalesAmount");
            result.setTotalSalesAmount(totalSalesAmount != null ? totalSalesAmount.doubleValue() : 0.0);
            
            Object totalSalesQuantityObj = overviewData.get("totalSalesQuantity");
            result.setTotalSalesQuantity(totalSalesQuantityObj != null ? ((Number) totalSalesQuantityObj).intValue() : 0);
            
            Object hotProductsObj = overviewData.get("hotProducts");
            result.setHotProducts(hotProductsObj != null ? ((Number) hotProductsObj).intValue() : 0);
        } else {
            result.setTotalProducts(0);
            result.setTotalSalesAmount(0.0);
            result.setTotalSalesQuantity(0);
            result.setHotProducts(0);
        }
        
        // 1. 获取按销售金额排行
        List<Map<String, Object>> topByAmountData = productStatisticsDao.getTopSellingByAmount(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getTopLimit()
        );
        result.setTopSellingByAmount(convertToProductRankingList(topByAmountData));
        
        // 2. 获取按销售数量排行
        List<Map<String, Object>> topByQuantityData = productStatisticsDao.getTopSellingByQuantity(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getTopLimit()
        );
        result.setTopSellingByQuantity(convertToProductRankingList(topByQuantityData));
        
        // 3. 获取盈利汇总数据
        Map<String, Object> profitSummaryData = productStatisticsDao.getProfitSummary(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId()
        );
        
        ProfitSummaryVO profitSummary = new ProfitSummaryVO();
        if (profitSummaryData != null) {
            BigDecimal totalProfit = (BigDecimal) profitSummaryData.get("totalProfit");
            profitSummary.setTotalProfit(totalProfit != null ? totalProfit : BigDecimal.ZERO);
            
            BigDecimal totalRevenue = (BigDecimal) profitSummaryData.get("totalRevenue");
            BigDecimal profitMargin = BigDecimal.ZERO;
            if (totalRevenue != null && totalRevenue.compareTo(BigDecimal.ZERO) > 0 && totalProfit != null) {
                profitMargin = totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                                         .multiply(new BigDecimal(100))
                                         .setScale(2, RoundingMode.HALF_UP);
            }
            profitSummary.setProfitMargin(profitMargin);
        } else {
            profitSummary.setTotalProfit(BigDecimal.ZERO);
            profitSummary.setProfitMargin(BigDecimal.ZERO);
        }
        
        // 获取利润最高的商品
        List<Map<String, Object>> topProfitData = productStatisticsDao.getTopProfitProducts(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId(), 
            query.getTopLimit()
        );
        profitSummary.setTopProfitProducts(convertToProductRankingList(topProfitData));
        result.setProfitSummary(profitSummary);
        
        // 4. 获取品类销售分布
        List<Map<String, Object>> categoryData = productStatisticsDao.getCategoryDistribution(
            query.getStartDate(), 
            query.getEndDate(), 
            query.getSchoolId()
        );
        
        List<CategoryDistributionVO> categoryDistribution = new ArrayList<>();
        BigDecimal totalCategoryRevenue = BigDecimal.ZERO;
        
        // 先计算总收入
        for (Map<String, Object> data : categoryData) {
            BigDecimal salesAmount = (BigDecimal) data.get("salesAmount");
            if (salesAmount != null) {
                totalCategoryRevenue = totalCategoryRevenue.add(salesAmount);
            }
        }
        
        // 再计算占比
        for (Map<String, Object> data : categoryData) {
            CategoryDistributionVO vo = new CategoryDistributionVO();
            vo.setCategory((String) data.get("category"));
            
            BigDecimal salesAmount = (BigDecimal) data.get("salesAmount");
            vo.setSalesAmount(salesAmount != null ? salesAmount : BigDecimal.ZERO);
            
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalCategoryRevenue.compareTo(BigDecimal.ZERO) > 0 && salesAmount != null) {
                percentage = salesAmount.divide(totalCategoryRevenue, 4, RoundingMode.HALF_UP)
                                       .multiply(new BigDecimal(100))
                                       .setScale(2, RoundingMode.HALF_UP);
            }
            vo.setPercentage(percentage);
            
            categoryDistribution.add(vo);
        }
        result.setCategoryDistribution(categoryDistribution);
        
        // 缓存结果
        redisService.set(cacheKey, result, CACHE_EXPIRE_TIME);
        LOGGER.info("商品销售数据统计已缓存: {}", cacheKey);
        
        return result;
    }
    
    /**
     * 转换为商品排行VO列表
     */
    private List<ProductRankingVO> convertToProductRankingList(List<Map<String, Object>> dataList) {
        List<ProductRankingVO> result = new ArrayList<>();
        
        for (Map<String, Object> data : dataList) {
            ProductRankingVO vo = new ProductRankingVO();
            
            Object productIdObj = data.get("productId");
            if (productIdObj != null) {
                vo.setProductId(productIdObj instanceof Long ? (Long) productIdObj : ((Integer) productIdObj).longValue());
            }
            
            vo.setProductName((String) data.get("productName"));
            vo.setSkuCode((String) data.get("skuCode"));
            
            BigDecimal salesAmount = (BigDecimal) data.get("salesAmount");
            vo.setSalesAmount(salesAmount != null ? salesAmount : BigDecimal.ZERO);
            
            Object salesQuantityObj = data.get("salesQuantity");
            Integer salesQuantity = 0;
            if (salesQuantityObj != null) {
                if (salesQuantityObj instanceof Long) {
                    salesQuantity = ((Long) salesQuantityObj).intValue();
                } else if (salesQuantityObj instanceof Integer) {
                    salesQuantity = (Integer) salesQuantityObj;
                }
            }
            vo.setSalesQuantity(salesQuantity);
            
            BigDecimal profit = (BigDecimal) data.get("profit");
            vo.setProfit(profit != null ? profit : BigDecimal.ZERO);
            
            result.add(vo);
        }
        
        return result;
    }
    
    /**
     * 构建缓存key
     */
    private String buildCacheKey(ProductStatisticsQuery query) {
        StringBuilder sb = new StringBuilder(REDIS_KEY_PREFIX);
        sb.append(query.getStartDate()).append(":");
        sb.append(query.getEndDate()).append(":");
        sb.append(query.getSchoolId() != null ? query.getSchoolId() : "all").append(":");
        sb.append(query.getTopLimit()).append(":");
        sb.append(query.getRankBy());
        return sb.toString();
    }
}
