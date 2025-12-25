/**
 * 应用自动更新服务
 * 负责版本检查、APK下载和安装管理
 */

import { Capacitor } from '@capacitor/core'
import { Device } from '@capacitor/device'
import { App } from '@capacitor/app'
import { Filesystem, Directory } from '@capacitor/filesystem'
import { CapacitorHttp } from '@capacitor/core'
import { config } from '@/config/env'
import { logger } from '@/config/env'
import AppInstaller from '@/plugins/AppInstaller'
import PermissionManager from '@/shared/utils/PermissionManager'

export interface VersionInfo {
  versionId: number
  versionName: string
  versionCode: number
  apkFileSize: number
  apkFileSizeFormatted: string
  apkFileMd5: string
  updateType: number
  updateTypeDesc: string
  updateContent: string
  downloadUrl: string
  releaseTime: string
}

export interface VersionCheckResult {
  hasUpdate: boolean
  forceUpdate: boolean
  latestVersion?: VersionInfo
  currentVersion?: VersionInfo
}

export interface UpdateProgress {
  status: 'checking' | 'downloading' | 'installing' | 'completed' | 'failed'
  progress: number
  message: string
  error?: string
}

export interface UpdateConfig {
  autoCheck: boolean
  checkInterval: number // 检查间隔（毫秒）
  downloadOnWifi: boolean
  installOnIdle: boolean
  allowedUpdateTime: {
    start: string // HH:mm
    end: string   // HH:mm
  }
}

export class AppUpdateService {
  private static instance: AppUpdateService
  private updateConfig: UpdateConfig
  private isChecking = false
  private isDownloading = false
  private isInstalling = false
  private checkTimer?: number
  private progressCallback?: (progress: UpdateProgress) => void

  private constructor() {
    this.updateConfig = this.getDefaultConfig()
    this.loadConfig()
  }

  public static getInstance(): AppUpdateService {
    if (!AppUpdateService.instance) {
      AppUpdateService.instance = new AppUpdateService()
    }
    return AppUpdateService.instance
  }

  /**
   * 初始化更新服务
   */
  public async initialize(): Promise<void> {
    try {
      logger.info('初始化应用更新服务')
      
      // 只在原生平台启用
      if (!Capacitor.isNativePlatform()) {
        logger.info('非原生平台，跳过更新服务初始化')
        return
      }

      // 启动定时检查
      if (this.updateConfig.autoCheck) {
        this.startAutoCheck()
      }

      // 应用启动时检查一次
      setTimeout(() => {
        this.checkForUpdates()
      }, 5000) // 延迟5秒，等待应用完全启动

    } catch (error) {
      logger.error('初始化更新服务失败', error)
    }
  }

  /**
   * 设置进度回调
   */
  public setProgressCallback(callback: (progress: UpdateProgress) => void): void {
    this.progressCallback = callback
  }

  /**
   * 检查版本更新
   */
  public async checkForUpdates(manual = false): Promise<VersionCheckResult | null> {
    if (this.isChecking && !manual) {
      logger.info('正在检查更新，跳过重复检查')
      return null
    }

    try {
      this.isChecking = true
      this.notifyProgress({
        status: 'checking',
        progress: 0,
        message: '正在检查更新...'
      })

      logger.info('开始检查版本更新')

      // 获取设备信息
      const deviceInfo = await this.getDeviceInfo()
      const appInfo = await this.getCurrentAppInfo()

      // 构建检查参数
      const checkParam = {
        deviceId: deviceInfo.identifier,
        currentVersionCode: appInfo.versionCode,
        currentVersionName: appInfo.versionName,
        deviceName: deviceInfo.name,
        deviceModel: deviceInfo.model,
        androidVersion: deviceInfo.osVersion,
        platform: 'android'
      }

      // 调用后端API检查更新
      const response = await CapacitorHttp.post({
        url: `${config.api.baseURL}/app/version/check`,
        headers: {
          'Content-Type': 'application/json'
        },
        data: checkParam
      })

      if (response.status === 200 && response.data.code === 200) {
        const result: VersionCheckResult = response.data.data
        
        logger.info('版本检查完成', {
          hasUpdate: result.hasUpdate,
          forceUpdate: result.forceUpdate,
          latestVersion: result.latestVersion?.versionName
        })

        this.notifyProgress({
          status: 'completed',
          progress: 100,
          message: result.hasUpdate ? '发现新版本' : '已是最新版本'
        })

        return result
      } else {
        throw new Error(response.data.message || '检查更新失败')
      }

    } catch (error) {
      logger.error('检查更新失败', error)
      this.notifyProgress({
        status: 'failed',
        progress: 0,
        message: '检查更新失败',
        error: error instanceof Error ? error.message : '未知错误'
      })
      return null
    } finally {
      this.isChecking = false
    }
  }

