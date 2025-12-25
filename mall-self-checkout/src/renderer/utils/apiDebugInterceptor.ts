/**
 * API调试拦截器
 * 自动拦截和记录所有API请求的详细信息
 */

import { debugLogger, type ApiLogData } from './debugLogger'

// API请求信息接口
interface ApiRequestInfo {
  id: string
  startTime: number
  method: string
  url: string
  requestData?: any
  headers?: Record<string, string>
}

// API响应信息接口
interface ApiResponseInfo {
  status: number
  statusText: string
  data?: any
  headers?: Record<string, string>
  duration: number
}

class ApiDebugInterceptor {
  private pendingRequests = new Map<string, ApiRequestInfo>()
  private isEnabled = false

  constructor() {
    this.isEnabled = debugLogger.isDebugEnabled()
    if (this.isEnabled) {
      console.log('🌐 API调试拦截器已启用')
    }
  }

  /**
   * 请求拦截器
   */
  interceptRequest = (config: any) => {
    if (!this.isEnabled) return config

    const requestId = this.generateRequestId()
    const startTime = Date.now()
    
    // 记录请求信息
    const requestInfo: ApiRequestInfo = {
      id: requestId,
      startTime,
      method: (config.method || 'GET').toUpperCase(),
      url: this.buildFullUrl(config),
      requestData: this.sanitizeData(config.data),
      headers: this.sanitizeHeaders(config.headers)
    }

    this.pendingRequests.set(requestId, requestInfo)

    // 在config中添加请求ID，用于响应时匹配
    config.metadata = { ...config.metadata, requestId }

    // 记录请求开始日志
    debugLogger.logApi(
      `API请求开始`,
      `${requestInfo.method} ${requestInfo.url}`,
      {
        method: requestInfo.method,
        url: requestInfo.url,
        requestData: requestInfo.requestData,
        status: 'pending'
      } as ApiLogData
    )

    return config
  }

  /**
   * 响应成功拦截器
   */
  interceptResponse = (response: any) => {
    if (!this.isEnabled) return response

    const requestId = response.config?.metadata?.requestId
    if (!requestId) return response

    const requestInfo = this.pendingRequests.get(requestId)
    if (!requestInfo) return response

    const duration = Date.now() - requestInfo.startTime
    
    // 构建响应信息
    const responseInfo: ApiResponseInfo = {
      status: response.status,
      statusText: response.statusText,
      data: this.sanitizeData(response.data),
      headers: this.sanitizeHeaders(response.headers),
      duration
    }

    // 记录成功响应日志
    debugLogger.logApi(
      `API请求成功`,
      `${requestInfo.method} ${requestInfo.url} - ${response.status} (${duration}ms)`,
      {
        method: requestInfo.method,
        url: requestInfo.url,
        requestData: requestInfo.requestData,
        responseData: responseInfo.data,
        status: response.status,
        duration
      } as ApiLogData
    )

    // 清理待处理请求
    this.pendingRequests.delete(requestId)

    return response
  }

  /**
   * 响应错误拦截器
   */
  interceptError = (error: any) => {
    if (!this.isEnabled) return Promise.reject(error)

    const requestId = error.config?.metadata?.requestId
    let requestInfo: ApiRequestInfo | undefined

    if (requestId) {
      requestInfo = this.pendingRequests.get(requestId)
      this.pendingRequests.delete(requestId)
    }

    const duration = requestInfo ? Date.now() - requestInfo.startTime : 0
    const method = requestInfo?.method || error.config?.method?.toUpperCase() || 'UNKNOWN'
    const url = requestInfo?.url || this.buildFullUrl(error.config) || 'UNKNOWN'

    // 构建错误信息
    const errorMessage = this.extractErrorMessage(error)
    const errorData = this.extractErrorData(error)

    // 记录错误日志
    debugLogger.logApi(
      `API请求失败`,
      `${method} ${url} - ${errorMessage} (${duration}ms)`,
      {
        method,
        url,
        requestData: requestInfo?.requestData,
        error: errorMessage,
        status: error.response?.status,
        duration
      } as ApiLogData
    )

    // 同时记录到错误日志
    debugLogger.logError(
      `API错误`,
      `${method} ${url}: ${errorMessage}`,
      {
        request: requestInfo,
        response: error.response,
        error: errorData,
        duration
      }
    )

    return Promise.reject(error)
  }

