//package com.macro.mall.portal.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.macro.mall.portal.domain.DiyDesignParam;
//import com.macro.mall.portal.service.impl.PortalDiyServiceImpl;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * DIY预览图生成功能调试测试
// * 用于验证图像合成逻辑是否正确工作
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class DIYPreviewDebugTest {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(DIYPreviewDebugTest.class);
//
//    @Autowired
//    private PortalDiyService diyService;
//
//    /**
//     * 测试预览图生成功能
//     */
//    @Test
//    public void testGeneratePreviewImages() {
//        try {
//            // 1. 构造测试数据
//            DiyDesignParam designParam = createTestDesignParam();
//
//            LOGGER.info("开始测试DIY预览图生成...");
//            LOGGER.info("测试参数: productId={}, templateId={}",
//                designParam.getProductId(), designParam.getTemplateId());
//
//            // 2. 调用预览图生成接口
//            var result = diyService.generateDiyPreview(designParam);
//
//            // 3. 验证结果
//            if (result != null && result.getCode() == 200) {
//                LOGGER.info("✅ 预览图生成成功!");
//                LOGGER.info("生成状态: {}", result.getData().getStatus());
//
//                if (result.getData().getPreviewImages() != null) {
//                    LOGGER.info("生成了 {} 个面的预览图:", result.getData().getPreviewImages().size());
//
//                    for (int i = 0; i < result.getData().getPreviewImages().size(); i++) {
//                        var faceResult = result.getData().getPreviewImages().get(i);
//                        LOGGER.info("面 {}: {} - {} - {}",
//                            i, faceResult.getFaceName(), faceResult.getStatus(), faceResult.getPreviewImageUrl());
//                    }
//                } else {
//                    LOGGER.warn("⚠️ 预览图列表为空");
//                }
//            } else {
//                LOGGER.error("❌ 预览图生成失败: {}",
//                    result != null ? result.getMessage() : "返回结果为空");
//            }
//
//        } catch (Exception e) {
//            LOGGER.error("❌ 测试过程中发生异常", e);
//        }
//    }
//
//    /**
//     * 创建测试用的设计参数
//     */
//    private DiyDesignParam createTestDesignParam() {
//        DiyDesignParam param = new DiyDesignParam();
//        param.setProductId(46L); // 使用已知的商品ID
//        param.setTemplateId(1L);  // 使用已知的模板ID
//
//        // 构造设计数据JSON
//        String designDataJson = createTestDesignData();
//        param.setDesignData(designDataJson);
//
//        return param;
//    }
//
//    /**
//     * 创建测试用的设计数据JSON
//     */
//    private String createTestDesignData() {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//
//            // 构造设计数据结构
//            var designData = mapper.createObjectNode();
//            var faces = mapper.createArrayNode();
//
//            // 添加第一个面的设计
//            var face1 = mapper.createObjectNode();
//            face1.put("id", 1);
//            face1.put("name", "正面");
//
//            var elements = mapper.createArrayNode();
//
//            // 添加一个测试图片元素
//            var imageElement = mapper.createObjectNode();
//            imageElement.put("id", "test_image_1");
//            imageElement.put("type", "image");
//            imageElement.put("src", "https://guanghengzou-mall.oss-cn-guangzhou.aliyuncs.com/static/diy/test-image.jpg");
//            imageElement.put("x", 0.2);  // 相对位置 20%
//            imageElement.put("y", 0.2);  // 相对位置 20%
//            imageElement.put("width", 0.6);   // 相对宽度 60%
//            imageElement.put("height", 0.6);  // 相对高度 60%
//            imageElement.put("keepAspectRatio", true);
//
//            elements.add(imageElement);
//
//            // 添加一个测试文本元素
//            var textElement = mapper.createObjectNode();
//            textElement.put("id", "test_text_1");
//            textElement.put("type", "text");
//            textElement.put("content", "测试文字");
//            textElement.put("x", 0.1);
//            textElement.put("y", 0.8);
//            textElement.put("fontSize", 24);
//            textElement.put("color", "#FF0000");
//            textElement.put("fontFamily", "Arial");
//
//            elements.add(textElement);
//
//            face1.set("elements", elements);
//            faces.add(face1);
//
//            designData.set("faces", faces);
//
//            return mapper.writeValueAsString(designData);
//
//        } catch (Exception e) {
//            LOGGER.error("创建测试设计数据失败", e);
//            return "{}";
//        }
//    }
//
//    /**
//     * 测试单独的图像合成逻辑
//     */
//    @Test
//    public void testImageCompositeLogic() {
//        LOGGER.info("开始测试图像合成逻辑...");
//
//        // 这里可以添加更详细的图像合成测试
//        // 比如测试不同尺寸的底图和用户设计图的合成效果
//
//        LOGGER.info("图像合成逻辑测试完成");
//    }
//}
