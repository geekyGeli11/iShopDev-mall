import request from '@/utils/request'

/**
 * 分页查询佣金配置列表
 */
export function fetchList(params) {
  return request({
    url: '/commission/config/list',
    method: 'get',
    params: params
  })
}

/**
 * 获取佣金配置详情
 */
export function getDetail(id) {
  return request({
    url: `/commission/config/detail/${id}`,
    method: 'get'
  })
}

/**
 * 创建佣金配置
 */
export function createConfig(data) {
  return request({
    url: '/commission/config/create',
    method: 'post',
    data: data
  })
}

/**
 * 更新佣金配置
 */
export function updateConfig(data) {
  return request({
    url: '/commission/config/update',
    method: 'post',
    data: data
  })
}

/**
 * 删除佣金配置
 */
export function deleteConfig(id) {
  return request({
    url: `/commission/config/delete/${id}`,
    method: 'post'
  })
}

/**
 * 批量删除佣金配置
 */
export function batchDelete(ids) {
  return request({
    url: '/commission/config/batch/delete',
    method: 'post',
    params: { ids: ids }
  })
}

/**
 * 更新配置状态
 */
export function updateStatus(id, isActive) {
  return request({
    url: '/commission/config/status/update',
    method: 'post',
    params: { 
      id: id, 
      isActive: isActive 
    }
  })
}

/**
 * 批量更新配置状态
 */
export function batchUpdateStatus(ids, isActive) {
  return request({
    url: '/commission/config/batch/status',
    method: 'post',
    params: { 
      ids: ids, 
      isActive: isActive 
    }
  })
}

/**
 * 获取配置统计数据
 */
export function getStatistics() {
  return request({
    url: '/commission/config/statistics',
    method: 'get'
  })
}

/**
 * 根据分类获取生效配置
 */
export function getActiveConfig(categoryId) {
  return request({
    url: `/commission/config/active/${categoryId}`,
    method: 'get'
  })
}

/**
 * 获取全局默认配置
 */
export function getGlobalDefault() {
  return request({
    url: '/commission/config/global/default',
    method: 'get'
  })
}

/**
 * 检查配置名称重复
 */
export function checkConfigName(configName, excludeId = null) {
  return request({
    url: '/commission/config/check/name',
    method: 'get',
    params: { 
      configName: configName,
      excludeId: excludeId
    }
  })
} 