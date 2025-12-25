import request from '@/utils/request'

// 订单DIY信息管理
export function fetchOrderDiyInfoList(params) {
  return request({
    url: '/orderDiyInfo/list',
    method: 'get',
    params: params
  })
}

export function createOrderDiyInfo(data) {
  return request({
    url: '/orderDiyInfo/create',
    method: 'post',
    data: data
  })
}

export function updateOrderDiyInfo(id, data) {
  return request({
    url: `/orderDiyInfo/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteOrderDiyInfo(id) {
  return request({
    url: `/orderDiyInfo/delete/${id}`,
    method: 'post'
  })
}

export function getOrderDiyInfo(id) {
  return request({
    url: `/orderDiyInfo/${id}`,
    method: 'get'
  })
}

export function fetchOrderDiyInfoByOrder(orderId) {
  return request({
    url: `/orderDiyInfo/listByOrder/${orderId}`,
    method: 'get'
  })
}

export function getOrderDiyInfoByOrderItem(orderItemId) {
  return request({
    url: `/orderDiyInfo/getByOrderItem/${orderItemId}`,
    method: 'get'
  })
}

export function updateProductionStatus(id, params) {
  return request({
    url: `/orderDiyInfo/updateProductionStatus/${id}`,
    method: 'post',
    params: params
  })
}

export function updateProductionStatusBatch(params) {
  return request({
    url: '/orderDiyInfo/updateProductionStatus/batch',
    method: 'post',
    params: params
  })
}

export function downloadDesignFile(id) {
  return request({
    url: `/orderDiyInfo/downloadDesignFile/${id}`,
    method: 'get'
  })
}

export function generateProductionFile(id) {
  return request({
    url: `/orderDiyInfo/generateProductionFile/${id}`,
    method: 'post'
  })
}

export function countByProductionStatus(params) {
  return request({
    url: '/orderDiyInfo/countByProductionStatus',
    method: 'get',
    params: params
  })
}

// 商品DIY配置管理
export function getProductDiyConfig(id) {
  return request({
    url: `/product/diyConfig/${id}`,
    method: 'get'
  })
}

export function updateProductDiyConfig(id, data) {
  return request({
    url: `/product/diyConfig/update/${id}`,
    method: 'post',
    data: data
  })
}

export function updateProductDiyStatus(params) {
  return request({
    url: '/product/diyConfig/updateStatus',
    method: 'post',
    params: params
  })
}
