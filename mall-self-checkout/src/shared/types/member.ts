/**
 * 会员相关类型定义
 */

// 会员信息
export interface MemberInfo {
  id: string
  phone: string
  name: string
  avatar?: string
  level: MemberLevel
  balance: number
  points: number
  joinDate: string
  status: MemberStatus
}

// 会员等级
export interface MemberLevel {
  id: string
  name: string
  icon: string
  color: string
  privileges: string[]
  discountRate: number
}

// 会员状态
export enum MemberStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

// 会员登录请求
export interface MemberLoginRequest {
  phone: string
  smsCode: string
}

// 会员登录响应
export interface MemberLoginResponse {
  member: MemberInfo
  token: string
  expiresIn: number
}

// 会员会话信息
export interface MemberSession {
  memberId: string
  memberInfo: MemberInfo
  loginTime: number
  lastActiveTime: number
  autoLogoutTimer?: NodeJS.Timeout
  orderInProgress: boolean
}

// 优惠券信息
export interface CouponInfo {
  id: string
  name: string
  type: CouponType
  value: number
  minAmount: number
  maxDiscount?: number
  validFrom: string
  validTo: string
  status: CouponStatus
  description: string
  applicableProducts?: string[]
}

// 优惠券类型
export enum CouponType {
  FIXED = 'FIXED',         // 固定金额
  PERCENTAGE = 'PERCENTAGE' // 百分比折扣
}

// 优惠券状态
export enum CouponStatus {
  AVAILABLE = 'AVAILABLE',
  USED = 'USED',
  EXPIRED = 'EXPIRED'
} 