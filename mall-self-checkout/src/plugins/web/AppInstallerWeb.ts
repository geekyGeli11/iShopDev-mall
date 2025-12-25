/**
 * Web平台的APK安装器实现（模拟）
 */

import { WebPlugin } from '@capacitor/core'
import type { AppInstallerPlugin } from '../AppInstaller'

export class AppInstallerWeb extends WebPlugin implements AppInstallerPlugin {
  
  async installApk(options: { 
    filePath: string
    packageName?: string
    silent?: boolean 
  }): Promise<{ success: boolean; error?: string }> {
    console.log('Web平台不支持APK安装', options)
    return { 
      success: false, 
      error: 'Web平台不支持APK安装功能' 
    }
  }

  async checkInstallPermission(): Promise<{ hasPermission: boolean }> {
    console.log('Web平台检查安装权限')
    return { hasPermission: false }
  }

  async requestInstallPermission(): Promise<{ granted: boolean }> {
    console.log('Web平台请求安装权限')
    return { granted: false }
  }

  async getApkInfo(options: { 
    filePath: string 
  }): Promise<{ 
    packageName: string
    versionName: string
    versionCode: number
    appName: string
  }> {
    console.log('Web平台获取APK信息', options)
    return {
      packageName: 'com.mall.selfcheckout',
      versionName: '1.0.0',
      versionCode: 1000,
      appName: 'Mall自助收银'
    }
  }

  async calculateMd5(options: { 
    filePath: string 
  }): Promise<{ md5: string }> {
    console.log('Web平台计算MD5', options)
    return { md5: 'mock_md5_hash' }
  }

  async isAppInstalled(options: { 
    packageName: string 
  }): Promise<{ installed: boolean; versionCode?: number }> {
    console.log('Web平台检查应用安装状态', options)
    return { installed: false }
  }

  async uninstallApp(options: { 
    packageName: string 
  }): Promise<{ success: boolean }> {
    console.log('Web平台卸载应用', options)
    return { success: false }
  }
}
