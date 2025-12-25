/**
 * 应用相关常量
 */

// 会员会话配置
export const MEMBER_SESSION_CONFIG = {
  TIMEOUT_WARNING: 30000,     // 30秒显示警告
  TIMEOUT_LOGOUT: 60000,      // 60秒强制退出
  POST_PAYMENT_DELAY: 3000,   // 支付后3秒退出
  ACTIVITY_CHECK_INTERVAL: 5000, // 活跃状态检查间隔
} as const

// 扫码配置
export const SCANNER_CONFIG = {
  AUTO_FOCUS: true,
  ENABLE_SOUND: true,
  ENABLE_VIBRATION: true,
  SCAN_DELAY: 1000,
  CAMERA_WIDTH: 640,
  CAMERA_HEIGHT: 480,
  FACING_MODE: 'environment',
} as const

// 应用配置
export const APP_CONFIG = {
  NAME: 'Mall自助收银系统',
  VERSION: '1.0.0',
  AUTHOR: 'Mall Team',
  DESCRIPTION: '基于Electron + Vue3的跨平台自助收银系统',
} as const

// 本地存储键名
export const STORAGE_KEYS = {
  USER_PREFERENCES: 'user_preferences',
  DEVICE_CONFIG: 'device_config',
  SCANNER_CONFIG: 'scanner_config',
  PRINTER_CONFIG: 'printer_config',
  LAST_LOGIN_PHONE: 'last_login_phone',
} as const

// 设备类型
export const DEVICE_TYPES = {
  WINDOWS: 'windows',
  ANDROID: 'android',
  WEB: 'web',
} as const 