import request from '@/utils/request'

/**
 * 会员等级管理API
 */

// 获取会员等级列表
export function fetchList(params) {
  return request({
    url: '/memberLevel/list',
    method: 'get',
    params: params
  })
}

// 创建会员等级
export function createMemberLevel(data) {
  return request({
    url: '/memberLevel/create',
    method: 'post',
    data: data
  })
}

// 更新会员等级
export function updateMemberLevel(id, data) {
  return request({
    url: `/memberLevel/update/${id}`,
    method: 'post',
    data: data
  })
}

// 删除会员等级
export function deleteMemberLevel(id) {
  return request({
    url: `/memberLevel/delete/${id}`,
    method: 'post'
  })
}

// 获取会员等级详情
export function getMemberLevel(id) {
  return request({
    url: `/memberLevel/${id}`,
    method: 'get'
  })
}

// 获取优惠券选项列表
export function getCouponOptions(keyword) {
  return request({
    url: '/memberLevel/coupon/options',
    method: 'get',
    params: { keyword }
  })
}
