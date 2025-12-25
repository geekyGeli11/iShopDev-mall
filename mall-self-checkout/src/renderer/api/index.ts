/**
 * API模块导出
 */

// 导入API模块
import { MemberAPI } from './modules/member'
import { ProductAPI } from './modules/product'
import { CartAPI } from './modules/cart'
import { OrderAPI } from './modules/order'
import { PaymentAPI } from './modules/payment'
import { CouponAPI } from './modules/coupon'
import { TestAPI } from './modules/test'
import StoreManager from '@/utils/storeManager'

// 导出所有API模块
export * from './modules/member'
export * from './modules/product'
export * from './modules/cart'
export * from './modules/order'
export * from './modules/payment'
export * from './modules/coupon'
export * from './modules/test'

// 导出请求工具
export * from './request'

// 工具类导出
export { RequestUtil } from './request'

// 常量导出
export { API_ENDPOINTS, HTTP_METHODS, BUSINESS_CODES } from '@shared/constants/api'

// 类型定义导出 (如果需要在业务代码中使用)
export type {
  ApiResponse,
  CommonPage,
  MemberLoginParam,
  MemberCodeLoginParam,
  GuestLoginParam,
  MemberLoginResult,
  ProductScanParam,
  ProductScanVO,
  CartItemParam,
  CartVO,
  OrderCreateParam,
  OrderVO,
  PaymentQRParam,
  PaymentCodeParam,
  PaymentResultVO,
  MemberCouponListParam,
  MemberCouponVO
} from '@shared/types'

/**
 * API服务集合
 * 提供统一的API调用方式
 */
export const API = {
  Member: MemberAPI,
  Product: ProductAPI,
  Cart: CartAPI,
  Order: OrderAPI,
  Payment: PaymentAPI,
  Coupon: CouponAPI,
  Test: TestAPI
} as const

/**
 * 常用API快捷方法
 */
export const QuickAPI = {
  /**
   * 游客登录
   * @param deviceInfo 设备信息
   * @returns 游客信息
   */
  guestLogin: (deviceInfo?: string) => {
    // 获取门店信息（使用StoreManager）
    const storeParams = StoreManager.getStoreParams()

    return MemberAPI.guestLogin(
      deviceInfo,
      'selfcheck',
      storeParams.storeId,
      storeParams.schoolId
    )
  },

  /**
   * 扫码查询商品
   * @param barcode 条码
   * @returns 商品信息
   */
  scanProduct: (barcode: string) => ProductAPI.scanProduct({ barcode }),

  /**
   * 快速下单
   * @param productId 商品ID
   * @param quantity 数量
   * @param guestId 游客ID
   * @returns 订单信息
   */
  quickOrder: (productId: number, quantity: number = 1, guestId?: string) => {
    // 获取门店信息（使用StoreManager）
    const storeParams = StoreManager.getStoreParams()

    return OrderAPI.createQuickOrder(
      productId,
      undefined,
      quantity,
      guestId,
      undefined, // terminalCode
      undefined, // deviceInfo
      storeParams.storeId,
      storeParams.schoolId
    )
  },

  /**
   * 生成支付二维码
   * @param orderId 订单ID
   * @param payType 支付方式
   * @returns 支付二维码信息
   */
  generatePaymentQR: (orderId: number, payType: 'WECHAT' | 'ALIPAY' = 'WECHAT') =>
    PaymentAPI.generatePaymentQR({
      orderId,
      payType,
      title: '自助收银付款',
      description: '自助收银系统付款',
      amount: 0 // 从订单获取
    }),

  /**
   * 获取推荐优惠券
   * @param orderAmount 订单金额
   * @returns 推荐优惠券
   */
  getRecommendedCoupons: (orderAmount: number, productIds?: number[]) => CouponAPI.getRecommendedCoupons(orderAmount, productIds)
}

// 默认导出
export default API 