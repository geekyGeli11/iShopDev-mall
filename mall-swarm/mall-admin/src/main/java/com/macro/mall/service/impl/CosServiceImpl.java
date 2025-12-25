package com.macro.mall.service.impl;

import com.macro.mall.dto.OssCallbackResult;
import com.macro.mall.dto.OssPolicyResult;
import com.macro.mall.service.OssService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CosServiceImpl implements OssService {

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

    // 定义临时密钥有效时长，单位为秒
    private static final int STS_DURATION_SECONDS = 1800;

    @PostConstruct
    public void init() {
        BasicCOSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        com.qcloud.cos.ClientConfig clientConfig = new com.qcloud.cos.ClientConfig(new Region(region));
        this.cosClient = new COSClient(credentials, clientConfig);
        LOGGER.info("COSClient 初始化完成");
    }

    /**
     * 签名生成
     */
    @Override
    public OssPolicyResult policy() {
        OssPolicyResult result = new OssPolicyResult();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dir = dirPrefix + sdf.format(new Date()) + "/";

        try {
            // 生成预签名URL
            URL presignedUrl = cosClient.generatePresignedUrl(bucketName, dir, new Date(System.currentTimeMillis() + 30 * 60 * 1000));
            result.setHost(presignedUrl.toString());
            result.setDir(dir);
            result.setAccessKeyId(secretId);
            result.setPolicy(null);
            result.setSignature(null);
        } catch (Exception e) {
            LOGGER.error("签名生成失败", e);
        }
        return result;
    }

    /**
     * 上传回调处理
     */
    @Override
    public OssCallbackResult callback(HttpServletRequest request) {
        OssCallbackResult result = new OssCallbackResult();
        try {
            String filename = request.getParameter("filename");
            String fileUrl = "https://" + bucketName + ".cos." + region + ".myqcloud.com/" + filename;
            result.setFilename(fileUrl);
            result.setSize(request.getParameter("size"));
            result.setMimeType(request.getParameter("mimeType"));
            result.setWidth(request.getParameter("width"));
            result.setHeight(request.getParameter("height"));
        } catch (Exception e) {
            LOGGER.error("回调处理失败", e);
        }
        return result;
    }

    /**
     * 文件上传到 COS
     */
    public String uploadFileToCos(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空，无法上传");
        }

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        String objectKey = dirPrefix + uniqueFileName;

        try (InputStream inputStream = file.getInputStream()) {
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
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + extension;
    }

    /**
     * 获取腾讯云COS临时密钥
     * @return Map 包含临时密钥信息
     */
    @Override
    public Map<String, Object> getStsToken() {
        try {
            // 生成临时密钥信息
            Date expiredTime = new Date(System.currentTimeMillis() + STS_DURATION_SECONDS * 1000);
            
            // 生成预签名URL用于验证
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dir = dirPrefix + sdf.format(new Date()) + "/";
            
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, dir, HttpMethodName.PUT);
            request.setExpiration(expiredTime);
            
            URL presignedUrl = cosClient.generatePresignedUrl(request);
            
            // 构建临时密钥信息
            Map<String, Object> credentials = new HashMap<>();
            credentials.put("tmpSecretId", secretId);
            credentials.put("tmpSecretKey", secretKey);
            credentials.put("sessionToken", "");  // 由于使用永久密钥，sessionToken为空
            credentials.put("expiredTime", expiredTime.getTime() / 1000);
            
            // 构建返回结果，包含COS所需的基础信息
            Map<String, Object> response = new HashMap<>();
            response.put("credentials", credentials);
            response.put("region", region);
            response.put("bucketName", bucketName);
            response.put("dirPrefix", dirPrefix);
            response.put("startTime", System.currentTimeMillis() / 1000);  // 当前时间戳，秒级
            response.put("expiredTime", expiredTime.getTime() / 1000);  // 过期时间戳，秒级
            
            LOGGER.info("获取临时密钥成功");
            return response;
        } catch (Exception e) {
            LOGGER.error("获取临时密钥失败", e);
            throw new RuntimeException("获取临时密钥失败: " + e.getMessage());
        }
    }

    @Override
    public OssCallbackResult uploadDiyMaterial(MultipartFile file) {
        try {
            // 生成文件名
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String key = "diy/materials/" + fileName;

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), null);
            cosClient.putObject(putObjectRequest);

            // 构建返回结果
            OssCallbackResult result = new OssCallbackResult();
            result.setFilename(fileName);
            result.setSize(String.valueOf(file.getSize()));
            result.setMimeType(file.getContentType());
            result.setWidth("0");
            result.setHeight("0");

            return result;

        } catch (Exception e) {
            LOGGER.error("上传DIY素材失败", e);
            throw new RuntimeException("上传DIY素材失败: " + e.getMessage());
        }
    }
}
