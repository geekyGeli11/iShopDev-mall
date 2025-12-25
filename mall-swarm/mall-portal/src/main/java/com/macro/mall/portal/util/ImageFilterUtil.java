package com.macro.mall.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * 图像滤镜工具类
 * 实现图像合成时的各种滤镜效果，提升视觉真实感
 * 
 * @author macro
 * @date 2025/01/27
 */
public class ImageFilterUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageFilterUtil.class);
    
    /**
     * 应用完整的滤镜效果到海报区域
     * 包括边缘融合、亮度对比度调整、阴影效果、颜色协调、环境光反射
     * 
     * @param baseImage 底图
     * @param posterImage 海报图（已缩放到目标尺寸）
     * @param x 海报在底图上的X坐标
     * @param y 海报在底图上的Y坐标
     * @return 应用滤镜后的合成图像
     */
    public static BufferedImage applyPosterFilter(BufferedImage baseImage, BufferedImage posterImage, int x, int y) {
        try {
            LOGGER.debug("开始应用海报滤镜效果: 底图={}x{}, 海报={}x{}, 位置=({},{})",
                baseImage.getWidth(), baseImage.getHeight(),
                posterImage.getWidth(), posterImage.getHeight(), x, y);
            
            // 创建结果图像
            BufferedImage result = new BufferedImage(
                baseImage.getWidth(), 
                baseImage.getHeight(), 
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = result.createGraphics();
            
            // 绘制底图
            g2d.drawImage(baseImage, 0, 0, null);
            
            int posterWidth = posterImage.getWidth();
            int posterHeight = posterImage.getHeight();
            
            // 1. 边缘融合 - 对海报边缘进行高斯模糊
            BufferedImage edgeBlendedPoster = applyEdgeBlending(posterImage);
            
            // 2. 调整亮度和对比度
            BufferedImage adjustedPoster = adjustBrightnessContrast(edgeBlendedPoster, 0.95f, -5);
            
            // 3. 添加阴影效果
            applyShadowEffect(g2d, x, y, posterWidth, posterHeight);
            
            // 4. 绘制调整后的海报
            g2d.drawImage(adjustedPoster, x, y, null);
            
            g2d.dispose();
            
            // 5. 整体颜色协调（HSV调整）
            result = adjustSaturation(result, 0.98f);
            
            // 6. 添加环境光反射
            result = applyEnvironmentReflection(result, x, y, posterWidth, posterHeight);
            
            LOGGER.debug("海报滤镜效果应用完成");
            return result;
            
        } catch (Exception e) {
            LOGGER.error("应用海报滤镜失败，返回原始合成", e);
            // 失败时返回简单合成
            BufferedImage fallback = new BufferedImage(
                baseImage.getWidth(), 
                baseImage.getHeight(), 
                BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = fallback.createGraphics();
            g2d.drawImage(baseImage, 0, 0, null);
            g2d.drawImage(posterImage, x, y, null);
            g2d.dispose();
            return fallback;
        }
    }
    
    /**
     * 边缘融合 - 对图像边缘进行高斯模糊处理
     */
    private static BufferedImage applyEdgeBlending(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();
        
        // 创建边缘蒙版（中心为1.0，边缘渐变到0.8）
        float[][] edgeMask = createEdgeMask(width, height, 15);
        
        // 应用轻微的整体模糊
        BufferedImage blurred = applyGaussianBlur(image, 3);
        
        // 根据蒙版混合原图和模糊图
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalRGB = image.getRGB(x, y);
                int blurredRGB = blurred.getRGB(x, y);
                
                float maskValue = edgeMask[y][x];
                int blendedRGB = blendPixels(originalRGB, blurredRGB, maskValue);
                
                result.setRGB(x, y, blendedRGB);
            }
        }
        
        g2d.dispose();
        return result;
    }
    
    /**
     * 创建边缘蒙版
     */
    private static float[][] createEdgeMask(int width, int height, int edgeWidth) {
        float[][] mask = new float[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 计算到边缘的最小距离
                int distToEdge = Math.min(
                    Math.min(x, width - 1 - x),
                    Math.min(y, height - 1 - y)
                );
                
                // 在边缘区域内进行渐变
                if (distToEdge < edgeWidth) {
                    mask[y][x] = (float) distToEdge / edgeWidth;
                } else {
                    mask[y][x] = 1.0f;
                }
            }
        }
        
        return mask;
    }
    
    /**
     * 高斯模糊
     */
    private static BufferedImage applyGaussianBlur(BufferedImage image, int radius) {
        if (radius <= 0) {
            return image;
        }
        
        // 创建高斯核
        int size = radius * 2 + 1;
        float[] kernelData = createGaussianKernel(size);
        
        Kernel kernel = new Kernel(size, size, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        // 创建临时图像以避免透明度问题
        BufferedImage temp = new BufferedImage(
            image.getWidth(), 
            image.getHeight(), 
            BufferedImage.TYPE_INT_ARGB
        );
        
        return op.filter(image, temp);
    }
    
    /**
     * 创建高斯核
     */
    private static float[] createGaussianKernel(int size) {
        float[] kernel = new float[size * size];
        float sigma = size / 3.0f;
        float sum = 0.0f;
        int center = size / 2;
        
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int dx = x - center;
                int dy = y - center;
                float value = (float) Math.exp(-(dx * dx + dy * dy) / (2 * sigma * sigma));
                kernel[y * size + x] = value;
                sum += value;
            }
        }
        
        // 归一化
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }
        
        return kernel;
    }
    
    /**
     * 混合两个像素
     */
    private static int blendPixels(int rgb1, int rgb2, float ratio) {
        int a1 = (rgb1 >> 24) & 0xFF;
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;
        
        int a2 = (rgb2 >> 24) & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;
        
        int a = (int) (a1 * ratio + a2 * (1 - ratio));
        int r = (int) (r1 * ratio + r2 * (1 - ratio));
        int g = (int) (g1 * ratio + g2 * (1 - ratio));
        int b = (int) (b1 * ratio + b2 * (1 - ratio));
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    /**
     * 调整亮度和对比度
     * 
     * @param image 原始图像
     * @param contrast 对比度系数（0.95表示降低5%对比度）
     * @param brightness 亮度调整值（-5表示降低5个亮度级别）
     */
    private static BufferedImage adjustBrightnessContrast(BufferedImage image, float contrast, int brightness) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                
                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // 应用对比度和亮度调整
                r = clamp((int) (r * contrast + brightness));
                g = clamp((int) (g * contrast + brightness));
                b = clamp((int) (b * contrast + brightness));
                
                int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRGB);
            }
        }
        
        return result;
    }
    
    /**
     * 添加阴影效果
     */
    private static void applyShadowEffect(Graphics2D g2d, int x, int y, int width, int height) {
        // 创建阴影区域（稍微扩大）
        int shadowOffset = 5;
        int shadowX = Math.max(0, x - shadowOffset);
        int shadowY = Math.max(0, y - shadowOffset);
        int shadowWidth = width + shadowOffset * 2;
        int shadowHeight = height + shadowOffset * 2;

        // 创建径向渐变阴影
        float shadowAlpha = 0.08f;

        // 绘制多层阴影以实现模糊效果
        for (int i = 10; i >= 0; i--) {
            float alpha = shadowAlpha * (1.0f - i / 10.0f);
            g2d.setColor(new Color(0, 0, 0, alpha));
            g2d.fillRect(
                shadowX - i,
                shadowY - i,
                shadowWidth + i * 2,
                shadowHeight + i * 2
            );
        }
    }

    /**
     * 调整饱和度（HSV色彩空间）
     *
     * @param image 原始图像
     * @param saturationFactor 饱和度系数（0.98表示降低2%饱和度）
     */
    private static BufferedImage adjustSaturation(BufferedImage image, float saturationFactor) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                int a = (rgb >> 24) & 0xFF;
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // RGB转HSV
                float[] hsv = rgbToHsv(r, g, b);

                // 调整饱和度
                hsv[1] = clamp(hsv[1] * saturationFactor, 0.0f, 1.0f);

                // HSV转回RGB
                int[] newRgb = hsvToRgb(hsv[0], hsv[1], hsv[2]);

                int newRGB = (a << 24) | (newRgb[0] << 16) | (newRgb[1] << 8) | newRgb[2];
                result.setRGB(x, y, newRGB);
            }
        }

        return result;
    }

    /**
     * 添加环境光反射效果
     */
    private static BufferedImage applyEnvironmentReflection(BufferedImage image, int x, int y, int width, int height) {
        BufferedImage result = new BufferedImage(
            image.getWidth(),
            image.getHeight(),
            BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, null);

        // 创建反射蒙版
        float reflectionIntensity = 0.03f;

        // 在海报区域添加轻微的白色叠加
        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                int imgX = x + px;
                int imgY = y + py;

                if (imgX >= 0 && imgX < image.getWidth() && imgY >= 0 && imgY < image.getHeight()) {
                    int rgb = image.getRGB(imgX, imgY);

                    int a = (rgb >> 24) & 0xFF;
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    // 添加环境光（轻微增亮）
                    r = clamp((int) (r + 255 * reflectionIntensity));
                    g = clamp((int) (g + 255 * reflectionIntensity));
                    b = clamp((int) (b + 255 * reflectionIntensity));

                    int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
                    result.setRGB(imgX, imgY, newRGB);
                }
            }
        }

        g2d.dispose();
        return result;
    }

    /**
     * RGB转HSV
     */
    private static float[] rgbToHsv(int r, int g, int b) {
        float rf = r / 255.0f;
        float gf = g / 255.0f;
        float bf = b / 255.0f;

        float max = Math.max(rf, Math.max(gf, bf));
        float min = Math.min(rf, Math.min(gf, bf));
        float delta = max - min;

        float h = 0;
        float s = 0;
        float v = max;

        if (delta > 0.00001f) {
            s = delta / max;

            if (rf == max) {
                h = (gf - bf) / delta;
            } else if (gf == max) {
                h = 2 + (bf - rf) / delta;
            } else {
                h = 4 + (rf - gf) / delta;
            }

            h *= 60;
            if (h < 0) {
                h += 360;
            }
        }

        return new float[]{h, s, v};
    }

    /**
     * HSV转RGB
     */
    private static int[] hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float rf = 0, gf = 0, bf = 0;

        if (h >= 0 && h < 60) {
            rf = c; gf = x; bf = 0;
        } else if (h >= 60 && h < 120) {
            rf = x; gf = c; bf = 0;
        } else if (h >= 120 && h < 180) {
            rf = 0; gf = c; bf = x;
        } else if (h >= 180 && h < 240) {
            rf = 0; gf = x; bf = c;
        } else if (h >= 240 && h < 300) {
            rf = x; gf = 0; bf = c;
        } else if (h >= 300 && h < 360) {
            rf = c; gf = 0; bf = x;
        }

        int r = clamp((int) ((rf + m) * 255));
        int g = clamp((int) ((gf + m) * 255));
        int b = clamp((int) ((bf + m) * 255));

        return new int[]{r, g, b};
    }

    /**
     * 限制值在0-255范围内
     */
    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * 限制浮点值在指定范围内
     */
    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

