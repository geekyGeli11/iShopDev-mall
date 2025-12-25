import request from '@/utils/request'

// 获取签到配置
export function getSigninConfig() {
  return request({
    url: '/signin/config',
    method: 'get'
  })
}

// 更新签到配置
export function updateSigninConfig(data) {
  return request({
    url: '/signin/config',
    method: 'post',
    data: data
  })
}

// 获取签到记录列表
export function getSigninLogs(params) {
  return request({
    url: '/signin/logs',
    method: 'get',
    params: params
  })
}

// 导出签到记录
export function exportSigninLogs(params) {
  return request({
    url: '/signin/logs/export',
    method: 'get',
    params: params
  })
}

// 获取签到统计数据
export function getSigninStatistics(params) {
  return request({
    url: '/signin/statistics',
    method: 'get',
    params: params
  })
} 