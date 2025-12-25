package com.macro.mall.selfcheck.service.impl;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.UpdateReportParam;
import com.macro.mall.selfcheck.dto.VersionCheckParam;
import com.macro.mall.mapper.AppDeviceMapper;
import com.macro.mall.mapper.AppUpdateLogMapper;
import com.macro.mall.mapper.AppVersionMapper;
import com.macro.mall.model.AppDevice;
import com.macro.mall.model.AppDeviceExample;
import com.macro.mall.model.AppUpdateLog;
import com.macro.mall.model.AppUpdateLogExample;
import com.macro.mall.model.AppVersion;
import com.macro.mall.model.AppVersionExample;
import com.macro.mall.selfcheck.service.AppUpdateService;
import com.macro.mall.selfcheck.vo.VersionCheckVO;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 应用更新服务实现类
 * 
 * @author macro
 * @since 1.0.0
 */
@Slf4j
@Service
public class AppUpdateServiceImpl implements AppUpdateService {
    
    @Autowired
    private AppVersionMapper versionMapper;
    
    @Autowired
    private AppDeviceMapper deviceMapper;
    
    @Autowired
    private AppUpdateLogMapper updateLogMapper;
    

    
    @Override
    public CommonResult<VersionCheckVO> checkVersion(VersionCheckParam param) {
        try {
            log.info("检查版本更新，设备ID：{}，当前版本：{}", param.getDeviceId(), param.getCurrentVersionCode());
            
            // 更新或创建设备信息
            updateOrCreateDevice(param);
            
            // 获取最新版本
            AppVersion latestVersion = getLatestActiveVersion(param.getPlatform());
            if (latestVersion == null) {
                return CommonResult.failed("未找到可用版本");
            }
            
            // 构建响应结果
            VersionCheckVO result = new VersionCheckVO();
            
            // 检查是否有更新
            boolean hasUpdate = latestVersion.getVersionCode() > param.getCurrentVersionCode();
            result.setHasUpdate(hasUpdate);

            if (hasUpdate) {
                // 检查是否强制更新 (Boolean true表示强制更新)
                boolean forceUpdate = Boolean.TRUE.equals(latestVersion.getUpdateType());
                result.setForceUpdate(forceUpdate);
                
                // 设置最新版本信息
                VersionCheckVO.VersionInfo latestVersionInfo = convertToVersionInfo(latestVersion);
                result.setLatestVersion(latestVersionInfo);
            }
            
            // 设置当前版本信息
            AppVersion currentVersion = getVersionByVersionCode(param.getCurrentVersionCode());
            if (currentVersion != null) {
                VersionCheckVO.VersionInfo currentVersionInfo = convertToVersionInfo(currentVersion);
                result.setCurrentVersion(currentVersionInfo);
            }
            
            log.info("版本检查完成，设备ID：{}，有更新：{}，强制更新：{}", 
                    param.getDeviceId(), hasUpdate, result.getForceUpdate());
            
            return CommonResult.success(result);
            
        } catch (Exception e) {
            log.error("检查版本更新失败，设备ID：{}，错误：{}", param.getDeviceId(), e.getMessage(), e);
            return CommonResult.failed("检查版本更新失败：" + e.getMessage());
        }
    }
    
