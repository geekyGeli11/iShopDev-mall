/**
 * APK安装器插件
 * 提供Android APK安装功能
 */

import { registerPlugin } from '@capacitor/core'

export interface AppInstallerPlugin {
  /**
   * 安装APK文件
   * @param options 安装选项
   */
  installApk(options: { 
    filePath: string
    packageName?: string
    silent?: boolean 
  }): Promise<{ success: boolean; error?: string }>

  /**
   * 检查安装权限
   */
  checkInstallPermission(): Promise<{ hasPermission: boolean }>

  /**
   * 请求安装权限
   */
  requestInstallPermission(): Promise<{ granted: boolean }>

  /**
   * 获取APK信息
   * @param options APK文件路径
   */
  getApkInfo(options: { 
    filePath: string 
  }): Promise<{ 
    packageName: string
    versionName: string
    versionCode: number
    appName: string
  }>

  /**
   * 计算文件MD5
   * @param options 文件路径
   */
  calculateMd5(options: { 
    filePath: string 
  }): Promise<{ md5: string }>

  /**
   * 检查应用是否已安装
   * @param options 包名
   */
  isAppInstalled(options: { 
    packageName: string 
  }): Promise<{ installed: boolean; versionCode?: number }>

  /**
   * 卸载应用
   * @param options 包名
   */
  uninstallApp(options: { 
    packageName: string 
  }): Promise<{ success: boolean }>
}

const AppInstaller = registerPlugin<AppInstallerPlugin>('AppInstaller', {
  web: () => import('./web/AppInstallerWeb').then(m => new m.AppInstallerWeb()),
})

export default AppInstaller
