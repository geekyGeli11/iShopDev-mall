/**
 * 简化配置系统使用示例
 * 只使用环境变量，更简洁明了
 */

import { API_CONFIG, API_ENDPOINTS } from '../constants/api'
import { isDevelopment, getAppInfo, getApiBaseUrl } from '../../../config'

// ===== 直接使用环境变量 =====

const appTitle = import.meta.env.VITE_APP_TITLE
const apiUrl = import.meta.env.VITE_API_BASE_URL
const debugMode = import.meta.env.VITE_DEBUG_MODE === 'true'

console.log('环境变量使用:')
console.log('应用标题:', appTitle)
console.log('API地址:', apiUrl)
console.log('调试模式:', debugMode)

// ===== 使用配置工具函数 =====

const appInfo = getAppInfo()
const baseUrl = getApiBaseUrl()

console.log('\n配置工具函数:')
console.log('应用信息:', appInfo)
console.log('API基础URL:', baseUrl)
console.log('是否开发环境:', isDevelopment())

// ===== 使用统一配置 =====

console.log('\n统一配置使用:')
console.log('完整API配置:', API_CONFIG)

// ===== 实际使用示例 =====

export const ConfigExample = {
  // HTTP 客户端配置
  createApiClient() {
    return {
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      headers: {
        'Content-Type': 'application/json'
      }
    }
  },

  // 支付配置
  getPaymentConfig() {
    return {
      timeout: 30000, // 30秒支付超时
      pollInterval: 2000, // 2秒轮询间隔
      enableMock: false // 关闭模拟支付
    }
  },

  // 会员会话配置
  getSessionConfig() {
    return {
      timeout: 300000, // 5分钟会话超时
      warning: 30000 // 30秒超时警告
    }
  },

  // 根据环境调整行为
  getEnvFeatures() {
    return {
      showDebugInfo: isDevelopment(),
      enableMockPayment: false, // 默认关闭模拟支付
      logLevel: isDevelopment() ? 'debug' : 'info' // 根据环境设置日志级别
    }
  }
}

// ===== 使用说明 =====

/*
简化后的配置使用方式：

1. 直接环境变量:
   const title = import.meta.env.VITE_APP_TITLE

2. 配置工具函数:
   const apiUrl = getApiBaseUrl()
   const isDebug = isDevelopment()

3. 统一配置对象:
   const config = API_CONFIG
   const timeout = API_CONFIG.TIMEOUT

环境文件对应关系：
- npm run dev → env.development
- npm run dev:staging → env.staging  
- npm run build:prod → env.production

部署时覆盖：
VITE_API_BASE_URL=https://new-api.com npm run build:prod
*/ 