    @Override
    public ResponseEntity<Resource> downloadApk(Long versionId, String deviceId) {
        try {
            log.info("下载APK文件，版本ID：{}，设备ID：{}", versionId, deviceId);

            // 查询版本信息
            AppVersion version = versionMapper.selectByPrimaryKey(versionId);
            if (version == null || !Boolean.TRUE.equals(version.getIsActive())) {
                return ResponseEntity.notFound().build();
            }

            // 检查是否有文件路径
            String filePath = version.getApkFilePath();
            if (StrUtil.isBlank(filePath)) {
                log.error("APK文件路径为空，版本ID：{}", versionId);
                return ResponseEntity.notFound().build();
            }

            // 记录下载日志
            recordDownloadLog(deviceId, version);

            // 如果是OSS URL，通过后端代理下载
            if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
                log.info("通过后端代理下载OSS文件：{}", filePath);
                return downloadFromOss(filePath, version);
            } else {
                // 兼容本地文件（如果还有的话）
                File file = new File(filePath);
                if (!file.exists()) {
                    log.error("APK文件不存在：{}", filePath);
                    return ResponseEntity.notFound().build();
                }

                Resource resource = new FileSystemResource(file);
                String filename = "mall-self-checkout-" + version.getVersionName() + ".apk";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                        .header("X-File-MD5", version.getApkFileMd5())
                        .body(resource);
            }

        } catch (Exception e) {
            log.error("下载APK文件失败，版本ID：{}，设备ID：{}，错误：{}", versionId, deviceId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 从OSS代理下载文件
     */
    private ResponseEntity<Resource> downloadFromOss(String ossUrl, AppVersion version) {
        try {
            log.info("开始从OSS代理下载文件：{}", ossUrl);

            // 使用RestTemplate下载OSS文件
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> ossResponse = restTemplate.getForEntity(ossUrl, byte[].class);

            if (ossResponse.getStatusCode().is2xxSuccessful() && ossResponse.getBody() != null) {
                byte[] fileContent = ossResponse.getBody();
                String filename = "mall-self-checkout-" + version.getVersionName() + ".apk";

                log.info("OSS文件下载成功，文件大小：{} 字节", fileContent.length);

                // 创建ByteArrayResource
                ByteArrayResource resource = new ByteArrayResource(fileContent);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileContent.length))
                        .header("X-File-MD5", version.getApkFileMd5())
                        .body(resource);
            } else {
                log.error("从OSS下载文件失败，状态码：{}", ossResponse.getStatusCode());
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            log.error("从OSS代理下载文件失败：{}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    @Transactional
    public CommonResult<Void> reportUpdateStatus(UpdateReportParam param) {
        try {
            log.info("上报更新状态，设备ID：{}，状态：{}", param.getDeviceId(), param.getUpdateStatus());
            
            // 创建或更新更新日志
            AppUpdateLog updateLog = createOrUpdateLog(param);
            
            // 更新设备状态
            updateDeviceStatus(param);
            
            log.info("更新状态上报成功，设备ID：{}，日志ID：{}", param.getDeviceId(), updateLog.getId());
            return CommonResult.success(null);
            
        } catch (Exception e) {
            log.error("上报更新状态失败，设备ID：{}，错误：{}", param.getDeviceId(), e.getMessage(), e);
            return CommonResult.failed("上报更新状态失败：" + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<AppVersion>> getVersionList(String platform) {
        try {
            AppVersionExample example = new AppVersionExample();
            AppVersionExample.Criteria criteria = example.createCriteria();
            criteria.andIsActiveEqualTo(true);
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
    
    @Override
    public CommonResult<List<AppDevice>> getDeviceList(Long storeId) {
        try {
            AppDeviceExample example = new AppDeviceExample();
            AppDeviceExample.Criteria criteria = example.createCriteria();

            if (storeId != null) {
                criteria.andStoreIdEqualTo(storeId);
            } else {
                criteria.andIsOnlineEqualTo(true);
            }
            example.setOrderByClause("updated_time desc");

            List<AppDevice> devices = deviceMapper.selectByExample(example);
            return CommonResult.success(devices);
        } catch (Exception e) {
            log.error("获取设备列表失败，门店ID：{}，错误：{}", storeId, e.getMessage(), e);
            return CommonResult.failed("获取设备列表失败：" + e.getMessage());
        }
    }
    
    @Override
    public CommonResult<List<AppUpdateLog>> getUpdateLogs(String deviceId) {
        try {
            AppUpdateLogExample example = new AppUpdateLogExample();
            example.createCriteria().andDeviceIdEqualTo(deviceId);
            example.setOrderByClause("created_time desc");

            List<AppUpdateLog> logs = updateLogMapper.selectByExampleWithBLOBs(example);
            return CommonResult.success(logs);
        } catch (Exception e) {
            log.error("获取更新日志失败，设备ID：{}，错误：{}", deviceId, e.getMessage(), e);
            return CommonResult.failed("获取更新日志失败：" + e.getMessage());
        }
    }
    

    
    @Override
    public CommonResult<AppVersion> createVersion(AppVersion version) {
        try {
            // 检查版本是否已存在
            AppVersionExample example = new AppVersionExample();
            example.createCriteria().andVersionNameEqualTo(version.getVersionName());
            List<AppVersion> existingVersions = versionMapper.selectByExample(example);
            if (!existingVersions.isEmpty()) {
                return CommonResult.validateFailed("版本名称已存在：" + version.getVersionName());
            }

            // 生成版本号
            if (version.getVersionCode() == null) {
                version.setVersionCode(generateVersionCode());
            }

            // 设置默认值
            if (version.getTargetPlatform() == null) {
                version.setTargetPlatform("android");
            }
            if (version.getIsActive() == null) {
                version.setIsActive(true);
            }
            if (version.getReleaseTime() == null) {
                version.setReleaseTime(new Date());
            }
            version.setCreatedTime(new Date());
            version.setUpdatedTime(new Date());

            int result = versionMapper.insertSelective(version);
            if (result > 0) {
                return CommonResult.success(version);
            } else {
                return CommonResult.failed("版本创建失败");
            }
        } catch (Exception e) {
            log.error("创建版本失败，错误：{}", e.getMessage(), e);
            return CommonResult.failed("创建版本失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<AppVersion> updateVersion(AppVersion version) {
        try {
            version.setUpdatedTime(new Date());
            int result = versionMapper.updateByPrimaryKeySelective(version);
            if (result > 0) {
                return CommonResult.success(version);
            } else {
                return CommonResult.failed("版本更新失败");
            }
        } catch (Exception e) {
            log.error("更新版本失败，版本ID：{}，错误：{}", version.getId(), e.getMessage(), e);
            return CommonResult.failed("更新版本失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> toggleVersionStatus(Long versionId, Boolean isActive) {
        try {
            AppVersion version = new AppVersion();
            version.setId(versionId);
            version.setIsActive(isActive);
            version.setUpdatedTime(new Date());

            int result = versionMapper.updateByPrimaryKeySelective(version);
            if (result > 0) {
                return CommonResult.success(null);
            } else {
                return CommonResult.failed("更新版本状态失败");
            }
        } catch (Exception e) {
            log.error("更新版本状态失败，版本ID：{}，错误：{}", versionId, e.getMessage(), e);
            return CommonResult.failed("更新版本状态失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Void> deleteVersion(Long versionId) {
        try {
            // 检查是否有设备正在使用此版本
            AppVersion version = versionMapper.selectByPrimaryKey(versionId);
            if (version == null) {
                return CommonResult.failed("版本不存在");
            }

            // 删除版本记录
            int result = versionMapper.deleteByPrimaryKey(versionId);
            if (result > 0) {
                // 删除APK文件
                try {
                    Files.deleteIfExists(Paths.get(version.getApkFilePath()));
                } catch (IOException e) {
                    log.warn("删除APK文件失败：{}", version.getApkFilePath(), e);
                }
                return CommonResult.success(null);
            } else {
                return CommonResult.failed("删除版本失败");
            }
        } catch (Exception e) {
            log.error("删除版本失败，版本ID：{}，错误：{}", versionId, e.getMessage(), e);
            return CommonResult.failed("删除版本失败：" + e.getMessage());
        }
    }

    @Override
    public CommonResult<Object> getDeviceStatistics() {
        // TODO: 实现设备统计逻辑
        return CommonResult.success("设备统计功能待实现");
    }

    @Override
    public CommonResult<Object> getUpdateStatistics(Integer days) {
        // TODO: 实现更新统计逻辑
        return CommonResult.success("更新统计功能待实现");
    }

    /**
     * 更新或创建设备信息
     */
    private void updateOrCreateDevice(VersionCheckParam param) {
        // 查找现有设备
        AppDeviceExample example = new AppDeviceExample();
        example.createCriteria().andDeviceIdEqualTo(param.getDeviceId());
        List<AppDevice> devices = deviceMapper.selectByExample(example);

        AppDevice device;
        if (devices.isEmpty()) {
            // 创建新设备
            device = new AppDevice();
            device.setDeviceId(param.getDeviceId());
            device.setDeviceName(param.getDeviceName());
            device.setDeviceModel(param.getDeviceModel());
            device.setAndroidVersion(param.getAndroidVersion());
            device.setAppVersionName(param.getCurrentVersionName());
            device.setAppVersionCode(param.getCurrentVersionCode());
            device.setStoreId(param.getStoreId());
            device.setStoreName(param.getStoreName());
            device.setLastCheckTime(new Date());
            device.setUpdateStatus(false); // false表示正常状态
            device.setIsOnline(true); // true表示在线
            device.setCreatedTime(new Date());
            device.setUpdatedTime(new Date());

            deviceMapper.insertSelective(device);
        } else {
            // 更新现有设备
            device = devices.get(0);
            device.setDeviceName(param.getDeviceName());
            device.setDeviceModel(param.getDeviceModel());
            device.setAndroidVersion(param.getAndroidVersion());
            device.setAppVersionName(param.getCurrentVersionName());
            device.setAppVersionCode(param.getCurrentVersionCode());
            device.setStoreId(param.getStoreId());
            device.setStoreName(param.getStoreName());
            device.setLastCheckTime(new Date());
            device.setIsOnline(true);
            device.setUpdatedTime(new Date());

            deviceMapper.updateByPrimaryKeySelective(device);
        }
    }

    /**
     * 转换为版本信息VO
     */
    private VersionCheckVO.VersionInfo convertToVersionInfo(AppVersion version) {
        VersionCheckVO.VersionInfo versionInfo = new VersionCheckVO.VersionInfo();
        versionInfo.setVersionId(version.getId());
        versionInfo.setVersionName(version.getVersionName());
        versionInfo.setVersionCode(version.getVersionCode());
        versionInfo.setApkFileSize(version.getApkFileSize());
        versionInfo.setApkFileSizeFormatted(VersionCheckVO.formatFileSize(version.getApkFileSize()));
        versionInfo.setApkFileMd5(version.getApkFileMd5());
        versionInfo.setUpdateType(Boolean.TRUE.equals(version.getUpdateType()) ? 1 : 0);
        versionInfo.setUpdateTypeDesc(Boolean.TRUE.equals(version.getUpdateType()) ? "强制更新" : "可选更新");
        versionInfo.setUpdateContent(version.getUpdateContent());
        versionInfo.setMinSupportVersion(version.getMinSupportVersion());
        versionInfo.setDownloadUrl(version.getDownloadUrl());
        versionInfo.setReleaseTime(version.getReleaseTime() != null ?
            java.time.LocalDateTime.ofInstant(version.getReleaseTime().toInstant(),
                java.time.ZoneId.systemDefault()) : null);
        return versionInfo;
    }

    /**
     * 记录下载日志
     */
    private void recordDownloadLog(String deviceId, AppVersion version) {
        try {
            // 查找设备信息
            AppDeviceExample deviceExample = new AppDeviceExample();
            deviceExample.createCriteria().andDeviceIdEqualTo(deviceId);
            List<AppDevice> devices = deviceMapper.selectByExample(deviceExample);
            Integer fromVersionCode = !devices.isEmpty() ? devices.get(0).getAppVersionCode() : null;

            AppUpdateLog updateLog = new AppUpdateLog();
            updateLog.setDeviceId(deviceId);
            updateLog.setFromVersionCode(fromVersionCode);
            updateLog.setToVersionCode(version.getVersionCode());
            updateLog.setUpdateType(version.getUpdateType());
            updateLog.setUpdateStatus(false); // false表示下载中状态，需要根据实际业务调整
            updateLog.setDownloadProgress(0);
            updateLog.setStartTime(new Date());
            updateLog.setCreatedTime(new Date());

            updateLogMapper.insertSelective(updateLog);
        } catch (Exception e) {
            log.warn("记录下载日志失败，设备ID：{}，错误：{}", deviceId, e.getMessage());
        }
    }

    /**
     * 创建或更新更新日志
     */
    private AppUpdateLog createOrUpdateLog(UpdateReportParam param) {
        // 查找最新的更新日志
        AppUpdateLogExample example = new AppUpdateLogExample();
        example.createCriteria().andDeviceIdEqualTo(param.getDeviceId());
        example.setOrderByClause("created_time desc");
        List<AppUpdateLog> logs = updateLogMapper.selectByExampleWithBLOBs(example);

        AppUpdateLog updateLog = null;
        if (!logs.isEmpty()) {
            updateLog = logs.get(0);
        }

        if (updateLog == null || !updateLog.getToVersionCode().equals(param.getToVersionCode())) {
            // 创建新的更新日志
            updateLog = new AppUpdateLog();
            updateLog.setDeviceId(param.getDeviceId());
            updateLog.setFromVersionCode(param.getFromVersionCode());
            updateLog.setToVersionCode(param.getToVersionCode());
            updateLog.setUpdateType(param.getUpdateType() == 1); // 转换为Boolean
            updateLog.setUpdateStatus(convertUpdateStatus(param.getUpdateStatus()));
            updateLog.setDownloadProgress(param.getDownloadProgress());
            updateLog.setErrorMessage(param.getErrorMessage());
            updateLog.setStartTime(new Date());
            updateLog.setCreatedTime(new Date());

            if (isUpdateComplete(param.getUpdateStatus())) {
                updateLog.setEndTime(new Date());
                updateLog.setDuration(calculateDuration(updateLog.getStartTime(), updateLog.getEndTime()));
            }

            updateLogMapper.insertSelective(updateLog);
        } else {
            // 更新现有日志
            updateLog.setUpdateStatus(convertUpdateStatus(param.getUpdateStatus()));
            updateLog.setDownloadProgress(param.getDownloadProgress());
            updateLog.setErrorMessage(param.getErrorMessage());

            if (isUpdateComplete(param.getUpdateStatus())) {
                updateLog.setEndTime(new Date());
                updateLog.setDuration(calculateDuration(updateLog.getStartTime(), updateLog.getEndTime()));
            }

            updateLogMapper.updateByPrimaryKeySelective(updateLog);
        }

        return updateLog;
    }

    /**
     * 更新设备状态
     */
    private void updateDeviceStatus(UpdateReportParam param) {
        // 查找设备
        AppDeviceExample example = new AppDeviceExample();
        example.createCriteria().andDeviceIdEqualTo(param.getDeviceId());
        List<AppDevice> devices = deviceMapper.selectByExample(example);

        if (!devices.isEmpty()) {
            AppDevice device = devices.get(0);

            // 更新设备更新状态
            if (param.getUpdateStatus() == 4) { // 成功
                // 更新成功，更新设备版本信息
                AppVersion newVersion = getVersionByVersionCode(param.getToVersionCode());
                if (newVersion != null) {
                    device.setAppVersionName(newVersion.getVersionName());
                    device.setAppVersionCode(newVersion.getVersionCode());
                    device.setLastUpdateTime(new Date());
                }
                device.setUpdateStatus(false); // false表示正常状态
            } else if (param.getUpdateStatus() == 5) { // 失败
                device.setUpdateStatus(true); // true表示更新失败状态
            } else {
                // 更新中，保持当前状态或设置为更新中状态
                // 根据业务需要调整
            }

            device.setUpdatedTime(new Date());
            deviceMapper.updateByPrimaryKeySelective(device);
        }
    }



    /**
     * 生成版本号
     */
    private Integer generateVersionCode() {
        AppVersion latestVersion = getLatestActiveVersion("android");
        if (latestVersion != null) {
            return latestVersion.getVersionCode() + 1;
        } else {
            return 1000; // 起始版本号
        }
    }





    /**
     * 判断更新是否完成
     */
    private boolean isUpdateComplete(Integer status) {
        // 4: 成功, 5: 失败
        return status == 4 || status == 5;
    }

    /**
     * 计算持续时间（秒）
     */
    private Integer calculateDuration(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }
        return (int) ((endTime.getTime() - startTime.getTime()) / 1000);
    }

    /**
     * 获取最新的激活版本
     */
    private AppVersion getLatestActiveVersion(String platform) {
        AppVersionExample example = new AppVersionExample();
        AppVersionExample.Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        if (platform != null && !platform.isEmpty()) {
            criteria.andTargetPlatformEqualTo(platform);
        }
        example.setOrderByClause("version_code desc");

        List<AppVersion> versions = versionMapper.selectByExample(example);
        return versions.isEmpty() ? null : versions.get(0);
    }

    /**
     * 根据版本号获取版本信息
     */
    private AppVersion getVersionByVersionCode(Integer versionCode) {
        AppVersionExample example = new AppVersionExample();
        example.createCriteria().andVersionCodeEqualTo(versionCode);

        List<AppVersion> versions = versionMapper.selectByExample(example);
        return versions.isEmpty() ? null : versions.get(0);
    }

    /**
     * 转换更新状态
     */
    private Boolean convertUpdateStatus(Integer status) {
        // 根据业务需要定义状态映射
        // 这里简化处理，可以根据实际需要调整
        return status != null && status > 0;
    }
}
