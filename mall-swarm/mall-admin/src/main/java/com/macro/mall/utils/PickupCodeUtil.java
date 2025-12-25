package com.macro.mall.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 核销码工具类
 * 用于验证和管理核销码
 */
@Component
public class PickupCodeUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PickupCodeUtil.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis key前缀
    private static final String PICKUP_CODE_PREFIX = "pickup_code:";
    private static final String ORDER_PICKUP_PREFIX = "order_pickup:";
    private static final String PICKUP_USED_PREFIX = "pickup_used:";
    
    // 核销码过期时间（30天）
    private static final long PICKUP_CODE_EXPIRE_TIME = 30 * 24 * 60 * 60;
    
    /**
     * 验证核销码格式（6位数字）
     */
    public boolean isValidFormat(String pickupCode) {
        if (pickupCode == null || pickupCode.length() != 6) {
            LOGGER.warn("核销码格式验证失败，核销码: {}, 长度: {}", pickupCode, pickupCode != null ? pickupCode.length() : "null");
            return false;
        }
        boolean isValid = pickupCode.matches("\\d{6}");
        LOGGER.info("核销码格式验证: {}, 结果: {}", pickupCode, isValid);
        return isValid;
    }
    
    /**
     * 检查核销码是否存在且有效
     */
    public boolean isValidCode(String pickupCode) {
        try {
            String key = PICKUP_CODE_PREFIX + pickupCode;
            LOGGER.info("检查核销码是否存在，Redis键: {}", key);
            
            Boolean exists = redisTemplate.hasKey(key);
            LOGGER.info("Redis hasKey 结果: {} (type: {})", exists, exists != null ? exists.getClass().getSimpleName() : "null");
            
            // 额外调试：尝试直接获取值
            Object value = redisTemplate.opsForValue().get(key);
            LOGGER.info("Redis 直接获取值: {} (type: {})", value, value != null ? value.getClass().getSimpleName() : "null");
            
            return exists != null && exists;
        } catch (Exception e) {
            LOGGER.error("检查核销码有效性失败，核销码: {}", pickupCode, e);
            return false;
        }
    }
    
    /**
     * 检查核销码是否已被使用
     */
    public boolean isCodeUsed(String pickupCode) {
        try {
            String key = PICKUP_USED_PREFIX + pickupCode;
            LOGGER.info("检查核销码是否已使用，Redis键: {}", key);
            
            Boolean exists = redisTemplate.hasKey(key);
            LOGGER.info("核销码已使用检查结果: {} (type: {})", exists, exists != null ? exists.getClass().getSimpleName() : "null");
            
            return exists != null && exists;
        } catch (Exception e) {
            LOGGER.error("检查核销码使用状态失败，核销码: {}", pickupCode, e);
            return false;
        }
    }
    
    /**
     * 根据核销码获取订单ID
     */
    public Long getOrderId(String pickupCode) {
        try {
            String key = PICKUP_CODE_PREFIX + pickupCode;
            LOGGER.info("获取订单ID，Redis键: {}", key);
            
            Object orderIdObj = redisTemplate.opsForValue().get(key);
            LOGGER.info("Redis 获取到的原始值: {} (type: {})", orderIdObj, orderIdObj != null ? orderIdObj.getClass().getSimpleName() : "null");
            
            if (orderIdObj != null) {
                Long orderId = Long.valueOf(orderIdObj.toString());
                LOGGER.info("转换后的订单ID: {}", orderId);
                return orderId;
            }
            
            LOGGER.warn("未找到核销码对应的订单ID，核销码: {}", pickupCode);
            return null;
        } catch (Exception e) {
            LOGGER.error("获取核销码对应订单ID失败，核销码: {}", pickupCode, e);
            return null;
        }
    }
    
    /**
     * 标记核销码为已使用
     */
    public boolean markCodeAsUsed(String pickupCode, String operator) {
        try {
            String usedKey = PICKUP_USED_PREFIX + pickupCode;
            String operatorInfo = operator + ":" + System.currentTimeMillis();
            
            LOGGER.info("标记核销码为已使用，Redis键: {}, 值: {}", usedKey, operatorInfo);
            
            redisTemplate.opsForValue().set(usedKey, operatorInfo, PICKUP_CODE_EXPIRE_TIME, TimeUnit.SECONDS);
            
            LOGGER.info("核销码已标记为使用，核销码: {}, 操作员: {}", pickupCode, operator);
            return true;
        } catch (Exception e) {
            LOGGER.error("标记核销码为已使用失败，核销码: {}, 操作员: {}", pickupCode, operator, e);
            return false;
        }
    }
} 