  /**
   * 下载并安装更新
   */
  public async downloadAndInstall(versionInfo: VersionInfo): Promise<boolean> {
    if (this.isDownloading || this.isInstalling) {
      logger.warn('正在下载或安装，跳过重复操作')
      return false
    }

    try {
      // 检查是否在允许的更新时间内
      if (!this.isUpdateTimeAllowed()) {
        logger.info('当前时间不在允许的更新时间范围内')
        return false
      }

      // 下载APK
      const downloadSuccess = await this.downloadApk(versionInfo)
      if (!downloadSuccess) {
        return false
      }

      // 安装APK
      const installSuccess = await this.installApk(versionInfo)
      return installSuccess

    } catch (error) {
      logger.error('下载安装失败', error)
      this.notifyProgress({
        status: 'failed',
        progress: 0,
        message: '下载安装失败',
        error: error instanceof Error ? error.message : '未知错误'
      })
      return false
    }
  }

  /**
   * 下载APK文件
   */
  private async downloadApk(versionInfo: VersionInfo): Promise<boolean> {
    try {
      this.isDownloading = true
      
      logger.info('开始下载APK', {
        versionName: versionInfo.versionName,
        fileSize: versionInfo.apkFileSizeFormatted
      })

      this.notifyProgress({
        status: 'downloading',
        progress: 0,
        message: `正在下载 ${versionInfo.versionName}...`
      })

      // 获取设备信息用于下载
      const deviceInfo = await this.getDeviceInfo()

      // 直接使用版本信息中的下载URL
      const downloadUrl = versionInfo.downloadUrl
      
      // 下载文件到本地
      const fileName = `mall-self-checkout-${versionInfo.versionName}.apk`
      const response = await CapacitorHttp.downloadFile({
        url: downloadUrl,
        filePath: fileName,
        fileDirectory: Directory.Cache,
        progress: true,
        progressCallback: (progress, total) => {
          const percent = Math.round((progress / total) * 100)
          this.notifyProgress({
            status: 'downloading',
            progress: percent,
            message: `正在下载 ${versionInfo.versionName}... ${percent}%`
          })
        }
      })

      if (response.path) {
        // 验证文件完整性
        const isValid = await this.verifyApkFile(response.path, versionInfo.apkFileMd5)
        if (!isValid) {
          throw new Error('APK文件校验失败')
        }

        logger.info('APK下载完成', { path: response.path })
        
        // 上报下载完成状态
        await this.reportUpdateStatus({
          deviceId: deviceInfo.identifier,
          toVersionCode: versionInfo.versionCode,
          updateType: versionInfo.updateType,
          updateStatus: 2, // 下载完成
          downloadProgress: 100
        })

        return true
      } else {
        throw new Error('下载失败，未获得文件路径')
      }

    } catch (error) {
      logger.error('下载APK失败', error)
      this.notifyProgress({
        status: 'failed',
        progress: 0,
        message: '下载失败',
        error: error instanceof Error ? error.message : '下载失败'
      })
      return false
    } finally {
      this.isDownloading = false
    }
  }

