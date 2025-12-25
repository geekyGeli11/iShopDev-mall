package com.macro.mall.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.AppVersionUploadDto;
import com.macro.mall.dto.OssCallbackResult;
import com.macro.mall.mapper.AppVersionMapper;
import com.macro.mall.model.AppVersion;
import com.macro.mall.model.AppVersionExample;
import com.macro.mall.service.AppUpdateFileService;
import com.macro.mall.service.OssService;
import lombok.extern.slf4j.Slf4j;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import java.util.Date;
import java.util.List;

/**
 * 应用更新文件管理服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class AppUpdateFileServiceImpl implements AppUpdateFileService {
    
    @Autowired
    private AppVersionMapper versionMapper;

    @Autowired
    @Qualifier("ossServiceImpl")
    private OssService ossService;

    @Value("${spring.servlet.multipart.max-file-size:100MB}")
    private String maxFileSize;
    
    @Override
    @Transactional
    public CommonResult<AppVersion> uploadApkAndCreateVersion(MultipartFile file, AppVersionUploadDto uploadDto) {
        try {
            log.info("开始上传APK文件并创建版本，版本名称：{}", uploadDto.getVersionName());
            
            // 1. 验证文件
            CommonResult<Void> validateResult = validateApkFile(file);
            if (validateResult.getCode() != 200) {
                return CommonResult.failed(validateResult.getMessage());
            }
            
            // 2. 检查版本是否已存在
            AppVersionExample example = new AppVersionExample();
            example.createCriteria().andVersionNameEqualTo(uploadDto.getVersionName());
            List<AppVersion> existingVersions = versionMapper.selectByExample(example);
            if (!existingVersions.isEmpty()) {
                return CommonResult.validateFailed("版本名称已存在：" + uploadDto.getVersionName());
            }
            
            // 3. 上传文件
            CommonResult<String> uploadResult = uploadApkFile(file, uploadDto.getVersionName());
            if (uploadResult.getCode() != 200) {
                return CommonResult.failed("文件上传失败：" + uploadResult.getMessage());
            }
            
            // 4. 先计算文件MD5（在解析APK之前）
            String fileMd5 = DigestUtil.md5Hex(file.getInputStream());

            // 5. 解析APK文件获取版本信息
            ApkMeta apkMeta = parseApkFile(file);
            if (apkMeta == null) {
                return CommonResult.failed("无法解析APK文件，请确保文件格式正确");
            }

            // 6. 使用APK中的版本信息
            Integer versionCode = Math.toIntExact(apkMeta.getVersionCode());
            String apkVersionName = apkMeta.getVersionName();

            // 验证版本名称是否一致
            if (!uploadDto.getVersionName().equals(apkVersionName)) {
                log.warn("上传的版本名称({})与APK中的版本名称({})不一致，使用APK中的版本名称",
                        uploadDto.getVersionName(), apkVersionName);
            }

            // 7. 检查版本号是否已存在
            AppVersionExample versionCodeExample = new AppVersionExample();
            versionCodeExample.createCriteria().andVersionCodeEqualTo(versionCode);
            List<AppVersion> existingVersionCodes = versionMapper.selectByExample(versionCodeExample);
            if (!existingVersionCodes.isEmpty()) {
                return CommonResult.validateFailed("版本号已存在：" + versionCode + "，请检查APK文件");
            }

            // 8. 创建版本记录
            AppVersion version = new AppVersion();
            version.setVersionName(apkVersionName); // 使用APK中的版本名称
            version.setVersionCode(versionCode);
            version.setApkFilePath(uploadResult.getData());
            version.setApkFileSize(file.getSize());
            version.setApkFileMd5(fileMd5);
            version.setUpdateType(uploadDto.getUpdateType());
            version.setUpdateContent(uploadDto.getUpdateContent());
            version.setMinSupportVersion(uploadDto.getMinSupportVersion());
            version.setTargetPlatform(uploadDto.getTargetPlatform());
            version.setReleaseTime(new Date());
            version.setIsActive(uploadDto.getIsActive());
            version.setCreatedTime(new Date());
            version.setUpdatedTime(new Date());
            
            // 7. 设置下载URL - 直接使用OSS文件URL
            version.setDownloadUrl(uploadResult.getData());
            
            // 8. 保存到数据库
            int result = versionMapper.insertSelective(version);
            if (result > 0) {
                log.info("版本创建成功，版本ID：{}，版本名称：{}", version.getId(), uploadDto.getVersionName());
                return CommonResult.success(version);
            } else {
                // 删除已上传的文件
                deleteApkFile(uploadResult.getData());
                return CommonResult.failed("版本创建失败");
            }
            
        } catch (Exception e) {
            log.error("上传APK文件并创建版本失败，版本名称：{}，错误：{}", uploadDto.getVersionName(), e.getMessage(), e);
            return CommonResult.failed("上传失败：" + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<String> uploadApkFile(MultipartFile file, String versionName) {
        try {
            log.info("开始上传APK文件，版本名称：{}", versionName);

            // 使用现有的OSS服务上传文件
            OssCallbackResult result = ossService.uploadDiyMaterial(file);

            log.info("APK文件上传成功，文件URL：{}", result.getFilename());
            return CommonResult.success(result.getFilename());

        } catch (Exception e) {
            log.error("上传APK文件失败，版本名称：{}，错误：{}", versionName, e.getMessage(), e);
            return CommonResult.failed("文件上传失败：" + e.getMessage());
        }
    }


    
    @Override
    public CommonResult<Void> deleteApkFile(String filePath) {
        try {
            if (StrUtil.isBlank(filePath)) {
                return CommonResult.success(null);
            }

            // 注意：OSS文件删除需要单独实现，这里暂时只记录日志
            log.info("APK文件删除请求，文件路径：{}", filePath);
            log.warn("OSS文件删除功能需要单独实现，当前只做标记删除");

            return CommonResult.success(null);
        } catch (Exception e) {
            log.error("删除APK文件失败，文件路径：{}，错误：{}", filePath, e.getMessage(), e);
            return CommonResult.failed("文件删除失败：" + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<AppVersionUploadDto.ApkInfo> getApkInfo(MultipartFile file) {
        try {
            // 解析APK文件
            ApkMeta apkMeta = parseApkFile(file);
            if (apkMeta == null) {
                return CommonResult.failed("无法解析APK文件，请确保文件格式正确");
            }

            // 构建APK信息
            AppVersionUploadDto.ApkInfo apkInfo = new AppVersionUploadDto.ApkInfo();
            apkInfo.setPackageName(apkMeta.getPackageName());
            apkInfo.setAppName(apkMeta.getLabel());
            apkInfo.setVersionName(apkMeta.getVersionName());
            apkInfo.setVersionCode(Math.toIntExact(apkMeta.getVersionCode()));
            apkInfo.setFileSize(file.getSize());
            apkInfo.setFileMd5(DigestUtil.md5Hex(file.getInputStream()));

            return CommonResult.success(apkInfo);
        } catch (Exception e) {
            log.error("获取APK文件信息失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("获取APK信息失败：" + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<Void> validateApkFile(MultipartFile file) {
        // 1. 检查文件是否为空
        if (file == null || file.isEmpty()) {
            return CommonResult.validateFailed("文件不能为空");
        }

        // 2. 检查文件大小 (使用Spring Boot配置的限制)
        long maxSizeBytes = parseFileSize(maxFileSize);
        if (file.getSize() > maxSizeBytes) {
            return CommonResult.validateFailed("文件大小超过限制：" + maxFileSize);
        }

        // 3. 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename) || !originalFilename.toLowerCase().endsWith(".apk")) {
            return CommonResult.validateFailed("只支持APK文件格式");
        }

        // 4. 检查文件内容类型
        String contentType = file.getContentType();
        if (StrUtil.isNotBlank(contentType) &&
            !contentType.equals("application/vnd.android.package-archive") &&
            !contentType.equals("application/octet-stream")) {
            log.warn("APK文件Content-Type异常：{}", contentType);
        }

        return CommonResult.success(null);
    }

    /**
     * 解析文件大小字符串为字节数
     */
    private long parseFileSize(String sizeStr) {
        if (StrUtil.isBlank(sizeStr)) {
            return 100 * 1024 * 1024; // 默认100MB
        }

        sizeStr = sizeStr.toUpperCase().trim();
        if (sizeStr.endsWith("MB")) {
            return Long.parseLong(sizeStr.substring(0, sizeStr.length() - 2)) * 1024 * 1024;
        } else if (sizeStr.endsWith("KB")) {
            return Long.parseLong(sizeStr.substring(0, sizeStr.length() - 2)) * 1024;
        } else if (sizeStr.endsWith("GB")) {
            return Long.parseLong(sizeStr.substring(0, sizeStr.length() - 2)) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(sizeStr); // 假设是字节数
        }
    }
    
    /**
     * 生成文件名
     */
    private String generateFileName(String versionName) {
        String timestamp = DateUtil.format(new Date(), "yyyyMMdd_HHmmss");
        return String.format("apk/mall-self-checkout-%s-%s.apk", versionName, timestamp);
    }
    
    /**
     * 解析APK文件获取版本信息
     */
    private ApkMeta parseApkFile(MultipartFile file) {
        File tempFile = null;
        try {
            // 创建临时文件
            tempFile = File.createTempFile("apk_", ".apk");

            // 将文件内容写入临时文件（使用字节数组避免消耗输入流）
            byte[] fileBytes = file.getBytes();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(fileBytes);
            }

            // 解析APK文件
            try (ApkFile apkFile = new ApkFile(tempFile)) {
                ApkMeta apkMeta = apkFile.getApkMeta();
                log.info("解析APK文件成功，包名：{}，版本名称：{}，版本号：{}",
                        apkMeta.getPackageName(), apkMeta.getVersionName(), apkMeta.getVersionCode());
                return apkMeta;
            }
        } catch (Exception e) {
            log.error("解析APK文件失败，错误：{}", e.getMessage(), e);
            return null;
        } finally {
            // 确保删除临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * 生成版本号 (已废弃，现在从APK文件中读取)
     */
    @Deprecated
    private Integer generateVersionCode() {
        AppVersionExample example = new AppVersionExample();
        example.createCriteria().andTargetPlatformEqualTo("android");
        example.setOrderByClause("version_code desc");

        List<AppVersion> versions = versionMapper.selectByExample(example);
        if (!versions.isEmpty()) {
            return versions.get(0).getVersionCode() + 1;
        } else {
            return 1000; // 起始版本号
        }
    }
    

}
