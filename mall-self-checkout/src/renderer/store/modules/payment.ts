/**
 * 支付相关状态管理
 */
import { defineStore } from 'pinia'
import { PaymentAPI } from '../../api'
import { useMemberStore } from './member'
import { useCartStore } from './cart'
import { envConfig } from '../../../config/env'
import type { 
  PaymentQRParam,
  PaymentCodeParam,
  PaymentResultVO,
  ApiResponse
} from '@shared/types'
import { PaymentMethod, PaymentStatus } from '@shared/types'

/**
 * 支付会话信息
 */
interface PaymentSession {
  orderId: number
  orderSn: string
  paymentId: string
  amount: number
  method: PaymentMethod
  status: PaymentStatus
  qrCodeUrl?: string
  qrCodeExpireTime?: number
  startTime: number
  pollingTimer?: NodeJS.Timeout
}

/**
 * 支付状态
 */
interface PaymentState {
  // 当前支付会话
  currentPayment: PaymentSession | null
  
  // 支付方式
  selectedMethod: PaymentMethod | null
  availableMethods: PaymentMethod[]
  
  // 支付状态
  isProcessing: boolean
  isPolling: boolean
  
  // 支付结果
  paymentResult: PaymentResultVO | null
  
  // 错误信息
  lastError: string | null
  
  // 支付配置
  paymentConfig: {
    timeout: number
    pollingInterval: number
    maxRetries: number
  }
}

