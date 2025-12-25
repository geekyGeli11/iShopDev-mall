/**
 * 支付相关类型定义
 */

// 支付方式
export enum PaymentMethod {
  WECHAT = 'WECHAT',
  ALIPAY = 'ALIPAY',
  BANK_CARD = 'BANK_CARD',
  DIGITAL_RMB = 'DIGITAL_RMB',
  MEMBER_BALANCE = 'MEMBER_BALANCE'
}

// 支付模式
export enum PaymentMode {
  QR_CODE = 'QR_CODE',     // 展示收款码
  SCAN_CODE = 'SCAN_CODE'  // 扫描付款码
}

// 支付状态
export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

// 支付请求
export interface PaymentRequest {
  orderId: string
  amount: number
  subject: string
  body?: string
  method: PaymentMethod
  mode: PaymentMode
  memberId?: string
  coupons?: string[]
  pointsUsed?: number
}

// 收款码信息
export interface PaymentQRCode {
  qrCodeUrl: string
  qrCodeData: string
  paymentId: string
  amount: number
  expireTime: number
  method: PaymentMethod
}

// 扫码支付请求
export interface ScanPaymentRequest {
  authCode: string
  amount: number
  orderId: string
  method: PaymentMethod
}

// 支付结果
export interface PaymentResult {
  paymentId: string
  orderId: string
  status: PaymentStatus
  amount: number
  paidAmount: number
  method: PaymentMethod
  transactionId?: string
  paidAt?: string
  failureReason?: string
}

// 组合支付项
export interface ComboPaymentItem {
  method: PaymentMethod
  amount: number
} 