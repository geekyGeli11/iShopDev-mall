import { request } from '../request'
import { API_ENDPOINTS } from '@shared/constants/api'
import type { 
  PaymentRequest,
  PaymentResult,
  PaymentMethod,
  PaymentStatus,
  PaymentQRParam,
  PaymentResultVO,
  ApiResponse 
} from '@shared/types'

/**
 * 支付相关API
 */

// 支付类型枚举
export enum PaymentType {
  WECHAT = 'WECHAT',
  ALIPAY = 'ALIPAY'
}

// 支付二维码生成参数 - 使用shared types中的定义
// export interface PaymentQRParam 已在 @shared/types 中定义

// 付款码扫描参数
export interface PaymentCodeParam {
  orderId: number
  paymentCode: string
  amount: number
  payType: PaymentType
  terminalCode?: string
  remark?: string
}

// PaymentResultVO 类型已在上面导入

/**
 * 支付相关API服务
 */
export class PaymentAPI {
  /**
   * 生成微信收款码
   * @param paymentRequest 支付请求
   * @returns 收款码信息
   */
  static async generateWechatQR(paymentRequest: PaymentRequest): Promise<ApiResponse<{
    qrCodeUrl: string
    qrCodeData: string
    paymentId: string
    orderId: number
    amount: number
    expiresIn: number
  }>> {
    return request.post(API_ENDPOINTS.PAYMENT_QR_WECHAT, paymentRequest)
  }

  /**
   * 生成支付宝收款码
   * @param paymentRequest 支付请求
   * @returns 收款码信息
   */
  static async generateAlipayQR(paymentRequest: PaymentRequest): Promise<ApiResponse<{
    qrCodeUrl: string
    qrCodeData: string
    paymentId: string
    orderId: number
    amount: number
    expiresIn: number
  }>> {
    return request.post(API_ENDPOINTS.PAYMENT_QR_ALIPAY, paymentRequest)
  }

  /**
   * 扫码支付
   * @param scanPayment 扫码支付请求
   * @returns 支付结果
   */
  static async scanPayment(scanPayment: {
    paymentCode: string
    orderId: number | string
    amount: number
    paymentMethod: PaymentMethod
  }): Promise<ApiResponse<PaymentResultVO>> {
    // 构造符合后端PaymentCodeParam的请求格式
    const paymentParam = {
      orderId: typeof scanPayment.orderId === 'string' ? parseInt(scanPayment.orderId) : scanPayment.orderId,
      paymentCode: scanPayment.paymentCode,
      amount: scanPayment.amount,
      payType: scanPayment.paymentMethod.toUpperCase(),
      terminalCode: 'SC001', // 默认终端编号
      remark: '自助收银扫码支付'
    }
    
    return request.post(API_ENDPOINTS.PAYMENT_SCAN_PAY, paymentParam)
  }

  /**
   * 查询支付状态
   * @param paymentId 支付ID
   * @returns 支付状态
   */
  static async getPaymentStatus(paymentId: string): Promise<ApiResponse<PaymentResultVO>> {
    return request.get(`${API_ENDPOINTS.PAYMENT_STATUS}/${paymentId}`)
  }

  /**
   * 取消支付
   * @param paymentId 支付ID
   * @param reason 取消原因
   * @returns 取消结果
   */
  static async cancelPayment(paymentId: string, reason?: string): Promise<ApiResponse<{
    paymentId: string
    cancelled: boolean
    refundInfo?: {
      refundId: string
      refundAmount: number
      refundStatus: string
    }
  }>> {
    return request.post(`${API_ENDPOINTS.PAYMENT_CANCEL}/${paymentId}`, { reason })
  }

  /**
   * 申请退款
   * @param refundRequest 退款请求
   * @returns 退款结果
   */
  static async requestRefund(refundRequest: {
    paymentId: string
    orderId: number
    refundAmount: number
    reason: string
    operatorId?: string
  }): Promise<ApiResponse<{
    refundId: string
    refundSn: string
    refundAmount: number
    refundStatus: string
    estimatedTime: string
  }>> {
    return request.post(API_ENDPOINTS.PAYMENT_REFUND, refundRequest)
  }