  /**
   * 安装APK文件
   */
  private async installApk(versionInfo: VersionInfo): Promise<boolean> {
    try {
      this.isInstalling = true
      
      logger.info('开始安装APK', { versionName: versionInfo.versionName })

      this.notifyProgress({
        status: 'installing',
        progress: 0,
        message: '正在安装更新...'
      })

      const deviceInfo = await this.getDeviceInfo()
      const fileName = `mall-self-checkout-${versionInfo.versionName}.apk`

      // 上报安装开始状态
      await this.reportUpdateStatus({
        deviceId: deviceInfo.identifier,
        toVersionCode: versionInfo.versionCode,
        updateType: versionInfo.updateType,
        updateStatus: 3, // 安装中
        downloadProgress: 100
      })

      // 获取APK文件路径
      const fileResult = await Filesystem.getUri({
        directory: Directory.Cache,
        path: fileName
      })

      // 调用原生安装方法（需要在原生代码中实现）
      const installResult = await this.callNativeInstall(fileResult.uri)
      
      if (installResult.success) {
        logger.info('APK安装成功')
        
        this.notifyProgress({
          status: 'completed',
          progress: 100,
          message: '安装完成，应用将重启'
        })

        // 上报安装成功状态
        await this.reportUpdateStatus({
          deviceId: deviceInfo.identifier,
          toVersionCode: versionInfo.versionCode,
          updateType: versionInfo.updateType,
          updateStatus: 4, // 成功
          downloadProgress: 100
        })

        // 清理下载的APK文件
        await this.cleanupDownloadedFile(fileName)

        return true
      } else {
        throw new Error(installResult.error || '安装失败')
      }

    } catch (error) {
      logger.error('安装APK失败', error)
      
      // 上报安装失败状态
      const deviceInfo = await this.getDeviceInfo()
      await this.reportUpdateStatus({
        deviceId: deviceInfo.identifier,
        toVersionCode: versionInfo.versionCode,
        updateType: versionInfo.updateType,
        updateStatus: 5, // 失败
        downloadProgress: 100,
        errorMessage: error instanceof Error ? error.message : '安装失败'
      })

      this.notifyProgress({
        status: 'failed',
        progress: 0,
        message: '安装失败',
        error: error instanceof Error ? error.message : '安装失败'
      })
      
      return false
    } finally {
      this.isInstalling = false
    }
  }

  /**
   * 获取设备信息
   */
  private async getDeviceInfo() {
    const deviceInfo = await Device.getInfo()
    const deviceId = await Device.getId()

    return {
      identifier: deviceId.identifier,
      name: deviceInfo.name,
      model: deviceInfo.model,
      osVersion: deviceInfo.osVersion,
      platform: deviceInfo.platform
    }
  }

  /**
   * 获取当前应用信息
   */
  private async getCurrentAppInfo() {
    const appInfo = await App.getInfo()
    return {
      versionName: appInfo.version,
      versionCode: parseInt(appInfo.build) || 1000
    }
  }

  /**
   * 验证APK文件完整性
   */
  private async verifyApkFile(filePath: string, expectedMd5: string): Promise<boolean> {
    try {
      logger.info('验证APK文件完整性', { filePath, expectedMd5 })

      if (!Capacitor.isNativePlatform()) {
        // Web平台跳过验证
        return true
      }

      // 使用原生插件计算MD5
      const result = await AppInstaller.calculateMd5({ filePath })
      const actualMd5 = result.md5.toLowerCase()
      const expectedMd5Lower = expectedMd5.toLowerCase()

      const isValid = actualMd5 === expectedMd5Lower

      if (!isValid) {
        logger.error('APK文件MD5校验失败', {
          expected: expectedMd5Lower,
          actual: actualMd5
        })
      }

      return isValid
    } catch (error) {
      logger.error('验证APK文件失败', error)
      return false
    }
  }

  /**
   * 调用原生安装方法
   */
  private async callNativeInstall(apkUri: string): Promise<{ success: boolean; error?: string }> {
    try {
      logger.info('调用原生安装', { apkUri })

      if (!Capacitor.isNativePlatform()) {
        return {
          success: false,
          error: 'Web平台不支持APK安装'
        }
      }

      // 检查设备是否支持自动安装
      const supportCheck = await PermissionManager.checkAutoInstallSupport()
      if (!supportCheck.supported) {
        return {
          success: false,
          error: `设备不支持自动安装: ${supportCheck.reason}`
        }
      }

      // 检查并请求所有必需权限
      const permissionResult = await PermissionManager.requestAllPermissions()
      if (!permissionResult.granted) {
        return {
          success: false,
          error: `权限不足: ${permissionResult.message}`
        }
      }

      // 执行安装
      const installResult = await AppInstaller.installApk({
        filePath: apkUri,
        silent: false
      })

      return installResult
    } catch (error) {
      logger.error('原生安装失败', error)
      return {
        success: false,
        error: error instanceof Error ? error.message : '安装失败'
      }
    }
  }

