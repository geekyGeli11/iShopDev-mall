import { request } from '../request'
import { API_ENDPOINTS } from '@shared/constants/api'
import type { 
  OrderCreateParam,
  OrderVO,
  OrderItemParam,
  ApiResponse,
  CommonPage,
  OrderConfirmParam,
  OrderStatus
} from '@shared/types'

/**
 * 订单相关API服务
 */
export class OrderAPI {
  /**
   * 快速下单（单商品）
   * @param productId 商品ID
   * @param skuId SKU ID
   * @param quantity 购买数量
   * @param guestId 游客ID（游客模式必传）
   * @param terminalCode 终端编码
   * @param deviceInfo 设备信息
   * @param storeId 门店ID
   * @param schoolId 学校ID
   * @returns 订单创建结果
   */
  static async createQuickOrder(
    productId: number,
    skuId?: number,
    quantity: number = 1,
    guestId?: string,
    terminalCode?: string,
    deviceInfo?: string,
    storeId?: number,
    schoolId?: number
  ): Promise<ApiResponse<{
    orderId: number
    orderSn: string
    totalAmount: number
    payAmount: number
    orderStatus: number
  }>> {
    const params: any = { productId, quantity }
    if (skuId) params.skuId = skuId
    if (guestId) params.guestId = guestId
    if (terminalCode) params.terminalCode = terminalCode
    if (deviceInfo) params.deviceInfo = deviceInfo
    if (storeId) params.storeId = storeId
    if (schoolId) params.schoolId = schoolId

    return request.post(API_ENDPOINTS.ORDER_CREATE_QUICK, params)
  }

  /**
   * 购物车下单
   * @param cartItemIds 购物车商品ID列表
   * @param orderParam 订单创建参数
   * @returns 订单创建结果
   */
  static async createCartOrder(
    cartItemIds: number[] | undefined,
    orderParam: OrderCreateParam
  ): Promise<ApiResponse<{
    orderId: number
    orderSn: string
    totalAmount: number
    payAmount: number
    orderStatus: number
    orderItems: any[]
  }>> {
    const data = {
      cartItemIds,
      ...orderParam
    }
    return request.post(API_ENDPOINTS.ORDER_CREATE_FROM_CART, data)
  }

  /**
   * 创建订单
   * @param orderParam 订单参数
   * @returns 订单创建结果
   */
  static async createOrder(orderParam: OrderCreateParam): Promise<ApiResponse<{
    orderId: number
    orderSn: string
    totalAmount: number
    payAmount: number
    needPayment: boolean
  }>> {
    return request.post(API_ENDPOINTS.ORDER_CREATE, orderParam)
  }

  /**
   * 计算订单价格
   * @param calculateParam 计算参数
   * @returns 价格计算结果
   */
  static async calculateOrder(calculateParam: {
    cartItemIds?: number[]
    couponId?: number
    usePoints?: number
  }): Promise<ApiResponse<{
    totalAmount: number
    discountAmount: number
    couponAmount: number
    pointsAmount: number
    finalAmount: number
    needPayment: boolean
    breakdown: {
      productAmount: number
      shippingAmount: number
      taxAmount: number
      promotionAmount: number
    }
  }>> {
    return request.post(API_ENDPOINTS.ORDER_CALCULATE, calculateParam)
  }

  /**
   * 获取订单详情
   * @param orderId 订单ID
   * @returns 订单详情
   */
  static async getOrderDetail(orderId: number): Promise<ApiResponse<OrderVO>> {
    return request.get(`${API_ENDPOINTS.ORDER_DETAIL}/${orderId}`)
  }

  /**
   * 获取订单列表
   * @param params 查询参数
   * @returns 订单列表
   */
  static async getOrderList(params: {
    status?: OrderStatus
    page?: number
    size?: number
    startTime?: string
    endTime?: string
  } = {}): Promise<ApiResponse<{
    list: OrderVO[]
    total: number
    page: number
    size: number
  }>> {
    return request.get(API_ENDPOINTS.ORDER_LIST, params)
  }

