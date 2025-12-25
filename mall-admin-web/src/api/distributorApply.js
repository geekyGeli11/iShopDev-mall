import request from '@/utils/request'

/**
 * 分页查询分销商申请列表
 */
export function fetchList(params) {
  return request({
    url: '/distributor/apply/list',
    method: 'get',
    params: params
  })
}

/**
 * 获取申请详情
 */
export function getDetail(id) {
  return request({
    url: `/distributor/apply/detail/${id}`,
    method: 'get'
  })
}

/**
 * 审核申请
 */
export function reviewApply(id, status, reviewComment) {
  return request({
    url: '/distributor/apply/review',
    method: 'post',
    data: {
      applyId: id,
      status: status,
      reviewComment: reviewComment
    }
  })
}

/**
 * 批量审核申请
 */
export function batchReview(ids, status, reviewComment) {
  return request({
    url: '/distributor/apply/batch/review',
    method: 'post',
    params: { 
      ids: ids,
      status: status,
      reviewComment: reviewComment 
    }
  })
}

/**
 * 删除申请
 */
export function deleteApply(id) {
  return request({
    url: `/distributor/apply/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取统计数据
 */
export function getStatistics() {
  return request({
    url: '/distributor/apply/statistics',
    method: 'get'
  })
} 