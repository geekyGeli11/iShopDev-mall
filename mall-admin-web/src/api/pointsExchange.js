import request from '@/utils/request'

// 积分换购商品配置管理
export function createPointsProductConfig(data) {
  return request({
    url: '/points/product/create',
    method: 'post',
    data: data
  })
}

export function updatePointsProductConfig(id, data) {
  return request({
    url: `/points/product/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deletePointsProductConfig(id) {
  return request({
    url: `/points/product/delete/${id}`,
    method: 'post'
  })
}

export function batchDeletePointsProductConfig(ids) {
  return request({
    url: '/points/product/delete/batch',
    method: 'post',
    params: { ids: ids.join(',') }
  })
}

export function fetchPointsProductConfigList(params) {
  return request({
    url: '/points/product/list',
    method: 'get',
    params: params
  })
}

export function fetchPointsProductConfig(id) {
  return request({
    url: `/points/product/${id}`,
    method: 'get'
  })
}

export function updatePointsProductStatus(ids, status) {
  return request({
    url: '/points/product/updateStatus',
    method: 'post',
    params: { ids: ids.join(','), status: status }
  })
}

export function updatePointsProductStock(id, totalStock) {
  return request({
    url: `/points/product/updateStock/${id}`,
    method: 'post',
    params: { totalStock: totalStock }
  })
}

export function fetchAvailablePointsProducts() {
  return request({
    url: '/points/product/listAvailable',
    method: 'get'
  })
}

// 积分换购优惠券配置管理
export function createPointsCouponConfig(data) {
  return request({
    url: '/points/coupon/create',
    method: 'post',
    data: data
  })
}

export function updatePointsCouponConfig(id, data) {
  return request({
    url: `/points/coupon/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deletePointsCouponConfig(id) {
  return request({
    url: `/points/coupon/delete/${id}`,
    method: 'post'
  })
}

export function batchDeletePointsCouponConfig(ids) {
  return request({
    url: '/points/coupon/delete/batch',
    method: 'post',
    params: { ids: ids.join(',') }
  })
}

export function fetchPointsCouponConfigList(params) {
  return request({
    url: '/points/coupon/list',
    method: 'get',
    params: params
  })
}

export function fetchPointsCouponConfig(id) {
  return request({
    url: `/points/coupon/${id}`,
    method: 'get'
  })
}

export function updatePointsCouponStatus(ids, status) {
  return request({
    url: '/points/coupon/updateStatus',
    method: 'post',
    params: { ids: ids.join(','), status: status }
  })
}

export function updatePointsCouponCount(id, totalCount) {
  return request({
    url: `/points/coupon/updateCount/${id}`,
    method: 'post',
    params: { totalCount: totalCount }
  })
}

export function fetchAvailablePointsCoupons() {
  return request({
    url: '/points/coupon/listAvailable',
    method: 'get'
  })
}

// 积分兑换记录管理
export function fetchPointsExchangeLogList(params) {
  return request({
    url: '/points/exchange/list',
    method: 'get',
    params: params
  })
}

export function fetchPointsExchangeLog(id) {
  return request({
    url: `/points/exchange/${id}`,
    method: 'get'
  })
}

export function fetchPointsExchangeStatistics(params) {
  return request({
    url: '/points/exchange/statistics',
    method: 'get',
    params: params
  })
}

export function fetchUserPointsExchangeList(memberId, params) {
  return request({
    url: `/points/exchange/user/${memberId}`,
    method: 'get',
    params: params
  })
}

export function exportPointsExchangeList(params) {
  return request({
    url: '/points/exchange/export',
    method: 'get',
    params: params
  })
} 