  /**
   * 查询订单状态
   * @param orderId 订单ID
   * @returns 订单状态
   */
  static async getOrderStatus(orderId: number): Promise<ApiResponse<{
    orderId: number
    status: OrderStatus
    statusText: string
    canCancel: boolean
    canPay: boolean
    paymentInfo?: {
      needPayment: boolean
      payAmount: number
      paymentTimeLimit: number
    }
  }>> {
    return request.get(`${API_ENDPOINTS.ORDER_STATUS}/${orderId}`)
  }

  /**
   * 取消订单
   * @param orderId 订单ID
   * @param guestId 游客ID（游客模式必传）
   * @param reason 取消原因
   * @returns 取消结果
   */
  static async cancelOrder(orderId: number, guestId?: string, reason?: string): Promise<ApiResponse<string>> {
    const params: any = {}
    if (guestId) params.guestId = guestId
    if (reason) params.reason = reason
    
    return request.post(`${API_ENDPOINTS.ORDER_CANCEL}/${orderId}`, null, params)
  }

  /**
   * 确认订单
   * @param confirmParam 确认参数
   * @returns 确认结果
   */
  static async confirmOrder(confirmParam: OrderConfirmParam): Promise<ApiResponse<{
    orderId: number
    confirmed: boolean
    message: string
  }>> {
    return request.post(API_ENDPOINTS.ORDER_CONFIRM, confirmParam)
  }

  /**
   * 获取订单历史记录（会员）
   * @param status 订单状态
   * @param pageNum 页码
   * @param pageSize 每页数量
   * @returns 订单列表
   */
  static async getOrderHistory(
    status?: number,
    pageNum: number = 1,
    pageSize: number = 10
  ): Promise<ApiResponse<CommonPage<OrderVO>>> {
    const params: any = { pageNum, pageSize }
    if (status !== undefined) params.status = status
    return request.get(API_ENDPOINTS.ORDER_HISTORY, params)
  }

  /**
   * 获取游客订单历史记录
   * @param guestId 游客ID
   * @param pageNum 页码
   * @param pageSize 每页数量
   * @returns 订单列表
   */
  static async getGuestOrderHistory(
    guestId: string,
    pageNum: number = 1,
    pageSize: number = 10
  ): Promise<ApiResponse<CommonPage<OrderVO>>> {
    const params = { guestId, pageNum, pageSize }
    return request.get(API_ENDPOINTS.ORDER_GUEST_HISTORY, params)
  }

  /**
   * 验证库存
   * @param orderParam 订单创建参数
   * @returns 库存验证结果
   */
  static async validateOrderStock(orderParam: OrderCreateParam): Promise<ApiResponse<{
    valid: boolean
    issues: Array<{
      productId: number
      skuId?: number
      productName: string
      requestedQuantity: number
      availableStock: number
      reason: string
    }>
    canProceed: boolean
    suggestions: string[]
  }>> {
    return request.post(API_ENDPOINTS.ORDER_VALIDATE_STOCK, orderParam)
  }

  /**
   * 支付成功回调
   * @param orderId 订单ID
   * @param payType 支付方式
   * @param transactionId 支付流水号
   * @returns 处理结果
   */
  static async paymentSuccess(
    orderId: number,
    payType: number,
    transactionId: string
  ): Promise<ApiResponse<boolean>> {
    // 使用查询参数而不是请求体
    return request.post(`${API_ENDPOINTS.ORDER_PAYMENT_SUCCESS}/${orderId}?payType=${payType}&transactionId=${encodeURIComponent(transactionId)}`)
  }

  /**
   * 支付失败回调
   * @param orderId 订单ID
   * @param reason 失败原因
   * @returns 处理结果
   */
  static async paymentFailed(orderId: number, reason: string): Promise<ApiResponse<boolean>> {
    return request.post(`${API_ENDPOINTS.ORDER_PAYMENT_FAILED}/${orderId}`, { reason })
  }

