import request from '@/utils/request'

/**
 * 获取首页统计数据
 */
export function getHomeStatistics() {
  return request({
    url: 'api/home/statistics/info',
    method: 'get'
  })
}

/**
 * 获取订单统计数据
 * @param startDate 开始日期，格式：yyyy-MM-dd
 * @param endDate 结束日期，格式：yyyy-MM-dd
 */
export function getOrderStatistics(startDate, endDate) {
  return request({
    url: 'api/home/statistics/orderStatistics',
    method: 'get',
    params: { startDate, endDate }
  })
}

/**
 * 刷新统计数据缓存
 */
export function refreshStatisticsCache() {
  return request({
    url: 'api/home/statistics/refreshCache',
    method: 'get'
  })
}

/**
 * 获取营业数据统计
 * @param startDate 开始日期，格式：yyyy-MM-dd
 * @param endDate 结束日期，格式：yyyy-MM-dd
 * @param schoolId 学校ID（可选）
 * @param storeId 门店ID（可选）
 */
export function getBusinessStatistics(startDate, endDate, schoolId, storeId) {
  return request({
    url: 'api/home/statistics/business',
    method: 'get',
    params: { startDate, endDate, schoolId, storeId }
  })
}

/**
 * 获取会员数据统计
 * @param startDate 开始日期，格式：yyyy-MM-dd
 * @param endDate 结束日期，格式：yyyy-MM-dd
 * @param schoolId 学校ID（可选）
 * @param storeId 门店ID（可选）
 * @param topLimit 排行榜数量限制（默认10）
 */
export function getMemberStatistics(startDate, endDate, schoolId, storeId, topLimit = 10) {
  return request({
    url: 'api/home/statistics/member',
    method: 'get',
    params: { startDate, endDate, schoolId, storeId, topLimit }
  })
}

/**
 * 获取商品销售数据统计
 * @param startDate 开始日期，格式：yyyy-MM-dd
 * @param endDate 结束日期，格式：yyyy-MM-dd
 * @param schoolId 学校ID（可选）
 * @param storeId 门店ID（可选）
 * @param topLimit 排行榜数量限制（默认10）
 * @param rankBy 排序方式（amount/quantity，默认amount）
 */
export function getProductStatistics(startDate, endDate, schoolId, storeId, topLimit = 10, rankBy = 'amount') {
  return request({
    url: 'api/home/statistics/product',
    method: 'get',
    params: { startDate, endDate, schoolId, storeId, topLimit, rankBy }
  })
} 