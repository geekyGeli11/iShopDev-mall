/**
 * 跨平台工具类
 * 处理 Windows 桌面端和 Android 移动端的平台差异
 */

// 平台类型枚举
export enum PlatformType {
  WINDOWS = 'windows',
  ANDROID = 'android',
  WEB = 'web',
  UNKNOWN = 'unknown'
}

// 设备类型枚举
export enum DeviceType {
  DESKTOP = 'desktop',
  TABLET = 'tablet',
  MOBILE = 'mobile',
  UNKNOWN = 'unknown'
}

// 平台信息接口
export interface PlatformInfo {
  platform: PlatformType
  deviceType: DeviceType
  screenWidth: number
  screenHeight: number
  pixelRatio: number
  isTouch: boolean
  isElectron: boolean
  isCapacitor: boolean
  userAgent: string
}

/**
 * 获取当前平台类型
 */
export function getPlatformType(): PlatformType {
  // 检查是否在 Capacitor 环境中（Android 应用）
  if (window.Capacitor) {
    return PlatformType.ANDROID
  }
  
  // 检查是否在 Electron 环境中（Windows 应用）
  if (window.electronAPI || window.require) {
    return PlatformType.WINDOWS
  }
  
  // 通过 User Agent 检测
  const userAgent = navigator.userAgent.toLowerCase()
  
  if (userAgent.includes('android')) {
    return PlatformType.ANDROID
  }
  
  if (userAgent.includes('windows')) {
    return PlatformType.WINDOWS
  }
  
  // 默认为 Web 平台
  return PlatformType.WEB
}

/**
 * 获取设备类型
 */
export function getDeviceType(): DeviceType {
  const width = window.innerWidth
  
  if (width >= 1024) {
    return DeviceType.DESKTOP
  } else if (width >= 768) {
    return DeviceType.TABLET
  } else if (width >= 320) {
    return DeviceType.MOBILE
  }
  
  return DeviceType.UNKNOWN
}

/**
 * 检测是否为触摸设备
 */
export function isTouchDevice(): boolean {
  return 'ontouchstart' in window || 
         navigator.maxTouchPoints > 0 || 
         (navigator as any).msMaxTouchPoints > 0
}

/**
 * 获取完整的平台信息
 */
export function getPlatformInfo(): PlatformInfo {
  return {
    platform: getPlatformType(),
    deviceType: getDeviceType(),
    screenWidth: window.innerWidth,
    screenHeight: window.innerHeight,
    pixelRatio: window.devicePixelRatio || 1,
    isTouch: isTouchDevice(),
    isElectron: !!(window.electronAPI || window.require),
    isCapacitor: !!window.Capacitor,
    userAgent: navigator.userAgent
  }
}

/**
 * 检查是否为移动端
 */
export function isMobile(): boolean {
  const deviceType = getDeviceType()
  return deviceType === DeviceType.MOBILE || deviceType === DeviceType.TABLET
}

/**
 * 检查是否为桌面端
 */
export function isDesktop(): boolean {
  return getDeviceType() === DeviceType.DESKTOP
}

/**
 * 检查是否为 Android 平台
 */
export function isAndroid(): boolean {
  return getPlatformType() === PlatformType.ANDROID
}

/**
 * 检查是否为 Windows 平台
 */
export function isWindows(): boolean {
  return getPlatformType() === PlatformType.WINDOWS
}

/**
 * 获取适合当前平台的CSS类名
 */
export function getPlatformClasses(): string[] {
  const info = getPlatformInfo()
  const classes: string[] = []
  
  // 添加平台类
  classes.push(`platform-${info.platform}`)
  
  // 添加设备类型类
  classes.push(`device-${info.deviceType}`)
  
  // 添加触摸类
  if (info.isTouch) {
    classes.push('touch-device')
  } else {
    classes.push('no-touch')
  }
  
  // 添加高分辨率类
  if (info.pixelRatio > 1) {
    classes.push('high-dpi')
  }
  
  // 添加特殊环境类
  if (info.isElectron) {
    classes.push('electron-app')
  }
  
  if (info.isCapacitor) {
    classes.push('capacitor-app')
  }
  
  return classes
}

/**
 * 应用平台特定的CSS类到body元素
 */
export function applyPlatformClasses(): void {
  const classes = getPlatformClasses()
  document.body.classList.add(...classes)
  
  // 初始化深色模式
  initializeDarkMode()
}

/**
 * 初始化深色模式
 */
function initializeDarkMode(): void {
  // 检测系统偏好或用户设置
  const savedTheme = localStorage.getItem('theme-preference')
  const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
  
  let shouldUseDarkMode = false
  
  if (savedTheme) {
    switch (savedTheme) {
      case 'dark':
        shouldUseDarkMode = true
        break
      case 'light':
        shouldUseDarkMode = false
        break
      case 'auto':
      default:
        shouldUseDarkMode = systemPrefersDark
        break
    }
  } else {
    // 默认跟随系统偏好
    shouldUseDarkMode = systemPrefersDark
  }
  
  // 应用主题
  const root = document.documentElement
  if (shouldUseDarkMode) {
    root.classList.add('dark-mode')
    root.classList.remove('light-mode')
    root.setAttribute('data-theme', 'dark')
  } else {
    root.classList.add('light-mode')
    root.classList.remove('dark-mode')
    root.setAttribute('data-theme', 'light')
  }
}

/**
 * 获取适合当前平台的字体大小倍数
 */
export function getFontSizeMultiplier(): number {
  const info = getPlatformInfo()
  
  // Android 移动端使用较大字体
  if (info.platform === PlatformType.ANDROID && info.deviceType === DeviceType.MOBILE) {
    return 1.1
  }
  
  // 高分辨率屏幕使用稍大字体
  if (info.pixelRatio > 2) {
    return 1.05
  }
  
  return 1.0
}

