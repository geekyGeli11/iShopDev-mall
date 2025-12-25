/**
 * 全局扫码管理器
 * 确保同一时间只有一个扫码监听器在工作，避免冲突
 */

export type ScanType = 'product' | 'member' | 'payment'

interface ScanListener {
  type: ScanType
  handler: (event: KeyboardEvent) => void
  active: boolean
}

class ScanManager {
  private listeners: Map<ScanType, ScanListener> = new Map()
  private currentActiveType: ScanType | null = null
  private globalHandler: ((event: KeyboardEvent) => void) | null = null

  /**
   * 注册扫码监听器
   */
  register(type: ScanType, handler: (event: KeyboardEvent) => void) {
    this.listeners.set(type, {
      type,
      handler,
      active: false
    })
    console.log(`📝 注册扫码监听器: ${type}`)
  }

  /**
   * 激活指定类型的扫码监听器
   */
  activate(type: ScanType) {
    const listener = this.listeners.get(type)
    if (!listener) {
      console.warn(`⚠️ 未找到扫码监听器: ${type}`)
      return
    }

    // 如果已经是当前激活的类型，直接返回
    if (this.currentActiveType === type) {
      console.log(`✅ 扫码监听器已激活: ${type}`)
      return
    }

    // 停用当前激活的监听器
    this.deactivateAll()

    // 激活新的监听器
    this.currentActiveType = type
    listener.active = true

    // 设置全局处理器
    this.globalHandler = (event: KeyboardEvent) => {
      if (this.currentActiveType === type && listener.active) {
        listener.handler(event)
      }
    }

    document.addEventListener('keydown', this.globalHandler)
    console.log(`🔍 激活扫码监听器: ${type}`)
  }

  /**
   * 停用指定类型的扫码监听器
   */
  deactivate(type: ScanType) {
    const listener = this.listeners.get(type)
    if (!listener) return

    if (this.currentActiveType === type) {
      this.deactivateAll()
      console.log(`🛑 停用扫码监听器: ${type}`)
    }
  }

  /**
   * 停用所有扫码监听器
   */
  deactivateAll() {
    if (this.globalHandler) {
      document.removeEventListener('keydown', this.globalHandler)
      this.globalHandler = null
    }

    this.listeners.forEach(listener => {
      listener.active = false
    })

    this.currentActiveType = null
    console.log('🛑 停用所有扫码监听器')
  }

  /**
   * 获取当前激活的扫码类型
   */
  getCurrentActiveType(): ScanType | null {
    return this.currentActiveType
  }

  /**
   * 检查指定类型是否激活
   */
  isActive(type: ScanType): boolean {
    return this.currentActiveType === type
  }

  /**
   * 注销扫码监听器
   */
  unregister(type: ScanType) {
    if (this.currentActiveType === type) {
      this.deactivateAll()
    }
    this.listeners.delete(type)
    console.log(`🗑️ 注销扫码监听器: ${type}`)
  }
}

// 创建全局单例
export const scanManager = new ScanManager()

// 导出类型
export { ScanManager }
