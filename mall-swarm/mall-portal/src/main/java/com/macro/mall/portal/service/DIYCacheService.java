package com.macro.mall.portal.service;

import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.portal.domain.ProductDiyConfig;

import java.util.List;

/**
 * DIY缓存服务接口
 * Created by macro on 2024/12/20.
 */
public interface DIYCacheService {
    
    /**
     * 缓存DIY素材列表
     */
    void cacheDiyMaterials(Long categoryId, List<PmsDiyMaterial> materials);
    
    /**
     * 获取缓存的DIY素材列表
     */
    List<PmsDiyMaterial> getCachedDiyMaterials(Long categoryId);
    
    /**
     * 清除DIY素材缓存
     */
    void clearDiyMaterialsCache(Long categoryId);
    
    /**
     * 清除所有DIY素材缓存
     */
    void clearAllDiyMaterialsCache();
    
    /**
     * 缓存DIY模板
     */
    void cacheDiyTemplate(Long templateId, PmsDiyTemplate template);
    
    /**
     * 获取缓存的DIY模板
     */
    PmsDiyTemplate getCachedDiyTemplate(Long templateId);
    
    /**
     * 清除DIY模板缓存
     */
    void clearDiyTemplateCache(Long templateId);
    
    /**
     * 清除所有DIY模板缓存
     */
    void clearAllDiyTemplateCache();
    
    /**
     * 缓存商品DIY配置
     */
    void cacheProductDiyConfig(Long productId, ProductDiyConfig config);
    
    /**
     * 获取缓存的商品DIY配置
     */
    ProductDiyConfig getCachedProductDiyConfig(Long productId);
    
    /**
     * 清除商品DIY配置缓存
     */
    void clearProductDiyConfigCache(Long productId);
    
    /**
     * 缓存用户设计数据
     */
    void cacheUserDesign(Long memberId, String designKey, Object designData);
    
    /**
     * 获取缓存的用户设计数据
     */
    <T> T getCachedUserDesign(Long memberId, String designKey, Class<T> clazz);
    
    /**
     * 清除用户设计缓存
     */
    void clearUserDesignCache(Long memberId, String designKey);
    
    /**
     * 清除用户所有设计缓存
     */
    void clearAllUserDesignCache(Long memberId);
    
    /**
     * 缓存AI风格化结果
     */
    void cacheAiStylizationResult(String requestHash, String resultUrl);
    
    /**
     * 获取缓存的AI风格化结果
     */
    String getCachedAiStylizationResult(String requestHash);
    
    /**
     * 清除AI风格化结果缓存
     */
    void clearAiStylizationCache(String requestHash);
    
    /**
     * 缓存预览图生成结果
     */
    void cachePreviewResult(String designHash, List<String> previewUrls);
    
    /**
     * 获取缓存的预览图生成结果
     */
    List<String> getCachedPreviewResult(String designHash);
    
    /**
     * 清除预览图缓存
     */
    void clearPreviewCache(String designHash);
    
    /**
     * 获取缓存统计信息
     */
    CacheStats getCacheStats();
    
    /**
     * 清除所有缓存
     */
    void clearAllCache();
    
    /**
     * 缓存统计信息
     */
    class CacheStats {
        private long totalKeys;
        private long totalMemoryUsage;
        private double hitRate;
        private long hitCount;
        private long missCount;

        public CacheStats(long totalKeys, long totalMemoryUsage, double hitRate, long hitCount, long missCount) {
            this.totalKeys = totalKeys;
            this.totalMemoryUsage = totalMemoryUsage;
            this.hitRate = hitRate;
            this.hitCount = hitCount;
            this.missCount = missCount;
        }

        // Getters and Setters
        public long getTotalKeys() { return totalKeys; }
        public void setTotalKeys(long totalKeys) { this.totalKeys = totalKeys; }

        public long getTotalMemoryUsage() { return totalMemoryUsage; }
        public void setTotalMemoryUsage(long totalMemoryUsage) { this.totalMemoryUsage = totalMemoryUsage; }

        public double getHitRate() { return hitRate; }
        public void setHitRate(double hitRate) { this.hitRate = hitRate; }

        public long getHitCount() { return hitCount; }
        public void setHitCount(long hitCount) { this.hitCount = hitCount; }

        public long getMissCount() { return missCount; }
        public void setMissCount(long missCount) { this.missCount = missCount; }
    }
}
