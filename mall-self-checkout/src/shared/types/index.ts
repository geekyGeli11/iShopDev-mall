// 导出所有类型定义
export * from './api'
export * from './payment'
export * from './member'
export * from './product'
export * from './order'
export * from './scanner'
export * from './app'

// 登录相关类型
export type LoginMode = 'member' | 'guest'

export interface MemberLoginForm {
  phone: string
  code: string
}

export interface GuestLoginForm {
  deviceId?: string
  deviceType?: string
}

// 通用API响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应类型
export interface CommonPage<T> {
  pageNum: number
  pageSize: number
  totalPage: number
  total: number
  list: T[]
}

// 会员相关类型
export interface MemberLoginParam {
  telephone: string
  verifyCode: string
}

export interface MemberCodeLoginParam {
  memberCode: string
  loginType: string
}

export interface GuestLoginParam {
  deviceId?: string
  deviceType?: string
  deviceInfo?: string
  terminalCode?: string
}

export interface MemberLoginResult {
  memberId: number
  telephone: string
  username: string
  nickname: string
  icon: string
  memberLevel: number
  integration: number
  balance: number
  token: string
  tokenType: string
  expiresIn: number
  loginTime: number
}

// 商品相关类型
export interface ProductScanParam {
  barcode: string
  scanType?: string
  needStockCheck?: boolean
  needPromotionInfo?: boolean
}

export interface ProductScanVO {
  productId: number
  skuId?: number
  productName: string
  productPic: string
  brandName: string
  categoryName: string
  productSn: string
  skuCode?: string
  skuSpecInfo?: string
  unit: string
  weight?: number
  originalPrice: number
  currentPrice: number
  promotionPrice?: number
  promotionType?: number
  promotionStartTime?: Date
  promotionEndTime?: Date
  stock: number
  stockStatus: string
  lowStock?: number
  sale?: number
  productStatus: string
  publishStatus: number
  saleable: boolean
  giftPoint?: number
  giftGrowth?: number
  subTitle?: string
  scanType?: string
  scanTime?: number
}

// 购物车相关类型
export interface CartItemParam {
  productId: number
  skuId?: number
  quantity: number
  operation?: string
  guestId?: string
  forceUpdate?: boolean
  remark?: string
}

export interface CartAddParam {
  productId: number
  skuId?: number
  quantity: number
  price?: number
  productSn?: string
  createTime?: number
  guestId?: string
  remark?: string
}

export interface CartUpdateParam {
  cartItemId?: number
  productId: number
  skuId?: number
  quantity: number
  operation?: string
  guestId?: string
  remark?: string
}

export interface CartItemVO {
  itemId?: number
  productId: number
  skuId?: number
  productName: string
  productPic: string
  brandName: string
  categoryName: string
  productSn: string
  skuCode?: string
  skuSpec?: string
  unit: string
  originalPrice: number
  currentPrice: number
  promotionPrice?: number
  memberPrice?: number
  actualPrice: number
  quantity: number
  subtotal: number
  stock: number
  stockStatus: string
  available: boolean
  saleable: boolean
  productStatus: string
  promotionType?: number
  limitQuantity?: number
  createTime?: Date
  updateTime?: Date
  remark?: string
  errorMessage?: string
  checked?: boolean
}

export interface CartVO {
  userType: string
  userId: string
  items: CartItemVO[]
  totalQuantity: number
  availableQuantity: number
  unavailableQuantity: number
  totalAmount: number
  availableAmount: number
  discountAmount: number
  shippingFee: number
  finalAmount: number
  availablePoints?: number
  usedPoints?: number
  pointsAmount?: number
  appliedCoupons?: MemberCouponVO[]
  availableCouponCount?: number
  needShipping: boolean
  estimatedShippingFee?: number
  cartStatus: string
  createTime?: Date
  updateTime?: Date
  expireTime?: Date
  memberLevel?: number
  memberDiscount?: number
  messages?: string[]
  errors?: string[]
}

// 订单相关类型
export interface OrderItemParam {
  productId: number
  skuId?: number
  quantity: number
  unitPrice?: number
  totalPrice?: number
  promotionType?: number
  promotionAmount?: number
  remark?: string
}