  /**
   * 获取支付方式列表
   * @returns 支付方式列表
   */
  static async getPaymentMethods(): Promise<ApiResponse<Array<{
    method: PaymentMethod
    name: string
    icon: string
    enabled: boolean
    minAmount?: number
    maxAmount?: number
    description?: string
  }>>> {
    return request.get(API_ENDPOINTS.PAYMENT_METHODS)
  }

  /**
   * 获取支付配置
   * @returns 支付配置
   */
  static async getPaymentConfig(): Promise<ApiResponse<{
    enabledMethods: PaymentMethod[]
    qrCodeExpiry: number
    paymentTimeout: number
    autoRefundEnabled: boolean
    mockPaymentEnabled: boolean
    supportedCurrencies: string[]
  }>> {
    return request.get(API_ENDPOINTS.PAYMENT_CONFIG)
  }

  /**
   * 验证支付金额
   * @param amount 支付金额
   * @param paymentMethod 支付方式
   * @returns 验证结果
   */
  static async validatePaymentAmount(
    amount: number,
    paymentMethod: PaymentMethod
  ): Promise<ApiResponse<{
    valid: boolean
    reason?: string
    minAmount: number
    maxAmount: number
    suggestedAmount?: number
  }>> {
    return request.post(`${API_ENDPOINTS.PAYMENT_CONFIG}/validate`, {
      amount,
      paymentMethod
    })
  }

  /**
   * 获取支付记录
   * @param params 查询参数
   * @returns 支付记录列表
   */
  static async getPaymentHistory(params: {
    orderId?: number
    paymentMethod?: PaymentMethod
    status?: PaymentStatus
    startTime?: string
    endTime?: string
    page?: number
    size?: number
  } = {}): Promise<ApiResponse<{
    list: PaymentResult[]
    total: number
    page: number
    size: number
  }>> {
    return request.get(`${API_ENDPOINTS.PAYMENT_STATUS}/history`, params)
  }

  /**
   * 重新发起支付
   * @param orderId 订单ID
   * @param paymentMethod 支付方式
   * @returns 新的支付信息
   */
  static async retryPayment(
    orderId: number,
    paymentMethod: PaymentMethod
  ): Promise<ApiResponse<{
    paymentId: string
    qrCodeUrl?: string
    qrCodeData?: string
    amount: number
    expiresIn: number
  }>> {
    return request.post(`${API_ENDPOINTS.PAYMENT_STATUS}/retry`, {
      orderId,
      paymentMethod
    })
  }

  /**
   * 检查支付限额
   * @param amount 支付金额
   * @param paymentMethod 支付方式
   * @returns 限额检查结果
   */
  static async checkPaymentLimit(
    amount: number,
    paymentMethod: PaymentMethod
  ): Promise<ApiResponse<{
    withinLimit: boolean
    dailyLimit: number
    usedToday: number
    remainingToday: number
    transactionLimit: number
  }>> {
    return request.get(`${API_ENDPOINTS.PAYMENT_CONFIG}/limit`, {
      amount,
      paymentMethod
    })
  }

  /**
   * 获取支付统计信息
   * @param dateRange 日期范围
   * @returns 统计信息
   */
  static async getPaymentStatistics(dateRange: {
    startDate: string
    endDate: string
  }): Promise<ApiResponse<{
    totalAmount: number
    totalCount: number
    successCount: number
    failedCount: number
    refundCount: number
    methodStatistics: Array<{
      method: PaymentMethod
      amount: number
      count: number
      successRate: number
    }>
    dailyStatistics: Array<{
      date: string
      amount: number
      count: number
    }>
  }>> {
    return request.get(`${API_ENDPOINTS.PAYMENT_STATUS}/statistics`, dateRange)
  }

