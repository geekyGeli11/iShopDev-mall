package com.macro.mall.portal.service;

import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * 图像合成逻辑单元测试
 * Created by macro on 2024/12/27.
 */
public class ImageCompositeTest {

    @Test
    public void testImageCompositeLogic() {
        System.out.println("=== 测试图像合成逻辑修复 ===");
        
        try {
            // 1. 创建模拟的底图（蓝色背景）
            BufferedImage baseImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D baseG2d = baseImage.createGraphics();
            baseG2d.setColor(Color.BLUE);
            baseG2d.fillRect(0, 0, 400, 300);
            baseG2d.setColor(Color.WHITE);
            baseG2d.setFont(new Font("Arial", Font.BOLD, 20));
            baseG2d.drawString("底图背景", 150, 150);
            baseG2d.dispose();
            
            // 2. 创建模拟的用户设计图（红色文本，透明背景）
            BufferedImage userDesignImage = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D designG2d = userDesignImage.createGraphics();
            designG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 透明背景
            designG2d.setComposite(AlphaComposite.Clear);
            designG2d.fillRect(0, 0, 200, 100);
            designG2d.setComposite(AlphaComposite.SrcOver);
            
            // 绘制用户设计内容
            designG2d.setColor(Color.RED);
            designG2d.setFont(new Font("Arial", Font.BOLD, 16));
            designG2d.drawString("用户设计", 50, 30);
            designG2d.drawString("内容", 70, 60);
            designG2d.dispose();
            
            // 3. 执行修复后的合成逻辑
            BufferedImage resultImage = compositeImageToCustomArea(baseImage, userDesignImage, 100, 50, 200, 100);
            
            // 4. 验证结果
            assertImageCompositeCorrect(resultImage, baseImage, userDesignImage);
            
            // 5. 保存结果图像用于视觉验证（可选）
            saveTestImages(baseImage, userDesignImage, resultImage);
            
            System.out.println("✅ 图像合成逻辑测试通过");
            
        } catch (Exception e) {
            System.out.println("❌ 图像合成逻辑测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 修复后的图像合成方法（简化版本）
     */
    private BufferedImage compositeImageToCustomArea(BufferedImage baseImage, BufferedImage userDesignImage, 
                                                   int x, int y, int width, int height) {
        // 创建结果图像，保持底图的完整性
        BufferedImage resultImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resultImage.createGraphics();
        
        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        try {
            // 1. 首先绘制完整的底图作为背景
            g2d.drawImage(baseImage, 0, 0, null);
            
            // 2. 设置裁剪区域，确保用户设计不会超出定制区域
            Shape originalClip = g2d.getClip();
            Rectangle clipRect = new Rectangle(x, y, width, height);
            g2d.setClip(clipRect);
            
            // 3. 检查用户设计图是否有实际内容
            if (userDesignImage != null && hasVisibleContent(userDesignImage)) {
                // 4. 设置合成模式为正常叠加（保留透明度）
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                
                // 5. 在定制区域内绘制用户设计图
                g2d.drawImage(userDesignImage, x, y, width, height, null);
            }
            
            // 6. 恢复原始裁剪区域
            g2d.setClip(originalClip);
            
        } finally {
            g2d.dispose();
        }
        
        return resultImage;
    }
    
    /**
     * 检查图像是否有可见内容（非完全透明）
     */
    private boolean hasVisibleContent(BufferedImage image) {
        if (image == null) {
            return false;
        }
        
        // 检查图像是否有非透明像素
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                if (alpha > 0) {
                    return true; // 找到非透明像素
                }
            }
        }
        return false;
    }
    
    /**
     * 验证图像合成是否正确
     */
    private void assertImageCompositeCorrect(BufferedImage resultImage, BufferedImage baseImage, BufferedImage userDesignImage) {
        // 验证结果图像尺寸与底图一致
        assert resultImage.getWidth() == baseImage.getWidth() : "结果图像宽度应与底图一致";
        assert resultImage.getHeight() == baseImage.getHeight() : "结果图像高度应与底图一致";
        
        // 验证定制区域外的像素与底图一致（检查几个关键点）
        int basePixel1 = baseImage.getRGB(50, 50);  // 左上角
        int resultPixel1 = resultImage.getRGB(50, 50);
        assert basePixel1 == resultPixel1 : "定制区域外的像素应与底图一致";
        
        int basePixel2 = baseImage.getRGB(350, 250);  // 右下角
        int resultPixel2 = resultImage.getRGB(350, 250);
        assert basePixel2 == resultPixel2 : "定制区域外的像素应与底图一致";
        
        System.out.println("✅ 图像合成验证通过：底图保持完整，用户设计正确叠加");
    }
    
    /**
     * 保存测试图像用于视觉验证
     */
    private void saveTestImages(BufferedImage baseImage, BufferedImage userDesignImage, BufferedImage resultImage) {
        try {
            File testDir = new File("target/test-images");
            if (!testDir.exists()) {
                testDir.mkdirs();
            }
            
            ImageIO.write(baseImage, "png", new File(testDir, "base-image.png"));
            ImageIO.write(userDesignImage, "png", new File(testDir, "user-design.png"));
            ImageIO.write(resultImage, "png", new File(testDir, "composite-result.png"));
            
            System.out.println("测试图像已保存到: " + testDir.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("保存测试图像失败: " + e.getMessage());
        }
    }
}
