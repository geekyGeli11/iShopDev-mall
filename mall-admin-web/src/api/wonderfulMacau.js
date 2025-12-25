import request from '@/utils/request'

// 获取精彩濠江列表
export function fetchList(params) {
  return request({
    url: '/wonderful-macau/list',
    method: 'get',
    params: params
  })
}

// 获取精彩濠江详情
export function getWonderfulMacau(id) {
  return request({
    url: '/wonderful-macau/' + id,
    method: 'get'
  })
}

// 添加精彩濠江
export function createWonderfulMacau(data) {
  return request({
    url: '/wonderful-macau/create',
    method: 'post',
    data: data
  })
}

// 更新精彩濠江
export function updateWonderfulMacau(id, data) {
  return request({
    url: '/wonderful-macau/update/' + id,
    method: 'post',
    data: data
  })
}

// 删除精彩濠江
export function deleteWonderfulMacau(id) {
  return request({
    url: '/wonderful-macau/delete/' + id,
    method: 'post'
  })
}

// 更新状态（包括状态和置顶状态）
export function updateStatus(id, data) {
  return request({
    url: '/wonderful-macau/update/' + id,
    method: 'post',
    data: data
  })
} 