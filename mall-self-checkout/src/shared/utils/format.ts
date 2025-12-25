/**
 * 格式化工具函数
 */

/**
 * 格式化金额
 * @param amount 金额
 * @param currency 货币符号
 * @returns 格式化后的金额字符串
 */
export const formatAmount = (amount: number, currency = '¥'): string => {
  return `${currency}${amount.toFixed(2)}`
}

/**
 * 格式化手机号
 * @param phone 手机号
 * @returns 脱敏手机号
 */
export const formatPhone = (phone: string): string => {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 格式化时间
 * @param timestamp 时间戳或时间字符串
 * @param format 格式
 * @returns 格式化后的时间字符串
 */
export const formatTime = (
  timestamp: string | number,
  format = 'YYYY-MM-DD HH:mm:ss'
): string => {
  // 这里可以使用 dayjs 库
  const date = new Date(timestamp)
  return date.toLocaleString()
}

/**
 * 格式化商品数量
 * @param quantity 数量
 * @param unit 单位
 * @returns 格式化后的数量字符串
 */
export const formatQuantity = (quantity: number, unit = '件'): string => {
  return `${quantity}${unit}`
}

/**
 * 格式化积分
 * @param points 积分
 * @returns 格式化后的积分字符串
 */
export const formatPoints = (points: number): string => {
  return `${points}积分`
} 