/**
 * 门店信息管理工具
 * 确保门店信息的持久化存储和一致性
 */

// 存储键名
export const STORE_KEYS = {
  STORE_SELECTION: 'mall_selfcheck_store_selected',
  FIRST_LAUNCH: 'mall_selfcheck_first_launch',
  STORE_BACKUP: 'mall_selfcheck_store_backup', // 备份存储
  LAST_UPDATE: 'mall_selfcheck_store_last_update' // 最后更新时间
} as const

// 门店信息接口
export interface StoreInfo {
  schoolId: number
  storeId: number
  schoolName: string
  storeName: string
  updateTime?: number
}

/**
 * 门店管理器
 */
export class StoreManager {
  
  /**
   * 保存门店信息（使用简洁的缓存方式）
   */
  static saveStoreInfo(storeInfo: StoreInfo): void {
    try {
      // 保存到简洁的键名
      localStorage.setItem('current_store_id', String(storeInfo.storeId))
      localStorage.setItem('current_school_id', String(storeInfo.schoolId))
      localStorage.setItem('current_store_name', storeInfo.storeName)
      localStorage.setItem('current_school_name', storeInfo.schoolName)
      localStorage.setItem('store_update_time', String(Date.now()))

      console.log('✅ 门店信息已保存:', {
        storeId: storeInfo.storeId,
        schoolId: storeInfo.schoolId,
        storeName: storeInfo.storeName,
        schoolName: storeInfo.schoolName
      })
    } catch (error) {
      console.error('❌ 保存门店信息失败:', error)
      throw new Error('门店信息保存失败')
    }
  }
  
  /**
   * 获取门店信息
   */
  static getStoreInfo(): StoreInfo | null {
    try {
      const storeId = localStorage.getItem('current_store_id')
      const schoolId = localStorage.getItem('current_school_id')
      const storeName = localStorage.getItem('current_store_name')
      const schoolName = localStorage.getItem('current_school_name')

      if (!storeId || !schoolId) {
        console.log('ℹ️ 没有找到门店信息')
        return null
      }

      const storeInfo: StoreInfo = {
        storeId: parseInt(storeId, 10),
        schoolId: parseInt(schoolId, 10),
        storeName: storeName || `门店${storeId}`,
        schoolName: schoolName || `学校${schoolId}`
      }

      console.log('✅ 获取门店信息成功:', storeInfo)
      return storeInfo
    } catch (error) {
      console.error('❌ 获取门店信息失败:', error)
      return null
    }
  }
  
  /**
   * 验证门店信息的完整性
   */
  static validateStoreInfo(storeInfo: any): storeInfo is StoreInfo {
    return (
      storeInfo &&
      typeof storeInfo.schoolId === 'number' &&
      typeof storeInfo.storeId === 'number' &&
      typeof storeInfo.schoolName === 'string' &&
      typeof storeInfo.storeName === 'string' &&
      storeInfo.schoolId > 0 &&
      storeInfo.storeId > 0 &&
      storeInfo.schoolName.trim().length > 0 &&
      storeInfo.storeName.trim().length > 0
    )
  }
  
  /**
   * 清除门店信息
   */
  static clearStoreInfo(): void {
    try {
      localStorage.removeItem('current_store_id')
      localStorage.removeItem('current_school_id')
      localStorage.removeItem('current_store_name')
      localStorage.removeItem('current_school_name')
      localStorage.removeItem('store_update_time')
      console.log('✅ 门店信息已清除')
    } catch (error) {
      console.error('❌ 清除门店信息失败:', error)
    }
  }
  
  /**
   * 检查是否有门店信息
   */
  static hasStoreInfo(): boolean {
    const storeInfo = this.getStoreInfo()
    return storeInfo !== null
  }

  /**
   * 检查门店信息是否有效（用于APK环境）
   */
  static isStoreInfoValid(): boolean {
    try {
      const storeId = localStorage.getItem('current_store_id')
      const schoolId = localStorage.getItem('current_school_id')
      const updateTime = localStorage.getItem('store_update_time')

      // 基本数据检查
      if (!storeId || !schoolId) {
        console.log('🔍 门店信息缺失基本数据')
        return false
      }

      // 数据格式检查
      const storeIdNum = parseInt(storeId, 10)
      const schoolIdNum = parseInt(schoolId, 10)

      if (isNaN(storeIdNum) || isNaN(schoolIdNum) || storeIdNum <= 0 || schoolIdNum <= 0) {
        console.log('🔍 门店信息数据格式无效')
        return false
      }

      // 时间检查（可选，防止过期数据）
      if (updateTime) {
        const updateTimeNum = parseInt(updateTime, 10)
        const now = Date.now()
        const maxAge = 30 * 24 * 60 * 60 * 1000 // 30天

        if (now - updateTimeNum > maxAge) {
          console.log('🔍 门店信息已过期（超过30天）')
          return false
        }
      }

      console.log('✅ 门店信息有效')
      return true
    } catch (error) {
      console.error('❌ 检查门店信息有效性失败:', error)
      return false
    }
  }
  
  /**
   * 获取门店信息的简化版本（用于API调用）
   */
  static getStoreParams(): { storeId?: number; schoolId?: number } {
    const storeInfo = this.getStoreInfo()
    if (storeInfo) {
      return {
        storeId: storeInfo.storeId,
        schoolId: storeInfo.schoolId
      }
    }

    console.log('❌ 没有找到门店信息')
    return {}
  }
  
  /**
   * 检查是否首次启动
   */
  static isFirstLaunch(): boolean {
    return !localStorage.getItem(STORE_KEYS.FIRST_LAUNCH)
  }
  
  /**
   * 标记已启动过
   */
  static markAsLaunched(): void {
    localStorage.setItem(STORE_KEYS.FIRST_LAUNCH, 'true')
  }
  
  /**
   * 获取门店信息的显示文本
   */
  static getStoreDisplayText(): string {
    const storeInfo = this.getStoreInfo()
    if (storeInfo) {
      return `${storeInfo.schoolName} - ${storeInfo.storeName}`
    }

    return '未选择门店'
  }
  

  
  /**
   * 强制刷新门店信息（清除缓存，触发重新选择）
   */
  static forceRefresh(): void {
    this.clearStoreInfo()
    console.log('🔄 门店信息已强制刷新，需要重新选择')
  }

  /**
   * 应用启动时的门店信息检查（用于APK环境）
   */
  static checkStoreInfoOnStartup(): boolean {
    console.log('🚀 应用启动 - 检查门店信息状态')

    if (!this.hasStoreInfo()) {
      console.log('📍 未找到门店信息，需要重新选择')
      return false
    }

    if (!this.isStoreInfoValid()) {
      console.log('⚠️ 门店信息无效或已过期，清除并重新选择')
      this.clearStoreInfo()
      return false
    }

    console.log('✅ 门店信息有效，可以继续使用')
    return true
  }

  /**
   * 获取门店信息状态描述（用于调试）
   */
  static getStoreInfoStatus(): string {
    if (!this.hasStoreInfo()) {
      return '未设置门店信息'
    }

    if (!this.isStoreInfoValid()) {
      return '门店信息无效或已过期'
    }

    const storeInfo = this.getStoreInfo()
    if (storeInfo) {
      return `当前门店：${storeInfo.schoolName} - ${storeInfo.storeName}`
    }

    return '门店信息状态未知'
  }
}

// 默认导出
export default StoreManager
