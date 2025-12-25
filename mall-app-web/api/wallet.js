import request from '@/utils/requestUtil'

/**
 * 获取用户余额信息
 */
export function getWalletInfo() {
  return request({
    method: 'GET',
    url: '/member/balance/info'
  })
}

/**
 * 创建充值订单
 * @param {Object} data 充值参数
 * @param {number} data.amount 充值金额
 * @param {number} data.payType 支付方式：1-支付宝，2-微信
 * @param {string} data.note 订单备注
 */
export function createRechargeOrder(data) {
  return request({
    method: 'POST',
    url: '/member/balance/recharge/create',
    data: data
  })
}

/**
 * 充值钱包（兼容旧接口名称）
 * @param {Object} data 充值参数
 */
export function rechargeWallet(data) {
  return createRechargeOrder(data)
}

/**
 * 充值支付成功回调
 * @param {Object} data 回调参数
 * @param {string} data.orderSn 订单号
 * @param {number} data.payType 支付方式
 * @param {string} data.paySn 第三方支付流水号
 */
export function rechargePaySuccess(data) {
  return request({
    method: 'POST',
    url: '/member/balance/recharge/pay-success',
    data: data,
    header: {
      'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
    }
  })
}

/**
 * 获取充值配置
 */
export function getRechargeConfig() {
  return request({
    method: 'GET',
    url: '/member/balance/recharge/config'
  })
}

/**
 * 余额消费（用于订单支付等）
 * @param {Object} data 消费参数
 * @param {number} data.amount 消费金额
 * @param {string} data.businessType 业务类型
 * @param {string} data.businessId 业务ID
 * @param {string} data.remark 备注
 */
export function consumeBalance(data) {
  return request({
    method: 'POST',
    url: '/member/balance/consume',
    data: data,
    header: {
      'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
    }
  })
}

/**
 * 余额退款（用于订单退款等）
 * @param {Object} data 退款参数
 * @param {number} data.amount 退款金额
 * @param {string} data.businessType 业务类型
 * @param {string} data.businessId 业务ID
 * @param {string} data.remark 备注
 */
export function refundBalance(data) {
  return request({
    method: 'POST',
    url: '/member/balance/refund',
    data: data,
    header: {
      'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
    }
  })
}

/**
 * 检查余额是否足够（本地检查，需要先获取余额信息）
 * @param {number} currentBalance 当前余额
 * @param {number} requiredAmount 需要的金额
 * @returns {boolean} 是否足够
 */
export function checkBalanceEnough(currentBalance, requiredAmount) {
  return parseFloat(currentBalance) >= parseFloat(requiredAmount)
}

/**
 * 格式化金额显示
 * @param {number|string} amount 金额
 * @returns {string} 格式化后的金额
 */
export function formatAmount(amount) {
  if (!amount && amount !== 0) return '0.00'
  const num = parseFloat(amount)
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

/**
 * 验证充值金额
 * @param {number|string} amount 充值金额
 * @param {Object} config 充值配置
 * @returns {Object} 验证结果
 */
export function validateRechargeAmount(amount, config = {}) {
  const num = parseFloat(amount)
  const minAmount = parseFloat(config.minAmount || 1)
  const maxAmount = parseFloat(config.maxAmount || 50000)
  
  if (isNaN(num) || num <= 0) {
    return {
      valid: false,
      message: '请输入有效的充值金额'
    }
  }
  
  if (num < minAmount) {
    return {
      valid: false,
      message: `充值金额不能小于${minAmount}元`
    }
  }
  
  if (num > maxAmount) {
    return {
      valid: false,
      message: `充值金额不能大于${maxAmount}元`
    }
  }
  
  return {
    valid: true,
    message: '验证通过'
  }
}

/**
 * 获取余额变动历史记录
 * @param {Object} params 查询参数
 * @param {number} params.changeType 变动类型（可选）：1-充值，2-消费，3-退款，4-转入，5-转出
 * @param {number} params.pageNum 页码，默认1
 * @param {number} params.pageSize 页大小，默认20
 */
export function getBalanceHistory(params = {}) {
  return request({
    method: 'GET',
    url: '/member/balance/history',
    params: params
  })
}

/**
 * 获取充值历史
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码，默认1
 * @param {number} params.pageSize 页大小，默认20
 */
export function getRechargeHistory(params = {}) {
  return request({
    method: 'GET',
    url: '/member/balance/recharge/history',
    params: params
  })
}

/**
 * 获取消费记录
 * @param {Object} params 查询参数
 * @param {number} params.pageNum 页码，默认1
 * @param {number} params.pageSize 页大小，默认20
 */
export function getConsumeHistory(params = {}) {
  return request({
    method: 'GET',
    url: '/member/balance/consume/history',
    params: params
  })
} 