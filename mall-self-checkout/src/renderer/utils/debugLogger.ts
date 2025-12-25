/**
 * 调试日志管理器
 * 用于收集和管理调试信息，仅在开发环境下工作
 */

import { config } from '../../config/env'

// 日志类型枚举
export enum LogType {
  SCAN = 'scan',           // 扫码相关
  API = 'api',             // API请求相关
  TTS = 'tts',             // 语音播报相关
  ERROR = 'error',         // 错误信息
  INFO = 'info',           // 一般信息
  WARNING = 'warning',     // 警告信息
  SUCCESS = 'success'      // 成功信息
}

// 日志级别枚举
export enum LogLevel {
  DEBUG = 0,
  INFO = 1,
  WARNING = 2,
  ERROR = 3
}

// 日志条目接口
export interface LogEntry {
  id: string
  timestamp: number
  type: LogType
  level: LogLevel
  title: string
  message: string
  data?: any
  stack?: string
}

// 扫码日志数据接口
export interface ScanLogData {
  scanType: 'product' | 'member' | 'payment'
  barcode?: string
  memberCode?: string
  status: 'start' | 'progress' | 'success' | 'error'
  result?: any
  error?: string
  duration?: number
}

// API日志数据接口
export interface ApiLogData {
  method: string
  url: string
  requestData?: any
  responseData?: any
  status?: number
  duration?: number
  error?: string
}

class DebugLogger {
  private logs: LogEntry[] = []
  private maxLogs = 500
  private listeners: Array<(logs: LogEntry[]) => void> = []
  private isEnabled = false

  constructor() {
    // 检查多种启用条件
    const isDev = config.isDev || import.meta.env.MODE === 'development' || import.meta.env.DEV
    const debugEnabled = config.debug?.enableLog !== false // 默认启用，除非明确禁用

    this.isEnabled = isDev && debugEnabled

    console.log('🔍 调试日志器初始化:')
    console.log('  - config.isDev:', config.isDev)
    console.log('  - import.meta.env.MODE:', import.meta.env.MODE)
    console.log('  - import.meta.env.DEV:', import.meta.env.DEV)
    console.log('  - config.debug.enableLog:', config.debug?.enableLog)
    console.log('  - 最终启用状态:', this.isEnabled)

    if (this.isEnabled) {
      console.log('🐛 调试日志管理器已启用')

      // 监听全局错误
      window.addEventListener('error', this.handleGlobalError.bind(this))
      window.addEventListener('unhandledrejection', this.handleUnhandledRejection.bind(this))

      // 添加初始化日志
      this.addLog(LogType.INFO, LogLevel.INFO, '调试系统启动', '调试日志管理器已成功初始化')
    } else {
      console.log('⚠️ 调试日志管理器未启用')
    }
  }

  /**
   * 添加日志条目
   */
  private addLog(type: LogType, level: LogLevel, title: string, message: string, data?: any, stack?: string): void {
    if (!this.isEnabled) return

    const logEntry: LogEntry = {
      id: this.generateId(),
      timestamp: Date.now(),
      type,
      level,
      title,
      message,
      data,
      stack
    }

    this.logs.unshift(logEntry)

    // 限制日志数量
    if (this.logs.length > this.maxLogs) {
      this.logs = this.logs.slice(0, this.maxLogs)
    }

    // 通知监听器
    this.notifyListeners()

    // 控制台输出
    this.consoleOutput(logEntry)
  }

  /**
   * 扫码相关日志
   */
  logScan(title: string, message: string, data?: ScanLogData): void {
    this.addLog(LogType.SCAN, LogLevel.INFO, title, message, data)
  }

  /**
   * API请求日志
   */
  logApi(title: string, message: string, data?: ApiLogData): void {
    const level = data?.error ? LogLevel.ERROR : LogLevel.INFO
    this.addLog(LogType.API, level, title, message, data)
  }

  /**
   * 错误日志
   */
  logError(title: string, message: string, error?: Error | any): void {
    const stack = error instanceof Error ? error.stack : undefined
    this.addLog(LogType.ERROR, LogLevel.ERROR, title, message, error, stack)
  }

  /**
   * 信息日志
   */
  logInfo(title: string, message: string, data?: any): void {
    this.addLog(LogType.INFO, LogLevel.INFO, title, message, data)
  }

