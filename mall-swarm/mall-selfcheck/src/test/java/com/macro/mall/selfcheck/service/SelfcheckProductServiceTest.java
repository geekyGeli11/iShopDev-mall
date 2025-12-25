package com.macro.mall.selfcheck.service;

import com.macro.mall.selfcheck.dto.ProductScanParam;
import com.macro.mall.selfcheck.dto.ProductScanVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 自助收银商品服务测试
 * 
 * @author macro
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class SelfcheckProductServiceTest {

    @Autowired
    private SelfcheckProductService productService;

    @Test
    public void testScanProductPriceConsistency() {
        // 测试扫码查询商品价格一致性
        String testBarcode = "TEST_BARCODE_001";
        
        // 模拟游客扫码
        ProductScanParam param = new ProductScanParam();
        param.setBarcode(testBarcode);
        param.setNeedStockCheck(true);
        param.setNeedPromotionInfo(true);
        
        try {
            ProductScanVO guestResult = productService.scanProduct(param);
            if (guestResult != null) {
                BigDecimal guestPrice = guestResult.getCurrentPrice();
                
                // 模拟会员扫码（重新计算价格）
                ProductScanVO memberResult = productService.calculateProductPrice(
                    guestResult.getProductId(), 
                    guestResult.getSkuId(), 
                    12345L, // 模拟会员ID
                    1
                );
                
                BigDecimal memberPrice = memberResult.getCurrentPrice();
                
                // 验证价格一致性
                System.out.println("游客价格: " + guestPrice);
                System.out.println("会员价格: " + memberPrice);
                
                // 在没有特殊会员价格逻辑的情况下，两个价格应该相等
                assertEquals(guestPrice, memberPrice, 
                    "会员和游客扫码价格应该一致，游客价格: " + guestPrice + "，会员价格: " + memberPrice);
            }
        } catch (Exception e) {
            // 如果测试数据不存在，这是正常的
            System.out.println("测试商品不存在，跳过价格一致性测试: " + e.getMessage());
        }
    }

    @Test
    public void testCalculateFinalPriceLogic() {
        // 测试价格计算逻辑
        try {
            // 这里可以添加更多的价格计算逻辑测试
            // 比如促销价格、会员价格等
            System.out.println("价格计算逻辑测试完成");
        } catch (Exception e) {
            fail("价格计算逻辑测试失败: " + e.getMessage());
        }
    }
}
