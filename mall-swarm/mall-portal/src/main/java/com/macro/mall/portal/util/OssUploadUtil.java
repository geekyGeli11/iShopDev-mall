package com.macro.mall.portal.util;

import com.macro.mall.portal.service.impl.CosServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * OSS文件上传工具类
 * Created by macro on 2024/12/20.
 */
@Component
public class OssUploadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(OssUploadUtil.class);

    @Autowired
    private CosServiceImpl cosService;
    
    /**
     * 上传字节数组到OSS
     * @param data 文件数据
     * @param fileName 文件名
     * @param contentType 文件类型
     * @return 文件URL
     */
    public String uploadBytes(byte[] data, String fileName, String contentType) {
        try {
            // 生成存储路径
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateDir = sdf.format(new Date());
            String subDir = "ai-stylization/" + dateDir;
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

            // 使用现有的COS服务上传文件
            String fileUrl = cosService.uploadBytesToCos(data, uniqueFileName, subDir);

            LOGGER.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            LOGGER.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传输入流到OSS
     * @param inputStream 输入流
     * @param fileName 文件名
     * @param contentType 文件类型
     * @return 文件URL
     */
    public String uploadStream(InputStream inputStream, String fileName, String contentType) {
        try {
            // 将输入流转换为字节数组
            byte[] data = inputStream.readAllBytes();

            // 调用字节数组上传方法
            return uploadBytes(data, fileName, contentType);

        } catch (Exception e) {
            LOGGER.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}
