package com.macro.mall.portal.service.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.macro.mall.portal.config.AliyunAiConfig;
import com.macro.mall.portal.service.AliyunWanxService;
import com.macro.mall.portal.util.OssUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;

/**
 * 阿里云万相API服务实现类
 * Created by macro on 2024/12/20.
 */
@Service
public class AliyunWanxServiceImpl implements AliyunWanxService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunWanxServiceImpl.class);
    
    @Autowired
    private AliyunAiConfig aliyunAiConfig;
    
    @Autowired
    private OssUploadUtil ossUploadUtil;
    
    @Override
    public String stylizeImage(String imageUrl, String fullPrompt, String functionType) {
        try {
            LOGGER.info("调用万相API进行图片风格化，原图：{}，完整提示词：{}，功能类型：{}", imageUrl, fullPrompt, functionType);

            // 获取API密钥
            String apiKey = aliyunAiConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new RuntimeException("阿里云万相API密钥未配置");
            }

            // 检查并预处理图片（确保尺寸符合要求）
            String processedImageUrl = preprocessImage(imageUrl);

            // 使用传递的 functionType，如果为空则使用智能选择
            String finalFunctionType;
            if (functionType != null && !functionType.trim().isEmpty()) {
                finalFunctionType = functionType.trim();
                LOGGER.info("使用指定的功能类型：{}", finalFunctionType);
            } else {
                // 如果没有指定 functionType，回退到智能选择
                FunctionSelection selection = selectOptimalFunction(fullPrompt);
                finalFunctionType = selection.functionName;
                LOGGER.info("智能选择API功能：{}，原因：{}", finalFunctionType, selection.reason);
            }

            // 构建万相API请求参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("n", 1);  // 生成1张图片
            parameters.put("strength", 0.5);  // 图像修改幅度，0.5为默认值

            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .apiKey(apiKey)
                    .model("wanx2.1-imageedit")  // 使用最新的万相2.1模型
                    .function(finalFunctionType)  // 使用指定的或智能选择的功能
                    .prompt(fullPrompt)  // 使用完整的提示词（包含风格描述和用户提示词）
                    .baseImageUrl(processedImageUrl)  // 使用预处理后的图片URL
                    .parameters(parameters)
                    .build();

            // 调用万相API
            ImageSynthesis imageSynthesis = new ImageSynthesis();
            ImageSynthesisResult result = imageSynthesis.call(param);

            LOGGER.info("万相API调用结果：{}", JsonUtils.toJson(result));

            if (result == null || result.getOutput() == null) {
                throw new RuntimeException("万相API返回结果为空");
            }

            // 解析结果
            var output = result.getOutput();

            // 检查任务状态
            String taskStatus = output.getTaskStatus();
            if (!"SUCCEEDED".equals(taskStatus)) {
                String errorCode = output.getCode();
                String errorMessage = output.getMessage();
                LOGGER.error("万相API处理失败，状态：{}，错误码：{}，错误信息：{}", taskStatus, errorCode, errorMessage);
                throw new RuntimeException("万相API处理失败：" + errorMessage);
            }

            // 获取生成结果
            var results = output.getResults();
            if (results == null || results.isEmpty()) {
                throw new RuntimeException("万相API未返回生成结果");
            }

            // 从结果中获取图片URL
            Map<String, String> firstResult = results.get(0);
            String generatedImageUrl = firstResult.get("url");
            if (generatedImageUrl == null || generatedImageUrl.isEmpty()) {
                throw new RuntimeException("万相API返回的图片URL为空");
            }

            LOGGER.info("万相API生成图片成功：{}", generatedImageUrl);

            // 下载生成的图片并上传到OSS
            String stylizedImageUrl = downloadAndUploadToOss(generatedImageUrl, "stylized_" + System.currentTimeMillis() + ".jpg");

            return stylizedImageUrl;

        } catch (NoApiKeyException e) {
            LOGGER.error("阿里云API密钥未配置", e);
            throw new RuntimeException("AI风格化服务配置错误：API密钥未配置");
        } catch (ApiException e) {
            LOGGER.error("阿里云万相API调用失败，错误码：{}，错误信息：{}", e.getStatus(), e.getMessage(), e);

            // 根据具体错误码提供更友好的错误信息
            String errorMessage = "万相API调用失败";
            if (e.getMessage().contains("InvalidParameter")) {
                errorMessage = "输入参数错误，请检查图片格式和尺寸";
            } else if (e.getMessage().contains("height") || e.getMessage().contains("width")) {
                errorMessage = "图片尺寸不符合要求，请使用512-4096像素的图片";
            } else if (e.getMessage().contains("format")) {
                errorMessage = "图片格式不支持，请使用JPG、PNG等常见格式";
            }

            throw new RuntimeException(errorMessage + "：" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("万相API调用异常", e);
            throw new RuntimeException("万相API调用异常：" + e.getMessage());
        }
    }

    /**
     * 智能选择最优的API功能
     * 根据提示词中的关键字智能选择对应的功能类型
     */
    private FunctionSelection selectOptimalFunction(String prompt) {
        String lowerPrompt = prompt.toLowerCase();

        // 1. 检查是否匹配全局风格化
        if (isGlobalStylizationMatch(lowerPrompt)) {
            return new FunctionSelection(
                "stylization_all",
                "匹配全局风格化关键字"
            );
        }

        // 2. 检查是否匹配局部风格化
        if (isLocalStylizationMatch(lowerPrompt)) {
            return new FunctionSelection(
                "stylization_local",
                "匹配局部风格化关键字"
            );
        }

        // 3. 检查是否匹配局部重绘
        if (isDescriptionEditWithMaskMatch(lowerPrompt)) {
            return new FunctionSelection(
                "description_edit_with_mask",
                "匹配局部重绘关键字"
            );
        }

        // 4. 检查是否匹配去文字水印
        if (isRemoveWatermarkMatch(lowerPrompt)) {
            return new FunctionSelection(
                "remove_watermark",
                "匹配去文字水印关键字"
            );
        }

        // 5. 检查是否匹配扩图
        if (isExpandMatch(lowerPrompt)) {
            return new FunctionSelection(
                "expand",
                "匹配扩图关键字"
            );
        }

        // 6. 检查是否匹配图像超分
        if (isSuperResolutionMatch(lowerPrompt)) {
            return new FunctionSelection(
                "super_resolution",
                "匹配图像超分关键字"
            );
        }

        // 7. 检查是否匹配图像上色
        if (isColorizationMatch(lowerPrompt)) {
            return new FunctionSelection(
                "colorization",
                "匹配图像上色关键字"
            );
        }

        // 8. 检查是否匹配线稿生图
        if (isDoodleMatch(lowerPrompt)) {
            return new FunctionSelection(
                "doodle",
                "匹配线稿生图关键字"
            );
        }

        // 9. 检查是否匹配参考卡通形象
        if (isControlCartoonFeatureMatch(lowerPrompt)) {
            return new FunctionSelection(
                "control_cartoon_feature",
                "匹配参考卡通形象关键字"
            );
        }

        // 10. 默认使用指令编辑（最灵活，支持任意风格和编辑）
        return new FunctionSelection(
            "description_edit",
            "默认使用指令编辑功能"
        );
    }



    @Override
    public boolean isServiceAvailable() {
        try {
            String apiKey = aliyunAiConfig.getApiKey();
            return apiKey != null && !apiKey.trim().isEmpty();
        } catch (Exception e) {
            LOGGER.error("检查万相API服务状态失败", e);
            return false;
        }
    }

    /**
     * 预处理图片，确保尺寸符合万相API要求
     * 万相API要求：图片高度和宽度都需要在512-4096像素之间
     */
    private String preprocessImage(String imageUrl) {
        try {
            LOGGER.info("开始图片预处理：{}", imageUrl);

            // 检查图片尺寸
            BufferedImage image = ImageIO.read(new URL(imageUrl));
            if (image == null) {
                LOGGER.warn("无法读取图片，使用原图URL：{}", imageUrl);
                return imageUrl;
            }

            int width = image.getWidth();
            int height = image.getHeight();

            LOGGER.info("原图尺寸：{}x{}", width, height);

            // 检查尺寸是否符合万相API要求（512-4096像素）
            if (width >= 512 && width <= 4096 && height >= 512 && height <= 4096) {
                LOGGER.info("图片尺寸符合要求，直接使用原图");
                return imageUrl;
            }

            // 如果尺寸不符合要求，进行缩放处理
            LOGGER.info("图片尺寸不符合要求（512-4096像素），当前尺寸：{}x{}，开始缩放处理", width, height);

            // 计算缩放比例
            double scale = 1.0;
            if (width < 512 || height < 512) {
                // 尺寸过小，需要放大
                double scaleX = 512.0 / width;
                double scaleY = 512.0 / height;
                scale = Math.max(scaleX, scaleY); // 取较大的缩放比，确保两个维度都≥512
                LOGGER.info("图片尺寸过小，需要放大，缩放比例：{}", scale);
            } else if (width > 4096 || height > 4096) {
                // 尺寸过大，需要缩小
                double scaleX = 4096.0 / width;
                double scaleY = 4096.0 / height;
                scale = Math.min(scaleX, scaleY); // 取较小的缩放比，确保两个维度都≤4096
                LOGGER.info("图片尺寸过大，需要缩小，缩放比例：{}", scale);
            }

            int newWidth = (int) Math.round(width * scale);
            int newHeight = (int) Math.round(height * scale);

            LOGGER.info("缩放后尺寸：{}x{}", newWidth, newHeight);

            // 创建缩放后的图片
            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = scaledImage.createGraphics();

            // 设置高质量渲染
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制缩放后的图片
            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            // 上传缩放后的图片到OSS
            String scaledImageUrl = uploadScaledImageToOSS(scaledImage);
            LOGGER.info("缩放后的图片已上传到OSS：{}", scaledImageUrl);

            return scaledImageUrl;

        } catch (Exception e) {
            LOGGER.error("图片预处理失败：{}", imageUrl, e);
            // 如果预处理失败，返回原图URL
            return imageUrl;
        }
    }

    /**
     * 上传缩放后的图片到OSS
     */
    private String uploadScaledImageToOSS(BufferedImage image) throws Exception {
        // 将BufferedImage转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        LOGGER.info("缩放后的图片大小：{} KB", imageBytes.length / 1024);

        // 生成唯一文件名
        String fileName = "scaled_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + ".png";

        // 上传到OSS
        String ossUrl = ossUploadUtil.uploadBytes(imageBytes, fileName, "image/png");

        return ossUrl;
    }
    
    /**
     * 下载图片并上传到OSS
     */
    private String downloadAndUploadToOss(String imageUrl, String fileName) {
        try {
            LOGGER.info("开始下载图片：{}", imageUrl);
            
            // 下载图片
            URL url = new URL(imageUrl);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            
            byte[] imageData = outputStream.toByteArray();
            inputStream.close();
            outputStream.close();
            
            LOGGER.info("图片下载完成，大小：{} bytes", imageData.length);
            
            // 上传到OSS
            String ossUrl = ossUploadUtil.uploadBytes(imageData, fileName, "image/jpeg");
            LOGGER.info("图片上传到OSS成功：{}", ossUrl);
            
            return ossUrl;
            
        } catch (Exception e) {
            LOGGER.error("下载并上传图片失败：{}", imageUrl, e);
            throw new RuntimeException("图片处理失败：" + e.getMessage());
        }
    }

    // ==================== 智能匹配方法 ====================

    /**
     * 检查是否匹配全局风格化
     * 支持：法国绘本风格、金箔艺术风格
     */
    private boolean isGlobalStylizationMatch(String prompt) {
        String[] keywords = {
            "转换成法国绘本风格", "法国绘本", "绘本风格",
            "转换成金箔艺术风格", "金箔艺术", "金箔风格",
            "french illustration", "gold foil art"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配局部风格化
     * 支持：冰雕、云朵、花灯、木板、青花瓷、毛茸茸、毛线、气球
     */
    private boolean isLocalStylizationMatch(String prompt) {
        String[] keywords = {
            "冰雕风格", "冰雕", "ice",
            "云朵风格", "云朵", "cloud",
            "花灯风格", "花灯", "chinese festive lantern",
            "木板风格", "木板", "wooden",
            "青花瓷风格", "青花瓷", "blue and white porcelain",
            "毛茸茸风格", "毛茸茸", "fluffy",
            "毛线风格", "毛线", "weaving",
            "气球风格", "气球", "balloon",
            "变成冰雕", "变成云朵", "变成花灯", "变成木板",
            "变成青花瓷", "变成毛茸茸", "变成毛线", "变成气球"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配局部重绘
     */
    private boolean isDescriptionEditWithMaskMatch(String prompt) {
        String[] keywords = {
            "局部重绘", "局部修改", "mask", "重绘区域"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配去文字水印
     */
    private boolean isRemoveWatermarkMatch(String prompt) {
        String[] keywords = {
            "去除文字", "去除水印", "去文字", "去水印",
            "remove watermark", "remove text"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配扩图
     */
    private boolean isExpandMatch(String prompt) {
        String[] keywords = {
            "扩图", "画布扩展", "扩展画布", "expand"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配图像超分
     */
    private boolean isSuperResolutionMatch(String prompt) {
        String[] keywords = {
            "图像超分", "超分辨率", "提升清晰度", "提高分辨率",
            "super resolution", "upscale"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配图像上色
     */
    private boolean isColorizationMatch(String prompt) {
        String[] keywords = {
            "图像上色", "黑白上色", "上色", "colorization", "colorize"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配线稿生图
     */
    private boolean isDoodleMatch(String prompt) {
        String[] keywords = {
            "线稿生图", "涂鸦", "线稿", "sketch", "doodle"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配参考卡通形象
     */
    private boolean isControlCartoonFeatureMatch(String prompt) {
        String[] keywords = {
            "卡通形象", "卡通人物", "cartoon character", "cartoon feature"
        };

        for (String keyword : keywords) {
            if (prompt.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 功能选择结果内部类
     */
    private static class FunctionSelection {
        final String functionName;
        final String reason;

        FunctionSelection(String functionName, String reason) {
            this.functionName = functionName;
            this.reason = reason;
        }
    }
}
