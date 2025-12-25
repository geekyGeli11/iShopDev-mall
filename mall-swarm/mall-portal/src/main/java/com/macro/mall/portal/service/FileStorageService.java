package com.macro.mall.portal.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件存储服务接口
 * Created by macro on 2024/12/20.
 */
public interface FileStorageService {
    
    /**
     * 上传文件
     * @param file 文件
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * 上传文件流
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param folder 文件夹路径
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String fileName, String folder);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 批量删除文件
     * @param fileUrls 文件URL列表
     * @return 删除成功的文件数量
     */
    int deleteFiles(List<String> fileUrls);
    
    /**
     * 获取文件访问URL
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getFileUrl(String filePath);
    
    /**
     * 检查文件是否存在
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    boolean fileExists(String fileUrl);
    
    /**
     * 获取文件大小
     * @param fileUrl 文件URL
     * @return 文件大小（字节）
     */
    long getFileSize(String fileUrl);
    
    /**
     * 复制文件
     * @param sourceUrl 源文件URL
     * @param targetFolder 目标文件夹
     * @param targetFileName 目标文件名
     * @return 新文件URL
     */
    String copyFile(String sourceUrl, String targetFolder, String targetFileName);
    
    /**
     * 生成预签名上传URL（用于前端直传）
     * @param fileName 文件名
     * @param folder 文件夹路径
     * @param expireMinutes 过期时间（分钟）
     * @return 预签名URL
     */
    String generatePresignedUploadUrl(String fileName, String folder, int expireMinutes);
    
    /**
     * 生成预签名下载URL
     * @param filePath 文件路径
     * @param expireMinutes 过期时间（分钟）
     * @return 预签名URL
     */
    String generatePresignedDownloadUrl(String filePath, int expireMinutes);
}