  /**
   * 手动记录API调用（用于非axios请求）
   */
  logApiCall(
    method: string,
    url: string,
    requestData?: any,
    responseData?: any,
    error?: string,
    duration?: number,
    status?: number
  ) {
    if (!this.isEnabled) return

    const title = error ? 'API请求失败' : 'API请求成功'
    const statusText = error ? error : `${status || 200}`
    const message = `${method.toUpperCase()} ${url} - ${statusText}${duration ? ` (${duration}ms)` : ''}`

    debugLogger.logApi(title, message, {
      method: method.toUpperCase(),
      url,
      requestData: this.sanitizeData(requestData),
      responseData: this.sanitizeData(responseData),
      error,
      status,
      duration
    } as ApiLogData)
  }

  // 私有方法

  private generateRequestId(): string {
    return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  }

  private buildFullUrl(config: any): string {
    if (!config) return 'UNKNOWN'
    
    const baseURL = config.baseURL || ''
    const url = config.url || ''
    
    if (url.startsWith('http')) {
      return url
    }
    
    return baseURL + url
  }

  private sanitizeData(data: any): any {
    if (!data) return data

    try {
      // 深拷贝数据以避免修改原始数据
      const sanitized = JSON.parse(JSON.stringify(data))
      
      // 移除敏感信息
      this.removeSensitiveFields(sanitized)
      
      return sanitized
    } catch {
      return String(data)
    }
  }

  private sanitizeHeaders(headers: any): Record<string, string> {
    if (!headers) return {}

    const sanitized: Record<string, string> = {}
    
    for (const [key, value] of Object.entries(headers)) {
      const lowerKey = key.toLowerCase()
      
      // 隐藏敏感头部信息
      if (lowerKey.includes('authorization') || 
          lowerKey.includes('token') || 
          lowerKey.includes('password') ||
          lowerKey.includes('secret')) {
        sanitized[key] = '***HIDDEN***'
      } else {
        sanitized[key] = String(value)
      }
    }
    
    return sanitized
  }

  private removeSensitiveFields(obj: any): void {
    if (!obj || typeof obj !== 'object') return

    const sensitiveFields = [
      'password', 'token', 'secret', 'key', 'authorization',
      'verifyCode', 'captcha', 'memberCode', 'cardNo'
    ]

    for (const field of sensitiveFields) {
      if (obj.hasOwnProperty(field)) {
        obj[field] = '***HIDDEN***'
      }
    }

    // 递归处理嵌套对象
    for (const value of Object.values(obj)) {
      if (typeof value === 'object' && value !== null) {
        this.removeSensitiveFields(value)
      }
    }
  }

  private extractErrorMessage(error: any): string {
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    
    if (error.response?.statusText) {
      return `${error.response.status} ${error.response.statusText}`
    }
    
    if (error.message) {
      return error.message
    }
    
    return '未知错误'
  }

  private extractErrorData(error: any): any {
    return {
      name: error.name,
      message: error.message,
      code: error.code,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: this.sanitizeData(error.response?.data),
      stack: error.stack
    }
  }

  /**
   * 获取待处理请求数量
   */
  getPendingRequestsCount(): number {
    return this.pendingRequests.size
  }

  /**
   * 获取待处理请求列表
   */
  getPendingRequests(): ApiRequestInfo[] {
    return Array.from(this.pendingRequests.values())
  }

  /**
   * 清理超时的待处理请求
   */
  cleanupTimeoutRequests(timeoutMs: number = 30000): void {
    const now = Date.now()
    const timeoutRequests: string[] = []

    for (const [id, request] of this.pendingRequests.entries()) {
      if (now - request.startTime > timeoutMs) {
        timeoutRequests.push(id)
        
        // 记录超时日志
        debugLogger.logApi(
          'API请求超时',
          `${request.method} ${request.url} - 请求超时 (${now - request.startTime}ms)`,
          {
            method: request.method,
            url: request.url,
            requestData: request.requestData,
            error: '请求超时',
            duration: now - request.startTime
          } as ApiLogData
        )
      }
    }

    // 清理超时请求
    timeoutRequests.forEach(id => this.pendingRequests.delete(id))
  }

  /**
   * 检查是否启用
   */
  isDebugEnabled(): boolean {
    return this.isEnabled
  }
}

// 创建全局实例
export const apiDebugInterceptor = new ApiDebugInterceptor()

// 导出类
export { ApiDebugInterceptor }

// 在全局对象上暴露拦截器用于调试
if (typeof window !== 'undefined' && apiDebugInterceptor.isDebugEnabled()) {
  (window as any).__API_DEBUG_INTERCEPTOR__ = apiDebugInterceptor
  
  // 定期清理超时请求
  setInterval(() => {
    apiDebugInterceptor.cleanupTimeoutRequests()
  }, 60000) // 每分钟清理一次
}
