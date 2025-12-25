import request from '@/utils/request'

export function fetchList(params) {
  return request({
    url: '/principalPerson/list',
    method: 'get',
    params: params
  })
}

export function createPrincipalPerson(data) {
  return request({
    url: '/principalPerson/create',
    method: 'post',
    data: data
  })
}

export function getPrincipalPerson(id) {
  return request({
    url: '/principalPerson/' + id,
    method: 'get'
  })
}

export function updatePrincipalPerson(id, data) {
  return request({
    url: '/principalPerson/update/' + id,
    method: 'post',
    data: data
  })
}

export function deletePrincipalPerson(id) {
  return request({
    url: '/principalPerson/delete/' + id,
    method: 'post'
  })
}

export function updateStatus(id, params) {
  return request({
    url: '/principalPerson/update/status/' + id,
    method: 'post',
    params: params
  })
}

export function updateRecommend(id, params) {
  return request({
    url: '/principalPerson/update/recommend/' + id,
    method: 'post',
    params: params
  })
} 