package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 文件存储服务实现类（本地存储）
 * Created by macro on 2024/12/20.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    @Value("${file.access.url:http://localhost:8080}")
    private String accessUrl;
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = generateFileName() + extension;
            
            // 创建文件夹
            String folderPath = createFolder(folder);
            
            // 保存文件
            File targetFile = new File(folderPath, fileName);
            file.transferTo(targetFile);
            
            // 返回访问URL
            String relativePath = folder + "/" + fileName;
            return getFileUrl(relativePath);
            
        } catch (IOException e) {
            LOGGER.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public String uploadFile(InputStream inputStream, String fileName, String folder) {
        if (inputStream == null || fileName == null) {
            throw new IllegalArgumentException("文件流和文件名不能为空");
        }
        
        try {
            // 生成唯一文件名
            String extension = getFileExtension(fileName);
            String uniqueFileName = generateFileName() + extension;
            
            // 创建文件夹
            String folderPath = createFolder(folder);
            
            // 保存文件
            File targetFile = new File(folderPath, uniqueFileName);
            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            // 返回访问URL
            String relativePath = folder + "/" + uniqueFileName;
            return getFileUrl(relativePath);
            
        } catch (IOException e) {
            LOGGER.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            String filePath = getFilePathFromUrl(fileUrl);
            File file = new File(uploadPath, filePath);
            
            if (file.exists() && file.isFile()) {
                return file.delete();
            }
            
            return false;
            
        } catch (Exception e) {
            LOGGER.error("删除文件失败: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public int deleteFiles(List<String> fileUrls) {
        int deletedCount = 0;
        
        for (String fileUrl : fileUrls) {
            if (deleteFile(fileUrl)) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }
    
    @Override
    public String getFileUrl(String filePath) {
        return accessUrl + "/uploads/" + filePath;
    }
    
    @Override
    public boolean fileExists(String fileUrl) {
        try {
            String filePath = getFilePathFromUrl(fileUrl);
            File file = new File(uploadPath, filePath);
            return file.exists() && file.isFile();
            
        } catch (Exception e) {
            LOGGER.error("检查文件是否存在失败: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public long getFileSize(String fileUrl) {
        try {
            String filePath = getFilePathFromUrl(fileUrl);
            File file = new File(uploadPath, filePath);
            
            if (file.exists() && file.isFile()) {
                return file.length();
            }
            
            return 0;
            
        } catch (Exception e) {
            LOGGER.error("获取文件大小失败: {}", fileUrl, e);
            return 0;
        }
    }
    
    @Override
    public String copyFile(String sourceUrl, String targetFolder, String targetFileName) {
        try {
            String sourceFilePath = getFilePathFromUrl(sourceUrl);
            File sourceFile = new File(uploadPath, sourceFilePath);
            
            if (!sourceFile.exists()) {
                throw new RuntimeException("源文件不存在");
            }
            
            // 创建目标文件夹
            String targetFolderPath = createFolder(targetFolder);
            
            // 生成目标文件名
            String extension = getFileExtension(targetFileName);
            String uniqueFileName = generateFileName() + extension;
            
            // 复制文件
            File targetFile = new File(targetFolderPath, uniqueFileName);
            Files.copy(sourceFile.toPath(), targetFile.toPath());
            
            // 返回新文件URL
            String relativePath = targetFolder + "/" + uniqueFileName;
            return getFileUrl(relativePath);
            
        } catch (Exception e) {
            LOGGER.error("复制文件失败: {} -> {}/{}", sourceUrl, targetFolder, targetFileName, e);
            throw new RuntimeException("复制文件失败", e);
        }
    }
    
    @Override
    public String generatePresignedUploadUrl(String fileName, String folder, int expireMinutes) {
        // 本地存储不支持预签名URL，返回普通上传接口
        return accessUrl + "/upload?folder=" + folder + "&fileName=" + fileName;
    }
    
    @Override
    public String generatePresignedDownloadUrl(String filePath, int expireMinutes) {
        // 本地存储直接返回文件URL
        return getFileUrl(filePath);
    }
    
    /**
     * 创建文件夹
     */
    private String createFolder(String folder) {
        String folderPath = uploadPath + "/" + folder;
        File dir = new File(folderPath);
        
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        return folderPath;
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + "_" + uuid;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * 从URL中提取文件路径
     */
    private String getFilePathFromUrl(String fileUrl) {
        if (fileUrl == null) {
            return "";
        }
        
        // 移除域名部分，只保留相对路径
        String prefix = accessUrl + "/uploads/";
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        
        return fileUrl;
    }
}
