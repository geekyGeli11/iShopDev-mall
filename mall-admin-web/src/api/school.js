import request from '@/utils/request'

// 获取学校列表
export function fetchList(params) {
  return request({
    url: '/school/list',
    method: 'get',
    params: params
  })
}

// 创建学校
export function createSchool(data) {
  return request({
    url: '/school/create',
    method: 'post',
    data: data
  })
}

// 更新学校
export function updateSchool(id, data) {
  return request({
    url: `/school/update/${id}`,
    method: 'post',
    data: data
  })
}

// 删除学校
export function deleteSchool(id) {
  return request({
    url: `/school/delete/${id}`,
    method: 'post'
  })
}

// 获取学校详情
export function getSchool(id) {
  return request({
    url: `/school/${id}`,
    method: 'get'
  })
}

// 获取所有启用的学校（用于下拉选择）
export function fetchEnabledSchools() {
  return request({
    url: '/school/listEnabled',
    method: 'get'
  })
}

// 更新学校状态
export function updateSchoolStatus(id, status) {
  return request({
    url: `/school/updateStatus/${id}`,
    method: 'post',
    params: { status }
  })
}
