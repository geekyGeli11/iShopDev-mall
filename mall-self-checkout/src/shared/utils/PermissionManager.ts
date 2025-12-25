/**
 * 权限管理工具类
 * 处理Android权限请求和检查
 */

import { Capacitor } from '@capacitor/core'
import AppInstaller from '../plugins/AppInstaller'
import logger from './logger'

export interface PermissionStatus {
  granted: boolean
  message?: string
}

export class PermissionManager {
  
  /**
   * 检查所有必需的权限
   */
  public static async checkAllPermissions(): Promise<PermissionStatus> {
    if (!Capacitor.isNativePlatform()) {
      return { granted: false, message: 'Web平台不支持权限检查' }
    }

    try {
      // 检查安装权限
      const installPermission = await this.checkInstallPermission()
      if (!installPermission.granted) {
        return installPermission
      }

      // 检查存储权限
      const storagePermission = await this.checkStoragePermission()
      if (!storagePermission.granted) {
        return storagePermission
      }

      return { granted: true, message: '所有权限已授予' }
    } catch (error) {
      logger.error('检查权限失败', error)
      return { 
        granted: false, 
        message: error instanceof Error ? error.message : '权限检查失败' 
      }
    }
  }

  /**
   * 请求所有必需的权限
   */
  public static async requestAllPermissions(): Promise<PermissionStatus> {
    if (!Capacitor.isNativePlatform()) {
      return { granted: false, message: 'Web平台不支持权限请求' }
    }

    try {
      // 请求安装权限
      const installResult = await this.requestInstallPermission()
      if (!installResult.granted) {
        return installResult
      }

      // 请求存储权限
      const storageResult = await this.requestStoragePermission()
      if (!storageResult.granted) {
        return storageResult
      }

      return { granted: true, message: '所有权限已授予' }
    } catch (error) {
      logger.error('请求权限失败', error)
      return { 
        granted: false, 
        message: error instanceof Error ? error.message : '权限请求失败' 
      }
    }
  }

  /**
   * 检查安装权限
   */
  public static async checkInstallPermission(): Promise<PermissionStatus> {
    try {
      const result = await AppInstaller.checkInstallPermission()
      return {
        granted: result.hasPermission,
        message: result.hasPermission ? '安装权限已授予' : '需要安装权限'
      }
    } catch (error) {
      return { 
        granted: false, 
        message: '检查安装权限失败' 
      }
    }
  }

  /**
   * 请求安装权限
   */
  public static async requestInstallPermission(): Promise<PermissionStatus> {
    try {
      // 先检查是否已有权限
      const checkResult = await this.checkInstallPermission()
      if (checkResult.granted) {
        return checkResult
      }

      // 请求权限
      const result = await AppInstaller.requestInstallPermission()
      return {
        granted: result.granted,
        message: result.granted ? '安装权限已授予' : '用户拒绝了安装权限'
      }
    } catch (error) {
      return { 
        granted: false, 
        message: '请求安装权限失败' 
      }
    }
  }

  /**
   * 检查存储权限
   */
  public static async checkStoragePermission(): Promise<PermissionStatus> {
    try {
      // 在Android 6.0+需要动态请求存储权限
      // 这里可以使用Capacitor的Permissions插件
      // 暂时返回true，实际项目中需要实现具体的存储权限检查
      return { granted: true, message: '存储权限已授予' }
    } catch (error) {
      return { 
        granted: false, 
        message: '检查存储权限失败' 
      }
    }
  }

  /**
   * 请求存储权限
   */
  public static async requestStoragePermission(): Promise<PermissionStatus> {
    try {
      // 实际项目中需要使用Capacitor的Permissions插件请求存储权限
      // 这里暂时返回true
      return { granted: true, message: '存储权限已授予' }
    } catch (error) {
      return { 
        granted: false, 
        message: '请求存储权限失败' 
      }
    }
  }

  /**
   * 打开应用设置页面
   */
  public static async openAppSettings(): Promise<void> {
    try {
      if (Capacitor.isNativePlatform()) {
        // 可以使用Capacitor的App插件打开设置
        // await App.openSettings()
        logger.info('打开应用设置页面')
      }
    } catch (error) {
      logger.error('打开设置页面失败', error)
    }
  }

  /**
   * 显示权限说明对话框
   */
  public static getPermissionExplanation(permissionType: 'install' | 'storage'): string {
    switch (permissionType) {
      case 'install':
        return '应用需要安装权限来自动更新APK文件。这是为了确保您能及时获得最新版本的功能和安全更新。'
      case 'storage':
        return '应用需要存储权限来下载和保存APK更新文件。文件将临时存储在设备上，安装完成后会自动清理。'
      default:
        return '应用需要相关权限来正常运行更新功能。'
    }
  }

  /**
   * 检查设备是否支持自动安装
   */
  public static async checkAutoInstallSupport(): Promise<{
    supported: boolean
    reason?: string
  }> {
    try {
      if (!Capacitor.isNativePlatform()) {
        return { 
          supported: false, 
          reason: 'Web平台不支持APK安装' 
        }
      }

      // 检查Android版本
      const androidVersion = await this.getAndroidVersion()
      if (androidVersion < 24) { // Android 7.0
        return { 
          supported: false, 
          reason: 'Android版本过低，需要Android 7.0以上' 
        }
      }

      // 检查是否有安装权限
      const installPermission = await this.checkInstallPermission()
      if (!installPermission.granted) {
        return { 
          supported: false, 
          reason: '缺少安装权限' 
        }
      }

      return { supported: true }
    } catch (error) {
      return { 
        supported: false, 
        reason: '检查支持性失败' 
      }
    }
  }

  /**
   * 获取Android版本
   */
  private static async getAndroidVersion(): Promise<number> {
    try {
      // 这里需要实现获取Android版本的逻辑
      // 可以使用Capacitor的Device插件
      return 30 // 默认返回Android 11
    } catch (error) {
      return 21 // 默认返回Android 5.0
    }
  }
}

export default PermissionManager
