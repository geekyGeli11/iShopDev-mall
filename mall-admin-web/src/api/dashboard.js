/**
 * 首页看板数据API
 */
import request from '@/utils/request'

/**
 * 获取营业数据统计
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} params.endDate - 结束日期 (YYYY-MM-DD)
 * @param {number} params.schoolId - 学校ID（可选）
 * @param {number} params.storeId - 门店ID（可选）
 */
export function getBusinessStatistics(params) {
  return request({
    url: '/api/home/statistics/business',
    method: 'get',
    params
  })
}

/**
 * 获取会员数据统计
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} params.endDate - 结束日期 (YYYY-MM-DD)
 * @param {number} params.schoolId - 学校ID（可选）
 * @param {number} params.topLimit - 排行榜数量限制（默认10）
 */
export function getMemberStatistics(params) {
  return request({
    url: '/api/home/statistics/member',
    method: 'get',
    params
  })
}

/**
 * 获取商品销售数据统计
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期 (YYYY-MM-DD)
 * @param {string} params.endDate - 结束日期 (YYYY-MM-DD)
 * @param {number} params.schoolId - 学校ID（可选）
 * @param {number} params.storeId - 门店ID（可选）
 * @param {number} params.topLimit - 排行榜数量限制（默认10）
 * @param {string} params.rankBy - 排序方式：'amount'（按金额）或'quantity'（按数量）
 */
export function getProductStatistics(params) {
  return request({
    url: '/api/home/statistics/product',
    method: 'get',
    params
  })
}

/**
 * 获取筛选选项（学校列表和门店列表）
 */
export function getFilterOptions() {
  return request({
    url: '/api/home/statistics/filter-options',
    method: 'get'
  })
}
