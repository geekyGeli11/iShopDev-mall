package com.macro.mall.portal.service;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.portal.service.impl.OmsPortalOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 订单服务测试类
 * 测试订单编号生成功能
 */
public class OmsPortalOrderServiceTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private OmsPortalOrderServiceImpl orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置必要的配置属性
        ReflectionTestUtils.setField(orderService, "REDIS_KEY_ORDER_ID", "order:id:");
        ReflectionTestUtils.setField(orderService, "REDIS_DATABASE", "0");
        
        // Mock Redis 自增操作
        when(redisService.incr(anyString(), anyLong())).thenReturn(1L, 2L, 3L, 4L);
    }

    /**
     * 测试开发环境订单编号生成（应该包含TEST_前缀）
     */
    @Test
    public void testGenerateOrderSnInDevEnvironment() throws Exception {
        // 设置开发环境
        ReflectionTestUtils.setField(orderService, "activeProfile", "dev");
        
        // 创建测试订单
        OmsOrder order = new OmsOrder();
        order.setSourceType(1); // 平台类型
        order.setPayType(1);    // 支付方式
        
        // 使用反射调用私有方法
        String orderSn = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
        
        // 验证订单编号格式
        assertNotNull(orderSn);
        assertTrue(orderSn.startsWith("TEST_GHZ"), "开发环境订单编号应该以TEST_GHZ开头");
        assertTrue(orderSn.length() >= 19, "订单编号长度应该至少19位（TEST_GHZ + 8位日期 + 2位平台 + 2位支付 + 6位自增）");
        
        System.out.println("开发环境订单编号: " + orderSn);
    }

    /**
     * 测试生产环境订单编号生成（不应该包含TEST_前缀）
     */
    @Test
    public void testGenerateOrderSnInProdEnvironment() throws Exception {
        // 设置生产环境
        ReflectionTestUtils.setField(orderService, "activeProfile", "prod");
        
        // 创建测试订单
        OmsOrder order = new OmsOrder();
        order.setSourceType(1); // 平台类型
        order.setPayType(1);    // 支付方式
        
        // 使用反射调用私有方法
        String orderSn = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
        
        // 验证订单编号格式
        assertNotNull(orderSn);
        assertTrue(orderSn.startsWith("GHZ"), "生产环境订单编号应该以GHZ开头");
        assertFalse(orderSn.startsWith("TEST_"), "生产环境订单编号不应该包含TEST_前缀");
        assertTrue(orderSn.length() >= 14, "订单编号长度应该至少14位（GHZ + 8位日期 + 2位平台 + 2位支付 + 6位自增）");
        
        System.out.println("生产环境订单编号: " + orderSn);
    }

    /**
     * 测试测试环境订单编号生成（应该包含TEST_前缀）
     */
    @Test
    public void testGenerateOrderSnInTestEnvironment() throws Exception {
        // 设置测试环境
        ReflectionTestUtils.setField(orderService, "activeProfile", "test");
        
        // 创建测试订单
        OmsOrder order = new OmsOrder();
        order.setSourceType(2); // 平台类型
        order.setPayType(2);    // 支付方式
        
        // 使用反射调用私有方法
        String orderSn = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
        
        // 验证订单编号格式
        assertNotNull(orderSn);
        assertTrue(orderSn.startsWith("TEST_GHZ"), "测试环境订单编号应该以TEST_GHZ开头");
        assertTrue(orderSn.length() >= 19, "订单编号长度应该至少19位");
        
        System.out.println("测试环境订单编号: " + orderSn);
    }

    /**
     * 测试订单编号的唯一性
     */
    @Test
    public void testOrderSnUniqueness() throws Exception {
        // 设置开发环境
        ReflectionTestUtils.setField(orderService, "activeProfile", "dev");
        
        // 创建测试订单
        OmsOrder order = new OmsOrder();
        order.setSourceType(1);
        order.setPayType(1);
        
        // 生成多个订单编号
        String orderSn1 = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
        String orderSn2 = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
        
        // 验证唯一性
        assertNotEquals(orderSn1, orderSn2, "连续生成的订单编号应该不同");
        
        System.out.println("订单编号1: " + orderSn1);
        System.out.println("订单编号2: " + orderSn2);
    }

    /**
     * 测试空环境配置（默认为非生产环境）
     */
    @Test
    public void testGenerateOrderSnWithNullProfile() throws Exception {
        // 设置空环境（模拟配置缺失）
        ReflectionTestUtils.setField(orderService, "activeProfile", null);
        
        // 创建测试订单
        OmsOrder order = new OmsOrder();
        order.setSourceType(1);
        order.setPayType(1);
        
        // 使用反射调用私有方法
        String orderSn = (String) ReflectionTestUtils.invokeMethod(orderService, "generateOrderSn", order);
        
        // 验证订单编号格式（null不等于"prod"，所以应该有TEST_前缀）
        assertNotNull(orderSn);
        assertTrue(orderSn.startsWith("TEST_GHZ"), "空环境配置应该被视为测试环境，订单编号应该以TEST_GHZ开头");
        
        System.out.println("空环境配置订单编号: " + orderSn);
    }
}
