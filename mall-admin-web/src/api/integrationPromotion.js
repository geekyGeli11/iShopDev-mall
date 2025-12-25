import request from '@/utils/request'

export function createIntegrationPromotion(data) {
  return request({
    url: '/integration/promotion/create',
    method: 'post',
    data: data
  })
}

export function updateIntegrationPromotion(id, data) {
  return request({
    url: `/integration/promotion/update/${id}`,
    method: 'post',
    data: data
  })
}

export function deleteIntegrationPromotion(id) {
  return request({
    url: `/integration/promotion/delete/${id}`,
    method: 'post'
  })
}

export function deleteBatchIntegrationPromotion(ids) {
  return request({
    url: '/integration/promotion/delete/batch',
    method: 'post',
    params: { ids: ids }
  })
}

export function getIntegrationPromotion(id) {
  return request({
    url: `/integration/promotion/${id}`,
    method: 'get'
  })
}

export function fetchIntegrationPromotionList(params) {
  return request({
    url: '/integration/promotion/list',
    method: 'get',
    params: params
  })
}

export function updateIntegrationPromotionStatus(id, status) {
  return request({
    url: `/integration/promotion/update/status/${id}`,
    method: 'post',
    params: { status: status }
  })
}

export function getEnabledIntegrationPromotions() {
  return request({
    url: '/integration/promotion/list/enabled',
    method: 'get'
  })
}

export function getCurrentActiveIntegrationPromotions() {
  return request({
    url: '/integration/promotion/current',
    method: 'get'
  })
}
