import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/product/list',
    method:'get',
    params:params
  })
}

export function fetchSimpleList(params) {
  return request({
    url:'/product/simpleList',
    method:'get',
    params:params
  })
}

export function updateDeleteStatus(params) {
  return request({
    url:'/product/update/deleteStatus',
    method:'post',
    params:params
  })
}

export function updateNewStatus(params) {
  return request({
    url:'/product/update/newStatus',
    method:'post',
    params:params
  })
}

export function updateRecommendStatus(params) {
  return request({
    url:'/product/update/recommendStatus',
    method:'post',
    params:params
  })
}

export function updatePublishStatus(params) {
  return request({
    url:'/product/update/publishStatus',
    method:'post',
    params:params
  })
}

export function createProduct(data) {
  return request({
    url:'/product/create',
    method:'post',
    data:data
  })
}

export function updateProduct(id,data) {
  return request({
    url:'/product/update/'+id,
    method:'post',
    data:data
  })
}

export function getProduct(id) {
  return request({
    url:'/product/updateInfo/'+id,
    method:'get',
  })
}

// DIY配置相关API
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

// 获取商品列表（包含运费模板和学校信息）
export function fetchListWithDetails(params) {
  return request({
    url: '/product/listWithDetails',
    method: 'get',
    params: params
  })
}

// 批量修改商品学校关联
export function updateProductSchools(params) {
  return request({
    url: '/product/update/schools',
    method: 'post',
    params: params
  })
}

// 批量修改商品分类
export function updateProductCategory(data) {
  return request({
    url: '/product/update/category',
    method: 'post',
    data: data
  })
}

// 导出商品数据
export function exportProducts(params) {
  return request({
    url: '/product/export',
    method: 'get',
    params: params,
    responseType: 'blob'
  })
}

// 生成商品分享信息
export function generateShareInfo(id) {
  return request({
    url: '/product/generateShareInfo/' + id,
    method: 'post',
  })
}

