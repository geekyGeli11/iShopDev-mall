import request from '@/utils/request'

// ==================== 报损记录相关 ====================

// 创建报损记录
export function createDamageReport(data) {
  return request({
    url: '/damage/report/create',
    method: 'post',
    data: data
  })
}

// 分页查询报损记录
export function fetchDamageReportList(params) {
  return request({
    url: '/damage/report/list',
    method: 'get',
    params: params
  })
}

// 获取报损详情
export function getDamageReportDetail(id) {
  return request({
    url: `/damage/report/${id}`,
    method: 'get'
  })
}

// 开始处理报损
export function startHandleDamage(id) {
  return request({
    url: `/damage/report/handle/start/${id}`,
    method: 'post'
  })
}

// 更新处理信息
export function updateHandleDamage(id, data) {
  return request({
    url: `/damage/report/handle/update/${id}`,
    method: 'post',
    data: data
  })
}

// 验收
export function acceptanceDamage(id, params) {
  return request({
    url: `/damage/report/acceptance/${id}`,
    method: 'post',
    params: params
  })
}

// 完成处理
export function completeDamage(id) {
  return request({
    url: `/damage/report/complete/${id}`,
    method: 'post'
  })
}

// 关闭报损
export function closeDamage(id, remark) {
  return request({
    url: `/damage/report/close/${id}`,
    method: 'post',
    params: { remark }
  })
}

// 批量删除报损记录
export function deleteDamageReport(ids) {
  return request({
    url: '/damage/report/delete',
    method: 'post',
    params: { ids: ids.join(',') }
  })
}

// 获取待处理报损数量
export function getPendingDamageCount() {
  return request({
    url: '/damage/report/pending/count',
    method: 'get'
  })
}

// 按门店统计报损
export function statisticsByStore(params) {
  return request({
    url: '/damage/report/statistics/store',
    method: 'get',
    params: params
  })
}

// 按报损类型统计
export function statisticsByType(params) {
  return request({
    url: '/damage/report/statistics/type',
    method: 'get',
    params: params
  })
}

// ==================== 报损原因相关 ====================

// 创建报损原因
export function createDamageReason(data) {
  return request({
    url: '/damage/reason/create',
    method: 'post',
    data: data
  })
}

// 更新报损原因
export function updateDamageReason(id, data) {
  return request({
    url: `/damage/reason/update/${id}`,
    method: 'post',
    data: data
  })
}

// 删除报损原因
export function deleteDamageReason(id) {
  return request({
    url: `/damage/reason/delete/${id}`,
    method: 'post'
  })
}

// 获取所有启用的报损原因
export function fetchAllDamageReasons() {
  return request({
    url: '/damage/reason/listAll',
    method: 'get'
  })
}

// 按类型获取报损原因
export function fetchDamageReasonsByType(type) {
  return request({
    url: '/damage/reason/listByType',
    method: 'get',
    params: { type }
  })
}

// 分页查询报损原因
export function fetchDamageReasonList(params) {
  return request({
    url: '/damage/reason/list',
    method: 'get',
    params: params
  })
}

// 获取报损原因详情
export function getDamageReasonDetail(id) {
  return request({
    url: `/damage/reason/${id}`,
    method: 'get'
  })
}

// 更新报损原因状态
export function updateDamageReasonStatus(id, status) {
  return request({
    url: `/damage/reason/updateStatus/${id}`,
    method: 'post',
    params: { status }
  })
}
