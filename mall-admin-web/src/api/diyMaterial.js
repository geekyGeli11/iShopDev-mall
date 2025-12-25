import request from '@/utils/request'

// DIY素材分类管理
export function fetchMaterialCategoryList(params) {
  return request({
    url: '/diyMaterialCategory/list',
    method: 'get',
    params: params
  })
}

export function createMaterialCategory(data) {
  return request({
    url: '/diyMaterialCategory/create',
    method: 'post',
    data: data
  })
}

export function updateMaterialCategory(id, data) {
  return request({
    url: `/diyMaterialCategory/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteMaterialCategory(id) {
  return request({
    url: `/diyMaterialCategory/delete/${id}`,
    method: 'post'
  })
}

export function getMaterialCategory(id) {
  return request({
    url: `/diyMaterialCategory/${id}`,
    method: 'get'
  })
}

export function updateMaterialCategoryStatus(params) {
  return request({
    url: '/diyMaterialCategory/update/status',
    method: 'post',
    params: params
  })
}

// DIY素材管理
export function fetchMaterialList(params) {
  return request({
    url: '/diyMaterial/list',
    method: 'get',
    params: params
  })
}

export function createMaterial(data) {
  return request({
    url: '/diyMaterial/create',
    method: 'post',
    data: data
  })
}

export function updateMaterial(id, data) {
  return request({
    url: `/diyMaterial/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteMaterial(id) {
  return request({
    url: `/diyMaterial/delete/${id}`,
    method: 'post'
  })
}

export function deleteMaterialBatch(params) {
  return request({
    url: '/diyMaterial/delete/batch',
    method: 'post',
    params: params
  })
}

export function getMaterial(id) {
  return request({
    url: `/diyMaterial/${id}`,
    method: 'get'
  })
}

export function updateMaterialStatus(params) {
  return request({
    url: '/diyMaterial/update/status',
    method: 'post',
    params: params
  })
}

export function fetchMaterialByCategory(categoryId) {
  return request({
    url: `/diyMaterial/listByCategory/${categoryId}`,
    method: 'get'
  })
}
