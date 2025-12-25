import request from '@/utils/request'

// 获取本地好物列表
export function fetchList(params) {
  return request({
    url: '/localGoods/list/detail',
    method: 'get',
    params: params
  })
}

// 获取单个本地好物
export function getLocalGoods(id) {
  return request({
    url: '/localGoods/detail/' + id,
    method: 'get'
  })
}

// 添加本地好物
export function createLocalGoods(data) {
  return request({
    url: '/localGoods/createWithRelation',
    method: 'post',
    data: data
  })
}

// 更新本地好物
export function updateLocalGoods(id, data) {
  return request({
    url: '/localGoods/updateWithRelation/' + id,
    method: 'post',
    data: data
  })
}

// 删除本地好物
export function deleteLocalGoods(id) {
  return request({
    url: '/localGoods/delete/' + id,
    method: 'post',
    params: { id }
  })
}

// 获取商品列表
export function fetchProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params: params
  })
}

// 获取本地好物关联商品
export function fetchRelatedProductList(id) {
  return request({
    url: '/localGoods/relation/' + id,
    method: 'get'
  })
}