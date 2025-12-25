import request from '@/utils/request'

/**
 * 获取所有销售类型
 */
export function getSaleTypes() {
  return request({
    url: '/pmsNonSystemSale/saleTypes',
    method: 'get'
  })
}

/**
 * 获取SKU库存信息
 */
export function getSkuStock(skuId) {
  return request({
    url: `/pmsNonSystemSale/skuStock/${skuId}`,
    method: 'get'
  })
}

/**
 * 提交非系统销售单
 */
export function submitNonSystemSale(data) {
  return request({
    url: '/pmsNonSystemSale/submit',
    method: 'post',
    data
  })
}

/**
 * 获取销售单列表
 */
export function getNonSystemSaleList(pageNum, pageSize) {
  return request({
    url: '/pmsNonSystemSale/list',
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

/**
 * 获取销售单详情
 */
export function getNonSystemSaleDetail(saleId) {
  return request({
    url: `/pmsNonSystemSale/${saleId}`,
    method: 'get'
  })
}

/**
 * 审核销售单
 */
export function approveSale(saleId) {
  return request({
    url: `/pmsNonSystemSale/${saleId}/approve`,
    method: 'post'
  })
}

/**
 * 驳回销售单
 */
export function rejectSale(saleId, reason) {
  return request({
    url: `/pmsNonSystemSale/${saleId}/reject`,
    method: 'post',
    params: { reason }
  })
}

/**
 * 获取门店列表
 */
export function getStoreList() {
  return request({
    url: '/pmsNonSystemSale/stores',
    method: 'get'
  })
}

/**
 * 根据学校ID获取门店列表
 */
export function getStoreListBySchool(schoolId) {
  return request({
    url: '/pmsNonSystemSale/stores',
    method: 'get',
    params: { schoolId }
  })
}

/**
 * 获取门店SKU库存
 */
export function getStoreSkuStock(storeId, skuId) {
  return request({
    url: '/pmsNonSystemSale/storeSkuStock',
    method: 'get',
    params: {
      storeId,
      skuId
    }
  })
}

/**
 * 获取SKU库存及门店库存信息
 */
export function getSkuStockWithStore(productId) {
  return request({
    url: `/sku/withStore/${productId}`,
    method: 'get'
  })
}



/**
 * 计算库存扣减方案
 */
export function calculateStockDeductionPlan(schoolId, storeId, items) {
  return request({
    url: '/pmsNonSystemSale/calculateStockDeductionPlan',
    method: 'post',
    data: {
      schoolId,
      storeId,
      items
    }
  })
}

/**
 * 导出销售单为 Excel
 */
export function exportSale(saleId) {
  return request({
    url: `/pmsNonSystemSale/export/${saleId}`,
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 生成销售单分享信息
 */
export function generateShareInfo(saleId) {
  return request({
    url: `/pmsNonSystemSale/generateShareInfo/${saleId}`,
    method: 'post'
  })
}
