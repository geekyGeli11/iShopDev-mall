//package com.macro.mall.service;
//
//import com.macro.mall.dto.PmsProductDiyConfigVO;
//import com.macro.mall.model.PmsDiyTemplate;
//import com.macro.mall.model.PmsProduct;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * 商品DIY配置功能测试
// * Created by macro on 2024/12/20.
// */
//@SpringBootTest
//@ActiveProfiles("test")
//@Transactional
//public class PmsProductDiyConfigTest {
//
//    @Autowired
//    private PmsProductService productService;
//
//    @Autowired
//    private PmsDiyTemplateService templateService;
//
//    @Test
//    public void testGetDiyConfig() {
//        // 创建测试商品
//        PmsProduct product = createTestProduct();
//
//        // 获取DIY配置
//        PmsProduct diyConfig = productService.getDiyConfig(product.getId());
//        assertNotNull(diyConfig);
//        assertEquals(product.getId(), diyConfig.getId());
//        assertEquals(product.getDiyEnabled(), diyConfig.getDiyEnabled());
//        assertEquals(product.getDiyTemplateId(), diyConfig.getDiyTemplateId());
//    }
//
//    @Test
//    public void testGetDiyConfigDetail() {
//        // 创建测试模板
//        PmsDiyTemplate template = createTestTemplate();
//
//        // 创建测试商品并关联模板
//        PmsProduct product = createTestProduct();
//        productService.updateDiyConfig(product.getId(), (byte) 1, template.getId());
//
//        // 获取DIY配置详情
//        PmsProductDiyConfigVO configVO = productService.getDiyConfigDetail(product.getId());
//        assertNotNull(configVO);
//        assertEquals(product.getId(), configVO.getId());
//        assertEquals((byte) 1, configVO.getDiyEnabled());
//        assertEquals(template.getId(), configVO.getDiyTemplateId());
//        assertNotNull(configVO.getDiyTemplate());
//        assertEquals(template.getName(), configVO.getDiyTemplate().getName());
//    }
//
//    @Test
//    public void testUpdateDiyConfig() {
//        // 创建测试商品和模板
//        PmsProduct product = createTestProduct();
//        PmsDiyTemplate template = createTestTemplate();
//
//        // 更新DIY配置
//        int result = productService.updateDiyConfig(product.getId(), (byte) 1, template.getId());
//        assertEquals(1, result);
//
//        // 验证更新结果
//        PmsProduct updatedProduct = productService.getDiyConfig(product.getId());
//        assertEquals((byte) 1, updatedProduct.getDiyEnabled());
//        assertEquals(template.getId(), updatedProduct.getDiyTemplateId());
//    }
//
//    @Test
//    public void testUpdateDiyConfigDisable() {
//        // 创建测试商品
//        PmsProduct product = createTestProduct();
//
//        // 先启用DIY
//        productService.updateDiyConfig(product.getId(), (byte) 1, 1L);
//
//        // 禁用DIY
//        int result = productService.updateDiyConfig(product.getId(), (byte) 0, null);
//        assertEquals(1, result);
//
//        // 验证禁用结果
//        PmsProduct updatedProduct = productService.getDiyConfig(product.getId());
//        assertEquals((byte) 0, updatedProduct.getDiyEnabled());
//    }
//
//    @Test
//    public void testUpdateDiyStatus() {
//        // 创建多个测试商品
//        PmsProduct product1 = createTestProduct();
//        PmsProduct product2 = createTestProduct();
//
//        // 批量启用DIY
//        List<Long> ids = Arrays.asList(product1.getId(), product2.getId());
//        int result = productService.updateDiyStatus(ids, (byte) 1);
//        assertEquals(2, result);
//
//        // 验证批量更新结果
//        PmsProduct updatedProduct1 = productService.getDiyConfig(product1.getId());
//        PmsProduct updatedProduct2 = productService.getDiyConfig(product2.getId());
//        assertEquals((byte) 1, updatedProduct1.getDiyEnabled());
//        assertEquals((byte) 1, updatedProduct2.getDiyEnabled());
//
//        // 批量禁用DIY
//        result = productService.updateDiyStatus(ids, (byte) 0);
//        assertEquals(2, result);
//
//        // 验证批量禁用结果
//        updatedProduct1 = productService.getDiyConfig(product1.getId());
//        updatedProduct2 = productService.getDiyConfig(product2.getId());
//        assertEquals((byte) 0, updatedProduct1.getDiyEnabled());
//        assertEquals((byte) 0, updatedProduct2.getDiyEnabled());
//    }
//
//    @Test
//    public void testGetDiyConfigDetailWithoutTemplate() {
//        // 创建没有关联模板的商品
//        PmsProduct product = createTestProduct();
//        productService.updateDiyConfig(product.getId(), (byte) 1, null);
//
//        // 获取DIY配置详情
//        PmsProductDiyConfigVO configVO = productService.getDiyConfigDetail(product.getId());
//        assertNotNull(configVO);
//        assertEquals(product.getId(), configVO.getId());
//        assertEquals((byte) 1, configVO.getDiyEnabled());
//        assertNull(configVO.getDiyTemplateId());
//        assertNull(configVO.getDiyTemplate());
//    }
//
//    @Test
//    public void testGetDiyConfigDetailNonExistentProduct() {
//        // 测试获取不存在商品的DIY配置
//        PmsProductDiyConfigVO configVO = productService.getDiyConfigDetail(999999L);
//        assertNull(configVO);
//    }
//
//    /**
//     * 创建测试商品
//     */
//    private PmsProduct createTestProduct() {
//        PmsProduct product = new PmsProduct();
//        product.setName("测试商品");
//        product.setProductSn("TEST001");
//        product.setPrice(new java.math.BigDecimal("99.00"));
//        product.setOriginalPrice(new java.math.BigDecimal("99.00"));
//        product.setStock(100);
//        product.setLowStock(10);
//        product.setUnit("件");
//        product.setWeight(new java.math.BigDecimal("0.5"));
//        product.setSort(0);
//        product.setGiftGrowth(0);
//        product.setGiftPoint(0);
//        product.setUsePointLimit(0);
//        product.setSubTitle("测试商品副标题");
//        product.setDescription("测试商品描述");
//        product.setKeywords("测试");
//        product.setNote("测试备注");
//        product.setAlbumPics("http://test.com/pic1.jpg");
//        product.setDetailTitle("详情标题");
//        product.setDetailDesc("详情描述");
//        product.setDetailHtml("详情HTML");
//        product.setDetailMobileHtml("移动端详情HTML");
//        product.setPromotionStartTime(new java.util.Date());
//        product.setPromotionEndTime(new java.util.Date());
//        product.setPromotionPerLimit(1);
//        product.setPromotionType(0);
//        product.setBrandName("测试品牌");
//        product.setProductCategoryName("测试分类");
//        product.setPublishStatus(1);
//        product.setNewStatus(0);
//        product.setRecommandStatus(0);
//        product.setVerifyStatus(1);
//        product.setSale(0);
//        product.setPrice(new java.math.BigDecimal("99.00"));
//        product.setPromotionPrice(new java.math.BigDecimal("89.00"));
//        product.setFeightTemplateId(1L);
//        product.setProductCategoryId(1L);
//        product.setBrandId(1L);
//        product.setDiyEnabled((byte) 0);
//
//        // 这里应该调用productService.create()方法，但为了简化测试，直接设置ID
//        product.setId(System.currentTimeMillis());
//        return product;
//    }
//
//    /**
//     * 创建测试模板
//     */
//    private PmsDiyTemplate createTestTemplate() {
//        PmsDiyTemplate template = new PmsDiyTemplate();
//        template.setName("测试DIY模板");
//        template.setProductCategoryId(1L);
//        template.setDescription("测试模板描述");
//        template.setStatus((byte) 1);
//
//        templateService.create(template);
//        return template;
//    }
//}
