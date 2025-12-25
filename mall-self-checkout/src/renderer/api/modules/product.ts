import { request } from '../request'
import { API_ENDPOINTS } from '@shared/constants/api'
import type { 
  ProductScanParam,
  ProductScanVO,
  ApiResponse 
} from '@shared/types'

/**
 * 商品相关API服务
 */
export class ProductAPI {
  /**
   * 扫码查询商品信息
   * @param scanParam 扫码参数
   * @returns 商品信息
   */
  static async scanProduct(scanParam: ProductScanParam): Promise<ApiResponse<ProductScanVO>> {
    return request.post(API_ENDPOINTS.PRODUCT_BARCODE_SCAN, scanParam)
  }

  /**
   * 根据商品ID获取商品详情
   * @param productId 商品ID
   * @param options 查询选项
   * @returns 商品详情
   */
  static async getProductDetail(
    productId: number, 
    options: {
      needStockCheck?: boolean
      needPromotionInfo?: boolean
    } = {}
  ): Promise<ApiResponse<ProductScanVO>> {
    const params = {
      needStockCheck: options.needStockCheck ?? true,
      needPromotionInfo: options.needPromotionInfo ?? true
    }
    return request.get(`${API_ENDPOINTS.PRODUCT_DETAIL}/${productId}`, params)
  }

  /**
   * 根据SKU ID获取SKU详情
   * @param skuId SKU ID
   * @param options 查询选项
   * @returns SKU详情
   */
  static async getSkuDetail(
    skuId: number,
    options: {
      needStockCheck?: boolean
      needPromotionInfo?: boolean
    } = {}
  ): Promise<ApiResponse<ProductScanVO>> {
    const params = {
      needStockCheck: options.needStockCheck ?? true,
      needPromotionInfo: options.needPromotionInfo ?? true
    }
    return request.get(`${API_ENDPOINTS.PRODUCT_DETAIL}/sku/${skuId}`, params)
  }

  /**
   * 检查商品是否可以销售
   * @param productId 商品ID
   * @param skuId SKU ID（可选）
   * @param quantity 购买数量
   * @returns 销售状态检查结果
   */
  static async checkProductSaleable(
    productId: number, 
    skuId?: number, 
    quantity: number = 1
  ): Promise<ApiResponse<{
    saleable: boolean
    reason?: string
    stockStatus: string
    availableStock: number
    maxBuyQuantity: number
  }>> {
    const params = {
      productId,
      quantity,
      ...(skuId && { skuId })
    }
    return request.post(`${API_ENDPOINTS.PRODUCT_DETAIL}/check`, params)
  }

  /**
   * 计算商品价格
   * @param productId 商品ID
   * @param skuId SKU ID（可选）
   * @param quantity 购买数量
   * @returns 价格计算结果
   */
  static async calculateProductPrice(
    productId: number,
    skuId?: number,
    quantity: number = 1
  ): Promise<ApiResponse<ProductScanVO>> {
    const params = {
      productId,
      quantity,
      ...(skuId && { skuId })
    }
    return request.post(API_ENDPOINTS.PRODUCT_PRICE_CHECK, params)
  }

  /**
   * 批量扫码查询商品
   * @param barcodes 条码列表
   * @returns 批量查询结果
   */
  static async batchScanProducts(barcodes: string[]): Promise<ApiResponse<{
    success: ProductScanVO[]
    failed: Array<{
      barcode: string
      reason: string
    }>
    summary: {
      total: number
      successCount: number
      failedCount: number
    }
  }>> {
    return request.post('/product/batchScan', { barcodes })
  }

  /**
   * 验证条码格式
   * @param barcode 条码
   * @returns 验证结果
   */
  static async validateBarcodeFormat(barcode: string): Promise<ApiResponse<{
    valid: boolean
    format: string
    type: 'EAN_13' | 'EAN_8' | 'CODE_128' | 'QR_CODE' | 'UNKNOWN'
    suggestion?: string
  }>> {
    return request.get('/product/validateBarcode', { barcode })
  }