  /**
   * 上报更新状态
   */
  private async reportUpdateStatus(params: {
    deviceId: string
    toVersionCode: number
    updateType: number
    updateStatus: number
    downloadProgress?: number
    errorMessage?: string
    fromVersionCode?: number
  }): Promise<void> {
    try {
      await CapacitorHttp.post({
        url: `${config.api.baseURL}/app/update/report`,
        headers: {
          'Content-Type': 'application/json'
        },
        data: params
      })
    } catch (error) {
      logger.warn('上报更新状态失败', error)
    }
  }

  /**
   * 清理下载的文件
   */
  private async cleanupDownloadedFile(fileName: string): Promise<void> {
    try {
      await Filesystem.deleteFile({
        directory: Directory.Cache,
        path: fileName
      })
      logger.info('清理下载文件成功', { fileName })
    } catch (error) {
      logger.warn('清理下载文件失败', error)
    }
  }

  /**
   * 检查是否在允许的更新时间内
   */
  private isUpdateTimeAllowed(): boolean {
    const now = new Date()
    const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`

    const { start, end } = this.updateConfig.allowedUpdateTime

    // 如果开始时间小于结束时间（同一天内）
    if (start < end) {
      return currentTime >= start && currentTime <= end
    }
    // 如果开始时间大于结束时间（跨天）
    else {
      return currentTime >= start || currentTime <= end
    }
  }

  /**
   * 启动自动检查
   */
  private startAutoCheck(): void {
    if (this.checkTimer) {
      clearInterval(this.checkTimer)
    }

    this.checkTimer = window.setInterval(() => {
      if (this.isUpdateTimeAllowed()) {
        this.checkForUpdates()
      }
    }, this.updateConfig.checkInterval)

    logger.info('启动自动检查更新', {
      interval: this.updateConfig.checkInterval,
      allowedTime: this.updateConfig.allowedUpdateTime
    })
  }

  /**
   * 停止自动检查
   */
  public stopAutoCheck(): void {
    if (this.checkTimer) {
      clearInterval(this.checkTimer)
      this.checkTimer = undefined
      logger.info('停止自动检查更新')
    }
  }

  /**
   * 通知进度更新
   */
  private notifyProgress(progress: UpdateProgress): void {
    if (this.progressCallback) {
      this.progressCallback(progress)
    }
  }

  /**
   * 获取默认配置
   */
  private getDefaultConfig(): UpdateConfig {
    return {
      autoCheck: true,
      checkInterval: 4 * 60 * 60 * 1000, // 4小时
      downloadOnWifi: true,
      installOnIdle: true,
      allowedUpdateTime: {
        start: '02:00',
        end: '06:00'
      }
    }
  }

  /**
   * 加载配置
   */
  private loadConfig(): void {
    try {
      const savedConfig = localStorage.getItem('app_update_config')
      if (savedConfig) {
        this.updateConfig = { ...this.updateConfig, ...JSON.parse(savedConfig) }
      }
    } catch (error) {
      logger.warn('加载更新配置失败，使用默认配置', error)
    }
  }

  /**
   * 保存配置
   */
  public saveConfig(config: Partial<UpdateConfig>): void {
    try {
      this.updateConfig = { ...this.updateConfig, ...config }
      localStorage.setItem('app_update_config', JSON.stringify(this.updateConfig))

      // 重启自动检查
      if (this.updateConfig.autoCheck) {
        this.startAutoCheck()
      } else {
        this.stopAutoCheck()
      }

      logger.info('保存更新配置成功', this.updateConfig)
    } catch (error) {
      logger.error('保存更新配置失败', error)
    }
  }

  /**
   * 获取当前配置
   */
  public getConfig(): UpdateConfig {
    return { ...this.updateConfig }
  }

  /**
   * 手动触发更新检查
   */
  public async manualCheck(): Promise<VersionCheckResult | null> {
    return this.checkForUpdates(true)
  }

  /**
   * 销毁服务
   */
  public destroy(): void {
    this.stopAutoCheck()
    this.progressCallback = undefined
    logger.info('应用更新服务已销毁')
  }
}
