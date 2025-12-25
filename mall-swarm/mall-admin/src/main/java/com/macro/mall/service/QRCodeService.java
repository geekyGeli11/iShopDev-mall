package com.macro.mall.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成服务
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class QRCodeService {

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;

    /**
     * 生成二维码Base64图片
     * 
     * @param content 二维码内容
     * @return Base64格式的图片数据
     */
    public String generateQRCodeBase64(String content) {
        return generateQRCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成二维码Base64图片
     * 
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return Base64格式的图片数据
     */
    public String generateQRCodeBase64(String content, int width, int height) {
        try {
            // 设置编码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); // 边距设置为1

            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 创建BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            // 转换为Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.info("生成二维码成功，内容：{}，大小：{}x{}", content, width, height);
            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("生成二维码失败，内容：{}，错误：{}", content, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 为核销码生成二维码
     * 
     * @param pickupCode 核销码
     * @return Base64格式的二维码图片
     */
    public String generatePickupCodeQR(String pickupCode) {
        if (pickupCode == null || pickupCode.trim().isEmpty()) {
            return null;
        }

        // 构建二维码内容，使用JSON格式便于扫码识别
        String qrContent = String.format("{\"type\":\"pickup_code\",\"code\":\"%s\",\"timestamp\":%d}", 
                pickupCode, System.currentTimeMillis());
        
        return generateQRCodeBase64(qrContent);
    }

    /**
     * 生成二维码（兼容方法）
     * 
     * @param content 二维码内容
     * @return Base64格式的二维码图片
     */
    public String generateQRCode(String content) {
        return generateQRCodeBase64(content);
    }
} 