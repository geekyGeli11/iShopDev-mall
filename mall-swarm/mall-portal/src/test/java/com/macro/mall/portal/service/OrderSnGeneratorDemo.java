package com.macro.mall.portal.service;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.portal.service.impl.OmsPortalOrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 订单编号生成器演示程序
 * 展示不同环境下的订单编号生成效果
 */
public class OrderSnGeneratorDemo {

    @Test
    public void demonstrateOrderSnGeneration() {
        System.out.println("=== 订单编号生成器演示 ===\n");
        
        // 创建模拟的服务实例
        OmsPortalOrderServiceImpl orderService = new OmsPortalOrderServiceImpl();
        RedisService redisService = mock(RedisService.class);
        
        // 设置必要的配置属性
        ReflectionTestUtils.setField(orderService, "REDIS_KEY_ORDER_ID", "order:id:");
        ReflectionTestUtils.setField(orderService, "REDIS_DATABASE", "0");
        ReflectionTestUtils.setField(orderService, "redisService", redisService);
        
        // Mock Redis 自增操作，模拟不同的自增值
        when(redisService.incr(anyString(), anyLong())).thenReturn(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        
        // 创建测试订单
        OmsOrder order = new OmsOrder();
        order.setSourceType(1); // 平台类型：1-PC端
        order.setPayType(1);    // 支付方式：1-微信支付
        
        // 测试不同环境
        String[] environments = {"prod", "dev", "test", "staging", null};
        String[] envNames = {"生产环境", "开发环境", "测试环境", "预发布环境", "未配置环境"};
        
        for (int i = 0; i < environments.length; i++) {
            System.out.println("【" + envNames[i] + "】");
            System.out.println("配置值: " + (environments[i] == null ? "null" : environments[i]));
            
            // 设置环境
            ReflectionTestUtils.setField(orderService, "activeProfile", environments[i]);
            
            // 生成订单编号
            String orderSn = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
            System.out.println("订单编号: " + orderSn);
            
            // 分析订单编号结构
            analyzeOrderSn(orderSn);
            System.out.println();
        }
        
        // 演示不同平台和支付方式
        System.out.println("=== 不同平台和支付方式演示 ===\n");
        ReflectionTestUtils.setField(orderService, "activeProfile", "dev");
        
        int[][] combinations = {
            {1, 1}, // PC端 + 微信支付
            {2, 2}, // 移动端 + 支付宝
            {3, 3}, // 小程序 + 银联支付
            {1, 2}, // PC端 + 支付宝
            {2, 1}  // 移动端 + 微信支付
        };
        
        String[] platformNames = {"", "PC端", "移动端", "小程序"};
        String[] paymentNames = {"", "微信支付", "支付宝", "银联支付"};
        
        for (int[] combo : combinations) {
            order.setSourceType(combo[0]);
            order.setPayType(combo[1]);
            
            String orderSn = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
            System.out.println(platformNames[combo[0]] + " + " + paymentNames[combo[1]] + ": " + orderSn);
        }
        
        System.out.println("\n=== 演示完成 ===");
    }
    
    /**
     * 分析订单编号结构
     */
    private static void analyzeOrderSn(String orderSn) {
        if (orderSn.startsWith("TEST_")) {
            System.out.println("前缀: TEST_ (测试环境标识)");
            orderSn = orderSn.substring(5); // 去掉 TEST_ 前缀
        } else {
            System.out.println("前缀: 无 (生产环境)");
        }
        
        if (orderSn.startsWith("GHZ")) {
            System.out.println("标识: GHZ (广恒州商城)");
            String remaining = orderSn.substring(3);
            
            if (remaining.length() >= 8) {
                String date = remaining.substring(0, 8);
                System.out.println("日期: " + date + " (" + 
                    date.substring(0, 4) + "年" + 
                    date.substring(4, 6) + "月" + 
                    date.substring(6, 8) + "日)");
                
                if (remaining.length() >= 10) {
                    String platform = remaining.substring(8, 10);
                    System.out.println("平台: " + platform + " (01=PC端, 02=移动端, 03=小程序)");
                    
                    if (remaining.length() >= 12) {
                        String payment = remaining.substring(10, 12);
                        System.out.println("支付: " + payment + " (01=微信, 02=支付宝, 03=银联)");
                        
                        if (remaining.length() > 12) {
                            String sequence = remaining.substring(12);
                            System.out.println("序号: " + sequence + " (当日自增序号)");
                        }
                    }
                }
            }
        }
    }
}
