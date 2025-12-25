/**
 * HTTP 请求封装
 * 
 * @description 统一的API请求服务，包含请求/响应拦截、错误处理、认证管理
 * @author Mall Team
 * @version 1.0.0
 */

import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError
} from 'axios'
import type { ApiResponse, ApiError } from '@shared/types'
import { API_CONFIG } from '../../config/env'
import { apiDebugInterceptor } from '../utils/apiDebugInterceptor'

// Capacitor HTTP支持
import { CapacitorHttp } from '@capacitor/core'
import { Capacitor } from '@capacitor/core'

/**
 * 设备类型枚举
 */
enum DeviceType {
  WINDOWS = 'windows',
  ANDROID = 'android', 
  WEB = 'web'
}

/**
 * 请求服务类
 */
class RequestService {
  private instance: AxiosInstance
  private deviceId: string
  private isNative: boolean

  constructor() {
    this.deviceId = this.getOrCreateDeviceId()
    this.isNative = Capacitor.isNativePlatform()
    this.instance = this.createAxiosInstance()
    if (!this.isNative) {
      this.setupInterceptors()
    }
  }

  /**
   * 创建 axios 实例
   */
  private createAxiosInstance(): AxiosInstance {
    return axios.create({
      baseURL: API_CONFIG.baseURL,
      timeout: API_CONFIG.timeout,
      headers: {
        'Content-Type': 'application/json',
      },
    })
  }

  /**
   * 设置请求和响应拦截器
   */
  private setupInterceptors(): void {
    this.setupRequestInterceptor()
    this.setupResponseInterceptor()
  }

  /**
   * 设置请求拦截器
   */
  private setupRequestInterceptor(): void {
    this.instance.interceptors.request.use(
      (config) => {
        // 添加认证token - 确保包含Bearer前缀
        const token = this.getAuthToken()
        if (token) {
          // 检查token是否已经包含Bearer前缀
          if (token.startsWith('Bearer ')) {
            config.headers.Authorization = token
          } else {
            config.headers.Authorization = `Bearer ${token}`
          }
        }

        // 添加设备信息
        config.headers['Device-Type'] = this.getDeviceType()
        config.headers['Device-Id'] = this.deviceId
        config.headers['Request-Time'] = Date.now().toString()

        // 调试拦截器 - 请求
        const debugConfig = apiDebugInterceptor.interceptRequest(config)

        return debugConfig
      },
      (error: AxiosError) => {
        console.error('Request interceptor error:', error)

        // 调试拦截器 - 请求错误
        apiDebugInterceptor.interceptError(error)

        return Promise.reject(this.createApiError(error))
      }
    )
  }

  /**
   * 设置响应拦截器
   */
  private setupResponseInterceptor(): void {
    this.instance.interceptors.response.use(
      (response: AxiosResponse<ApiResponse>) => {
        const { data } = response

        // 调试拦截器 - 响应成功
        const debugResponse = apiDebugInterceptor.interceptResponse(response)

        // 检查业务状态码
        if (data.code === 200) {
          return debugResponse
        } else {
          const error = this.createApiErrorFromResponse(data)

          // 调试拦截器 - 业务错误
          apiDebugInterceptor.interceptError({
            ...error,
            config: response.config,
            response: response
          })

          return Promise.reject(error)
        }
      },
      (error: AxiosError) => {
        // 调试拦截器 - 响应错误
        apiDebugInterceptor.interceptError(error)

        const apiError = this.handleResponseError(error)
        return Promise.reject(apiError)
      }
    )
  }

  /**
   * 处理响应错误
   */
  private handleResponseError(error: AxiosError): ApiError {
    const apiError = this.createApiError(error)
    
    // 处理特殊错误码
    switch (apiError.code) {
      case 401:
        this.handleUnauthorized()
        break
      case 403:
        this.handleForbidden()
        break
      case 500:
        this.handleServerError()
        break
      default:
        console.error('API Error:', apiError)
    }
    
    return apiError
  }

  /**
   * 处理未授权错误
   */
  private handleUnauthorized(): void {
    // 清除本地认证信息
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    
    // 可以触发重新登录事件
    window.dispatchEvent(new CustomEvent('auth:unauthorized'))
  }

  /**
   * 处理禁止访问错误
   */
  private handleForbidden(): void {
    console.warn('Access forbidden - insufficient permissions')
  }

  /**
   * 处理服务器错误
   */
  private handleServerError(): void {
    console.error('Server error - please try again later')
  }