export interface OrderCreateParam {
  orderType: string
  guestId?: string
  orderItems: OrderItemParam[]
  addressId?: number
  couponHistoryIds?: number[]
  usePoints?: number
  note?: string
  payType: string
  deliveryType?: number
  expectedAmount?: number
  deviceInfo?: string
  terminalCode?: string
  forceOrder?: boolean
  storeId?: number
  schoolId?: number
}

export interface OrderConfirmParam {
  orderId: number
  confirmType?: string
  note?: string
  operatorId?: string
}

export interface OrderItemVO {
  id?: number
  orderId: number
  orderSn: string
  productId: number
  productPic: string
  productName: string
  productBrand: string
  productCategoryName: string
  productSn: string
  productPrice: number
  productQuantity: number
  productSkuId?: number
  productSkuCode?: string
  productAttr?: string
  promotionName?: string
  promotionAmount?: number
  couponAmount?: number
  integrationAmount?: number
  realAmount: number
  giftIntegration?: number
  giftGrowth?: number
  productAttrJson?: string
}

export interface OrderOperateHistoryVO {
  id: number
  orderId: number
  operateMan: string
  createTime: Date
  orderStatus: number
  note: string
  operateType: string
  description: string
}

export interface OrderVO {
  orderId: number
  orderSn: string
  userType: string
  userId: string
  memberId?: number
  memberUsername?: string
  status: number
  statusName: string
  orderType: string
  sourceType: number
  totalAmount: number
  freightAmount: number
  promotionAmount: number
  integrationAmount: number
  couponAmount: number
  discountAmount: number
  payAmount: number
  payType: string
  payStatus: number
  payTime?: Date
  deliveryType: number
  receiverName?: string
  receiverPhone?: string
  receiverPostCode?: string
  receiverProvince?: string
  receiverCity?: string
  receiverRegion?: string
  receiverDetailAddress?: string
  note?: string
  confirmStatus: number
  deleteStatus: number
  useIntegration?: number
  createTime: Date
  modifyTime?: Date
  autoConfirmDay?: number
  integration?: number
  growth?: number
  terminalCode?: string
  deviceInfo?: string
  orderItems: OrderItemVO[]
  usedCoupons?: MemberCouponVO[]
  operateHistory?: OrderOperateHistoryVO[]
  deliveryTime?: Date
  receiveTime?: Date
  commentTime?: Date
}

// 支付相关类型
export interface PaymentQRParam {
  orderId: number
  amount: number
  payType: string
  title: string
  description?: string
  terminalCode?: string
  deviceInfo?: string
  guestId?: string
  expireMinutes?: number
}

export interface PaymentCodeParam {
  orderId: number
  paymentCode: string
  amount: number
  payType: string
  terminalCode?: string
  deviceInfo?: string
  guestId?: string
  remark?: string
}

export interface PaymentResultVO {
  paymentId: string
  orderId: number
  orderSn: string
  payType: string
  payStatus: string
  amount: number
  paidAmount?: number
  transactionId?: string
  payTime?: Date
  createTime: Date
  qrCodeUrl?: string
  qrCodeText?: string
  failureReason?: string
  terminalCode?: string
  remark?: string
}

// 优惠券相关类型
export interface MemberCouponListParam {
  useStatus?: number
  pageNum: number
  pageSize: number
  orderBy?: string
  orderDirection?: string
}

export interface MemberCouponVO {
  historyId: number
  couponId: number
  name: string
  type: number
  couponCode: string
  amount: number
  couponType: number
  discountRate?: number
  minPoint: number
  useType: number
  startTime: Date
  endTime: Date
  useStatus: number
  createTime: Date
  useTime?: Date
  note?: string
  remainingDays?: number
  nearExpiry?: boolean
  available?: boolean
}

// 优惠券基础信息类型
export interface CouponVO {
  id: number
  name: string
  type: number
  amount: number
  minPoint: number
  useType: number
  startTime: Date
  endTime: Date
  status: number
  description?: string
  discount?: number
  productIds?: number[]
  categoryIds?: number[]
}

