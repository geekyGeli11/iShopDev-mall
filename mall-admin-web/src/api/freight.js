import request from '@/utils/request'

// 分页查询运费模板
export function fetchFreightTemplateList(params) {
  return request({
    url: '/freight/template/list',
    method: 'get',
    params: params
  })
}

// 创建运费模板
export function createFreightTemplate(data) {
  return request({
    url: '/freight/template/create',
    method: 'post',
    data: data
  })
}

// 更新运费模板
export function updateFreightTemplate(id, data) {
  return request({
    url: `/freight/template/update/${id}`,
    method: 'post',
    data: data
  })
}

// 删除运费模板
export function deleteFreightTemplate(id) {
  return request({
    url: `/freight/template/delete/${id}`,
    method: 'post'
  })
}

// 批量删除运费模板
export function batchDeleteFreightTemplate(ids) {
  return request({
    url: '/freight/template/delete',
    method: 'post',
    params: { ids: ids }
  })
}

// 获取运费模板详情
export function getFreightTemplateDetail(id) {
  return request({
    url: `/freight/template/${id}`,
    method: 'get'
  })
}

// 获取所有启用的运费模板
export function fetchAllEnabledFreightTemplates() {
  return request({
    url: '/freight/template/listAll',
    method: 'get'
  })
}

// 修改运费模板状态
export function updateFreightTemplateStatus(ids, status) {
  return request({
    url: '/freight/template/update/status',
    method: 'post',
    params: { 
      ids: ids,
      status: status 
    }
  })
}

// 计算运费
export function calculateFreight(data) {
  return request({
    url: '/freight/template/calculate',
    method: 'post',
    data: data
  })
}

// 获取默认运费模板
export function getDefaultFreightTemplate() {
  return request({
    url: '/freight/template/default',
    method: 'get'
  })
}

// 设置默认运费模板
export function setDefaultFreightTemplate(id) {
  return request({
    url: `/freight/template/setDefault/${id}`,
    method: 'post'
  })
}

// 刷新全部商品运费模板
export function refreshAllProductFreightTemplate(templateId) {
  return request({
    url: `/freight/template/refreshAllProduct/${templateId}`,
    method: 'post'
  })
}