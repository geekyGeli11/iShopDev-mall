import request from '@/utils/request'

// 获取所有门店列表
export function fetchAllStores() {
  return request({
    url: '/storeSkuStock/stores',
    method: 'get'
  })
}

// 根据商品ID获取门店库存分配情况
export function fetchStoreSkuStockByProduct(productId) {
  return request({
    url: `/storeSkuStock/listByProduct/${productId}`,
    method: 'get'
  })
}

// 根据SKU ID获取门店库存分配情况
export function fetchStoreSkuStockBySku(skuId) {
  return request({
    url: `/storeSkuStock/listBySku/${skuId}`,
    method: 'get'
  })
}

// 获取指定门店的SKU库存
export function getStoreSkuStock(storeId, skuId) {
  return request({
    url: '/storeSkuStock/get',
    method: 'get',
    params: {
      storeId,
      skuId
    }
  })
}

// 保存单个门店SKU库存
export function saveStoreSkuStock(data) {
  return request({
    url: '/storeSkuStock/save',
    method: 'post',
    data
  })
}

// 批量保存门店SKU库存分配
export function batchSaveStoreSkuStock(data) {
  return request({
    url: '/storeSkuStock/batchSave',
    method: 'post',
    data
  })
}

// 删除门店SKU库存记录
export function deleteStoreSkuStock(storeId, skuId) {
  return request({
    url: '/storeSkuStock/delete',
    method: 'delete',
    params: {
      storeId,
      skuId
    }
  })
}

// 校验门店库存总和是否等于SKU总库存
export function validateStoreStockSum(skuId, skuTotalStock) {
  return request({
    url: '/storeSkuStock/validate',
    method: 'get',
    params: {
      skuId,
      skuTotalStock
    }
  })
}

// 自动均分库存到所有门店
export function autoDistributeStock(skuId, totalStock) {
  return request({
    url: '/storeSkuStock/autoDistribute',
    method: 'post',
    params: {
      skuId,
      totalStock
    }
  })
}

// 根据商品ID和门店库存参数批量保存
export function batchSaveStoreSkuStockByProduct(productId, data) {
  return request({
    url: `/storeSkuStock/batchSaveByProduct/${productId}`,
    method: 'post',
    data
  })
}

// 根据商品ID删除所有门店库存记录
export function deleteStoreSkuStockByProduct(productId) {
  return request({
    url: `/storeSkuStock/deleteByProduct/${productId}`,
    method: 'delete'
  })
}

// 根据门店ID获取该门店所有SKU库存
export function fetchStoreSkuStockByStore(storeId) {
  return request({
    url: `/storeSkuStock/listByStore/${storeId}`,
    method: 'get'
  })
} 