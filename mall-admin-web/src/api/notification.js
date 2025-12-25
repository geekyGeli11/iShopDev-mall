import request from '@/utils/request'

export function fetchList(params) {
  return request({
    url: '/sms/notification/list',
    method: 'get',
    params: params
  })
}

export function createNotification(data) {
  return request({
    url: '/sms/notification/create',
    method: 'post',
    data: data
  })
}

export function getNotification(id) {
  return request({
    url: '/sms/notification/' + id,
    method: 'get'
  })
}

export function updateNotification(id, data) {
  return request({
    url: '/sms/notification/update/' + id,
    method: 'post',
    data: data
  })
}

export function deleteNotification(data) {
  return request({
    url: '/sms/notification/delete',
    method: 'post',
    data: data
  })
}

export function updateStatus(id, params) {
  return request({
    url: '/sms/notification/update/' + id,
    method: 'post',
    params: params
  })
}

export function getReadStats(id) {
  return fetchReadRecords(id, {pageNum: 1, pageSize: 10});
}

export function fetchReadRecords(id, params) {
  return request({
    url: '/sms/notification/readRecords/' + id,
    method: 'get',
    params: params
  })
} 