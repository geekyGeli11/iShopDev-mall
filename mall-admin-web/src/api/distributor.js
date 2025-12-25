import request from '@/utils/request'

/**
 * 分页查询分销商列表
 */
export function fetchList(params) {
  return request({
    url: '/distributor/list',
    method: 'get',
    params: params
  })
}

/**
 * 获取分销商详情
 */
export function getDetail(userId) {
  return request({
    url: `/distributor/detail/${userId}`,
    method: 'get'
  })
}

/**
 * 更新分销商状态
 */
export function updateStatus(userId, status, reason = '') {
  return request({
    url: '/distributor/status/update',
    method: 'post',
    data: { 
      userId: userId,
      status: status,
      reason: reason
    }
  })
}

/**
 * 更新分销商等级
 */
export function updateLevel(userId, level, reason = '') {
  return request({
    url: '/distributor/level/update',
    method: 'post',
    data: { 
      userId: userId,
      distributorLevel: level,
      reason: reason
    }
  })
}

/**
 * 启用分销商
 */
export function enableDistributor(userId, reason = '') {
  return request({
    url: `/distributor/enable/${userId}`,
    method: 'post',
    params: { reason: reason }
  })
}

/**
 * 禁用分销商
 */
export function disableDistributor(userId, reason = '') {
  return request({
    url: `/distributor/disable/${userId}`,
    method: 'post',
    params: { reason: reason }
  })
}

/**
 * 暂停分销商
 */
export function pauseDistributor(userId, reason = '') {
  return request({
    url: `/distributor/pause/${userId}`,
    method: 'post',
    params: { reason: reason }
  })
}

/**
 * 批量更新分销商状态
 */
export function batchUpdateStatus(userIds, status, reason = '') {
  return request({
    url: '/distributor/batch/status',
    method: 'post',
    params: { 
      userIds: userIds,
      status: status,
      reason: reason
    }
  })
}

/**
 * 获取分销商统计数据
 */
export function getStatistics() {
  return request({
    url: '/distributor/statistics',
    method: 'get'
  })
} 