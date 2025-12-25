import request from '@/utils/request'

// 邀请配置相关API
export function getInviteConfig() {
  return request({
    url: '/invite/config',
    method: 'get'
  })
}

export function updateInviteConfig(data) {
  return request({
    url: '/invite/config',
    method: 'put',
    data: data
  })
}

export function getConfigHistory(params) {
  return request({
    url: '/invite/config/history',
    method: 'get',
    params
  })
}

// 邀请关系相关API
export function getInviteRelations(params) {
  return request({
    url: '/invite/relations',
    method: 'get',
    params
  })
}

export function getInviteRelationDetail(id) {
  return request({
    url: `/invite/relations/${id}`,
    method: 'get'
  })
}

export function updateInviteRelationStatus(id, data) {
  return request({
    url: `/invite/relations/${id}/status`,
    method: 'put',
    data: data
  })
}

export function batchUpdateInviteStatus(data) {
  return request({
    url: '/invite/relations/batch-update',
    method: 'post',
    data: data
  })
}

export function exportInviteRecords(params) {
  return request({
    url: '/invite/relations/export',
    method: 'get',
    params
  })
}

export function getInviteOverview() {
  return request({
    url: '/invite/relations/overview',
    method: 'get'
  })
}

// 邀请奖励相关API（扩展支持分销佣金）
export function getInviteRewards(params) {
  return request({
    url: '/invite/reward/list',
    method: 'get',
    params
  })
}

export function getInviteRewardDetail(id) {
  return request({
    url: `/invite/reward/${id}`,
    method: 'get'
  })
}

export function updateInviteRewardStatus(id, data) {
  return request({
    url: `/invite/reward/updateStatus/${id}`,
    method: 'post',
    data: data
  })
}

export function retrySendReward(id) {
  return request({
    url: `/invite/reward/retry/${id}`,
    method: 'post'
  })
}

export function batchSendReward(data) {
  return request({
    url: '/invite/reward/batchSend',
    method: 'post',
    data: data
  })
}

export function exportRewardRecords(params) {
  return request({
    url: '/invite/reward/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function getRewardSummary() {
  return request({
    url: '/invite/reward/summary',
    method: 'get'
  })
}

// 新增：佣金类型统计API
export function getCommissionStatistics(params) {
  return request({
    url: '/invite/reward/commission-statistics',
    method: 'get',
    params
  })
}

// 新增：佣金趋势数据API
export function getCommissionTrend(params) {
  return request({
    url: '/invite/reward/commission-trend',
    method: 'get',
    params
  })
}

// 提现申请相关API（扩展支持资金来源）
export function getWithdrawApplies(params) {
  return request({
    url: '/invite/withdraw/list',
    method: 'get',
    params
  })
}

export function getWithdrawApplyDetail(id) {
  return request({
    url: `/invite/withdraw/${id}`,
    method: 'get'
  })
}

// 新增：获取提现申请的资金构成明细
export function getWithdrawComposition(id) {
  return request({
    url: `/invite/withdraw/${id}/composition`,
    method: 'get'
  })
}

export function approveWithdrawApply(id, data) {
  return request({
    url: `/invite/withdraw/approve/${id}`,
    method: 'post',
    params: data
  })
}

export function batchApproveWithdraw(data) {
  return request({
    url: '/invite/withdraw/batchApprove',
    method: 'post',
    data: data
  })
}

export function exportWithdrawRecords(params) {
  return request({
    url: '/invite/withdraw/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function getWithdrawSummary() {
  return request({
    url: '/invite/withdraw/summary',
    method: 'get'
  })
}

// 新增：提现资金来源统计
export function getWithdrawSourceStatistics(params) {
  return request({
    url: '/invite/withdraw/source-statistics',
    method: 'get',
    params
  })
}

// 新增：获取用户可提现金额详情
export function getUserAvailableAmount(userId) {
  return request({
    url: `/invite/withdraw/available-amount/${userId}`,
    method: 'get'
  })
}

// 新增：提现趋势分析
export function getWithdrawTrend(params) {
  return request({
    url: '/invite/withdraw/trend-analysis',
    method: 'get',
    params
  })
}

// 邀请统计相关API

// 统一获取所有统计数据（推荐使用）
export function getAllInviteStatistics(params) {
  return request({
    url: '/invite/statistics/all',
    method: 'get',
    params
  })
}

// 刷新统计数据缓存
export function refreshStatisticsCache() {
  return request({
    url: '/invite/statistics/refresh',
    method: 'post'
  })
}

export function getInviteOverviewStats() {
  return request({
    url: '/invite/statistics/overview',
    method: 'get'
  })
}

export function getInviteTrend(params) {
  return request({
    url: '/invite/statistics/trend',
    method: 'get',
    params
  })
}

export function getUserRanking(params) {
  return request({
    url: '/invite/statistics/ranking',
    method: 'get',
    params
  })
}

export function getRewardStatistics() {
  return request({
    url: '/invite/statistics/reward',
    method: 'get'
  })
}

export function getWithdrawStatistics() {
  return request({
    url: '/invite/statistics/withdraw',
    method: 'get'
  })
}

export function getConversionAnalysis() {
  return request({
    url: '/invite/statistics/conversion',
    method: 'get'
  })
}

export function getRegionDistribution() {
  return request({
    url: '/invite/statistics/region',
    method: 'get'
  })
}

export function getTopInviters(params) {
  return request({
    url: '/invite/stats/top-inviters',
    method: 'get',
    params
  })
}

export function getDailyReport(params) {
  return request({
    url: '/invite/stats/daily-report',
    method: 'get',
    params
  })
}

export function getCustomReport(data) {
  return request({
    url: '/invite/stats/custom-report',
    method: 'post',
    data: data
  })
}

// === 缺失的提现管理API函数 ===

// 获取提现申请列表（兼容函数名）
export function getInviteWithdraws(params) {
  return request({
    url: '/invite/withdraw/list',
    method: 'get',
    params
  })
}

// 获取提现申请详情（兼容函数名）
export function getInviteWithdrawDetail(id) {
  return request({
    url: `/invite/withdraw/detail/${id}`,
    method: 'get'
  })
}

// 获取提现资金来源构成
export function getWithdrawFundsSources(id) {
  return request({
    url: `/invite/withdraw/funds-sources/${id}`,
    method: 'get'
  })
}

// 审核提现申请
export function auditWithdrawApply(id, data) {
  return request({
    url: `/invite/withdraw/audit/${id}`,
    method: 'post',
    data
  })
}

// 处理提现申请
export function processWithdrawApply(id, data) {
  return request({
    url: `/invite/withdraw/process/${id}`,
    method: 'post',
    data
  })
}

// 批量审核提现申请
export function batchAuditWithdraw(ids, data) {
  return request({
    url: '/invite/withdraw/batch/audit',
    method: 'post',
    data: {
      ids,
      ...data
    }
  })
} 

// === 提现配置管理API ===

// 获取提现配置
export function getWithdrawConfig() {
  return request({
    url: '/invite/withdraw/config',
    method: 'get'
  })
}

// 更新提现配置
export function updateWithdrawConfig(data) {
  return request({
    url: '/invite/withdraw/config',
    method: 'put',
    data
  })
} 