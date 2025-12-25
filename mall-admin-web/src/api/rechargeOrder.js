import request from '@/utils/request'

// 获取充值订单列表
export function fetchRechargeOrderList(params) {
  return request({
    url: '/rechargeOrder/list',
    method: 'get',
    params: params
  })
}

// 导出充值订单数据
export function exportRechargeOrder(params) {
  return request({
    url: '/rechargeOrder/export',
    method: 'get',
    params: params,
    responseType: 'blob'
  })
}

// 删除充值订单
export function deleteRechargeOrder(ids) {
  return request({
    url: '/rechargeOrder/delete',
    method: 'post',
    params: { ids: ids }
  })
}

// 获取充值订单详情
export function getRechargeOrderDetail(id) {
  return request({
    url: `/rechargeOrder/${id}`,
    method: 'get'
  })
}

// 获取用户充值记录（用于用户详情页面）
export function getMemberRechargeHistory(params) {
  return request({
    url: '/rechargeOrder/member/history',
    method: 'get',
    params: params
  })
}

// 获取用户消费记录（用于用户详情页面）
export function getMemberConsumeHistory(params) {
  return request({
    url: '/balanceHistory/member/consume',
    method: 'get',
    params: params
  })
} 