/**
 * 防抖函数
 * @param {Function} func - 要防抖的函数
 * @param {number} wait - 延迟时间（毫秒）
 * @param {boolean} immediate - 是否立即执行
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, wait = 300, immediate = false) {
  let timeout
  
  return function executedFunction(...args) {
    const context = this
    
    const later = function() {
      timeout = null
      if (!immediate) func.apply(context, args)
    }
    
    const callNow = immediate && !timeout
    
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
    
    if (callNow) func.apply(context, args)
  }
}

/**
 * 创建可取消的请求
 * 用于避免竞态条件
 */
export class CancellableRequest {
  constructor() {
    this.pendingRequests = new Map()
  }
  
  /**
   * 执行请求
   * @param {string} key - 请求的唯一标识
   * @param {Function} requestFn - 请求函数
   * @returns {Promise} 请求Promise
   */
  async execute(key, requestFn) {
    // 取消之前的同类请求
    this.cancel(key)
    
    // 创建新的取消令牌
    const cancelToken = {
      cancelled: false,
      cancel: () => {
        cancelToken.cancelled = true
      }
    }
    
    this.pendingRequests.set(key, cancelToken)
    
    try {
      const result = await requestFn()
      
      // 检查请求是否已被取消
      if (cancelToken.cancelled) {
        throw new Error('Request cancelled')
      }
      
      this.pendingRequests.delete(key)
      return result
    } catch (error) {
      this.pendingRequests.delete(key)
      throw error
    }
  }
  
  /**
   * 取消指定的请求
   * @param {string} key - 请求的唯一标识
   */
  cancel(key) {
    const cancelToken = this.pendingRequests.get(key)
    if (cancelToken) {
      cancelToken.cancel()
      this.pendingRequests.delete(key)
    }
  }
  
  /**
   * 取消所有请求
   */
  cancelAll() {
    this.pendingRequests.forEach(cancelToken => {
      cancelToken.cancel()
    })
    this.pendingRequests.clear()
  }
}

// 创建全局的请求管理器
export const dashboardRequestManager = new CancellableRequest()
