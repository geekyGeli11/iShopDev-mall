/**
 * 应用状态管理
 */

import { defineStore } from 'pinia'
import { DeviceType } from '@shared/types'

interface AppState {
  isInitialized: boolean
  deviceType: DeviceType
  isOnline: boolean
  currentRoute: string
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    isInitialized: false,
    deviceType: DeviceType.DESKTOP,
    isOnline: navigator.onLine,
    currentRoute: '/'
  }),

  getters: {
    isElectron: (state) => state.deviceType === DeviceType.DESKTOP,
    isCapacitor: (state) => state.deviceType === DeviceType.MOBILE,
    isWeb: (state) => state.deviceType === DeviceType.DESKTOP
  },

  actions: {
    /**
     * 初始化应用
     */
    async initialize(): Promise<void> {
      try {
        // 检测设备类型
        this.detectDeviceType()
        
        // 监听网络状态
        this.setupNetworkListener()
        
        // 标记为已初始化
        this.isInitialized = true
        
        console.log('应用初始化完成')
      } catch (error) {
        console.error('应用初始化失败:', error)
      }
    },

    /**
     * 检测设备类型
     */
    detectDeviceType(): void {
      if (typeof window !== 'undefined') {
        if (window.electronAPI) {
          this.deviceType = DeviceType.DESKTOP
        } else if ((window as any).Capacitor) {
          this.deviceType = DeviceType.MOBILE
        } else {
          this.deviceType = DeviceType.DESKTOP
        }
      }
    },

    /**
     * 设置网络状态监听
     */
    setupNetworkListener(): void {
      window.addEventListener('online', () => {
        this.isOnline = true
      })
      
      window.addEventListener('offline', () => {
        this.isOnline = false
      })
    },

    /**
     * 更新当前路由
     */
    updateCurrentRoute(route: string): void {
      this.currentRoute = route
    }
  }
}) 