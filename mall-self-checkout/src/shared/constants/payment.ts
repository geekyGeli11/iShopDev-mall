/**
 * 支付相关常量
 */

// 支付超时时间（毫秒）
export const PAYMENT_TIMEOUT = {
  QR_CODE_EXPIRE: 300000,    // 收款码5分钟过期
  PAYMENT_POLLING: 1000,     // 支付状态轮询间隔1秒
  SCAN_TIMEOUT: 30000,       // 扫码超时30秒
  PAYMENT_PROCESS: 60000,    // 支付处理超时1分钟
} as const

// 支付金额限制
export const PAYMENT_LIMITS = {
  MIN_AMOUNT: 0.01,          // 最小支付金额
  MAX_AMOUNT: 50000,         // 最大支付金额
  MAX_POINTS_RATIO: 0.5,     // 积分最大抵扣比例
} as const

// 付款码格式验证
export const PAYMENT_CODE_PATTERNS = {
  WECHAT: /^1[0-5]\d{16,17}$/,
  ALIPAY: /^2[5-9]\d{16,17}$/,
} as const

// 支付方式配置
export const PAYMENT_METHODS_CONFIG = {
  WECHAT: {
    name: '微信支付',
    icon: '/images/payment/wechat.png',
    color: '#07C160',
    enabled: true,
  },
  ALIPAY: {
    name: '支付宝',
    icon: '/images/payment/alipay.png',
    color: '#1677FF',
    enabled: true,
  },
  MEMBER_BALANCE: {
    name: '会员余额',
    icon: '/images/payment/balance.png',
    color: '#FF6B35',
    enabled: true,
  },
} as const 