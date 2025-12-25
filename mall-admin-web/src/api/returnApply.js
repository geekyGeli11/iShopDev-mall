import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/returnApply/list',
    method:'get',
    params:params
  })
}

export function deleteApply(params) {
  return request({
    url:'/returnApply/delete',
    method:'post',
    params:params
  })
}
export function updateApplyStatus(id,data) {
  return request({
    url:'/returnApply/update/status/'+id,
    method:'post',
    data:data
  })
}

export function getApplyDetail(id) {
  return request({
    url:'/returnApply/'+id,
    method:'get'
  })
}

// 处理退款
export function processRefund(id, refundParam) {
  return request({
    url: `/returnApply/refund/${id}`,
    method: 'post',
    data: refundParam
  })
}

// 查询退款记录
export function getRefundRecords(returnApplyId) {
  return request({
    url: `/returnApply/refund/records/${returnApplyId}`,
    method: 'get'
  })
}

// 获取未处理售后申请数量
export function getPendingReturnApplyCount() {
  return request({
    url: '/returnApply/pending/count',
    method: 'get'
  })
}

// 获取指定订单的售后申请列表
export function getReturnApplyByOrderId(orderId) {
  return request({
    url: `/returnApply/order/${orderId}`,
    method: 'get'
  })
}
