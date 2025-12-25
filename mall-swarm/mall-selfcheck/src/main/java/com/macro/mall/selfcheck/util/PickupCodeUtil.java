package com.macro.mall.selfcheck.util;

import com.macro.mall.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 核销码管理工具类（Selfcheck模块）
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Component
public class PickupCodeUtil {

    private static final String REDIS_KEY_PICKUP_CODE = "pickup_code:";
    private static final String REDIS_KEY_ORDER_PICKUP = "order_pickup:";
    private static final String REDIS_KEY_PICKUP_USED = "pickup_used:";
    private static final long PICKUP_CODE_EXPIRE = 30 * 24 * 3600; // 30天过期

    @Autowired
    private RedisService redisService;

    /**
     * 根据核销码获取订单ID
     * 
     * @param pickupCode 核销码
     * @return 订单ID
     */
    public Long getOrderId(String pickupCode) {
        if (pickupCode == null || pickupCode.trim().isEmpty()) {
            return null;
        }

        String key = REDIS_KEY_PICKUP_CODE + pickupCode;
        try {
            Object orderIdObj = redisService.get(key);
            if (orderIdObj != null) {
                if (orderIdObj instanceof Number) {
                    return ((Number) orderIdObj).longValue();
                } else {
                    return Long.valueOf(orderIdObj.toString());
                }
            }
        } catch (Exception e) {
            log.warn("获取订单ID缓存失败，核销码：{}，错误：{}", pickupCode, e.getMessage());
        }
        return null;
    }

    /**
     * 验证核销码格式
     * 
     * @param pickupCode 核销码
     * @return 是否格式正确
     */
    public boolean isValidFormat(String pickupCode) {
        if (pickupCode == null || pickupCode.trim().isEmpty()) {
            return false;
        }

        // 格式：6位数字，总长度6位
        return pickupCode.matches("^\\d{6}$");
    }

    /**
     * 检查核销码是否存在
     * 
     * @param pickupCode 核销码
     * @return 是否存在
     */
    public boolean isCodeExists(String pickupCode) {
        if (pickupCode == null || pickupCode.trim().isEmpty()) {
            return false;
        }

        String key = REDIS_KEY_PICKUP_CODE + pickupCode;
        return redisService.hasKey(key);
    }

    /**
     * 验证核销码是否有效（格式正确且存在）
     * 
     * @param pickupCode 核销码
     * @return 是否有效
     */
    public boolean isValidCode(String pickupCode) {
        return isValidFormat(pickupCode) && isCodeExists(pickupCode);
    }

    /**
     * 检查核销码是否已使用
     * 
     * @param pickupCode 核销码
     * @return 是否已使用
     */
    public boolean isCodeUsed(String pickupCode) {
        if (pickupCode == null || pickupCode.trim().isEmpty()) {
            return false;
        }

        String key = REDIS_KEY_PICKUP_USED + pickupCode;
        return redisService.hasKey(key);
    }

    /**
     * 标记核销码为已使用
     * 
     * @param pickupCode 核销码
     * @param operator 操作员
     * @return 是否成功
     */
    public boolean markCodeAsUsed(String pickupCode, String operator) {
        if (pickupCode == null || pickupCode.trim().isEmpty()) {
            return false;
        }

        try {
            String key = REDIS_KEY_PICKUP_USED + pickupCode;
            String value = operator + ":" + System.currentTimeMillis();
            redisService.set(key, value, PICKUP_CODE_EXPIRE);
            
            log.info("标记核销码为已使用，核销码：{}，操作员：{}", pickupCode, operator);
            return true;
        } catch (Exception e) {
            log.error("标记核销码为已使用失败，核销码：{}，操作员：{}，错误：{}", pickupCode, operator, e.getMessage());
            return false;
        }
    }

    /**
     * 检查核销码是否可以使用（存在且未使用）
     * 
     * @param pickupCode 核销码
     * @return 是否可以使用
     */
    public boolean canUseCode(String pickupCode) {
        return isValidCode(pickupCode) && !isCodeUsed(pickupCode);
    }
} 