  /**
   * 警告日志
   */
  logWarning(title: string, message: string, data?: any): void {
    this.addLog(LogType.WARNING, LogLevel.WARNING, title, message, data)
  }

  /**
   * 成功日志
   */
  logSuccess(title: string, message: string, data?: any): void {
    this.addLog(LogType.SUCCESS, LogLevel.INFO, title, message, data)
  }

  /**
   * TTS语音播报日志
   */
  logTTS(title: string, message: string, level: LogLevel = LogLevel.INFO, data?: any): void {
    this.addLog(LogType.TTS, level, title, message, data)
  }

  /**
   * 获取所有日志
   */
  getLogs(): LogEntry[] {
    return [...this.logs]
  }

  /**
   * 根据类型过滤日志
   */
  getLogsByType(type: LogType): LogEntry[] {
    return this.logs.filter(log => log.type === type)
  }

  /**
   * 根据级别过滤日志
   */
  getLogsByLevel(level: LogLevel): LogEntry[] {
    return this.logs.filter(log => log.level >= level)
  }

  /**
   * 清空日志
   */
  clearLogs(): void {
    this.logs = []
    this.notifyListeners()
  }

  /**
   * 添加监听器
   */
  addListener(listener: (logs: LogEntry[]) => void): void {
    this.listeners.push(listener)
  }

  /**
   * 移除监听器
   */
  removeListener(listener: (logs: LogEntry[]) => void): void {
    const index = this.listeners.indexOf(listener)
    if (index > -1) {
      this.listeners.splice(index, 1)
    }
  }

  /**
   * 导出日志为JSON
   */
  exportLogs(): string {
    return JSON.stringify(this.logs, null, 2)
  }

  /**
   * 检查是否启用
   */
  isDebugEnabled(): boolean {
    return this.isEnabled
  }

  // 私有方法

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2)
  }

  private notifyListeners(): void {
    this.listeners.forEach(listener => {
      try {
        listener([...this.logs])
      } catch (error) {
        console.error('调试日志监听器错误:', error)
      }
    })
  }

  private consoleOutput(log: LogEntry): void {
    const emoji = this.getLogEmoji(log.type, log.level)
    const timestamp = new Date(log.timestamp).toLocaleTimeString()
    
    const style = this.getLogStyle(log.level)
    const message = `${emoji} [${timestamp}] ${log.title}: ${log.message}`
    
    switch (log.level) {
      case LogLevel.ERROR:
        console.error(message, log.data || '', log.stack || '')
        break
      case LogLevel.WARNING:
        console.warn(message, log.data || '')
        break
      default:
        console.log(`%c${message}`, style, log.data || '')
    }
  }

  private getLogEmoji(type: LogType, level: LogLevel): string {
    if (level === LogLevel.ERROR) return '❌'
    if (level === LogLevel.WARNING) return '⚠️'
    
    switch (type) {
      case LogType.SCAN: return '📱'
      case LogType.API: return '🌐'
      case LogType.SUCCESS: return '✅'
      case LogType.INFO: return 'ℹ️'
      default: return '🐛'
    }
  }

  private getLogStyle(level: LogLevel): string {
    switch (level) {
      case LogLevel.ERROR: return 'color: #ff4757; font-weight: bold;'
      case LogLevel.WARNING: return 'color: #ffa502; font-weight: bold;'
      case LogLevel.INFO: return 'color: #3742fa;'
      default: return 'color: #2f3542;'
    }
  }

  private handleGlobalError(event: ErrorEvent): void {
    this.logError(
      '全局错误',
      event.message,
      {
        filename: event.filename,
        lineno: event.lineno,
        colno: event.colno,
        error: event.error
      }
    )
  }

  private handleUnhandledRejection(event: PromiseRejectionEvent): void {
    this.logError(
      '未处理的Promise拒绝',
      event.reason?.message || '未知错误',
      event.reason
    )
  }
}

// 创建全局实例
export const debugLogger = new DebugLogger()

// 导出类型和枚举
export { DebugLogger }

// 在全局对象上暴露调试器用于调试
if (typeof window !== 'undefined' && debugLogger.isDebugEnabled()) {
  (window as any).__DEBUG_LOGGER__ = debugLogger
}
