package com.macro.mall.portal.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.model.PmsDiyMaterial;
import com.macro.mall.model.PmsDiyTemplate;
import com.macro.mall.portal.domain.ProductDiyConfig;
import com.macro.mall.portal.service.DIYCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * DIY缓存服务实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class DIYCacheServiceImpl implements DIYCacheService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DIYCacheServiceImpl.class);
    
    // 缓存键前缀
    private static final String CACHE_PREFIX = "diy:";
    private static final String MATERIAL_PREFIX = CACHE_PREFIX + "material:";
    private static final String TEMPLATE_PREFIX = CACHE_PREFIX + "template:";
    private static final String PRODUCT_CONFIG_PREFIX = CACHE_PREFIX + "product:";
    private static final String USER_DESIGN_PREFIX = CACHE_PREFIX + "user:design:";
    private static final String AI_RESULT_PREFIX = CACHE_PREFIX + "ai:result:";
    private static final String PREVIEW_PREFIX = CACHE_PREFIX + "preview:";
    
    // 缓存过期时间（秒）
    private static final long MATERIAL_CACHE_EXPIRE = 3600; // 1小时
    private static final long TEMPLATE_CACHE_EXPIRE = 7200; // 2小时
    private static final long PRODUCT_CONFIG_CACHE_EXPIRE = 1800; // 30分钟
    private static final long USER_DESIGN_CACHE_EXPIRE = 86400; // 24小时
    private static final long AI_RESULT_CACHE_EXPIRE = 604800; // 7天
    private static final long PREVIEW_CACHE_EXPIRE = 3600; // 1小时
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public void cacheDiyMaterials(Long categoryId, List<PmsDiyMaterial> materials) {
        try {
            String key = MATERIAL_PREFIX + categoryId;
            redisTemplate.opsForValue().set(key, materials, MATERIAL_CACHE_EXPIRE, TimeUnit.SECONDS);
            LOGGER.debug("缓存DIY素材列表: categoryId={}, count={}", categoryId, materials.size());
        } catch (Exception e) {
            LOGGER.error("缓存DIY素材列表失败", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<PmsDiyMaterial> getCachedDiyMaterials(Long categoryId) {
        try {
            String key = MATERIAL_PREFIX + categoryId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return (List<PmsDiyMaterial>) cached;
            }
        } catch (Exception e) {
            LOGGER.error("获取缓存的DIY素材列表失败", e);
        }
        return null;
    }
    
    @Override
    public void clearDiyMaterialsCache(Long categoryId) {
        try {
            String key = MATERIAL_PREFIX + categoryId;
            redisTemplate.delete(key);
            LOGGER.debug("清除DIY素材缓存: categoryId={}", categoryId);
        } catch (Exception e) {
            LOGGER.error("清除DIY素材缓存失败", e);
        }
    }
    
    @Override
    public void clearAllDiyMaterialsCache() {
        try {
            Set<String> keys = redisTemplate.keys(MATERIAL_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                LOGGER.debug("清除所有DIY素材缓存: count={}", keys.size());
            }
        } catch (Exception e) {
            LOGGER.error("清除所有DIY素材缓存失败", e);
        }
    }
    
    @Override
    public void cacheDiyTemplate(Long templateId, PmsDiyTemplate template) {
        try {
            String key = TEMPLATE_PREFIX + templateId;
            redisTemplate.opsForValue().set(key, template, TEMPLATE_CACHE_EXPIRE, TimeUnit.SECONDS);
            LOGGER.debug("缓存DIY模板: templateId={}", templateId);
        } catch (Exception e) {
            LOGGER.error("缓存DIY模板失败", e);
        }
    }
    
    @Override
    public PmsDiyTemplate getCachedDiyTemplate(Long templateId) {
        try {
            String key = TEMPLATE_PREFIX + templateId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return (PmsDiyTemplate) cached;
            }
        } catch (Exception e) {
            LOGGER.error("获取缓存的DIY模板失败", e);
        }
        return null;
    }
    
    @Override
    public void clearDiyTemplateCache(Long templateId) {
        try {
            String key = TEMPLATE_PREFIX + templateId;
            redisTemplate.delete(key);
            LOGGER.debug("清除DIY模板缓存: templateId={}", templateId);
        } catch (Exception e) {
            LOGGER.error("清除DIY模板缓存失败", e);
        }
    }
    
    @Override
    public void clearAllDiyTemplateCache() {
        try {
            Set<String> keys = redisTemplate.keys(TEMPLATE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                LOGGER.debug("清除所有DIY模板缓存: count={}", keys.size());
            }
        } catch (Exception e) {
            LOGGER.error("清除所有DIY模板缓存失败", e);
        }
    }
    
    @Override
    public void cacheProductDiyConfig(Long productId, ProductDiyConfig config) {
        try {
            String key = PRODUCT_CONFIG_PREFIX + productId;
            redisTemplate.opsForValue().set(key, config, PRODUCT_CONFIG_CACHE_EXPIRE, TimeUnit.SECONDS);
            LOGGER.debug("缓存商品DIY配置: productId={}", productId);
        } catch (Exception e) {
            LOGGER.error("缓存商品DIY配置失败", e);
        }
    }
    
    @Override
    public ProductDiyConfig getCachedProductDiyConfig(Long productId) {
        try {
            String key = PRODUCT_CONFIG_PREFIX + productId;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return (ProductDiyConfig) cached;
            }
        } catch (Exception e) {
            LOGGER.error("获取缓存的商品DIY配置失败", e);
        }
        return null;
    }
    
    @Override
    public void clearProductDiyConfigCache(Long productId) {
        try {
            String key = PRODUCT_CONFIG_PREFIX + productId;
            redisTemplate.delete(key);
            LOGGER.debug("清除商品DIY配置缓存: productId={}", productId);
        } catch (Exception e) {
            LOGGER.error("清除商品DIY配置缓存失败", e);
        }
    }
    
    @Override
    public void cacheUserDesign(Long memberId, String designKey, Object designData) {
        try {
            String key = USER_DESIGN_PREFIX + memberId + ":" + designKey;
            redisTemplate.opsForValue().set(key, designData, USER_DESIGN_CACHE_EXPIRE, TimeUnit.SECONDS);
            LOGGER.debug("缓存用户设计数据: memberId={}, designKey={}", memberId, designKey);
        } catch (Exception e) {
            LOGGER.error("缓存用户设计数据失败", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCachedUserDesign(Long memberId, String designKey, Class<T> clazz) {
        try {
            String key = USER_DESIGN_PREFIX + memberId + ":" + designKey;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                if (clazz.isInstance(cached)) {
                    return (T) cached;
                } else {
                    // 尝试JSON转换
                    return objectMapper.convertValue(cached, clazz);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取缓存的用户设计数据失败", e);
        }
        return null;
    }
    
    @Override
    public void clearUserDesignCache(Long memberId, String designKey) {
        try {
            String key = USER_DESIGN_PREFIX + memberId + ":" + designKey;
            redisTemplate.delete(key);
            LOGGER.debug("清除用户设计缓存: memberId={}, designKey={}", memberId, designKey);
        } catch (Exception e) {
            LOGGER.error("清除用户设计缓存失败", e);
        }
    }
    
    @Override
    public void clearAllUserDesignCache(Long memberId) {
        try {
            String pattern = USER_DESIGN_PREFIX + memberId + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                LOGGER.debug("清除用户所有设计缓存: memberId={}, count={}", memberId, keys.size());
            }
        } catch (Exception e) {
            LOGGER.error("清除用户所有设计缓存失败", e);
        }
    }
    
    @Override
    public void cacheAiStylizationResult(String requestHash, String resultUrl) {
        try {
            String key = AI_RESULT_PREFIX + requestHash;
            redisTemplate.opsForValue().set(key, resultUrl, AI_RESULT_CACHE_EXPIRE, TimeUnit.SECONDS);
            LOGGER.debug("缓存AI风格化结果: requestHash={}", requestHash);
        } catch (Exception e) {
            LOGGER.error("缓存AI风格化结果失败", e);
        }
    }
    
    @Override
    public String getCachedAiStylizationResult(String requestHash) {
        try {
            String key = AI_RESULT_PREFIX + requestHash;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return (String) cached;
            }
        } catch (Exception e) {
            LOGGER.error("获取缓存的AI风格化结果失败", e);
        }
        return null;
    }
    
    @Override
    public void clearAiStylizationCache(String requestHash) {
        try {
            String key = AI_RESULT_PREFIX + requestHash;
            redisTemplate.delete(key);
            LOGGER.debug("清除AI风格化结果缓存: requestHash={}", requestHash);
        } catch (Exception e) {
            LOGGER.error("清除AI风格化结果缓存失败", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void cachePreviewResult(String designHash, List<String> previewUrls) {
        try {
            String key = PREVIEW_PREFIX + designHash;
            redisTemplate.opsForValue().set(key, previewUrls, PREVIEW_CACHE_EXPIRE, TimeUnit.SECONDS);
            LOGGER.debug("缓存预览图生成结果: designHash={}, count={}", designHash, previewUrls.size());
        } catch (Exception e) {
            LOGGER.error("缓存预览图生成结果失败", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getCachedPreviewResult(String designHash) {
        try {
            String key = PREVIEW_PREFIX + designHash;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return (List<String>) cached;
            }
        } catch (Exception e) {
            LOGGER.error("获取缓存的预览图生成结果失败", e);
        }
        return null;
    }
    
    @Override
    public void clearPreviewCache(String designHash) {
        try {
            String key = PREVIEW_PREFIX + designHash;
            redisTemplate.delete(key);
            LOGGER.debug("清除预览图缓存: designHash={}", designHash);
        } catch (Exception e) {
            LOGGER.error("清除预览图缓存失败", e);
        }
    }
    
    @Override
    public CacheStats getCacheStats() {
        try {
            Set<String> allKeys = redisTemplate.keys(CACHE_PREFIX + "*");
            long totalKeys = allKeys != null ? allKeys.size() : 0;
            
            // 这里可以添加更详细的统计信息
            // 由于Redis没有直接的内存使用和命中率统计，这里返回基本信息
            return new CacheStats(totalKeys, 0, 0.0, 0, 0);
            
        } catch (Exception e) {
            LOGGER.error("获取缓存统计信息失败", e);
            return new CacheStats(0, 0, 0.0, 0, 0);
        }
    }
    
    @Override
    public void clearAllCache() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                LOGGER.info("清除所有DIY缓存: count={}", keys.size());
            }
        } catch (Exception e) {
            LOGGER.error("清除所有DIY缓存失败", e);
        }
    }
}
