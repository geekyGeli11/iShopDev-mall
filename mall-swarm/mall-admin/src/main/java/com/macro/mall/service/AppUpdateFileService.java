package com.macro.mall.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.AppVersionUploadDto;
import com.macro.mall.model.AppVersion;
import org.springframework.web.multipart.MultipartFile;

/**
 * 应用更新文件管理服务
 * 
 * @author macro
 * @since 1.0.0
 */
public interface AppUpdateFileService {
    
    /**
     * 上传APK文件并创建版本记录
     * 
     * @param file APK文件
     * @param uploadDto 上传参数
     * @return 创建的版本信息
     */
    CommonResult<AppVersion> uploadApkAndCreateVersion(MultipartFile file, AppVersionUploadDto uploadDto);
    
    /**
     * 上传APK文件到存储服务
     * 
     * @param file APK文件
     * @param versionName 版本名称
     * @return 文件存储路径
     */
    CommonResult<String> uploadApkFile(MultipartFile file, String versionName);
    
    /**
     * 删除APK文件
     * 
     * @param filePath 文件路径
     * @return 删除结果
     */
    CommonResult<Void> deleteApkFile(String filePath);
    
    /**
     * 获取APK文件信息
     * 
     * @param file APK文件
     * @return APK信息
     */
    CommonResult<AppVersionUploadDto.ApkInfo> getApkInfo(MultipartFile file);
    
    /**
     * 验证APK文件
     * 
     * @param file 文件
     * @return 验证结果
     */
    CommonResult<Void> validateApkFile(MultipartFile file);
}
