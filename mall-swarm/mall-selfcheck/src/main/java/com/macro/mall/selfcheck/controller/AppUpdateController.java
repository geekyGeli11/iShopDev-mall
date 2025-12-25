package com.macro.mall.selfcheck.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.UpdateReportParam;
import com.macro.mall.selfcheck.dto.VersionCheckParam;
import com.macro.mall.model.AppDevice;
import com.macro.mall.model.AppUpdateLog;
import com.macro.mall.model.AppVersion;
import com.macro.mall.selfcheck.service.AppUpdateService;
import com.macro.mall.selfcheck.vo.VersionCheckVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 应用更新管理Controller
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/app")
@Tag(name = "AppUpdateController", description = "应用更新管理")
public class AppUpdateController {
    
    @Autowired
    private AppUpdateService appUpdateService;
    
    @Operation(summary = "检查版本更新")
    @PostMapping("/version/check")
    public CommonResult<VersionCheckVO> checkVersion(@Valid @RequestBody VersionCheckParam param) {
        return appUpdateService.checkVersion(param);
    }
    
    @Operation(summary = "下载APK文件")
    @GetMapping("/download/{versionId}")
    public ResponseEntity<Resource> downloadApk(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId,
            @Parameter(description = "设备ID") @RequestParam(required = false) String deviceId) {
        return appUpdateService.downloadApk(versionId, deviceId);
    }
    
    @Operation(summary = "上报更新状态")
    @PostMapping("/update/report")
    public CommonResult<Void> reportUpdateStatus(@Valid @RequestBody UpdateReportParam param) {
        return appUpdateService.reportUpdateStatus(param);
    }
    
    @Operation(summary = "获取版本列表")
    @GetMapping("/version/list")
    public CommonResult<List<AppVersion>> getVersionList(
            @Parameter(description = "平台类型") @RequestParam(defaultValue = "android") String platform) {
        return appUpdateService.getVersionList(platform);
    }
    
    @Operation(summary = "获取设备列表")
    @GetMapping("/device/list")
    public CommonResult<List<AppDevice>> getDeviceList(
            @Parameter(description = "门店ID") @RequestParam(required = false) Long storeId) {
        return appUpdateService.getDeviceList(storeId);
    }
    
    @Operation(summary = "获取更新日志")
    @GetMapping("/update/logs")
    public CommonResult<List<AppUpdateLog>> getUpdateLogs(
            @Parameter(description = "设备ID", required = true) @RequestParam String deviceId) {
        return appUpdateService.getUpdateLogs(deviceId);
    }
    

    
    @Operation(summary = "创建新版本")
    @PostMapping("/version/create")
    public CommonResult<AppVersion> createVersion(@Valid @RequestBody AppVersion version) {
        return appUpdateService.createVersion(version);
    }
    
    @Operation(summary = "更新版本信息")
    @PutMapping("/version/update")
    public CommonResult<AppVersion> updateVersion(@Valid @RequestBody AppVersion version) {
        return appUpdateService.updateVersion(version);
    }
    
    @Operation(summary = "激活/停用版本")
    @PutMapping("/version/{versionId}/toggle")
    public CommonResult<Void> toggleVersionStatus(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId,
            @Parameter(description = "是否激活", required = true) @RequestParam Boolean isActive) {
        return appUpdateService.toggleVersionStatus(versionId, isActive);
    }
    
    @Operation(summary = "删除版本")
    @DeleteMapping("/version/{versionId}")
    public CommonResult<Void> deleteVersion(
            @Parameter(description = "版本ID", required = true) @PathVariable Long versionId) {
        return appUpdateService.deleteVersion(versionId);
    }
    
    @Operation(summary = "获取设备统计信息")
    @GetMapping("/statistics/device")
    public CommonResult<Object> getDeviceStatistics() {
        return appUpdateService.getDeviceStatistics();
    }
    
    @Operation(summary = "获取更新统计信息")
    @GetMapping("/statistics/update")
    public CommonResult<Object> getUpdateStatistics(
            @Parameter(description = "统计天数") @RequestParam(defaultValue = "7") Integer days) {
        return appUpdateService.getUpdateStatistics(days);
    }
}