  /**
   * 从响应数据创建API错误
   */
  private createApiErrorFromResponse(data: ApiResponse): ApiError {
    return {
      name: 'ApiError',
      code: data.code,
      message: data.message,
      details: data.data,
    } as ApiError
  }

  /**
   * 从 Axios 错误创建API错误
   */
  private createApiError(error: AxiosError): ApiError {
    return {
      name: 'ApiError',
      code: error.response?.status || -1,
      message: (error.response?.data as any)?.message || error.message || '网络连接错误',
      details: error.response?.data,
    } as ApiError
  }

  /**
   * 获取认证token
   */
  private getAuthToken(): string | null {
    // 优先使用token（完整格式含Bearer前缀）
    const token = localStorage.getItem('token')
    if (token) {
      return token
    }
    
    // 后备方案：使用access_token（纯token值，需要添加前缀）
    const accessToken = localStorage.getItem('access_token')
    if (accessToken) {
      return accessToken // 返回纯token，在拦截器中会自动添加Bearer前缀
    }
    
    return null
  }

  /**
   * 获取设备类型
   */
  private getDeviceType(): DeviceType {
    // 检查是否为 Electron 环境
    if (typeof window !== 'undefined' && (window as any).electronAPI) {
      return DeviceType.WINDOWS
    }
    
    // 检查是否为 Capacitor 环境
    if (typeof window !== 'undefined' && (window as any).Capacitor) {
      return DeviceType.ANDROID
    }
    
    // 默认为 Web 环境
    return DeviceType.WEB
  }

  /**
   * 获取或创建设备ID
   */
  private getOrCreateDeviceId(): string {
    let deviceId = localStorage.getItem('device_id')
    
    if (!deviceId) {
      deviceId = this.generateDeviceId()
      localStorage.setItem('device_id', deviceId)
    }
    
    return deviceId
  }

  /**
   * 生成设备ID
   */
  private generateDeviceId(): string {
    const timestamp = Date.now().toString(36)
    const randomStr = Math.random().toString(36).substring(2)
    const deviceType = this.getDeviceType()
    
    return `${deviceType}_${timestamp}_${randomStr}`
  }

  /**
   * 发送GET请求
   */
  public async get<T = unknown>(
    url: string, 
    params?: Record<string, any>,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    if (this.isNative) {
      return this.nativeGet<T>(url, params, config)
    }
    
    const response = await this.instance.get<ApiResponse<T>>(url, { params, ...config })
    return response.data
  }

  /**
   * 发送POST请求
   */
  public async post<T = unknown>(
    url: string, 
    data?: unknown, 
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    if (this.isNative) {
      return this.nativePost<T>(url, data, config)
    }
    
    const response = await this.instance.post<ApiResponse<T>>(url, data, config)
    return response.data
  }

  /**
   * PUT 请求
   */
  public async put<T = unknown>(
    url: string, 
    data?: unknown, 
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.put<ApiResponse<T>>(url, data, config)
    return response.data
  }

  /**
   * DELETE 请求
   */
  public async delete<T = unknown>(
    url: string, 
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const response = await this.instance.delete<ApiResponse<T>>(url, config)
    return response.data
  }

