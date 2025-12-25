/**
 * 商品相关类型定义
 */

// 商品信息
export interface ProductInfo {
  id: string
  barcode: string
  name: string
  brand: string
  category: string
  description?: string
  images: string[]
  price: number
  memberPrice?: number
  stock: number
  unit: string
  weight?: number
  status: ProductStatus
  tags?: string[]
}

// 商品状态
export enum ProductStatus {
  AVAILABLE = 'AVAILABLE',
  OUT_OF_STOCK = 'OUT_OF_STOCK',
  DISCONTINUED = 'DISCONTINUED'
}

// 购物车商品项
export interface CartItem {
  product: ProductInfo
  quantity: number
  unitPrice: number
  totalPrice: number
  memberDiscount?: number
  appliedCoupons?: string[]
}

// 商品扫码结果
export interface ScanResult {
  barcode: string
  product?: ProductInfo
  isValid: boolean
  error?: string
} 