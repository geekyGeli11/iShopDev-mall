package com.macro.mall.selfcheck.service;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.selfcheck.dto.UpdateReportParam;
import com.macro.mall.selfcheck.dto.VersionCheckParam;
import com.macro.mall.model.AppDevice;
import com.macro.mall.model.AppUpdateLog;
import com.macro.mall.model.AppVersion;
import com.macro.mall.selfcheck.vo.VersionCheckVO;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 应用更新服务接口
 *
 * @author macro
 * @since 1.0.0
 */
public interface AppUpdateService {

    /**
     * 检查版本更新
     *
     * @param param 版本检查参数
     * @return 版本检查结果
     */
    CommonResult<VersionCheckVO> checkVersion(VersionCheckParam param);

    /**
     * 下载APK文件
     *
     * @param versionId 版本ID
     * @param deviceId 设备ID
     * @return 文件下载响应
     */
    ResponseEntity<Resource> downloadApk(Long versionId, String deviceId);

    /**
     * 上报更新状态
     *
     * @param param 更新状态参数
     * @return 操作结果
     */
    CommonResult<Void> reportUpdateStatus(UpdateReportParam param);

    /**
     * 获取版本列表
     *
     * @param platform 平台类型
     * @return 版本列表
     */
    CommonResult<List<AppVersion>> getVersionList(String platform);

    /**
     * 获取设备列表
     *
     * @param storeId 门店ID
     * @return 设备列表
     */
    CommonResult<List<AppDevice>> getDeviceList(Long storeId);

    /**
     * 获取更新日志
     *
     * @param deviceId 设备ID
     * @return 更新日志列表
     */
    CommonResult<List<AppUpdateLog>> getUpdateLogs(String deviceId);



    /**
     * 创建新版本
     *
     * @param version 版本信息
     * @return 操作结果
     */
    CommonResult<AppVersion> createVersion(AppVersion version);

    /**
     * 更新版本信息
     *
     * @param version 版本信息
     * @return 操作结果
     */
    CommonResult<AppVersion> updateVersion(AppVersion version);

    /**
     * 激活/停用版本
     *
     * @param versionId 版本ID
     * @param isActive 是否激活
     * @return 操作结果
     */
    CommonResult<Void> toggleVersionStatus(Long versionId, Boolean isActive);

    /**
     * 删除版本
     *
     * @param versionId 版本ID
     * @return 操作结果
     */
    CommonResult<Void> deleteVersion(Long versionId);

    /**
     * 获取设备统计信息
     *
     * @return 统计信息
     */
    CommonResult<Object> getDeviceStatistics();

    /**
     * 获取更新统计信息
     *
     * @param days 统计天数
     * @return 统计信息
     */
    CommonResult<Object> getUpdateStatistics(Integer days);
}