// 用户相关类型
export interface UmsMember {
  id: number
  memberLevelId?: number
  username?: string
  password?: string
  nickname: string
  phone: string
  memberCode: string
  status: number
  createTime: string
  icon?: string
  gender?: number
  birthday?: string
  city?: string
  job?: string
  personalizedSignature?: string
  sourceType?: number
  integration: number
  growth?: number
  luckeyCount?: number
  historyIntegration?: number
  openid?: string
}

export interface UmsGuest {
  id: number
  guestId: string
  deviceId?: string
  deviceType?: string
  deviceInfo?: string
  loginIp: string
  sessionTimeout: number
  createTime: Date
  updateTime: Date
  lastActiveTime: Date
  hasActiveOrder: boolean
}

// 错误类型
export class ApiError extends Error {
  code: number
  data?: any
  details?: any

  constructor(message: string, code: number, data?: any) {
    super(message)
    this.code = code
    this.data = data
    this.name = 'ApiError'
  }
}

// 扫码相关类型
export interface ScannerConfig {
  width: number
  height: number
  facingMode: string
  enableSound: boolean
  enableVibration: boolean
}

export interface ScannerResult {
  text: string
  format: BarcodeFormat
  type: ScanType
  timestamp: number
}

export enum BarcodeFormat {
  EAN_13 = 'EAN_13',
  EAN_8 = 'EAN_8',
  CODE_128 = 'CODE_128',
  QR_CODE = 'QR_CODE'
}

export enum ScanType {
  BARCODE = 'BARCODE',
  QR_CODE = 'QR_CODE', 
  PAYMENT_CODE = 'PAYMENT_CODE'
}

// 支付相关枚举
export enum PaymentMethod {
  WECHAT = 'WECHAT',
  ALIPAY = 'ALIPAY'
}

export enum PaymentMode {
  QR_CODE = 'QR_CODE',
  SCAN_CODE = 'SCAN_CODE'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  TIMEOUT = 'TIMEOUT'
}

// 会员相关枚举
export enum UserType {
  MEMBER = 'MEMBER',
  GUEST = 'GUEST'
}

export enum MemberLevel {
  BRONZE = 1,
  SILVER = 2,
  GOLD = 3,
  PLATINUM = 4,
  DIAMOND = 5
}

// 订单相关枚举
export enum OrderStatus {
  PENDING_PAYMENT = 0,
  PENDING_DELIVERY = 1,
  DELIVERED = 2,
  COMPLETED = 3,
  CLOSED = 4,
  INVALID = 5
}

export enum OrderType {
  QUICK = 'QUICK',
  CART = 'CART'
}

// 商品相关枚举
export enum ProductStatus {
  NORMAL = 'NORMAL',
  OFF_SHELF = 'OFF_SHELF',
  DELETED = 'DELETED'
}

export enum StockStatus {
  SUFFICIENT = 'SUFFICIENT',
  LOW = 'LOW',
  OUT_OF_STOCK = 'OUT_OF_STOCK'
}

// 购物车相关枚举
export enum CartOperation {
  ADD = 'ADD',
  UPDATE = 'UPDATE',
  REMOVE = 'REMOVE'
}

export enum CartStatus {
  NORMAL = 'NORMAL',
  EXPIRED = 'EXPIRED',
  LOCKED = 'LOCKED'
}

// 常量定义
export const CONSTANTS = {
  // 会员session配置
  MEMBER_SESSION_CONFIG: {
    TIMEOUT_WARNING: 30000, // 30秒警告
    TIMEOUT_LOGOUT: 60000,  // 60秒强制退出
    POST_PAYMENT_DELAY: 3000 // 支付完成后3秒自动退出
  },
  
  // 扫码器配置
  SCANNER_CONFIG: {
    width: 640,
    height: 480,
    facingMode: 'environment',
    enableSound: true,
    enableVibration: true
  } as ScannerConfig,
  
  // 支付配置
  PAYMENT_CONFIG: {
    QR_CODE_EXPIRE_MINUTES: 5,
    POLLING_INTERVAL: 1000,
    MAX_POLLING_COUNT: 300
  }
} 