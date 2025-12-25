import request from '@/utils/request';

export function fetchSimpleProducts(params){
  return request({url:'/pms/product/simple/list',method:'get',params});
}
export function fetchSimpleCoupons(params){
  return request({url:'/coupon/simple/list',method:'get',params});
} 