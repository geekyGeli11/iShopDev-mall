/**
 * 验证工具函数
 */

/**
 * 验证手机号
 * @param phone 手机号
 * @returns 是否有效
 */
export const validatePhone = (phone: string): boolean => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证短信验证码
 * @param code 验证码
 * @returns 是否有效
 */
export const validateSmsCode = (code: string): boolean => {
  const codeRegex = /^\d{6}$/
  return codeRegex.test(code)
}

/**
 * 验证条码
 * @param barcode 条码
 * @returns 是否有效
 */
export const validateBarcode = (barcode: string): boolean => {
  // EAN-13: 13位数字
  const ean13Regex = /^\d{13}$/
  // EAN-8: 8位数字
  const ean8Regex = /^\d{8}$/
  
  return ean13Regex.test(barcode) || ean8Regex.test(barcode)
}

/**
 * 验证付款码
 * @param authCode 付款码
 * @returns 是否有效
 */
export const validatePaymentCode = (authCode: string): boolean => {
  // 微信付款码：10-15位数字，以10-15开头
  const wechatPattern = /^1[0-5]\d{16,17}$/
  // 支付宝付款码：25-30位数字，以25-30开头
  const alipayPattern = /^2[5-9]\d{16,17}$/
  
  return wechatPattern.test(authCode) || alipayPattern.test(authCode)
}

/**
 * 验证金额
 * @param amount 金额
 * @param min 最小值
 * @param max 最大值
 * @returns 是否有效
 */
export const validateAmount = (
  amount: number,
  min = 0.01,
  max = 50000
): boolean => {
  return amount >= min && amount <= max && Number.isFinite(amount)
} 