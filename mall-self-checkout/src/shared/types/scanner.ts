/**
 * 扫码相关类型定义
 */

// 扫码类型
export enum ScanType {
  BARCODE = 'BARCODE',     // 商品条码
  QR_CODE = 'QR_CODE',     // 二维码
  PAYMENT_CODE = 'PAYMENT_CODE' // 付款码
}

// 条码格式
export enum BarcodeFormat {
  EAN_13 = 'EAN_13',
  EAN_8 = 'EAN_8',
  CODE_128 = 'CODE_128',
  QR_CODE = 'QR_CODE'
}

// 扫码配置
export interface ScannerConfig {
  enableSound: boolean
  enableVibration: boolean
  autoFocus: boolean
  flashMode: boolean
  scanFormats: BarcodeFormat[]
  scanDelay: number
}

// 扫码结果
export interface ScannerResult {
  text: string
  format: BarcodeFormat
  type: ScanType
  timestamp: number
  confidence?: number
}

// 摄像头配置
export interface CameraConfig {
  deviceId?: string
  width: number
  height: number
  facingMode: 'user' | 'environment'
  focusMode: 'auto' | 'manual'
} 