  /**
   * 创建支付会话
   * @param orderId 订单ID
   * @param payType 支付方式
   * @param guestId 游客ID（可选）
   * @returns 支付会话信息
   */
  static async createPaymentSession(
    orderId: number,
    payType: 'WECHAT' | 'ALIPAY',
    guestId?: string
  ): Promise<ApiResponse<{
    sessionId: string
    paymentId: string
    qrCodeUrl?: string
    expiresIn: number
  }>> {
    const param: PaymentQRParam = {
      orderId,
      payType: payType as any, // 临时类型转换
      title: '自助收银付款',
      description: '自助收银系统付款',
      amount: 0, // 将由后端从订单获取
      guestId
    }

    const response = await this.generatePaymentQR(param)
    
    return {
      ...response,
      data: {
        sessionId: response.data.paymentId,
        paymentId: response.data.paymentId,
        qrCodeUrl: response.data.qrCodeUrl,
        expiresIn: 300 // 5分钟
      }
    }
  }

  /**
   * 检查支付环境
   * @returns 支付环境检查结果
   */
  static async checkPaymentEnvironment(): Promise<ApiResponse<{
    wechatAvailable: boolean
    alipayAvailable: boolean
    scanAvailable: boolean
    qrAvailable: boolean
    recommendations: string[]
  }>> {
    // 这是一个模拟检查，实际可能需要调用后端或检查设备能力
    return Promise.resolve({
      code: 200,
      message: 'success',
      data: {
        wechatAvailable: true,
        alipayAvailable: true,
        scanAvailable: !!navigator.mediaDevices?.getUserMedia,
        qrAvailable: true,
        recommendations: [
          '建议使用微信或支付宝扫码支付',
          '确保摄像头权限已开启用于扫码',
          '网络连接正常'
        ]
      }
    })
  }

  /**
   * 获取支付限额信息
   * @param payType 支付方式
   * @returns 限额信息
   */
  static async getPaymentLimits(payType: 'WECHAT' | 'ALIPAY'): Promise<ApiResponse<{
    minAmount: number
    maxAmount: number
    dailyLimit?: number
    monthlyLimit?: number
    tips: string[]
  }>> {
    // 这可能需要从后端配置获取
    const limits = {
      WECHAT: {
        minAmount: 0.01,
        maxAmount: 50000,
        dailyLimit: 200000,
        tips: ['微信支付单笔限额5万元', '日累计限额20万元']
      },
      ALIPAY: {
        minAmount: 0.01,
        maxAmount: 50000,
        dailyLimit: 200000,
        tips: ['支付宝单笔限额5万元', '日累计限额20万元']
      }
    }

    return Promise.resolve({
      code: 200,
      message: 'success',
      data: limits[payType]
    })
  }

  // ===== Store使用的方法别名 =====

  /**
   * 生成支付二维码（store使用的方法名）
   * @param param 支付参数
   * @returns 二维码信息
   */
  static async generatePaymentQR(param: any): Promise<ApiResponse<{
    qrCodeUrl: string
    paymentId: string
    expireTime: number
  }>> {
    const response = await this.generateWechatQR(param)
    return {
      code: response.code,
      message: response.message,
      data: {
        qrCodeUrl: response.data.qrCodeUrl,
        paymentId: response.data.paymentId,
        expireTime: Date.now() + (response.data.expiresIn * 1000)
      }
    }
  }



  /**
   * 扫码支付（store使用的方法名）
   * @param param 扫码参数
   * @returns 支付结果
   */
  static async scanPaymentCode(param: any): Promise<ApiResponse<any>> {
    return this.scanPayment(param)
  }

  /**
   * 查询支付状态（store使用的方法名）
   * @param paymentId 支付ID
   * @returns 支付状态
   */
  static async queryPaymentStatus(paymentId: string): Promise<ApiResponse<any>> {
    return this.getPaymentStatus(paymentId)
  }
} 