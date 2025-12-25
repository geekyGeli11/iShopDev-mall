package com.macro.mall.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 蒙版混合工具类
 * 实现基于蒙版的图像合成，支持正片叠底混合模式
 * 
 * @author macro
 * @date 2025/01/03
 */
public class MaskBlendUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MaskBlendUtil.class);
    
    /**
     * 使用蒙版进行图像合成(正片叠底模式)
     *
     * 实现逻辑:
     * 1. 将设计图和蒙版缩放到底图尺寸
     * 2. 创建处理后的设计图：
     *    - 蒙版黑色区域(可定制区域) → 使用设计图
     *    - 蒙版白色区域(不可定制区域) → 使用白色
     * 3. 对整个图像进行正片叠底混合
     *
     * 蒙版说明：
     * - 黑色区域(值=0) = 可定制区域 → 使用设计图 → 正片叠底后显示设计图与底图的混合
     * - 白色区域(值=255) = 不可定制区域 → 使用白色 → 正片叠底后保持底图不变
     *
     * 正片叠底公式: result = (base * processed) / 255
     * - 当 processed = 白色(255) 时: result = (base * 255) / 255 = base (保持底图)
     * - 当 processed = 设计图 时: result = (base * design) / 255 (混合效果)
     *
     * 注意: 蒙版图片本身不会出现在最终结果中，它只用于控制哪些区域显示设计图
     *
     * @param baseImage 底图(商品图)
     * @param designImage 设计图(用户DIY图，已经是白色画布+定制区域设计图的组合)
     * @param maskImage 蒙版图片(黑白图，黑色=可定制区域，白色=不可定制区域)
     * @return 合成后的图像
     */
    public static BufferedImage blendWithMask(
        BufferedImage baseImage,
        BufferedImage designImage,
        BufferedImage maskImage
    ) {
        LOGGER.info("🎨 开始蒙版混合 - 底图尺寸: {}x{}, 设计图尺寸: {}x{}, 蒙版尺寸: {}x{}",
            baseImage.getWidth(), baseImage.getHeight(),
            designImage.getWidth(), designImage.getHeight(),
            maskImage.getWidth(), maskImage.getHeight());

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();

        // 1. 缩放蒙版到底图尺寸 (设计图已经是底图尺寸)
        BufferedImage scaledMask = scaleImage(maskImage, width, height);

        LOGGER.info("✅ 蒙版缩放完成");

        // 2. 创建处理后的设计图
        // 关键: 根据蒙版决定每个像素使用设计图还是白色
        BufferedImage processedDesign = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取蒙版灰度值(0-255)
                int maskRGB = scaledMask.getRGB(x, y);
                Color maskColor = new Color(maskRGB);
                int maskValue = maskColor.getRed();  // 灰度值

                if (maskValue < 128) {  // 黑色区域(可定制区域) - 使用设计图
                    processedDesign.setRGB(x, y, designImage.getRGB(x, y));
                } else {  // 白色区域(不可定制区域) - 使用白色
                    processedDesign.setRGB(x, y, 0xFFFFFF);  // 白色
                }
            }
        }

        LOGGER.info("✅ 蒙版应用完成 - 黑色区域使用设计图，白色区域使用白色");

        // 3. 正片叠底混合
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseRGB = baseImage.getRGB(x, y);
                int processedRGB = processedDesign.getRGB(x, y);

                Color baseColor = new Color(baseRGB);
                Color processedColor = new Color(processedRGB);

                // 正片叠底公式: result = (base * processed) / 255
                int r = (baseColor.getRed() * processedColor.getRed()) / 255;
                int g = (baseColor.getGreen() * processedColor.getGreen()) / 255;
                int b = (baseColor.getBlue() * processedColor.getBlue()) / 255;

                Color resultColor = new Color(r, g, b);
                result.setRGB(x, y, resultColor.getRGB());
            }
        }

        LOGGER.info("✅ 正片叠底混合完成 - 蒙版图片未出现在最终结果中");

        return result;
    }
    
    /**
     * 应用蒙版到设计图
     * 蒙版区域(黑色)使用设计图，非蒙版区域(白色)使用白色
     * 
     * @param designImage 设计图
     * @param maskImage 蒙版图
     * @return 处理后的设计图
     */
    private static BufferedImage applyMaskToDesign(
        BufferedImage designImage,
        BufferedImage maskImage
    ) {
        int width = designImage.getWidth();
        int height = designImage.getHeight();
        
        BufferedImage result = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB
        );
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 获取蒙版灰度值(0-255)
                int maskRGB = maskImage.getRGB(x, y);
                Color maskColor = new Color(maskRGB);
                int maskValue = maskColor.getRed();  // 灰度值
                
                if (maskValue < 128) {  // 黑色区域(蒙版区域)
                    // 使用设计图
                    result.setRGB(x, y, designImage.getRGB(x, y));
                } else {  // 白色区域(非蒙版区域)
                    // 使用白色
                    result.setRGB(x, y, 0xFFFFFFFF);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 正片叠底混合
     * 公式: result = (base * blend) / 255
     * 
     * 正片叠底是一种常用的图层混合模式，可以产生自然的融合效果
     * 
     * @param base 底图
     * @param blend 混合图
     * @return 混合后的图像
     */
    private static BufferedImage multiplyBlend(
        BufferedImage base,
        BufferedImage blend
    ) {
        int width = base.getWidth();
        int height = base.getHeight();
        
        BufferedImage result = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB
        );
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int baseRGB = base.getRGB(x, y);
                int blendRGB = blend.getRGB(x, y);
                
                Color baseColor = new Color(baseRGB);
                Color blendColor = new Color(blendRGB);
                
                // 正片叠底公式: result = (base * blend) / 255
                int r = (baseColor.getRed() * blendColor.getRed()) / 255;
                int g = (baseColor.getGreen() * blendColor.getGreen()) / 255;
                int b = (baseColor.getBlue() * blendColor.getBlue()) / 255;
                
                // 保持alpha通道
                int a = Math.min(baseColor.getAlpha(), blendColor.getAlpha());
                
                Color resultColor = new Color(r, g, b, a);
                result.setRGB(x, y, resultColor.getRGB());
            }
        }
        
        return result;
    }
    
    /**
     * 缩放图片
     * 
     * @param image 原图
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 缩放后的图片
     */
    private static BufferedImage scaleImage(
        BufferedImage image,
        int targetWidth,
        int targetHeight
    ) {
        // 如果尺寸已经匹配，直接返回
        if (image.getWidth() == targetWidth && image.getHeight() == targetHeight) {
            return image;
        }
        
        BufferedImage scaled = new BufferedImage(
            targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = scaled.createGraphics();
        
        // 设置高质量渲染
        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );
        g2d.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        );
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        
        g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return scaled;
    }
    
    /**
     * 检查图片是否为有效的蒙版图片
     * 蒙版图片应该是黑白图
     * 
     * @param maskImage 蒙版图片
     * @return 是否为有效蒙版
     */
    public static boolean isValidMask(BufferedImage maskImage) {
        if (maskImage == null) {
            return false;
        }
        
        // 采样检查：检查图片的几个点是否为灰度值
        int width = maskImage.getWidth();
        int height = maskImage.getHeight();
        
        int sampleCount = 10;
        for (int i = 0; i < sampleCount; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            
            Color color = new Color(maskImage.getRGB(x, y));
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            
            // 检查是否为灰度值(R=G=B)
            if (Math.abs(r - g) > 10 || Math.abs(g - b) > 10 || Math.abs(r - b) > 10) {
                LOGGER.warn("蒙版图片不是灰度图，采样点({},{})的RGB值为({},{},{})", x, y, r, g, b);
                return false;
            }
        }
        
        return true;
    }
}

