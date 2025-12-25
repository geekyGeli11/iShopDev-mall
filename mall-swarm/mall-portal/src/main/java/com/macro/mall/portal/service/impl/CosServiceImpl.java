package com.macro.mall.portal.service.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * COS服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Service
public class CosServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CosServiceImpl.class);

    @Value("${tencent.cos.secretId}")
    private String secretId;

    @Value("${tencent.cos.secretKey}")
    private String secretKey;

    @Value("${tencent.cos.bucketName}")
    private String bucketName;

    @Value("${tencent.cos.region}")
    private String region;

    @Value("${tencent.cos.dir.prefix}")
    private String dirPrefix;

    private COSClient cosClient;

    @PostConstruct
    public void init() {
        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        com.qcloud.cos.ClientConfig clientConfig = new com.qcloud.cos.ClientConfig(new Region(region));
        this.cosClient = new COSClient(credentials, clientConfig);
        LOGGER.info("COSClient 初始化完成");
    }

    /**
     * 上传字节数组到COS
     * 
     * @param fileBytes 文件字节数组
     * @param fileName 文件名
     * @param subDir 子目录
     * @return 文件访问URL
     */
    public String uploadBytesToCos(byte[] fileBytes, String fileName, String subDir) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("文件字节数组为空，无法上传");
        }

        String objectKey = dirPrefix + subDir + "/" + fileName;

        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            // 创建 PutObjectRequest 对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, null);

            // 上传文件到 COS
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            // 检查上传是否成功
            if (putObjectResult == null) {
                throw new RuntimeException("上传失败，PutObjectResult 为 null");
            }

            // 获取文件的访问 URL
            String fileUrl = cosClient.getObjectUrl(bucketName, objectKey).toString();
            LOGGER.info("文件上传成功，URL: {}", fileUrl);
            return fileUrl;
        } catch (IOException e) {
            LOGGER.error("文件上传失败，IO异常", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 生成唯一的文件名
     * 
     * @param originalFilename 原始文件名
     * @return 唯一文件名
     */
    public String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + extension;
    }
} 