  /**
   * 文件上传
   */
  public async upload<T = unknown>(
    url: string,
    file: File,
    onProgress?: (progress: number) => void
  ): Promise<ApiResponse<T>> {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await this.instance.post<ApiResponse<T>>(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          )
          onProgress(percentCompleted)
        }
      },
    })
    
    return response.data
  }

  /**
   * 设置请求token
   */
  public setToken(token: string): void {
    localStorage.setItem('access_token', token)
  }

  /**
   * 清除请求token
   */
  public clearToken(): void {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
  }

  /**
   * 获取完整的请求URL
   */
  public getFullUrl(path: string): string {
    return `${API_CONFIG.baseURL}${path}`;
  }

  /**
   * 原生GET请求（绕过CORS）
   */
  private async nativeGet<T = unknown>(
    url: string,
    params?: Record<string, any>,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const fullUrl = this.buildUrl(url, params)
    const headers = this.buildHeaders(config?.headers)

    const options = {
      url: fullUrl,
      headers,
      method: 'GET' as const
    }

    // 调试日志 - 原生GET请求开始
    const startTime = Date.now()
    apiDebugInterceptor.logApiCall('GET', fullUrl, params, undefined, undefined, undefined, undefined)

    try {
      const response = await CapacitorHttp.request(options)
      const duration = Date.now() - startTime
      const result = this.handleNativeResponse<T>(response)

      // 调试日志 - 原生GET请求成功
      apiDebugInterceptor.logApiCall('GET', fullUrl, params, result, undefined, duration, response.status)

      return result
    } catch (error: any) {
      const duration = Date.now() - startTime

      // 调试日志 - 原生GET请求失败
      apiDebugInterceptor.logApiCall('GET', fullUrl, params, undefined, error.message, duration, error.code)

      throw error
    }
  }

  /**
   * 原生POST请求（绕过CORS）
   */
  private async nativePost<T = unknown>(
    url: string,
    data?: unknown,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T>> {
    const fullUrl = this.buildUrl(url)
    const headers = this.buildHeaders(config?.headers)

    // 调试日志
    console.log('🔍 nativePost 原始数据:', data)
    console.log('🔍 数据类型:', typeof data)
    console.log('🔍 数据构造函数:', data?.constructor?.name)

    let serializedData: string | undefined
    if (data) {
      // 特殊处理URLSearchParams
      if (data instanceof URLSearchParams) {
        serializedData = data.toString()
        console.log('🔍 URLSearchParams序列化:', serializedData)
      } else {
        serializedData = JSON.stringify(data)
        console.log('🔍 JSON序列化后数据:', serializedData)
      }
      console.log('🔍 序列化后长度:', serializedData.length)
    }

    const options = {
      url: fullUrl,
      headers,
      method: 'POST' as const,
      data: serializedData
    }

    console.log('🔍 发送请求选项:', options)

    // 调试日志 - 原生POST请求开始
    const startTime = Date.now()
    apiDebugInterceptor.logApiCall('POST', fullUrl, data, undefined, undefined, undefined, undefined)

    try {
      const response = await CapacitorHttp.request(options)
      console.log('🔍 收到响应:', response)

      const duration = Date.now() - startTime
      const result = this.handleNativeResponse<T>(response)

      // 调试日志 - 原生POST请求成功
      apiDebugInterceptor.logApiCall('POST', fullUrl, data, result, undefined, duration, response.status)

      return result
    } catch (error: any) {
      const duration = Date.now() - startTime

      // 调试日志 - 原生POST请求失败
      apiDebugInterceptor.logApiCall('POST', fullUrl, data, undefined, error.message, duration, error.code)

      throw error
    }
  }

  /**
   * 构建完整URL
   */
  private buildUrl(url: string, params?: Record<string, any>): string {
    const fullUrl = url.startsWith('http') ? url : `${API_CONFIG.baseURL}${url}`
    
    if (params && Object.keys(params).length > 0) {
      const searchParams = new URLSearchParams()
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          searchParams.append(key, String(value))
        }
      })
      return `${fullUrl}?${searchParams.toString()}`
    }
    
    return fullUrl
  }

  /**
   * 构建请求头
   */
  private buildHeaders(additionalHeaders?: Record<string, string> | any): Record<string, string> {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    }
    
    // 添加认证token
    const token = this.getAuthToken()
    if (token) {
      headers.Authorization = token.startsWith('Bearer ') ? token : `Bearer ${token}`
    }
    
    // 添加设备信息
    headers['Device-Type'] = this.getDeviceType()
    headers['Device-Id'] = this.deviceId
    headers['Request-Time'] = Date.now().toString()
    
    // 合并额外头部 - 安全处理Axios头部类型
    if (additionalHeaders) {
      if (typeof additionalHeaders === 'object') {
        Object.entries(additionalHeaders).forEach(([key, value]) => {
          if (typeof value === 'string') {
            headers[key] = value
          } else if (value != null) {
            headers[key] = String(value)
          }
        })
      }
    }
    
    return headers
  }

  /**
   * 处理原生请求响应
   */
  private handleNativeResponse<T>(response: any): ApiResponse<T> {
    console.log('🔍 原生响应状态:', response.status)
    console.log('🔍 原生响应数据类型:', typeof response.data)
    console.log('🔍 原生响应数据:', response.data)

    const data = typeof response.data === 'string' ? JSON.parse(response.data) : response.data
    console.log('🔍 解析后数据:', data)

    if (response.status === 200 && data.code === 200) {
      return data
    } else {
      console.log('❌ 响应错误:', { status: response.status, code: data.code, message: data.message })
      throw {
        name: 'ApiError',
        code: data.code || response.status,
        message: data.message || '请求失败',
        details: data
      } as ApiError
    }
  }
}

// 创建默认实例
export const request = new RequestService()

// 导出请求服务类
export { RequestService }

// 兼容性导出
export const RequestUtil = request 