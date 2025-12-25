import request from '@/utils/request'

// ==================== 新版批次管理接口 ====================

/**
 * 创建补货批次
 */
export function createBatch(data) {
  return request({
    url: '/payback/batch/create',
    method: 'post',
    data: data
  })
}

/**
 * 更新补货批次
 */
export function updateBatch(batchId, data) {
  return request({
    url: '/payback/batch/' + batchId,
    method: 'put',
    data: data
  })
}

/**
 * 删除补货批次
 */
export function deleteBatch(batchId) {
  return request({
    url: '/payback/batch/' + batchId,
    method: 'delete'
  })
}

/**
 * 强制启动批次
 */
export function forceStartBatch(batchId) {
  return request({
    url: '/payback/batch/' + batchId + '/forceStart',
    method: 'post'
  })
}

/**
 * 获取批次列表
 */
export function listBatches(params) {
  return request({
    url: '/payback/batch/list',
    method: 'get',
    params: params
  })
}

/**
 * 获取批次详情
 */
export function getBatchDetail(batchId) {
  return request({
    url: '/payback/batch/' + batchId + '/detail',
    method: 'get'
  })
}

/**
 * 刷新批次销售数据
 */
export function refreshBatchSales(batchId) {
  return request({
    url: '/payback/batch/' + batchId + '/refresh',
    method: 'post'
  })
}

/**
 * 批量刷新所有活跃批次
 */
export function refreshAllBatches() {
  return request({
    url: '/payback/batch/refreshAll',
    method: 'post'
  })
}

/**
 * 获取批次统计信息
 */
export function getBatchStatistics() {
  return request({
    url: '/payback/batch/statistics',
    method: 'get'
  })
}

/**
 * 导出批次数据CSV
 */
export function exportBatches(params) {
  return request({
    url: '/payback/batch/export',
    method: 'get',
    params: params,
    responseType: 'blob'
  })
}

/**
 * 导出批次销售明细Excel
 */
export function exportBatchSalesToExcel(batchId) {
  return request({
    url: '/payback/batch/' + batchId + '/exportExcel',
    method: 'get',
    responseType: 'blob'
  })
}

// ==================== 旧版接口（保留兼容） ====================

/**
 * 设置商品回本目标
 */
export function setPaybackTarget(params) {
  return request({
    url: '/payback/setTarget',
    method: 'post',
    params: params
  })
}

/**
 * 获取回本分析数据列表
 */
export function listPaybackAnalysis(params) {
  return request({
    url: '/payback/list',
    method: 'get',
    params: params
  })
}

/**
 * 获取单个商品的回本分析详情
 */
export function getPaybackAnalysis(productId) {
  return request({
    url: '/payback/detail/' + productId,
    method: 'get'
  })
}

/**
 * 手动刷新商品回本分析数据
 */
export function refreshPaybackAnalysis(productId) {
  return request({
    url: '/payback/refresh/' + productId,
    method: 'post'
  })
}

/**
 * 批量刷新回本分析数据
 */
export function refreshAllPaybackAnalysis() {
  return request({
    url: '/payback/refreshAll',
    method: 'post'
  })
}

/**
 * 删除商品回本分析数据
 */
export function deletePaybackAnalysis(productId) {
  return request({
    url: '/payback/delete/' + productId,
    method: 'post'
  })
}

/**
 * 获取回本分析统计信息
 */
export function getPaybackStatistics() {
  return request({
    url: '/payback/statistics',
    method: 'get'
  })
}

/**
 * 订单支付成功时更新回本分析
 */
export function updateOnPaymentSuccess(params) {
  return request({
    url: '/payback/updateOnPayment',
    method: 'post',
    params: params
  })
}

/**
 * 订单退款时更新回本分析
 */
export function updateOnRefund(params) {
  return request({
    url: '/payback/updateOnRefund',
    method: 'post',
    params: params
  })
}

/**
 * 导出回本分析数据CSV
 */
export function exportPaybackAnalysis(params) {
  return request({
    url: '/payback/export',
    method: 'get',
    params: params,
    responseType: 'blob'
  })
} 