  /**
   * 快速扫码（简化版）
   * @param barcode 条码
   * @returns 商品基本信息
   */
  static async quickScan(barcode: string): Promise<ApiResponse<ProductScanVO>> {
    return request.get('/product/quickScan', { barcode })
  }

  /**
   * 获取商品促销信息
   * @param productId 商品ID
   * @returns 促销信息
   */
  static async getPromotionInfo(productId: number): Promise<ApiResponse<{
    hasPromotion: boolean
    promotionType: number
    promotionPrice?: number
    promotionName?: string
    promotionDesc?: string
    startTime?: string
    endTime?: string
    limitQuantity?: number
  }>> {
    return request.get(`${API_ENDPOINTS.PRODUCT_DETAIL}/${productId}`, {
      needStockCheck: false,
      needPromotionInfo: true
    }).then(response => {
      // 从完整商品信息中提取促销信息
      const product = response.data as any
      return {
        ...response,
        data: {
          hasPromotion: (product.promotionType || 0) > 0,
          promotionType: product.promotionType || 0,
          promotionPrice: product.promotionPrice,
          promotionName: (product.brandName || '') + ' 促销活动',
          startTime: product.promotionStartTime,
          endTime: product.promotionEndTime
        }
      }
    })
  }

  /**
   * 检查库存状态
   * @param productId 商品ID
   * @param skuId SKU ID（可选）
   * @returns 库存状态
   */
  static async checkStock(
    productId: number, 
    skuId?: number
  ): Promise<ApiResponse<{
    stock: number
    stockStatus: 'SUFFICIENT' | 'LOW' | 'OUT_OF_STOCK'
    lowStockThreshold: number
    available: boolean
  }>> {
    const params = {
      productId,
      ...(skuId && { skuId })
    }
    return request.get(`${API_ENDPOINTS.PRODUCT_DETAIL}/stock`, params)
  }

  /**
   * 获取商品分类列表
   * @returns 分类列表
   */
  static async getCategories(): Promise<ApiResponse<Array<{
    id: number
    name: string
    parentId: number
    level: number
    sort: number
    productCount: number
  }>>> {
    return request.get(API_ENDPOINTS.PRODUCT_CATEGORIES)
  }

  /**
   * 搜索商品
   * @param keyword 搜索关键词
   * @param page 页码
   * @param size 每页数量
   * @returns 搜索结果
   */
  static async searchProducts(
    keyword: string,
    page: number = 1,
    size: number = 20
  ): Promise<ApiResponse<{
    list: ProductScanVO[]
    total: number
    page: number
    size: number
  }>> {
    return request.get(API_ENDPOINTS.PRODUCT_SEARCH, {
      keyword,
      page,
      size
    })
  }

  /**
   * 获取热门商品
   * @param limit 数量限制
   * @returns 热门商品列表
   */
  static async getHotProducts(limit: number = 10): Promise<ApiResponse<ProductScanVO[]>> {
    return request.get(API_ENDPOINTS.PRODUCT_HOT, { limit })
  }

  /**
   * 获取新品推荐
   * @param limit 数量限制
   * @returns 新品列表
   */
  static async getNewProducts(limit: number = 10): Promise<ApiResponse<ProductScanVO[]>> {
    return request.get(API_ENDPOINTS.PRODUCT_NEW, { limit })
  }

  /**
   * 获取推荐商品
   * @param productId 基于此商品推荐
   * @param limit 数量限制
   * @returns 推荐商品列表
   */
  static async getRecommendations(
    productId?: number,
    limit: number = 10
  ): Promise<ApiResponse<ProductScanVO[]>> {
    const params = { limit, ...(productId && { productId }) }
    return request.get(API_ENDPOINTS.PRODUCT_RECOMMENDATIONS, params)
  }
} 