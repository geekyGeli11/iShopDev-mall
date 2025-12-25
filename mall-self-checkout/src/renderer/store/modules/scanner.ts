/**
 * 扫码相关状态管理
 */
import { defineStore } from 'pinia'
import type { ScanResult, ProductInfo } from '@shared/types'

export interface ScannerState {
  isScanning: boolean
  lastScanResult: ScanResult | null
  scanHistory: ScanResult[]
  cameraEnabled: boolean
}

export const useScannerStore = defineStore('scanner', {
  state: (): ScannerState => ({
    isScanning: false,
    lastScanResult: null,
    scanHistory: [],
    cameraEnabled: false
  }),

  getters: {
    // 是否正在扫描
    getIsScanning: (state) => state.isScanning,
    
    // 获取最后扫描结果
    getLastScanResult: (state) => state.lastScanResult,
    
    // 获取扫描历史
    getScanHistory: (state) => state.scanHistory,
    
    // 摄像头是否可用
    isCameraEnabled: (state) => state.cameraEnabled
  },

  actions: {
    // 开始扫描
    startScanning() {
      this.isScanning = true
    },

    // 停止扫描
    stopScanning() {
      this.isScanning = false
    },

    // 设置扫描结果
    setScanResult(result: ScanResult) {
      this.lastScanResult = result
      this.scanHistory.unshift(result) // 添加到历史记录开头
      
      // 限制历史记录数量
      if (this.scanHistory.length > 50) {
        this.scanHistory = this.scanHistory.slice(0, 50)
      }
    },

    // 启用摄像头
    enableCamera() {
      this.cameraEnabled = true
    },

    // 禁用摄像头
    disableCamera() {
      this.cameraEnabled = false
      this.isScanning = false
    },

    // 清除扫描历史
    clearScanHistory() {
      this.scanHistory = []
      this.lastScanResult = null
    }
  }
}) 