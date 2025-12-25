import request from '@/utils/request'

/**
 * 分页查询组合商品列表
 */
export function fetchList(params) {
  return request({
    url: '/productBundle/list',
    method: 'get',
    params: params
  })
}

/**
 * 创建组合商品
 */
export function createBundle(data) {
  return request({
    url: '/productBundle/create',
    method: 'post',
    data: data
  })
}

/**
 * 更新组合商品
 */
export function updateBundle(id, data) {
  return request({
    url: '/productBundle/update/' + id,
    method: 'post',
    data: data
  })
}

/**
 * 获取组合商品详情
 */
export function getBundle(id) {
  return request({
    url: '/productBundle/' + id,
    method: 'get'
  })
}

/**
 * 删除组合商品
 */
export function deleteBundle(id) {
  return request({
    url: '/productBundle/delete/' + id,
    method: 'post'
  })
}

/**
 * 批量删除组合商品
 */
export function deleteBundleBatch(ids) {
  return request({
    url: '/productBundle/delete/batch',
    method: 'post',
    params: { ids: ids }
  })
}

/**
 * 更新上架状态
 */
export function updatePublishStatus(ids, publishStatus) {
  return request({
    url: '/productBundle/update/publishStatus',
    method: 'post',
    params: {
      ids: ids,
      publishStatus: publishStatus
    }
  })
}
