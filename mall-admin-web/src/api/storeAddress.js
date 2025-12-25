import request from '@/utils/request'

// 获取门店地址列表
export function fetchList(params) {
  return request({
    url: '/storeAddress/list',
    method: 'get',
    params: params
  })
}

// 获取门店地址详情
export function getStoreAddress(id) {
  return request({
    url: '/storeAddress/' + id,
    method: 'get'
  })
}

// 新增门店地址
export function createStoreAddress(data) {
  return request({
    url: '/storeAddress/create',
    method: 'post',
    data: data
  })
}

// 更新门店地址
export function updateStoreAddress(id, data) {
  return request({
    url: '/storeAddress/update/' + id,
    method: 'post',
    data: data
  })
}

// 删除门店地址
export function deleteStoreAddress(id) {
  return request({
    url: '/storeAddress/delete/' + id,
    method: 'post'
  })
}

// ==================== 地库相关 API ====================

// 获取所有门店（包含地库）- 用于库存操作
export function getAllStores() {
  return request({
    url: '/storeAddress/listAll',
    method: 'get'
  })
}

// 获取可见门店列表（不包含地库）- 用于门店地址管理
export function getVisibleStores() {
  return request({
    url: '/storeAddress/listVisible',
    method: 'get'
  })
}

// 获取地库信息
export function getWarehouse() {
  return request({
    url: '/storeAddress/warehouse',
    method: 'get'
  })
}

// 初始化地库
export function initializeWarehouse() {
  return request({
    url: '/storeAddress/initWarehouse',
    method: 'post'
  })
} 