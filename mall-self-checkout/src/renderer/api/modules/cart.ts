import { request } from '../request'
import { API_ENDPOINTS } from '@shared/constants/api'
import type { 
  CartAddParam,
  CartVO,
  CartUpdateParam,
  ApiResponse 
} from '@shared/types'

/**
 * 购物车相关API服务
 */
export class CartAPI {
  /**
   * 添加商品到购物车
   * @param cartParam 购物车参数
   * @returns 添加结果
   */
  static async addToCart(cartParam: CartAddParam): Promise<ApiResponse<CartVO>> {
    return request.post(API_ENDPOINTS.CART_ADD, cartParam)
  }

  /**
   * 更新购物车商品数量
   * @param updateParam 更新参数
   * @returns 更新结果
   */
  static async updateCartItem(updateParam: CartUpdateParam): Promise<ApiResponse<CartVO>> {
    return request.post(API_ENDPOINTS.CART_UPDATE, updateParam)
  }

  /**
   * 从购物车移除商品
   * @param cartItemId 购物车项ID
   * @returns 移除结果
   */
  static async removeFromCart(cartItemId: number): Promise<ApiResponse<string>> {
    return request.post(API_ENDPOINTS.CART_REMOVE, { cartItemId })
  }

  /**
   * 获取购物车列表
   * @returns 购物车列表
   */
  static async getCartList(): Promise<ApiResponse<{
    cartItems: CartVO[]
    totalCount: number
    totalAmount: number
    selectedCount: number
    selectedAmount: number
  }>> {
    return request.get(API_ENDPOINTS.CART_LIST)
  }

  /**
   * 清空购物车
   * @returns 清空结果
   */
  static async clearCart(): Promise<ApiResponse<string>> {
    return request.post(API_ENDPOINTS.CART_CLEAR)
  }

  /**
   * 获取购物车商品总数
   * @returns 商品总数
   */
  static async getCartCount(): Promise<ApiResponse<{
    totalCount: number
    selectedCount: number
  }>> {
    return request.get(API_ENDPOINTS.CART_COUNT)
  }

  /**
   * 获取购物车摘要信息
   * @returns 摘要信息
   */
  static async getCartSummary(): Promise<ApiResponse<{
    totalCount: number
    totalAmount: number
    selectedCount: number
    selectedAmount: number
    discountAmount: number
    finalAmount: number
    hasPromotionItems: boolean
  }>> {
    return request.get(API_ENDPOINTS.CART_SUMMARY)
  }

  /**
   * 验证购物车商品状态
   * @returns 验证结果
   */
  static async validateCart(): Promise<ApiResponse<{
    valid: boolean
    invalidItems: Array<{
      cartItemId: number
      productId: number
      skuId?: number
      reason: string
      action: 'REMOVE' | 'UPDATE_PRICE' | 'UPDATE_STOCK'
    }>
    totalCount: number
    validCount: number
  }>> {
    return request.post(API_ENDPOINTS.CART_VALIDATE)
  }

  /**
   * 批量添加商品到购物车
   * @param cartItems 购物车商品列表
   * @returns 批量添加结果
   */
  static async batchAddToCart(cartItems: CartAddParam[]): Promise<ApiResponse<{
    success: CartVO[]
    failed: Array<{
      item: CartAddParam
      reason: string
    }>
    summary: {
      total: number
      successCount: number
      failedCount: number
    }
  }>> {
    return request.post(`${API_ENDPOINTS.CART_ADD}/batch`, { cartItems })
  }

  /**
   * 选择/取消选择购物车商品
   * @param cartItemIds 购物车项ID列表
   * @param selected 是否选中
   * @returns 选择结果
   */
  static async selectCartItems(
    cartItemIds: number[], 
    selected: boolean = true
  ): Promise<ApiResponse<{
    updatedItems: number[]
    selectedCount: number
    selectedAmount: number
  }>> {
    return request.post(`${API_ENDPOINTS.CART_UPDATE}/select`, {
      cartItemIds,
      selected
    })
  }

  /**
   * 全选/全不选购物车商品
   * @param selected 是否全选
   * @returns 全选结果
   */
  static async selectAllCartItems(selected: boolean = true): Promise<ApiResponse<{
    selectedCount: number
    selectedAmount: number
  }>> {
    return request.post(`${API_ENDPOINTS.CART_UPDATE}/selectAll`, { selected })
  }

  /**
   * 移动商品到收藏夹
   * @param cartItemId 购物车项ID
   * @returns 移动结果
   */
  static async moveToFavorites(cartItemId: number): Promise<ApiResponse<string>> {
    return request.post(`${API_ENDPOINTS.CART_REMOVE}/toFavorites`, { cartItemId })
  }

