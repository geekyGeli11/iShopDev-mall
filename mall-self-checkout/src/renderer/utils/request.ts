/**
 * HTTP 请求工具类
 * 统一处理 API 调用，包含错误处理、认证、重试等功能
 */

import { config, log } from '../../config/env'
import { API_ENDPOINTS, BASE_URL, HTTP_METHODS, STATUS_CODES, BUSINESS_CODES } from '@shared/constants/api'
import type { ApiResponse } from '@shared/types'
import { ApiError } from '@shared/types'

// 请求接口
export interface RequestOptions {
  method?: string
  headers?: Record<string, string>
  params?: Record<string, any>
  data?: any
  timeout?: number
  retry?: number
  skipAuth?: boolean
}

// 请求工具类
class RequestUtil {
  private baseURL: string
  private timeout: number
  private retryCount: number
  private retryDelay: number

  constructor() {
    this.baseURL = config.api.baseURL
    this.timeout = config.api.timeout
    this.retryCount = config.api.retryCount
    this.retryDelay = config.api.retryDelay
  }

  /**
   * 通用请求方法
   */
  async request<T = any>(url: string, options: RequestOptions = {}): Promise<T> {
    const {
      method = HTTP_METHODS.GET,
      headers = {},
      params,
      data,
      timeout = this.timeout,
      retry = this.retryCount,
      skipAuth = false
    } = options

    // 构建完整URL
    const fullUrl = this.buildUrl(url, params)
    
    // 构建请求配置
    const requestConfig: RequestInit = {
      method,
      headers: this.buildHeaders(headers, skipAuth),
      body: data ? JSON.stringify(data) : undefined,
      signal: AbortSignal.timeout(timeout)
    }

    log.debug(`🚀 API Request: ${method} ${fullUrl}`, { data, headers })

    try {
      const response = await this.executeRequest(fullUrl, requestConfig, retry)
      return await this.handleResponse<T>(response)
    } catch (error) {
      log.error(`❌ API Request Failed: ${method} ${fullUrl}`, error)
      throw this.handleError(error)
    }
  }

  /**
   * GET 请求
   */
  async get<T = any>(url: string, params?: Record<string, any>, options?: Omit<RequestOptions, 'method' | 'params'>): Promise<T> {
    return this.request<T>(url, { ...options, method: HTTP_METHODS.GET, params })
  }

  /**
   * POST 请求
   */
  async post<T = any>(url: string, data?: any, options?: Omit<RequestOptions, 'method' | 'data'>): Promise<T> {
    return this.request<T>(url, { ...options, method: HTTP_METHODS.POST, data })
  }

  /**
   * PUT 请求
   */
  async put<T = any>(url: string, data?: any, options?: Omit<RequestOptions, 'method' | 'data'>): Promise<T> {
    return this.request<T>(url, { ...options, method: HTTP_METHODS.PUT, data })
  }

  /**
   * DELETE 请求
   */
  async delete<T = any>(url: string, options?: Omit<RequestOptions, 'method'>): Promise<T> {
    return this.request<T>(url, { ...options, method: HTTP_METHODS.DELETE })
  }

  /**
   * 构建完整URL
   */
  private buildUrl(url: string, params?: Record<string, any>): string {
    const fullUrl = url.startsWith('http') ? url : `${this.baseURL}${url}`
    
    if (!params || Object.keys(params).length === 0) {
      return fullUrl
    }

    const searchParams = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        searchParams.append(key, String(value))
      }
    })

    const queryString = searchParams.toString()
    return queryString ? `${fullUrl}?${queryString}` : fullUrl
  }

  /**
   * 构建请求头
   */
  private buildHeaders(customHeaders: Record<string, string>, skipAuth: boolean): Record<string, string> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...customHeaders
    }

    // 添加认证信息
    if (!skipAuth) {
      const token = this.getAuthToken()
      if (token) {
        headers['Authorization'] = `Bearer ${token}`
      }
    }

    return headers
  }

  /**
   * 执行请求（带重试）
   */
  private async executeRequest(url: string, config: RequestInit, retryCount: number): Promise<Response> {
    let lastError: Error | null = null

    for (let i = 0; i <= retryCount; i++) {
      try {
        const response = await fetch(url, config)
        
        // 网络级别成功
        if (response.ok || response.status < 500) {
          return response
        }
        
        // 服务器错误且还有重试次数
        if (i < retryCount) {
          await this.delay(this.retryDelay * (i + 1))
          continue
        }
        
        throw new Error(`HTTP Error: ${response.status} ${response.statusText}`)
      } catch (error) {
        lastError = error as Error
        
        // 如果是网络错误且还有重试次数
        if (i < retryCount && this.isRetryableError(error)) {
          log.warn(`⚠️ Request failed, retrying... (${i + 1}/${retryCount})`, error)
          await this.delay(this.retryDelay * (i + 1))
          continue
        }
        
        throw error
      }
    }

    throw lastError
  }

  /**
   * 处理响应
   */
  private async handleResponse<T>(response: Response): Promise<T> {
    const contentType = response.headers.get('content-type')
    
    let responseData: any
    if (contentType?.includes('application/json')) {
      responseData = await response.json()
    } else {
      responseData = await response.text()
    }

    log.debug(`✅ API Response: ${response.status}`, responseData)

    // HTTP 状态码检查
    if (!response.ok) {
      throw new ApiError(
        responseData?.message || `HTTP Error: ${response.status}`,
        response.status,
        responseData
      )
    }

    // 业务状态码检查
    if (responseData && typeof responseData === 'object' && 'code' in responseData) {
      if (responseData.code !== BUSINESS_CODES.SUCCESS) {
        throw new ApiError(
          responseData.message || '业务处理失败',
          responseData.code,
          responseData.data
        )
      }
      
      return responseData.data as T
    }

    return responseData as T
  }

  /**
   * 处理错误
   */
  private handleError(error: any): ApiError {
    // 如果已经是 ApiError 格式
    if (error && typeof error === 'object' && 'code' in error) {
      return error as ApiError
    }

    // 网络错误
    if (error instanceof TypeError && error.message.includes('fetch')) {
      return new ApiError(
        '网络连接失败，请检查网络设置',
        STATUS_CODES.BAD_GATEWAY,
        error.message
      )
    }

    // 超时错误
    if (error.name === 'AbortError' || error.message.includes('timeout')) {
      return new ApiError(
        '请求超时，请重试',
        STATUS_CODES.SERVICE_UNAVAILABLE,
        error.message
      )
    }

    // 其他错误
    return new ApiError(
      error.message || '请求失败',
      STATUS_CODES.INTERNAL_ERROR,
      error
    )
  }

  /**
   * 获取认证Token
   */
  private getAuthToken(): string | null {
    // 从 localStorage 获取 token
    return localStorage.getItem('access_token')
  }

  /**
   * 判断是否为可重试错误
   */
  private isRetryableError(error: any): boolean {
    // 网络错误
    if (error instanceof TypeError && error.message.includes('fetch')) {
      return true
    }
    
    // 超时错误
    if (error.name === 'AbortError') {
      return true
    }
    
    return false
  }

  /**
   * 延迟函数
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms))
  }

  /**
   * 清除认证信息
   */
  clearAuth(): void {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
  }

  /**
   * 设置认证信息
   */
  setAuth(token: string, refreshToken?: string): void {
    localStorage.setItem('access_token', token)
    if (refreshToken) {
      localStorage.setItem('refresh_token', refreshToken)
    }
  }
}

// 创建请求工具实例
export const request = new RequestUtil()

// 导出常用方法
export const { get, post, put, delete: del } = request

// RequestOptions 已在上面作为 interface 导出 