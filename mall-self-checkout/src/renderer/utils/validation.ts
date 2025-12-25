/**
 * 表单验证工具函数
 */

/**
 * 验证手机号格式
 * @param phone 手机号
 * @returns 是否有效
 */
export const validatePhone = (phone: string): boolean => {
  if (!phone) return false
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证验证码格式
 * @param code 验证码
 * @returns 是否有效
 */
export const validateVerifyCode = (code: string): boolean => {
  if (!code) return false
  return /^\d{6}$/.test(code)
}

/**
 * 验证邮箱格式
 * @param email 邮箱
 * @returns 是否有效
 */
export const validateEmail = (email: string): boolean => {
  if (!email) return false
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

/**
 * 验证密码强度
 * @param password 密码
 * @returns 是否有效
 */
export const validatePassword = (password: string): boolean => {
  if (!password) return false
  // 至少8位，包含字母和数字
  return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/.test(password)
}

/**
 * 验证身份证号
 * @param idCard 身份证号
 * @returns 是否有效
 */
export const validateIdCard = (idCard: string): boolean => {
  if (!idCard) return false
  return /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/.test(idCard)
}

/**
 * 验证银行卡号
 * @param cardNumber 银行卡号
 * @returns 是否有效
 */
export const validateBankCard = (cardNumber: string): boolean => {
  if (!cardNumber) return false
  return /^[1-9]\d{12,19}$/.test(cardNumber)
}

/**
 * 验证金额格式
 * @param amount 金额
 * @returns 是否有效
 */
export const validateAmount = (amount: string | number): boolean => {
  if (amount === '' || amount === null || amount === undefined) return false
  const numAmount = typeof amount === 'string' ? parseFloat(amount) : amount
  return !isNaN(numAmount) && numAmount > 0 && numAmount <= 999999.99
} 