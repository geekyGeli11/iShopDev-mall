import request from '@/utils/request'

// 添加活动
export function createActivity(data) {
  return request({
    url: '/activity/create',
    method: 'post',
    data: data
  })
}

// 更新活动
export function updateActivity(id, data) {
  return request({
    url: '/activity/update/' + id,
    method: 'post',
    data: data
  })
}

// 删除活动
export function deleteActivity(id) {
  return request({
    url: '/activity/delete/' + id,
    method: 'post'
  })
}

// 获取活动详情
export function getActivity(id) {
  return request({
    url: '/activity/' + id,
    method: 'get'
  })
}

// 获取活动列表
export function fetchList(params) {
  return request({
    url: '/activity/list',
    method: 'get',
    params: params
  })
}

export function getActivitySignupList(activityId, pageNum, pageSize) {
  return request({
    url: '/activitySignup/list',
    method: 'get',
    params: { 
      activityId,
      pageNum,
      pageSize
    }
  })
} 