  /**
   * 获取订单统计信息
   * @param guestId 游客ID（可选）
   * @returns 统计信息
   */
  static async getOrderStatistics(guestId?: string): Promise<ApiResponse<{
    totalOrders: number
    pendingPayment: number
    processing: number
    completed: number
    cancelled: number
    totalAmount: number
    averageAmount: number
    lastOrderTime?: number
  }>> {
    // 注意：这个接口在后端可能需要添加，这里先模拟实现
    const historyResponse = guestId 
      ? await this.getGuestOrderHistory(guestId, 1, 100)
      : await this.getOrderHistory(undefined, 1, 100)
    
    const orders = historyResponse.data.list || []
    
    const stats = {
      totalOrders: orders.length,
      pendingPayment: orders.filter(o => o.status === 0).length,
      processing: orders.filter(o => [1, 2].includes(o.status)).length,
      completed: orders.filter(o => o.status === 3).length,
      cancelled: orders.filter(o => [4, 5].includes(o.status)).length,
      totalAmount: orders.reduce((sum, o) => sum + (o.payAmount || 0), 0),
      averageAmount: orders.length > 0 ? orders.reduce((sum, o) => sum + (o.payAmount || 0), 0) / orders.length : 0,
      lastOrderTime: orders.length > 0 ? Math.max(...orders.map(o => new Date(o.createTime).getTime())) : undefined
    }

    return {
      code: 200,
      message: 'success',
      data: stats
    }
  }

  /**
   * 重新下单
   * @param orderId 原订单ID
   * @returns 新订单创建结果
   */
  static async reorder(orderId: number): Promise<ApiResponse<{
    orderId: number
    orderSn: string
    totalAmount: number
    unavailableItems: Array<{
      productId: number
      productName: string
      reason: string
    }>
  }>> {
    return request.post(`${API_ENDPOINTS.ORDER_CREATE}/reorder/${orderId}`)
  }

  /**
   * 获取订单支付信息
   * @param orderId 订单ID
   * @returns 支付信息
   */
  static async getOrderPaymentInfo(orderId: number): Promise<ApiResponse<{
    orderId: number
    orderSn: string
    payAmount: number
    needPayment: boolean
    paymentMethods: string[]
    paymentTimeLimit: number
    paymentStatus: string
  }>> {
    return request.get(`${API_ENDPOINTS.ORDER_DETAIL}/${orderId}/payment`)
  }

  /**
   * 获取订单物流信息
   * @param orderId 订单ID
   * @returns 物流信息
   */
  static async getOrderDeliveryInfo(orderId: number): Promise<ApiResponse<{
    orderId: number
    deliveryType: string
    deliveryStatus: string
    trackingNumber?: string
    deliveryCompany?: string
    deliveryTime?: string
    deliveryAddress?: string
    deliveryTrack: Array<{
      time: string
      status: string
      description: string
      location?: string
    }>
  }>> {
    return request.get(`${API_ENDPOINTS.ORDER_DETAIL}/${orderId}/delivery`)
  }

  /**
   * 评价订单
   * @param orderId 订单ID
   * @param evaluation 评价内容
   * @returns 评价结果
   */
  static async evaluateOrder(
    orderId: number,
    evaluation: {
      rating: number
      comment?: string
      images?: string[]
    }
  ): Promise<ApiResponse<string>> {
    return request.post(`${API_ENDPOINTS.ORDER_DETAIL}/${orderId}/evaluate`, evaluation)
  }

  /**
   * 申请退款
   * @param orderId 订单ID
   * @param refundInfo 退款信息
   * @returns 退款申请结果
   */
  static async applyRefund(
    orderId: number,
    refundInfo: {
      reason: string
      amount: number
      description?: string
      images?: string[]
    }
  ): Promise<ApiResponse<{
    refundId: string
    refundSn: string
    refundAmount: number
    estimatedTime: string
  }>> {
    return request.post(`${API_ENDPOINTS.ORDER_DETAIL}/${orderId}/refund`, refundInfo)
  }
} 