  /**
   * 获取相似商品推荐
   * @param cartItemId 购物车项ID
   * @returns 推荐商品
   */
  static async getSimilarProducts(cartItemId: number): Promise<ApiResponse<Array<{
    productId: number
    productName: string
    productPic: string
    price: number
    originalPrice?: number
    promotionType: number
  }>>> {
    return request.get(`${API_ENDPOINTS.CART_LIST}/similar/${cartItemId}`)
  }

  /**
   * 获取购物车（store使用的方法名）
   * @returns 购物车信息
   */
  static async getCart(): Promise<ApiResponse<CartVO>> {
    const response = await this.getCartList()
    // 转换响应格式以匹配CartVO接口
    return {
      code: response.code,
      message: response.message,
      data: {
        userType: 'GUEST',
        userId: '',
        items: response.data.cartItems as any,
        totalQuantity: response.data.totalCount,
        availableQuantity: response.data.selectedCount,
        unavailableQuantity: 0,
        totalAmount: response.data.totalAmount,
        availableAmount: response.data.selectedAmount,
        discountAmount: 0,
        shippingFee: 0,
        finalAmount: response.data.selectedAmount,
        needShipping: false,
        cartStatus: 'NORMAL'
      } as CartVO
    }
  }

  /**
   * 添加商品（store使用的方法名）
   * @param param 添加参数
   * @returns 添加结果
   */
  static async addItem(param: CartAddParam): Promise<ApiResponse<CartVO>> {
    return this.addToCart(param)
  }

  /**
   * 更新商品（store使用的方法名）
   * @param param 更新参数
   * @returns 更新结果
   */
  static async updateItem(param: CartUpdateParam): Promise<ApiResponse<CartVO>> {
    return this.updateCartItem(param)
  }

  /**
   * 移除商品（store使用的方法名）
   * @param param 移除参数
   * @returns 移除结果
   */
  static async removeItem(param: { productId: number; skuId?: number }): Promise<ApiResponse<CartVO>> {
    // 这里需要根据productId和skuId查找对应的cartItemId
    return request.post(API_ENDPOINTS.CART_REMOVE, param)
  }

  /**
   * 切换商品选中状态
   * @param productId 商品ID
   * @param skuId SKU ID
   * @param checked 是否选中
   * @returns 切换结果
   */
  static async toggleItemCheck(productId: number, skuId: number | undefined, checked: boolean): Promise<ApiResponse<CartVO>> {
    return request.post(`${API_ENDPOINTS.CART_UPDATE}/toggleCheck`, {
      productId,
      skuId,
      checked
    })
  }

  /**
   * 全选/反选
   * @param checked 是否全选
   * @returns 切换结果
   */
  static async toggleSelectAll(checked: boolean): Promise<ApiResponse<CartVO>> {
    const response = await this.selectAllCartItems(checked)
    // 转换响应格式以匹配CartVO接口
    return {
      code: response.code,
      message: response.message,
      data: {
        userType: 'GUEST',
        userId: '',
        items: [],
        totalQuantity: 0,
        availableQuantity: response.data.selectedCount,
        unavailableQuantity: 0,
        totalAmount: 0,
        availableAmount: response.data.selectedAmount,
        discountAmount: 0,
        shippingFee: 0,
        finalAmount: response.data.selectedAmount,
        needShipping: false,
        cartStatus: 'NORMAL'
      } as CartVO
    }
  }

  /**
   * 获取可用优惠券
   * @param param 查询参数
   * @returns 优惠券列表
   */
  static async getAvailableCoupons(param: {
    totalAmount: number
    cartItems: Array<{
      productId: number
      skuId?: number
      quantity: number
      price: number
    }>
  }): Promise<ApiResponse<any[]>> {
    return request.post(API_ENDPOINTS.CART_AVAILABLE_COUPONS, param)
  }

  /**
   * 计算购物车价格
   * @param param 计算参数
   * @returns 计算结果
   */
  static async calculateCart(param: {
    cartItems: Array<{
      productId: number
      skuId?: number
      quantity: number
      price: number
    }>
    couponIds: number[]
  }): Promise<ApiResponse<{
    totalAmount: number
    discountAmount: number
    finalAmount: number
    appliedCoupons: any[]
  }>> {
    return request.post(API_ENDPOINTS.CART_CALCULATE, param)
  }

  /**
   * 检查购物车商品库存
   * @param cartItemIds 购物车项ID列表（可选，不传则检查全部）
   * @returns 库存检查结果
   */
  static async checkCartStock(cartItemIds?: number[]): Promise<ApiResponse<{
    allAvailable: boolean
    unavailableItems: Array<{
      cartItemId: number
      productId: number
      skuId?: number
      productName: string
      requestQuantity: number
      availableStock: number
      stockStatus: string
    }>
  }>> {
    const params = cartItemIds ? { cartItemIds } : {}
    return request.post(`${API_ENDPOINTS.CART_VALIDATE}/stock`, params)
  }
} 