export const usePaymentStore = defineStore('payment', {
  state: (): PaymentState => ({
    currentPayment: null,
    selectedMethod: null,
    availableMethods: [
      PaymentMethod.WECHAT,
      PaymentMethod.ALIPAY
    ],
    isProcessing: false,
    isPolling: false,
    paymentResult: null,
    lastError: null,
    paymentConfig: {
      timeout: envConfig.payment.timeout,
      pollingInterval: envConfig.payment.statusPollInterval,
      maxRetries: envConfig.payment.maxPollCount
    }
  }),

  getters: {
    /**
     * 是否有支付进行中
     */
    hasActivePayment: (state): boolean => {
      return state.currentPayment !== null && 
             state.currentPayment.status === PaymentStatus.PENDING
    },

    /**
     * 支付剩余时间（秒）
     */
    paymentRemainingTime: (state): number => {
      if (!state.currentPayment || !state.currentPayment.qrCodeExpireTime) {
        return 0
      }
      
      const now = Date.now()
      const expireTime = state.currentPayment.qrCodeExpireTime
      
      return Math.max(0, Math.floor((expireTime - now) / 1000))
    },

    /**
     * 是否支付超时
     */
    isPaymentTimeout: (state): boolean => {
      if (!state.currentPayment) return false
      
      const elapsed = Date.now() - state.currentPayment.startTime
      return elapsed > state.paymentConfig.timeout * 1000
    },

    /**
     * 支付方式显示名称
     */
    paymentMethodName: (state): string => {
      if (!state.selectedMethod) return ''
      
      const methodNames = {
        [PaymentMethod.WECHAT]: '微信支付',
        [PaymentMethod.ALIPAY]: '支付宝'
      }
      
      return methodNames[state.selectedMethod] || '未知支付方式'
    },

    /**
     * 是否可以开始支付
     */
    canStartPayment: (state): boolean => {
      return !state.isProcessing && 
             state.selectedMethod !== null && 
             state.currentPayment === null
    },

    /**
     * 支付状态文本
     */
    paymentStatusText: (state): string => {
      if (!state.currentPayment) return '等待支付'
      
      const statusTexts = {
        [PaymentStatus.PENDING]: '等待支付',
        [PaymentStatus.SUCCESS]: '支付成功',
        [PaymentStatus.FAILED]: '支付失败',
        [PaymentStatus.TIMEOUT]: '支付超时'
      }
      
      return statusTexts[state.currentPayment.status] || '未知状态'
    }
  },

  actions: {
    /**
     * 设置支付方式
     */
    setPaymentMethod(method: PaymentMethod): void {
      this.selectedMethod = method
      console.log('选择支付方式:', method)
    },

    /**
     * 生成收款二维码
     */
    async generatePaymentQR(orderId: number, orderSn: string, amount: number): Promise<string> {
      if (!this.selectedMethod) {
        throw new Error('请选择支付方式')
      }

      this.isProcessing = true
      this.lastError = null

      try {
        const param: PaymentQRParam = {
          orderId,
          amount,
          payType: this.selectedMethod,
          title: `订单支付-${orderSn}`,
          description: `订单号: ${orderSn}`,
          terminalCode: 'SELF_CHECKOUT_001',
          expireMinutes: 5
        }

        const response = await PaymentAPI.generatePaymentQR(param)
        
        if (response.code === 200 && response.data) {
          const result = response.data
          
          // 创建支付会话
          this.currentPayment = {
            orderId,
            orderSn,
            paymentId: result.paymentId,
            amount,
            method: this.selectedMethod,
            status: PaymentStatus.PENDING,
            qrCodeUrl: result.qrCodeUrl,
            qrCodeExpireTime: result.expireTime || (Date.now() + 300 * 1000),
            startTime: Date.now()
          }
          
          // 开始轮询支付状态
          this.startPaymentPolling()
          
          console.log('收款二维码生成成功:', result)
          return result.qrCodeUrl
        } else {
          throw new Error(response.message || '生成收款二维码失败')
        }
      } catch (error) {
        this.lastError = error instanceof Error ? error.message : '生成收款二维码失败'
        console.error('生成收款二维码失败:', error)
        throw error
      } finally {
        this.isProcessing = false
      }
    },

    /**
     * 扫描付款码支付
     */
    async scanPaymentCode(
      orderId: number, 
      orderSn: string, 
      amount: number, 
      paymentCode: string
    ): Promise<PaymentResultVO> {
      this.isProcessing = true
      this.lastError = null

      try {
        const param: PaymentCodeParam = {
          orderId,
          paymentCode,
          amount,
          payType: this.getPaymentMethodFromCode(paymentCode),
          terminalCode: 'SELF_CHECKOUT_001'
        }

        const response = await PaymentAPI.scanPaymentCode(param)
        
        if (response.code === 200 && response.data) {
          const result = response.data
          
          // 创建支付会话
          this.currentPayment = {
            orderId,
            orderSn,
            paymentId: result.paymentId,
            amount,
            method: this.getPaymentMethodFromCode(paymentCode),
            status: result.status,
            startTime: Date.now()
          }
          
          // 处理支付结果
          if (result.status === PaymentStatus.SUCCESS) {
            this.handlePaymentSuccess(result)
          } else if (result.status === PaymentStatus.FAILED) {
            this.handlePaymentFailed(result.failureReason || '支付失败')
          }
          
          console.log('扫码支付完成:', result)
          return result
        } else {
          throw new Error(response.message || '扫码支付失败')
        }
      } catch (error) {
        this.lastError = error instanceof Error ? error.message : '扫码支付失败'
        console.error('扫码支付失败:', error)
        throw error
      } finally {
        this.isProcessing = false
      }
    },

    /**
     * 查询支付状态
     */
    async queryPaymentStatus(paymentId?: string): Promise<PaymentResultVO | null> {
      const targetPaymentId = paymentId || this.currentPayment?.paymentId
      
      if (!targetPaymentId) {
        return null
      }

      try {
        const response = await PaymentAPI.getPaymentStatus(targetPaymentId)
        
        if (response.code === 200 && response.data) {
          const result = response.data
          
          // 更新当前支付状态
          if (this.currentPayment && this.currentPayment.paymentId === targetPaymentId) {
            this.currentPayment.status = (result as any).status || (result as any).payStatus
          }
          
          return result as any
        } else {
          console.warn('查询支付状态失败:', response.message)
          return null
        }
      } catch (error) {
        console.error('查询支付状态失败:', error)
        return null
      }
    },

    /**
     * 开始支付状态轮询
     */
    startPaymentPolling(): void {
      if (!this.currentPayment || this.isPolling) {
        return
      }

      this.isPolling = true
      let retryCount = 0

      const poll = async (): Promise<void> => {
        try {
          const result = await this.queryPaymentStatus()
          
          if (result) {
            if (result.payStatus === 'SUCCESS') {
              this.handlePaymentSuccess(result)
              this.stopPaymentPolling()
              return
            } else if (result.payStatus === 'FAILED') {
              this.handlePaymentFailed(result.failureReason || '支付失败')
              this.stopPaymentPolling()
              return
            }
          }
          
          // 检查是否超时
          if (this.isPaymentTimeout) {
            this.handlePaymentTimeout()
            this.stopPaymentPolling()
            return
          }
          
          // 重置重试计数
          retryCount = 0
          
          // 继续轮询
          this.currentPayment!.pollingTimer = setTimeout(poll, this.paymentConfig.pollingInterval)
          
        } catch (error) {
          retryCount++
          console.warn(`支付状态轮询失败 (${retryCount}/${this.paymentConfig.maxRetries}):`, error)
          
          if (retryCount >= this.paymentConfig.maxRetries) {
            this.handlePaymentError('支付状态查询失败，请重试')
            this.stopPaymentPolling()
          } else {
            // 继续重试
            this.currentPayment!.pollingTimer = setTimeout(poll, this.paymentConfig.pollingInterval)
          }
        }
      }

      // 开始第一次轮询
      this.currentPayment.pollingTimer = setTimeout(poll, this.paymentConfig.pollingInterval)
    },

    /**
     * 停止支付状态轮询
     */
    stopPaymentPolling(): void {
      if (this.currentPayment?.pollingTimer) {
        clearTimeout(this.currentPayment.pollingTimer)
        this.currentPayment.pollingTimer = undefined
      }
      
      this.isPolling = false
    },

    /**
     * 处理支付成功
     */
    handlePaymentSuccess(result: PaymentResultVO): void {
      this.paymentResult = result
      this.lastError = null
      
      if (this.currentPayment) {
        this.currentPayment.status = PaymentStatus.SUCCESS
      }
      
      // 通知其他模块
      this.notifyPaymentSuccess(result)
      
      console.log('支付成功:', result)
    },

    /**
     * 处理支付失败
     */
    handlePaymentFailed(reason: string): void {
      this.lastError = reason
      
      if (this.currentPayment) {
        this.currentPayment.status = PaymentStatus.FAILED
      }
      
      // 通知其他模块
      this.notifyPaymentFailed(reason)
      
      console.log('支付失败:', reason)
    },

    /**
     * 处理支付超时
     */
    handlePaymentTimeout(): void {
      if (this.currentPayment) {
        this.currentPayment.status = PaymentStatus.TIMEOUT
      }
      
      this.lastError = '支付超时'
      
      // 通知其他模块
      this.notifyPaymentTimeout()
      
      console.log('支付超时')
    },

    /**
     * 处理支付错误
     */
    handlePaymentError(error: string): void {
      this.lastError = error
      
      if (this.currentPayment) {
        this.currentPayment.status = PaymentStatus.FAILED
      }
      
      console.error('支付错误:', error)
    },

    /**
     * 取消支付
     */
    async cancelPayment(): Promise<void> {
      if (!this.currentPayment) return

      this.stopPaymentPolling()
      
      try {
        // 调用取消支付API
        await PaymentAPI.cancelPayment(this.currentPayment.paymentId)
        
        if (this.currentPayment) {
          this.currentPayment.status = PaymentStatus.FAILED
        }
        
        console.log('支付已取消')
      } catch (error) {
        console.error('取消支付失败:', error)
      }
    },

    /**
     * 重置支付状态
     */
    resetPayment(): void {
      this.stopPaymentPolling()
      this.currentPayment = null
      this.paymentResult = null
      this.lastError = null
      this.isProcessing = false
      this.selectedMethod = null
      
      console.log('支付状态已重置')
    },

    /**
     * 从付款码获取支付方式
     */
    getPaymentMethodFromCode(paymentCode: string): PaymentMethod {
      // 微信付款码: 1开头，18位数字
      if (/^1[0-5]\d{16}$/.test(paymentCode)) {
        return PaymentMethod.WECHAT
      }
      
      // 支付宝付款码: 25-30开头，18位数字
      if (/^2[5-9]\d{16}$/.test(paymentCode) || /^30\d{16}$/.test(paymentCode)) {
        return PaymentMethod.ALIPAY
      }
      
      // 默认返回微信支付
      return PaymentMethod.WECHAT
    },

    /**
     * 通知支付成功
     */
    notifyPaymentSuccess(result: PaymentResultVO): void {
      // 通知会员模块
      const memberStore = useMemberStore()
      if (memberStore.isMemberLogin && this.currentPayment) {
        memberStore.onPaymentCompleted(this.currentPayment.orderId)
      }
      
      // 通知购物车模块 - 使用本地清理方法
      const cartStore = useCartStore()
      cartStore.clearLocalCart()
      
      // 发送全局事件
      window.dispatchEvent(new CustomEvent('payment:success', { 
        detail: result 
      }))
    },

    /**
     * 通知支付失败
     */
    notifyPaymentFailed(reason: string): void {
      // 发送全局事件
      window.dispatchEvent(new CustomEvent('payment:failed', { 
        detail: { reason } 
      }))
    },

    /**
     * 通知支付超时
     */
    notifyPaymentTimeout(): void {
      // 发送全局事件
      window.dispatchEvent(new CustomEvent('payment:timeout'))
    },

    /**
     * 获取支付方式图标
     */
    getPaymentMethodIcon(method: PaymentMethod): string {
      const iconMap = {
        [PaymentMethod.WECHAT]: '/images/payment/wechat.png',
        [PaymentMethod.ALIPAY]: '/images/payment/alipay.png'
      }
      
      return iconMap[method] || ''
    },

    /**
     * 验证支付金额
     */
    validatePaymentAmount(amount: number): boolean {
      return amount > 0 && amount <= 50000
    },

    /**
     * 获取支付限额信息
     */
    getPaymentLimits(method: PaymentMethod): {
      minAmount: number
      maxAmount: number
      dailyLimit: number
    } {
      const limits = {
        [PaymentMethod.WECHAT]: {
          minAmount: 0.01,
          maxAmount: 50000,
          dailyLimit: 200000
        },
        [PaymentMethod.ALIPAY]: {
          minAmount: 0.01,
          maxAmount: 50000,
          dailyLimit: 200000
        }
      }
      
      return limits[method] || limits[PaymentMethod.WECHAT]
    },

    /**
     * 格式化支付金额
     */
    formatPaymentAmount(amount: number): string {
      return `¥${amount.toFixed(2)}`
    },

    /**
     * 检查支付方式是否可用
     */
    isPaymentMethodAvailable(method: PaymentMethod): boolean {
      return this.availableMethods.includes(method)
    }
  }
}) 