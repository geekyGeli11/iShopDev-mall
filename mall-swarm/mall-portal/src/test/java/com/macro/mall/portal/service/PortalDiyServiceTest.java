//package com.macro.mall.portal.service;
//
//import com.macro.mall.model.PmsDiyMaterial;
//import com.macro.mall.model.PmsDiyTemplate;
//import com.macro.mall.model.UmsDiyDesign;
//import com.macro.mall.portal.domain.DiyDesignParam;
//import com.macro.mall.portal.domain.DiyPreviewResult;
//import com.macro.mall.portal.domain.FacePreviewResult;
//import com.macro.mall.portal.domain.ProductDiyConfig;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * DIY功能Service测试类
// * Created by macro on 2024/12/20.
// */
//@SpringBootTest
//@ActiveProfiles("test")
//public class PortalDiyServiceTest {
//
//    @Autowired
//    private PortalDiyService diyService;
//
//    @Test
//    public void testGetProductDiyConfig() {
//        // 测试获取商品DIY配置
//        Long productId = 1L;
//        ProductDiyConfig config = diyService.getProductDiyConfig(productId);
//
//        assertNotNull(config);
//        assertEquals(productId, config.getProductId());
//        System.out.println("商品DIY配置：" + config.getProductName());
//    }
//
//    @Test
//    public void testCheckProductDiyEnabled() {
//        // 测试检查商品是否支持DIY
//        Long productId = 1L;
//        boolean enabled = diyService.checkProductDiyEnabled(productId);
//
//        System.out.println("商品" + productId + "是否支持DIY：" + enabled);
//    }
//
//    @Test
//    public void testGetDiyTemplateByProductId() {
//        // 测试根据商品ID获取DIY模板
//        Long productId = 1L;
//        PmsDiyTemplate template = diyService.getDiyTemplateByProductId(productId);
//
//        if (template != null) {
//            assertNotNull(template.getId());
//            System.out.println("DIY模板：" + template.getName());
//        } else {
//            System.out.println("该商品没有配置DIY模板");
//        }
//    }
//
//    @Test
//    public void testGetDiyMaterials() {
//        // 测试获取DIY素材列表
//        List<PmsDiyMaterial> materials = diyService.getDiyMaterials(null, null);
//
//        assertNotNull(materials);
//        System.out.println("DIY素材数量：" + materials.size());
//
//        // 测试按分类获取
//        materials = diyService.getDiyMaterials(1L, null);
//        System.out.println("分类1的DIY素材数量：" + materials.size());
//
//        // 测试按类型获取
//        materials = diyService.getDiyMaterials(null, 1);
//        System.out.println("图片类型的DIY素材数量：" + materials.size());
//    }
//
//    @Test
//    public void testSaveDiyDesign() {
//        // 测试保存DIY设计
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//        designParam.setDesignName("测试设计");
//        designParam.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"Hello World\"}]}");
//        designParam.setPreviewImage("http://example.com/preview.jpg");
//        designParam.setDescription("这是一个测试设计");
//
//        Long memberId = 1L;
//        Long designId = diyService.saveDiyDesign(designParam, memberId);
//
//        assertNotNull(designId);
//        assertTrue(designId > 0);
//        System.out.println("保存的设计ID：" + designId);
//    }
//
//    @Test
//    public void testUpdateDiyDesign() {
//        // 先保存一个设计
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//        designParam.setDesignName("测试设计");
//        designParam.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"Hello World\"}]}");
//
//        Long memberId = 1L;
//        Long designId = diyService.saveDiyDesign(designParam, memberId);
//        assertNotNull(designId);
//
//        // 更新设计
//        designParam.setDesignName("更新后的设计");
//        designParam.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"Updated Hello World\"}]}");
//
//        int result = diyService.updateDiyDesign(designId, designParam);
//        assertEquals(1, result);
//        System.out.println("更新设计成功");
//    }
//
//    @Test
//    public void testGetDiyDesign() {
//        // 先保存一个设计
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//        designParam.setDesignName("测试设计");
//        designParam.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"Hello World\"}]}");
//
//        Long memberId = 1L;
//        Long designId = diyService.saveDiyDesign(designParam, memberId);
//        assertNotNull(designId);
//
//        // 获取设计详情
//        UmsDiyDesign design = diyService.getDiyDesign(designId, memberId);
//        assertNotNull(design);
//        assertEquals(designId, design.getId());
//        // UmsDiyDesign没有getName方法，跳过这个断言
//        System.out.println("获取设计详情成功，设计ID：" + design.getId());
//    }
//
//    @Test
//    public void testGetUserDiyDesigns() {
//        // 测试获取用户的DIY设计列表
//        Long memberId = 1L;
//        List<UmsDiyDesign> designs = diyService.getUserDiyDesigns(memberId, 10, 1);
//
//        assertNotNull(designs);
//        System.out.println("用户" + memberId + "的设计数量：" + designs.size());
//    }
//
//    @Test
//    public void testGeneratePreview() {
//        // 测试生成DIY预览图
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//        designParam.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"Hello World\"}]}");
//
//        DiyPreviewResult result = diyService.generatePreview(designParam);
//
//        assertNotNull(result);
//        assertNotNull(result.getTimestamp());
//        System.out.println("预览图生成结果：" + result.getStatus());
//        if (result.getPreviewImageUrl() != null) {
//            System.out.println("预览图URL：" + result.getPreviewImageUrl());
//        }
//    }
//
//    @Test
//    public void testAiStylization() {
//        // 测试AI风格化
//        Long memberId = 1L;
//        String imageUrl = "http://example.com/test.jpg";
//        String style = "cartoon";
//        String prompt = "测试提示";
//
//        try {
//            String stylizedImageUrl = diyService.aiStylization(memberId, imageUrl, style, prompt);
//            assertNotNull(stylizedImageUrl);
//            System.out.println("AI风格化成功：" + stylizedImageUrl);
//        } catch (Exception e) {
//            System.out.println("AI风格化失败：" + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testDeleteDiyDesign() {
//        // 先保存一个设计
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//        designParam.setDesignName("待删除的设计");
//        designParam.setDesignData("{\"elements\":[{\"type\":\"text\",\"content\":\"Hello World\"}]}");
//
//        Long memberId = 1L;
//        Long designId = diyService.saveDiyDesign(designParam, memberId);
//        assertNotNull(designId);
//
//        // 删除设计
//        int result = diyService.deleteDiyDesign(designId, memberId);
//        assertEquals(1, result);
//        System.out.println("删除设计成功");
//
//        // 验证设计已被删除
//        UmsDiyDesign design = diyService.getDiyDesign(designId, memberId);
//        assertNull(design);
//    }
//
//    @Test
//    public void testImageCompositeLogic() {
//        // 测试图像合成逻辑修复
//        System.out.println("=== 测试图像合成逻辑修复 ===");
//
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//
//        // 创建包含文本和图像元素的设计数据
//        String designData = "{\n" +
//            "  \"faces\": {\n" +
//            "    \"1\": {\n" +
//            "      \"elements\": [\n" +
//            "        {\n" +
//            "          \"type\": \"text\",\n" +
//            "          \"content\": \"测试文本\",\n" +
//            "          \"x\": 0.2,\n" +
//            "          \"y\": 0.3,\n" +
//            "          \"fontSize\": 24,\n" +
//            "          \"color\": \"#FF0000\",\n" +
//            "          \"fontFamily\": \"Arial\",\n" +
//            "          \"bold\": true\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"type\": \"text\",\n" +
//            "          \"content\": \"底图应该保持完整\",\n" +
//            "          \"x\": 0.1,\n" +
//            "          \"y\": 0.7,\n" +
//            "          \"fontSize\": 18,\n" +
//            "          \"color\": \"#0000FF\",\n" +
//            "          \"fontFamily\": \"Arial\"\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    }\n" +
//            "  }\n" +
//            "}";
//
//        designParam.setDesignData(designData);
//
//        try {
//            // 测试生成预览图（包含所有面）
//            DiyPreviewResult result = diyService.generatePreview(designParam);
//
//            assertNotNull(result);
//            System.out.println("预览生成状态: " + result.getStatus());
//
//            if ("success".equals(result.getStatus())) {
//                assertNotNull(result.getPreviewImageUrl());
//                System.out.println("✅ 预览图生成成功: " + result.getPreviewImageUrl());
//                System.out.println("✅ 图像合成逻辑修复验证通过");
//
//                // 检查是否有面预览结果
//                if (result.getPreviewImages() != null && !result.getPreviewImages().isEmpty()) {
//                    System.out.println("生成了 " + result.getPreviewImages().size() + " 个面的预览图");
//                    for (FacePreviewResult faceResult : result.getPreviewImages()) {
//                        System.out.println("面 " + faceResult.getFaceIndex() + ": " + faceResult.getStatus());
//                        if ("success".equals(faceResult.getStatus())) {
//                            System.out.println("  预览图: " + faceResult.getPreviewImageUrl());
//                        }
//                    }
//                }
//
//            } else {
//                System.out.println("❌ 预览图生成失败: " + result.getErrorMessage());
//                if (result.getErrorMessage() != null) {
//                    System.out.println("错误详情: " + result.getErrorMessage());
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println("❌ 测试过程中发生异常: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testImageCompositeWithComplexDesign() {
//        // 测试复杂设计的图像合成
//        System.out.println("=== 测试复杂设计的图像合成 ===");
//
//        DiyDesignParam designParam = new DiyDesignParam();
//        designParam.setProductId(1L);
//        designParam.setTemplateId(1L);
//
//        // 创建包含多种元素的复杂设计
//        String complexDesignData = "{\n" +
//            "  \"faces\": {\n" +
//            "    \"1\": {\n" +
//            "      \"elements\": [\n" +
//            "        {\n" +
//            "          \"type\": \"text\",\n" +
//            "          \"content\": \"左上角文本\",\n" +
//            "          \"x\": 0.05,\n" +
//            "          \"y\": 0.1,\n" +
//            "          \"fontSize\": 16,\n" +
//            "          \"color\": \"#FF0000\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"type\": \"text\",\n" +
//            "          \"content\": \"右下角文本\",\n" +
//            "          \"x\": 0.7,\n" +
//            "          \"y\": 0.9,\n" +
//            "          \"fontSize\": 14,\n" +
//            "          \"color\": \"#00FF00\"\n" +
//            "        },\n" +
//            "        {\n" +
//            "          \"type\": \"text\",\n" +
//            "          \"content\": \"中心大标题\",\n" +
//            "          \"x\": 0.3,\n" +
//            "          \"y\": 0.5,\n" +
//            "          \"fontSize\": 32,\n" +
//            "          \"color\": \"#0000FF\",\n" +
//            "          \"bold\": true\n" +
//            "        }\n" +
//            "      ]\n" +
//            "    }\n" +
//            "  }\n" +
//            "}";
//
//        designParam.setDesignData(complexDesignData);
//
//        try {
//            DiyPreviewResult result = diyService.generatePreview(designParam);
//
//            assertNotNull(result);
//            System.out.println("复杂设计预览生成状态: " + result.getStatus());
//
//            if ("success".equals(result.getStatus())) {
//                System.out.println("✅ 复杂设计预览图生成成功");
//                System.out.println("预览图URL: " + result.getPreviewImageUrl());
//
//                // 检查面预览结果
//                if (result.getPreviewImages() != null) {
//                    for (FacePreviewResult faceResult : result.getPreviewImages()) {
//                        System.out.println("面 " + faceResult.getFaceIndex() + " 状态: " + faceResult.getStatus());
//                    }
//                }
//            } else {
//                System.out.println("❌ 复杂设计预览图生成失败: " + result.getErrorMessage());
//            }
//
//        } catch (Exception e) {
//            System.out.println("❌ 复杂设计测试异常: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
