package com.macro.mall.selfcheck.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 版本检查响应结果
 * 
 * @author macro
 * @since 1.0.0
 */
@Data
@Schema(description = "版本检查响应结果")
public class VersionCheckVO {
    
    @Schema(description = "是否有新版本")
    private Boolean hasUpdate;
    
    @Schema(description = "是否强制更新")
    private Boolean forceUpdate;
    
    @Schema(description = "最新版本信息")
    private VersionInfo latestVersion;
    
    @Schema(description = "当前版本信息")
    private VersionInfo currentVersion;
    
    @Data
    @Schema(description = "版本信息")
    public static class VersionInfo {
        
        @Schema(description = "版本ID")
        private Long versionId;
        
        @Schema(description = "版本名称(如1.2.3)")
        private String versionName;
        
        @Schema(description = "版本号(递增数字)")
        private Integer versionCode;
        
        @Schema(description = "APK文件大小(字节)")
        private Long apkFileSize;
        
        @Schema(description = "APK文件大小(格式化)")
        private String apkFileSizeFormatted;
        
        @Schema(description = "APK文件MD5校验值")
        private String apkFileMd5;
        
        @Schema(description = "更新类型(0:可选更新 1:强制更新)")
        private Integer updateType;
        
        @Schema(description = "更新类型描述")
        private String updateTypeDesc;
        
        @Schema(description = "更新内容描述")
        private String updateContent;
        
        @Schema(description = "最低支持版本")
        private String minSupportVersion;
        
        @Schema(description = "下载地址")
        private String downloadUrl;
        
        @Schema(description = "发布时间")
        private LocalDateTime releaseTime;
    }
    
    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