/**
 * 获取适合当前平台的触摸目标最小尺寸
 */
export function getMinTouchTargetSize(): number {
  const info = getPlatformInfo()
  
  // 移动端需要更大的触摸目标
  if (info.isTouch) {
    return info.deviceType === DeviceType.MOBILE ? 48 : 44
  }
  
  // 桌面端可以使用较小的目标
  return 32
}

/**
 * 防抖函数 - 优化移动端性能
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout | null = null
  
  return (...args: Parameters<T>) => {
    if (timeout) {
      clearTimeout(timeout)
    }
    
    timeout = setTimeout(() => {
      func(...args)
    }, wait)
  }
}

/**
 * 节流函数 - 优化滚动和触摸事件
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let inThrottle = false
  
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func(...args)
      inThrottle = true
      setTimeout(() => {
        inThrottle = false
      }, wait)
    }
  }
}

/**
 * 触觉反馈 - Android 设备振动
 */
export function hapticFeedback(pattern: number | number[] = 50): void {
  if ('vibrate' in navigator && isAndroid()) {
    try {
      navigator.vibrate(pattern)
    } catch (error) {
      console.warn('Vibration not supported:', error)
    }
  }
}

/**
 * 安全区域适配
 */
export function getSafeAreaInsets(): {
  top: number
  right: number
  bottom: number
  left: number
} {
  const style = getComputedStyle(document.documentElement)
  
  return {
    top: parseInt(style.getPropertyValue('--safe-area-inset-top') || '0', 10),
    right: parseInt(style.getPropertyValue('--safe-area-inset-right') || '0', 10),
    bottom: parseInt(style.getPropertyValue('--safe-area-inset-bottom') || '0', 10),
    left: parseInt(style.getPropertyValue('--safe-area-inset-left') || '0', 10)
  }
}

/**
 * 应用安全区域内边距
 */
export function applySafeAreaPadding(element: HTMLElement): void {
  const insets = getSafeAreaInsets()
  
  if (insets.top > 0) {
    element.style.paddingTop = `${insets.top}px`
  }
  
  if (insets.bottom > 0) {
    element.style.paddingBottom = `${insets.bottom}px`
  }
  
  if (insets.left > 0) {
    element.style.paddingLeft = `${insets.left}px`
  }
  
  if (insets.right > 0) {
    element.style.paddingRight = `${insets.right}px`
  }
}

/**
 * 检查网络连接状态
 */
export function getNetworkStatus(): {
  online: boolean
  effectiveType?: string
  downlink?: number
  rtt?: number
} {
  const connection = (navigator as any).connection || 
                    (navigator as any).mozConnection || 
                    (navigator as any).webkitConnection
  
  return {
    online: navigator.onLine,
    effectiveType: connection?.effectiveType,
    downlink: connection?.downlink,
    rtt: connection?.rtt
  }
}

/**
 * 监听网络状态变化
 */
export function onNetworkStatusChange(callback: (online: boolean) => void): () => void {
  const handleOnline = () => callback(true)
  const handleOffline = () => callback(false)
  
  window.addEventListener('online', handleOnline)
  window.addEventListener('offline', handleOffline)
  
  // 返回清理函数
  return () => {
    window.removeEventListener('online', handleOnline)
    window.removeEventListener('offline', handleOffline)
  }
}

/**
 * 存储管理 - 处理不同平台的存储限制
 */
export class PlatformStorage {
  private static getStorageQuota(): number {
    // Android 设备通常有更严格的存储限制
    if (isAndroid()) {
      return 5 * 1024 * 1024 // 5MB
    }
    
    // Windows 桌面端可以使用更多存储
    if (isWindows()) {
      return 50 * 1024 * 1024 // 50MB
    }
    
    return 10 * 1024 * 1024 // 10MB 默认
  }
  
  static setItem(key: string, value: any): boolean {
    try {
      const serialized = JSON.stringify(value)
      const currentSize = this.getStorageSize()
      const itemSize = new Blob([serialized]).size
      
      if (currentSize + itemSize > this.getStorageQuota()) {
        console.warn('Storage quota exceeded')
        return false
      }
      
      localStorage.setItem(key, serialized)
      return true
    } catch (error) {
      console.error('Storage error:', error)
      return false
    }
  }
  
  static getItem<T>(key: string): T | null {
    try {
      const item = localStorage.getItem(key)
      return item ? JSON.parse(item) : null
    } catch (error) {
      console.error('Storage read error:', error)
      return null
    }
  }
  
  static removeItem(key: string): void {
    localStorage.removeItem(key)
  }
  
  static clear(): void {
    localStorage.clear()
  }
  
  static getStorageSize(): number {
    let total = 0
    for (const key in localStorage) {
      if (localStorage.hasOwnProperty(key)) {
        total += localStorage[key].length + key.length
      }
    }
    return total
  }
  
  static getAvailableSpace(): number {
    return this.getStorageQuota() - this.getStorageSize()
  }
}

// 类型声明扩展
declare global {
  interface Window {
    Capacitor?: any
    require?: any
  }
}

export default {
  getPlatformType,
  getDeviceType,
  getPlatformInfo,
  isMobile,
  isDesktop,
  isAndroid,
  isWindows,
  isTouchDevice,
  getPlatformClasses,
  applyPlatformClasses,
  getFontSizeMultiplier,
  getMinTouchTargetSize,
  debounce,
  throttle,
  hapticFeedback,
  getSafeAreaInsets,
  applySafeAreaPadding,
  getNetworkStatus,
  onNetworkStatusChange,
  PlatformStorage
} 