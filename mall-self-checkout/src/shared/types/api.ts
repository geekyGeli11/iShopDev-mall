/**
 * API 相关类型定义
 */

// 基础API响应结构
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
  success: boolean
}

// 分页响应结构
export interface PaginatedResponse<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  hasNext: boolean
}

// 请求配置
export interface RequestConfig {
  baseURL?: string
  timeout?: number
  headers?: Record<string, string>
  withCredentials?: boolean
}

// API错误类型
export interface ApiError {
  code: number
  message: string
  details?: any
} 