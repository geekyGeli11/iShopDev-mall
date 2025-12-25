import request from '@/utils/request'

// DIY模板管理
export function fetchTemplateList(params) {
  return request({
    url: '/diyTemplate/list',
    method: 'get',
    params: params
  })
}

export function createTemplate(data) {
  return request({
    url: '/diyTemplate/create',
    method: 'post',
    data: data
  })
}

export function updateTemplate(id, data) {
  return request({
    url: `/diyTemplate/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteTemplate(id) {
  return request({
    url: `/diyTemplate/delete/${id}`,
    method: 'post'
  })
}

export function deleteTemplateBatch(params) {
  return request({
    url: '/diyTemplate/delete/batch',
    method: 'post',
    params: params
  })
}

export function getTemplate(id) {
  return request({
    url: `/diyTemplate/${id}`,
    method: 'get'
  })
}

export function getTemplateDetail(id) {
  return request({
    url: `/diyTemplate/detail/${id}`,
    method: 'get'
  })
}

export function fetchEnabledTemplates() {
  return request({
    url: '/diyTemplate/listEnabled',
    method: 'get'
  })
}

export function updateTemplateStatus(params) {
  return request({
    url: '/diyTemplate/update/status',
    method: 'post',
    params: params
  })
}

export function copyTemplate(id, params) {
  return request({
    url: `/diyTemplate/copy/${id}`,
    method: 'post',
    params: params
  })
}

export function fetchTemplateByCategory(categoryId) {
  return request({
    url: `/diyTemplate/listByProductCategory/${categoryId}`,
    method: 'get'
  })
}

// DIY模板面管理
export function fetchSurfaceList(templateId) {
  return request({
    url: `/diyTemplateSurface/listByTemplate/${templateId}`,
    method: 'get'
  })
}

export function createSurface(data) {
  return request({
    url: '/diyTemplateSurface/create',
    method: 'post',
    data: data
  })
}

export function updateSurface(id, data) {
  return request({
    url: `/diyTemplateSurface/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteSurface(id) {
  return request({
    url: `/diyTemplateSurface/delete/${id}`,
    method: 'post'
  })
}

export function getSurface(id) {
  return request({
    url: `/diyTemplateSurface/${id}`,
    method: 'get'
  })
}

// DIY区域管理
export function fetchAreaList(surfaceId) {
  return request({
    url: `/diyArea/listBySurface/${surfaceId}`,
    method: 'get'
  })
}

export function createArea(data) {
  return request({
    url: '/diyArea/create',
    method: 'post',
    data: data
  })
}

export function updateArea(id, data) {
  return request({
    url: `/diyArea/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteArea(id) {
  return request({
    url: `/diyArea/delete/${id}`,
    method: 'post'
  })
}

export function getArea(id) {
  return request({
    url: `/diyArea/${id}`,
    method: 'get'
  })
}
