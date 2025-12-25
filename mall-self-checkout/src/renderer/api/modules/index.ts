/**
 * API模块统一导出
 */

// 导出所有API模块
export * from './payment'
export * from './member'
export * from './product'

// 导出类型定义 - 从shared types导入
export type {
  PaymentQRParam,
  PaymentCodeParam,
  PaymentResultVO,
  MemberLoginParam,
  MemberCodeLoginParam,
  GuestLoginParam,
  MemberLoginResult,
  ProductScanParam,
  ProductScanVO
} from '@shared/types' 