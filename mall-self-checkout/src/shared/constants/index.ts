/**
 * 共享常量定义
 */

// API相关常量
export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8201/mall-selfcheck',
  TIMEOUT: 10000,
  RETRY_TIMES: 3
}

// 导出API端点
export { API_ENDPOINTS } from './api'

// 应用配置常量
export const APP_CONFIG = {
  NAME: 'Mall 自助收银系统',
  VERSION: '1.0.0',
  AUTO_LOGOUT_TIME: 60000, // 60秒
  SCAN_TIMEOUT: 5000, // 5秒
  PAYMENT_TIMEOUT: 30000 // 30秒
}

// 本地存储键名
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  DEVICE_ID: 'device_id',
  USER_SETTINGS: 'user_settings',
  CART_DATA: 'cart_data'
}

// 事件名称
export const EVENTS = {
  AUTH_UNAUTHORIZED: 'auth:unauthorized',
  CART_UPDATED: 'cart:updated',
  PAYMENT_SUCCESS: 'payment:success',
  SCAN_SUCCESS: 'scan:success'
} 