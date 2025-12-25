package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 小程序端DIY文件上传Controller
 * Created by macro on 2024/12/20.
 */
@RestController
@Tag(name = "PortalDiyUploadController", description = "小程序端DIY文件上传")
@RequestMapping("/diy/upload")
public class PortalDiyUploadController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalDiyUploadController.class);
    
    @Value("${mall.upload.path:/tmp/mall/upload}")
    private String uploadPath;
    
    @Value("${mall.upload.baseUrl:http://localhost:8201}")
    private String baseUrl;

    @Operation(summary = "上传DIY设计图片")
    @PostMapping("/image")
    public CommonResult<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CommonResult.failed("上传文件不能为空");
        }
        
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return CommonResult.failed("只支持图片文件上传");
            }
            
            // 验证文件大小（限制为10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return CommonResult.failed("文件大小不能超过10MB");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 创建日期目录
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = sdf.format(new Date());
            String relativePath = "diy/images/" + datePath + "/" + fileName;
            
            // 确保目录存在
            File uploadDir = new File(uploadPath + "/diy/images/" + datePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(uploadPath + "/" + relativePath);
            file.transferTo(destFile);
            
            // 返回文件URL
            String fileUrl = baseUrl + "/upload/" + relativePath;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            
            LOGGER.info("DIY图片上传成功：{}", fileUrl);
            return CommonResult.success(result);
            
        } catch (IOException e) {
            LOGGER.error("DIY图片上传失败", e);
            return CommonResult.failed("文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "上传用户头像")
    @PostMapping("/avatar")
    public CommonResult<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return CommonResult.failed("上传文件不能为空");
        }
        
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return CommonResult.failed("只支持图片文件上传");
            }
            
            // 验证文件大小（限制为2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return CommonResult.failed("头像文件大小不能超过2MB");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String fileName = "avatar_" + System.currentTimeMillis() + extension;
            
            // 创建目录
            String relativePath = "diy/avatars/" + fileName;
            File uploadDir = new File(uploadPath + "/diy/avatars");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 保存文件
            File destFile = new File(uploadPath + "/" + relativePath);
            file.transferTo(destFile);
            
            // 返回文件URL
            String fileUrl = baseUrl + "/upload/" + relativePath;
            
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", fileName);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            
            LOGGER.info("用户头像上传成功：{}", fileUrl);
            return CommonResult.success(result);
            
        } catch (IOException e) {
            LOGGER.error("用户头像上传失败", e);
            return CommonResult.failed("文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量上传DIY素材")
    @PostMapping("/materials")
    public CommonResult<Map<String, Object>> uploadMaterials(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return CommonResult.failed("上传文件不能为空");
        }
        
        if (files.length > 10) {
            return CommonResult.failed("一次最多只能上传10个文件");
        }
        
        Map<String, Object> result = new HashMap<>();
        java.util.List<Map<String, String>> successList = new java.util.ArrayList<>();
        java.util.List<String> failList = new java.util.ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    failList.add(file.getOriginalFilename() + ": 文件为空");
                    continue;
                }
                
                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    failList.add(file.getOriginalFilename() + ": 只支持图片文件");
                    continue;
                }
                
                // 验证文件大小
                if (file.getSize() > 5 * 1024 * 1024) {
                    failList.add(file.getOriginalFilename() + ": 文件大小不能超过5MB");
                    continue;
                }
                
                // 生成文件名
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                
                String fileName = UUID.randomUUID().toString() + extension;
                
                // 创建日期目录
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String datePath = sdf.format(new Date());
                String relativePath = "diy/materials/" + datePath + "/" + fileName;
                
                // 确保目录存在
                File uploadDir = new File(uploadPath + "/diy/materials/" + datePath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                // 保存文件
                File destFile = new File(uploadPath + "/" + relativePath);
                file.transferTo(destFile);
                
                // 返回文件信息
                String fileUrl = baseUrl + "/upload/" + relativePath;
                
                Map<String, String> fileInfo = new HashMap<>();
                fileInfo.put("url", fileUrl);
                fileInfo.put("filename", fileName);
                fileInfo.put("originalName", originalFilename);
                fileInfo.put("size", String.valueOf(file.getSize()));
                
                successList.add(fileInfo);
                
            } catch (IOException e) {
                LOGGER.error("文件上传失败：{}", file.getOriginalFilename(), e);
                failList.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        
        result.put("success", successList);
        result.put("failed", failList);
        result.put("successCount", successList.size());
        result.put("failedCount", failList.size());
        
        LOGGER.info("批量上传完成：成功{}个，失败{}个", successList.size(), failList.size());
        return CommonResult.success(result);
    }
}
