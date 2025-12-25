import request from '@/utils/request'

// 获取风格模型列表
export function fetchList(params) {
  return request({
    url: '/styleModel/list',
    method: 'get',
    params: params
  })
}

// 创建风格模型
export function createStyleModel(data) {
  return request({
    url: '/styleModel/create',
    method: 'post',
    data: data
  })
}

// 更新风格模型状态
export function updateStatus(data) {
  return request({
    url: '/styleModel/update/status',
    method: 'post',
    data: data
  })
}

// 删除风格模型
export function deleteStyleModel(id) {
  return request({
    url: '/styleModel/delete/' + id,
    method: 'get'
  })
}

// 获取风格模型详情
export function getStyleModel(id) {
  return request({
    url: '/styleModel/' + id,
    method: 'get'
  })
}

// 更新风格模型
export function updateStyleModel(id, data) {
  return request({
    url: '/styleModel/update/' + id,
    method: 'post',
    data: data
  })
}

// 获取风格模型关联的商品列表
export function getStyleModelProducts(id, params) {
  return request({
    url: '/styleModel/' + id + '/products',
    method: 'get',
    params: params
  })
}

// 添加商品到风格模型
export function addProductToStyleModel(styleModelId, productIds) {
  return request({
    url: '/styleModel/' + styleModelId + '/products/add',
    method: 'post',
    data: { productIds: productIds }
  })
}

// 从风格模型移除商品
export function removeProductFromStyleModel(styleModelId, productIds) {
  return request({
    url: '/styleModel/' + styleModelId + '/products/remove',
    method: 'post',
    data: { productIds: productIds }
  })
}

// 提取图片风格介绍
export function extractStyleDescription(imageUrl) {
  return request({
    url: '/styleModel/extractStyleDescription',
    method: 'post',
    params: { imageUrl: imageUrl }
  })
}

// 批量操作
export function batchOperate(data) {
  return request({
    url: '/styleModel/batch',
    method: 'post',
    data: data
  })
}
