import request from '@/utils/request'

/**
 * 获取礼物心愿列表
 * @param {Object} params 查询参数
 */
export function fetchList(params) {
  return request({
    url: '/giftWish/list',
    method: 'get',
    params: params
  })
}

/**
 * 根据ID获取礼物心愿详情
 * @param {Number} id 心愿ID
 */
export function getGiftWish(id) {
  return request({
    url: '/giftWish/' + id,
    method: 'get'
  })
}

/**
 * 添加礼物心愿
 * @param {Object} data 心愿数据
 */
export function createGiftWish(data) {
  return request({
    url: '/giftWish/create',
    method: 'post',
    data: data
  })
}

/**
 * 更新礼物心愿
 * @param {Number} id 心愿ID
 * @param {Object} data 心愿数据
 */
export function updateGiftWish(id, data) {
  return request({
    url: '/giftWish/update/' + id,
    method: 'post',
    data: data
  })
}

/**
 * 删除礼物心愿
 * @param {Number} id 心愿ID
 */
export function deleteGiftWish(id) {
  return request({
    url: '/giftWish/delete/' + id,
    method: 'post'
  })
} 