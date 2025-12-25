import request from '@/utils/requestUtil'

// 获取我的积分
export function getMyPoints() {
  return request({
    url: '/member/points',
    method: 'get'
  })
}

// 获取积分明细
export function getPointRecords(params) {
  return request({
    url: '/member/points/history',
    method: 'get',
    params
  })
}

// 积分兑换商品
export function exchangePoints(data) {
  return request({
    url: '/member/points/exchange',
    method: 'post',
    data
  })
}

// 获取当前用户积分变动历史记录
export function getIntegrationHistory(params) {
  return request({
    url: '/member/integration/history/list',
    method: 'get',
    params
  })
}

// 获取积分变动历史详情
export function getIntegrationHistoryDetail(id) {
  return request({
    url: `/member/integration/history/${id}`,
    method: 'get'
  })
}

// 获取当前用户积分总数
export function getCurrentIntegration() {
  return request({
    url: `/member/integration/history/integration`,
    method: 'get'
  })
}

// ============ 积分换购相关API ============

// 获取换购商品列表
export function getPointsProductList(params) {
  return request({
    url: '/app/points/product/list',
    method: 'get',
    params
  })
}

// 获取换购商品详情
export function getPointsProductDetail(configId) {
  return request({
    url: `/app/points/product/detail/${configId}`,
    method: 'get'
  })
}

// 兑换商品
export function exchangeProduct(data) {
  return request({
    url: '/app/points/product/exchange',
    method: 'post',
    data
  })
}

// 检查商品兑换资格
export function checkProductExchangeEligible(configId) {
  return request({
    url: `/app/points/product/checkEligible/${configId}`,
    method: 'get'
  })
}

// 获取换购优惠券列表
export function getPointsCouponList(params) {
  return request({
    url: '/app/points/coupon/list',
    method: 'get',
    params
  })
}

// 获取换购优惠券详情
export function getPointsCouponDetail(configId) {
  return request({
    url: `/app/points/coupon/detail/${configId}`,
    method: 'get'
  })
}

// 兑换优惠券
export function exchangeCoupon(data) {
  return request({
    url: '/app/points/coupon/exchange',
    method: 'post',
    data
  })
}

// 检查优惠券兑换资格
export function checkCouponExchangeEligible(configId) {
  return request({
    url: `/app/points/coupon/checkEligible/${configId}`,
    method: 'get'
  })
}

// 获取用户兑换记录
export function getUserExchangeList(params) {
  return request({
    url: '/app/points/exchange/list',
    method: 'get',
    params
  })
}

// 获取兑换记录详情
export function getExchangeDetail(id) {
  return request({
    url: `/app/points/exchange/detail/${id}`,
    method: 'get'
  })
}

// 获取用户积分余额
export function getUserPointsBalance() {
  return request({
    url: '/app/points/balance',
    method: 'get'
  })
}

// 创建积分兑换支付订单
export function createExchangePayOrder(data) {
  return request({
    url: '/app/points/exchange/createPayOrder',
    method: 'post',
    data
  })
}

// 积分兑换支付成功回调
export function exchangePaySuccess(data) {
  return request({
    url: '/app/points/exchange/paySuccess',
    method: 'post',
    data
  })
}

// 创建商品兑换记录（仅记录，不扣积分库存）
export function createProductExchangeRecord(data) {
  return request({
    url: '/app/points/product/createExchangeRecord',
    method: 'post',
    data
  })
}