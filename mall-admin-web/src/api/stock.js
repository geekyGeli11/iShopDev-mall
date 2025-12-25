import request from '@/utils/request'

// 执行库存操作
export function executeStockOperation(data) {
  return request({
    url: '/stock/operation',
    method: 'post',
    data
  })
}

// 获取库存操作记录
export function fetchStockOperationLogs(params) {
  return request({
    url: '/stock/operation/logs',
    method: 'get',
    params
  })
}

// 获取库存统计
export function fetchStockStats() {
  return request({
    url: '/stock/stats',
    method: 'get'
  })
}

// 检查库存是否充足
export function checkStockAvailable(productId, skuId, quantity) {
  return request({
    url: '/stock/check',
    method: 'get',
    params: {
      productId,
      skuId,
      quantity
    }
  })
}

// ==================== 地库相关 API ====================

// 验证门店（包括地库）库存充足性
export function validateStockAvailability(storeId, skuId, quantity) {
  return request({
    url: '/stock/validateAvailability',
    method: 'get',
    params: {
      storeId,
      skuId,
      quantity
    }
  })
}

// ==================== 调货审核相关 API ====================

// 提交调货申请
export function submitTransferApproval(data) {
  return request({
    url: '/stock/transfer/apply',
    method: 'post',
    data
  })
}

// 确认发货
export function confirmShipment(id) {
  return request({
    url: `/stock/transfer/ship/${id}`,
    method: 'put'
  })
}

// 确认收货
export function confirmReceipt(data) {
  return request({
    url: '/stock/transfer/confirm',
    method: 'put',
    data
  })
}

// 驳回调货申请
export function rejectTransfer(id, rejectReason) {
  return request({
    url: `/stock/transfer/reject/${id}`,
    method: 'put',
    params: { rejectReason }
  })
}

// 获取调货申请列表
export function fetchTransferApprovalList(params) {
  return request({
    url: '/stock/transfer/list',
    method: 'get',
    params
  })
}

// 获取调货申请详情
export function fetchTransferApprovalDetail(id) {
  return request({
    url: `/stock/transfer/detail/${id}`,
    method: 'get'
  })
} 