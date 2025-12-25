/**
 * 环境配置管理
 * 统一管理不同环境下的配置项
 */

// 环境类型定义
export type Environment = 'development' | 'production' | 'test'

// 平台类型定义
export type Platform = 'desktop' | 'mobile' | 'capacitor'

// 应用配置接口
export interface AppConfig {
  // 环境信息
  env: Environment
  platform: Platform
  isDev: boolean
  isProd: boolean
  isMobile: boolean
  
  // API配置
  api: {
    baseURL: string
    timeout: number
    retryCount: number
    retryDelay: number
  }
  
  // 支付配置
  payment: {
    timeout: number
    qrExpireMinutes: number
    statusPollInterval: number
    maxPollCount: number
    mockEnabled: boolean
  }
  
  // 会员配置
  member: {
    sessionTimeout: number
    autoLogoutDelay: number
    verifyCodeExpire: number
    activityRefreshInterval: number
  }
  
  // 扫码配置
  scanner: {
    timeout: number
    supportedFormats: string[]
    enableSound: boolean
    enableVibration: boolean
  }
  
  // 安全配置
  security: {
    maxLoginAttempts: number
    loginLockDuration: number
    dataEncryption: boolean
    enableMask: boolean
  }
  
  // 调试配置
  debug: {
    enableLog: boolean
    enableMock: boolean
    showErrorDetail: boolean
  }
}

// 获取当前环境
const getEnvironment = (): Environment => {
  const env = import.meta.env.MODE || 'development'
  return env as Environment
}

// 检测当前平台
const detectPlatform = (): Platform => {
  const isCapacitor = window.location.protocol === 'capacitor:'
  const isAndroid = window.navigator.userAgent.includes('Android')
  const isMobile = window.navigator.userAgent.includes('Mobile')
  
  if (isCapacitor) return 'capacitor'
  if (isAndroid || isMobile) return 'mobile'
  return 'desktop'
}

// 根据平台和环境获取API地址
const getApiBaseUrl = (platform: Platform, env: Environment): string => {
  const defaultUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8201/mall-selfcheck'
  
  // 只在开发环境区分移动端和桌面端
  if (env === 'development') {
    switch (platform) {
      case 'mobile':
      case 'capacitor':
        return import.meta.env.VITE_MOBILE_API_BASE_URL || 'http://10.0.2.2:8201/mall-selfcheck'
      case 'desktop':
      default:
        return defaultUrl
    }
  }
  
  // 生产环境和预发布环境，所有平台使用统一API地址
  return defaultUrl
}



// 创建配置对象
const createConfig = (): AppConfig => {
  const env = getEnvironment()
  const platform = detectPlatform()
  const isDev = env === 'development'
  const isProd = env === 'production'
  const isMobile = platform === 'mobile' || platform === 'capacitor'
  
  return {
    env,
    platform,
    isDev,
    isProd,
    isMobile,
    
    api: {
      baseURL: getApiBaseUrl(platform, env),
      timeout: isDev ? 10000 : 30000,
      retryCount: 3,
      retryDelay: isDev ? 1000 : 2000
    },
    
    payment: {
      timeout: 300, // 5分钟
      qrExpireMinutes: 5,
      statusPollInterval: 2000, // 2秒
      maxPollCount: 150,
      mockEnabled: import.meta.env.VITE_MOCK_PAYMENT === 'true' || isDev
    },
    
    member: {
      sessionTimeout: 3600, // 1小时
      autoLogoutDelay: 3, // 3秒
      verifyCodeExpire: 300, // 5分钟
      activityRefreshInterval: isDev ? 30000 : 60000 // 开发环境30秒，生产环境1分钟
    },
    
    scanner: {
      timeout: 30000, // 30秒
      supportedFormats: ['EAN_13', 'EAN_8', 'CODE_128', 'QR_CODE'],
      enableSound: true,
      enableVibration: true
    },
    
    security: {
      maxLoginAttempts: isDev ? 5 : 3,
      loginLockDuration: isDev ? 900 : 1800, // 开发环境15分钟，生产环境30分钟
      dataEncryption: isProd, // 生产环境启用加密
      enableMask: true
    },
    
    debug: {
      enableLog: import.meta.env.VITE_DEBUG_MODE === 'true' || isDev,
      enableMock: import.meta.env.VITE_MOCK_PAYMENT === 'true' || isDev,
      showErrorDetail: isDev
    }
  }
}

// 导出配置
export const config = createConfig()

// 导出常用配置（旧版本，已被上面新版本替代）
// export const {
//   api: API_CONFIG,
//   payment: PAYMENT_CONFIG,
//   member: MEMBER_CONFIG,
//   scanner: SCANNER_CONFIG,
//   security: SECURITY_CONFIG,
//   debug: DEBUG_CONFIG
// } = config

// 工具函数
export const isDevelopment = () => config.isDev
export const isProduction = () => config.isProd

// 日志工具
export const log = {
  info: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog) {
      console.log(`[INFO] ${message}`, ...args)
    }
  },
  
  warn: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog) {
      console.warn(`[WARN] ${message}`, ...args)
    }
  },
  
  error: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog) {
      console.error(`[ERROR] ${message}`, ...args)
    }
  },
  
  debug: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog && config.isDev) {
      console.debug(`[DEBUG] ${message}`, ...args)
    }
  }
} 

// 重新导出配置以保持兼容性
export const envConfig = config

// 导出兼容 unified-config 的接口
export const unifiedConfig = config
export const API_CONFIG = config.api
export const PAYMENT_CONFIG = config.payment
export const MEMBER_CONFIG = config.member
export const SCANNER_CONFIG = config.scanner
export const SECURITY_CONFIG = config.security
export const DEBUG_CONFIG = config.debug

// 导出平台检测工具
export const isMobile = () => config.isMobile

// 导出日志工具（从 unified-config 合并过来的功能）
export const logger = {
  info: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog) {
      console.log(`[INFO] ${message}`, ...args)
    }
  },
  
  warn: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog) {
      console.warn(`[WARN] ${message}`, ...args)
    }
  },
  
  error: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog) {
      console.error(`[ERROR] ${message}`, ...args)
    }
  },
  
  debug: (message: string, ...args: any[]) => {
    if (DEBUG_CONFIG.enableLog && config.isDev) {
      console.debug(`[DEBUG] ${message}`, ...args)
    }
  }
}

// 导出应用配置
export const APP_CONFIG = {
  title: import.meta.env.VITE_APP_TITLE || 'Mall自助收银系统',
  version: import.meta.env.VITE_APP_VERSION || '1.0.0'
}

// 在全局对象上暴露配置用于调试
if (typeof window !== 'undefined') {
  (window as any).__ENV_CONFIG__ = config
  ;(window as any).__UNIFIED_CONFIG__ = config // 兼容性
} 