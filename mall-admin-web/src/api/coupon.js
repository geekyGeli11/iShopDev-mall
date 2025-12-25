import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/coupon/list',
    method:'get',
    params:params
  })
}

export function createCoupon(data) {
  return request({
    url:'/coupon/create',
    method:'post',
    data:data
  })
}

export function getCoupon(id) {
  return request({
    url:'/coupon/'+id,
    method:'get',
  })
}

export function updateCoupon(id,data) {
  return request({
    url:'/coupon/update/'+id,
    method:'post',
    data:data
  })
}

export function deleteCoupon(id) {
  return request({
    url:'/coupon/delete/'+id,
    method:'post',
  })
}

export function generateShareInfo(id) {
  return request({
    url:'/coupon/generateShareInfo/'+id,
    method:'post',
  })
}

export function getCouponSchools(id) {
  return request({
    url:'/coupon/getSchools/'+id,
    method:'get',
  })
}

export function updateCouponSchools(id, schoolIds) {
  return request({
    url:'/coupon/updateSchools/'+id,
    method:'post',
    data: schoolIds
  })
}
