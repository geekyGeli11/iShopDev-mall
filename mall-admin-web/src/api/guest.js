import request from '@/utils/request'

/**
 * 分页查询游客列表
 */
export function fetchList(params) {
  return request({
    url: '/guest/list',
    method: 'get',
    params: params
  })
}

/**
 * 更新游客状态
 */
export function updateStatus(guestId, status) {
  return request({
    url: '/guest/updateStatus',
    method: 'post',
    params: { guestId, status }
  })
}

/**
 * 删除游客
 */
export function deleteGuest(guestId) {
  return request({
    url: '/guest/delete',
    method: 'post',
    params: { guestId }
  })
}

/**
 * 清理过期游客
 */
export function cleanExpiredGuests(expiredDays = 7) {
  return request({
    url: '/guest/cleanExpired',
    method: 'post',
    params: { expiredDays }
  })
}

/**
 * 获取学校列表
 */
export function getSchoolList() {
  return request({
    url: '/member/schools',
    method: 'get'
  })
}
