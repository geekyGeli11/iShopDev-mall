/**
 * 应用相关类型定义
 */

// 应用主题
export enum AppTheme {
  LIGHT = 'light',
  DARK = 'dark',
  AUTO = 'auto'
}

// 语言设置
export enum Language {
  ZH_CN = 'zh-cn',
  EN_US = 'en-us'
}

// 设备类型
export enum DeviceType {
  DESKTOP = 'desktop',
  TABLET = 'tablet',
  MOBILE = 'mobile'
}

// 应用设置
export interface AppSettings {
  theme: AppTheme
  language: Language
  deviceType: DeviceType
  autoLogoutTime: number // 自动退出时间（秒）
  scanSoundEnabled: boolean // 扫码音效
  printReceiptAuto: boolean // 自动打印小票
}

// 应用状态
export interface AppState {
  isInitialized: boolean
  isLoading: boolean
  settings: AppSettings
  version: string
  buildTime: string
}

// 错误信息
export interface AppError {
  code: string
  message: string
  details?: any
  timestamp: number
} 