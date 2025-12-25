import { request } from '../request'
import type { ApiResponse } from '@shared/types'
import { API_ENDPOINTS } from '@shared/constants'
import type {
  CouponVO,
  MemberCouponVO,
  MemberCouponListParam
} from '@shared/types'

/**
 * 优惠券相关API服务
 */
export class CouponAPI {
  /**
   * 获取可用优惠券列表
   * @param params 查询参数
   * @returns 可用优惠券列表
   */
  static async getAvailableCoupons(params: {
    orderAmount?: number
    productIds?: number[]
  } = {}): Promise<ApiResponse<CouponVO[]>> {
    return request.get(API_ENDPOINTS.COUPON_AVAILABLE, params)
  }

  /**
   * 获取会员优惠券列表
   * @param params 查询参数
   * @returns 会员优惠券列表
   */
  static async getMemberCoupons(params: MemberCouponListParam): Promise<ApiResponse<{
    list: MemberCouponVO[]
    total: number
    pageNum: number
    pageSize: number
  }>> {
    return request.post(API_ENDPOINTS.COUPON_LIST, params)
  }

  /**
   * 获取优惠券统计信息
   * @returns 统计信息
   */
  static async getCouponStatistics(): Promise<ApiResponse<{
    availableCount: number
    nearExpiryCount: number
    memberId: number
  }>> {
    return request.get(API_ENDPOINTS.COUPON_STATISTICS)
  }

  /**
   * 获取优惠券详情
   * @param historyId 优惠券历史ID
   * @returns 优惠券详情
   */
  static async getCouponDetail(historyId: number): Promise<ApiResponse<MemberCouponVO>> {
    return request.get(`${API_ENDPOINTS.COUPON_DETAIL}/${historyId}`)
  }

  /**
   * 验证优惠券是否可用
   * @param historyId 优惠券历史ID
   * @param orderAmount 订单金额
   * @returns 验证结果
   */
  static async validateCoupon(
    historyId: number,
    orderAmount: number
  ): Promise<ApiResponse<{
    historyId: number
    orderAmount: number
    isValid: boolean
    message: string
  }>> {
    return request.post(`${API_ENDPOINTS.COUPON_VALIDATE}/${historyId}`, {
      orderAmount
    })
  }

  /**
   * 使用优惠券
   * @param couponId 优惠券ID
   * @param orderId 订单ID
   * @returns 使用结果
   */
  static async useCoupon(
    couponId: number,
    orderId: number
  ): Promise<ApiResponse<{
    couponId: number
    orderId: number
    discountAmount: number
    usedTime: string
    finalAmount: number
  }>> {
    return request.post(API_ENDPOINTS.COUPON_USE, {
      couponId,
      orderId
    })
  }

  /**
   * 获取订单可用优惠券
   * @param orderAmount 订单金额
   * @returns 可用优惠券列表
   */
  static async getAvailableCouponsForOrder(
    orderAmount: number
  ): Promise<ApiResponse<{
    list: MemberCouponVO[]
    total: number
    pageNum: number
    pageSize: number
  }>> {
    return request.get(API_ENDPOINTS.COUPON_AVAILABLE, { orderAmount })
  }

  /**
   * 获取未使用优惠券列表
   * @param pageNum 页码
   * @param pageSize 每页数量
   * @returns 未使用优惠券列表
   */
  static async getUnusedCoupons(
    pageNum: number = 1,
    pageSize: number = 10
  ): Promise<ApiResponse<{
    list: MemberCouponVO[]
    total: number
    pageNum: number
    pageSize: number
  }>> {
    return request.get(API_ENDPOINTS.COUPON_UNUSED, { pageNum, pageSize })
  }

  /**
   * 获取即将过期优惠券列表
   * @param pageNum 页码
   * @param pageSize 每页数量
   * @returns 即将过期优惠券列表
   */
  static async getNearExpiryCoupons(
    pageNum: number = 1,
    pageSize: number = 10
  ): Promise<ApiResponse<{
    list: MemberCouponVO[]
    total: number
    pageNum: number
    pageSize: number
  }>> {
    return request.get(API_ENDPOINTS.COUPON_NEAR_EXPIRY, { pageNum, pageSize })
  }

  /**
   * 计算优惠券折扣
   * @param couponIds 优惠券ID列表
   * @param orderAmount 订单金额
   * @param productIds 商品ID列表
   * @returns 折扣计算结果
   */
  static async calculateCouponDiscount(
    couponIds: number[],
    orderAmount: number,
    productIds?: number[]
  ): Promise<ApiResponse<{
    totalDiscount: number
    finalAmount: number
    couponDiscounts: Array<{
      couponId: number
      couponName: string
      discountAmount: number
      applicable: boolean
      reason?: string
    }>
    bestCombination: {
      couponIds: number[]
      totalDiscount: number
      finalAmount: number
    }
  }>> {
    return request.post(`${API_ENDPOINTS.COUPON_LIST}/calculate`, {
      couponIds,
      orderAmount,
      productIds
    })
  }

  /**
   * 获取推荐优惠券
   * @param orderAmount 订单金额
   * @param productIds 商品ID列表
   * @returns 推荐优惠券
   */
  static async getRecommendedCoupons(
    orderAmount: number,
    productIds?: number[]
  ): Promise<ApiResponse<Array<CouponVO & {
    recommendReason: string
    estimatedDiscount: number
    priority: number
  }>>> {
    return request.get(`${API_ENDPOINTS.COUPON_AVAILABLE}/recommended`, {
      orderAmount,
      productIds
    })
  }

  /**
   * 领取优惠券
   * @param couponId 优惠券ID
   * @returns 领取结果
   */
  static async receiveCoupon(couponId: number): Promise<ApiResponse<{
    couponId: number
    memberCouponId: number
    couponName: string
    received: boolean
    validPeriod: {
      startTime: string
      endTime: string
    }
  }>> {
    return request.post(`${API_ENDPOINTS.COUPON_LIST}/${couponId}/receive`)
  }
} 