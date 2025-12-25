package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 应用版本上传参数
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "应用版本上传参数")
public class AppVersionUploadDto {
    
    @Schema(description = "版本名称", required = true, example = "1.2.0")
    @NotBlank(message = "版本名称不能为空")
    private String versionName;
    
    @Schema(description = "更新类型", required = true, example = "false")
    @NotNull(message = "更新类型不能为空")
    private Boolean updateType; // false:可选更新 true:强制更新
    
    @Schema(description = "更新内容描述", example = "修复已知问题，优化用户体验")
    private String updateContent;
    
    @Schema(description = "最低支持版本", example = "1.0.0")
    private String minSupportVersion;
    
    @Schema(description = "目标平台", example = "android")
    private String targetPlatform = "android";
    
    @Schema(description = "是否立即激活", example = "true")
    private Boolean isActive = true;
    
    @Schema(description = "发布说明")
    private String releaseNotes;
    
    /**
     * APK文件信息
     */
    @Data
    @Schema(description = "APK文件信息")
    public static class ApkInfo {
        
        @Schema(description = "包名")
        private String packageName;
        
        @Schema(description = "应用名称")
        private String appName;
        
        @Schema(description = "版本名称")
        private String versionName;
        
        @Schema(description = "版本号")
        private Integer versionCode;
        
        @Schema(description = "最小SDK版本")
        private Integer minSdkVersion;
        
        @Schema(description = "目标SDK版本")
        private Integer targetSdkVersion;
        
        @Schema(description = "文件大小")
        private Long fileSize;
        
        @Schema(description = "文件MD5")
        private String fileMd5;
        
        @Schema(description = "权限列表")
        private String[] permissions;
        
        @Schema(description = "签名信息")
        private String signature;
    }
    
    /**
     * 上传结果
     */
    @Data
    @Schema(description = "上传结果")
    public static class UploadResult {
        
        @Schema(description = "文件路径")
        private String filePath;
        
        @Schema(description = "文件URL")
        private String fileUrl;
        
        @Schema(description = "文件大小")
        private Long fileSize;
        
        @Schema(description = "文件MD5")
        private String fileMd5;
        
        @Schema(description = "上传时间")
        private String uploadTime;
    }
}
