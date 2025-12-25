/**
 * 订单相关类型定义
 */
import type { PaymentMethod, PaymentStatus } from './payment'
import type { MemberInfo, CouponInfo } from './member'
import type { CartItem } from './product'

// 订单信息
export interface OrderInfo {
  orderId: string
  orderType: OrderType
  items: CartItem[]
  subtotal: number
  discountAmount: number
  totalAmount: number
  paidAmount: number
  memberId?: string
  memberInfo?: MemberInfo
  appliedCoupons: CouponInfo[]
  pointsUsed: number
  pointsEarned: number
  paymentMethod?: PaymentMethod
  paymentStatus: PaymentStatus
  status: OrderStatus
  createdAt: string
  paidAt?: string
  guestId?: string
  deviceId: string
}

// 订单类型
export enum OrderType {
  MEMBER = 'MEMBER',
  GUEST = 'GUEST'
}

// 订单状态
export enum OrderStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED'
}

// 创建订单请求
export interface CreateOrderRequest {
  items: CartItem[]
  memberId?: string
  appliedCoupons?: string[]
  pointsUsed?: number
  deviceId: string
} 