package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.AppVersionUploadDto;
import com.macro.mall.mapper.AppVersionMapper;
import com.macro.mall.model.AppVersion;
import com.macro.mall.model.AppVersionExample;
import com.macro.mall.service.AppUpdateFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * 应用更新管理Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/app-update")
@Tag(name = "AppUpdateController", description = "应用更新管理")
public class AppUpdateController {
    
    @Autowired
    private AppUpdateFileService appUpdateFileService;
    
    @Autowired
    private AppVersionMapper versionMapper;
    
    @Operation(summary = "上传APK文件并创建版本")
    @PostMapping("/upload")
    public CommonResult<AppVersion> uploadApkAndCreateVersion(
            @Parameter(description = "APK文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "版本名称", required = true) @RequestParam String versionName,
            @Parameter(description = "更新类型", required = true) @RequestParam Boolean updateType,
            @Parameter(description = "更新内容") @RequestParam(required = false) String updateContent,
            @Parameter(description = "最低支持版本") @RequestParam(required = false) String minSupportVersion,
            @Parameter(description = "目标平台") @RequestParam(defaultValue = "android") String targetPlatform,
            @Parameter(description = "是否立即激活") @RequestParam(defaultValue = "true") Boolean isActive) {
        
        // 构建上传参数
        AppVersionUploadDto uploadDto = new AppVersionUploadDto();
        uploadDto.setVersionName(versionName);
        uploadDto.setUpdateType(updateType);
        uploadDto.setUpdateContent(updateContent);
        uploadDto.setMinSupportVersion(minSupportVersion);
        uploadDto.setTargetPlatform(targetPlatform);
        uploadDto.setIsActive(isActive);
        
        return appUpdateFileService.uploadApkAndCreateVersion(file, uploadDto);
    }
    
    @Operation(summary = "上传APK文件")
    @PostMapping("/upload-file")
    public CommonResult<String> uploadApkFile(
            @Parameter(description = "APK文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "版本名称", required = true) @RequestParam String versionName) {
        
        return appUpdateFileService.uploadApkFile(file, versionName);
    }
    
    @Operation(summary = "获取APK文件信息")
    @PostMapping("/apk-info")
    public CommonResult<AppVersionUploadDto.ApkInfo> getApkInfo(
            @Parameter(description = "APK文件", required = true) @RequestParam("file") MultipartFile file) {
        
        return appUpdateFileService.getApkInfo(file);
    }
    
    @Operation(summary = "验证APK文件")
    @PostMapping("/validate")
    public CommonResult<Void> validateApkFile(
            @Parameter(description = "APK文件", required = true) @RequestParam("file") MultipartFile file) {
        
        return appUpdateFileService.validateApkFile(file);
    }
    
    @Operation(summary = "获取版本列表")
    @GetMapping("/versions")
    public CommonResult<List<AppVersion>> getVersionList(
            @Parameter(description = "平台类型") @RequestParam(defaultValue = "android") String platform) {
        
        try {
            AppVersionExample example = new AppVersionExample();
            AppVersionExample.Criteria criteria = example.createCriteria();
            if (platform != null && !platform.isEmpty()) {
                criteria.andTargetPlatformEqualTo(platform);
            }
            example.setOrderByClause("version_code desc");
            
            List<AppVersion> versions = versionMapper.selectByExampleWithBLOBs(example);
            return CommonResult.success(versions);
        } catch (Exception e) {
            log.error("获取版本列表失败，平台：{}，错误：{}", platform, e.getMessage(), e);
            return CommonResult.failed("获取版本列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "更新版本信息")
    @PutMapping("/versions/{id}")
    public CommonResult<AppVersion> updateVersion(
            @Parameter(description = "版本ID", required = true) @PathVariable Long id,
            @RequestBody @Valid AppVersionUploadDto updateDto) {
        
        try {
            AppVersion version = versionMapper.selectByPrimaryKey(id);
            if (version == null) {
                return CommonResult.failed("版本不存在");
            }
            
            // 更新版本信息
            version.setUpdateType(updateDto.getUpdateType());
            version.setUpdateContent(updateDto.getUpdateContent());
            version.setMinSupportVersion(updateDto.getMinSupportVersion());
            version.setIsActive(updateDto.getIsActive());
            version.setUpdatedTime(new Date());
            
            int result = versionMapper.updateByPrimaryKeySelective(version);
            if (result > 0) {
                return CommonResult.success(version);
            } else {
                return CommonResult.failed("版本更新失败");
            }
        } catch (Exception e) {
            log.error("更新版本失败，版本ID：{}，错误：{}", id, e.getMessage(), e);
            return CommonResult.failed("更新版本失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "激活/停用版本")
    @PutMapping("/versions/{id}/toggle")
    public CommonResult<Void> toggleVersionStatus(
            @Parameter(description = "版本ID", required = true) @PathVariable Long id,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        
        try {
            AppVersion version = new AppVersion();
            version.setId(id);
            version.setIsActive(isActive);
            version.setUpdatedTime(new Date());
            
            int result = versionMapper.updateByPrimaryKeySelective(version);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.failed("更新版本状态失败");
            }
        } catch (Exception e) {
            log.error("更新版本状态失败，版本ID：{}，错误：{}", id, e.getMessage(), e);
            return CommonResult.failed("更新版本状态失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "删除版本")
    @DeleteMapping("/versions/{id}")
    public CommonResult<Void> deleteVersion(
            @Parameter(description = "版本ID", required = true) @PathVariable Long id) {
        
        try {
            // 获取版本信息
            AppVersion version = versionMapper.selectByPrimaryKey(id);
            if (version == null) {
                return CommonResult.failed("版本不存在");
            }
            
            // 删除版本记录
            int result = versionMapper.deleteByPrimaryKey(id);
            if (result > 0) {
                // 删除APK文件
                appUpdateFileService.deleteApkFile(version.getApkFilePath());
                return CommonResult.success(null);
            } else {
                return CommonResult.failed("删除版本失败");
            }
        } catch (Exception e) {
            log.error("删除版本失败，版本ID：{}，错误：{}", id, e.getMessage(), e);
            return CommonResult.failed("删除版本失败：" + e.getMessage());
        }
    }
}
