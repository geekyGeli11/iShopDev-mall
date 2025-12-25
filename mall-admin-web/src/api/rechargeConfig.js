import request from '@/utils/request'

/**
 * 获取充值配置
 */
export function getRechargeConfig() {
  return request({
    url: '/member/balance/recharge/config',
    method: 'get'
  })
}

/**
 * 更新充值配置
 */
export function updateRechargeConfig(data) {
  return request({
    url: '/member/balance/recharge/config',
    method: 'post',
    data: data
  })
}

/**
 * 获取充值配置历史
 */
export function getRechargeConfigHistory(params) {
  return request({
    url: '/member/balance/recharge/config/history',
    method: 'get',
    params: params
  })
}

/**
 * 验证快速充值选项配置
 */
export function validateQuickAmounts(quickAmounts) {
  return request({
    url: '/member/balance/recharge/config/validate',
    method: 'post',
    data: { quickAmounts }
  })
}

/**
 * 获取充值统计数据
 */
export function getRechargeStatistics(params) {
  return request({
    url: '/member/balance/recharge/statistics',
    method: 'get',
    params: params
  })
}

/**
 * 重置充值配置为默认值
 */
export function resetRechargeConfig() {
  return request({
    url: '/member/balance/recharge/config/reset',
    method: 'post'
  })
}

/**
 * 预览充值配置效果
 */
export function previewRechargeConfig(data) {
  return request({
    url: '/member/balance/recharge/config/preview',
    method: 'post',
    data: data
  })
}
