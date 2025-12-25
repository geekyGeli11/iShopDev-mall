import request from '@/utils/request'

const api = {
  // 版本管理 - mall-admin接口
  versionUpload: '/app-update/upload',
  versionList: '/app-update/versions',
  versionUpdate: '/app-update/versions/{id}',
  versionToggle: '/app-update/versions/{id}/toggle',
  versionDelete: '/app-update/versions/{id}',
  apkInfo: '/app-update/apk-info',
  validateApk: '/app-update/validate',

  // 设备管理和统计 - mall-selfcheck接口
  deviceList: '/mall-selfcheck/app/device/list',
  deviceStatistics: '/mall-selfcheck/app/statistics/device',
  updateLogs: '/mall-selfcheck/app/update/logs',
  updateStatistics: '/mall-selfcheck/app/statistics/update'
}

/**
 * 上传APK文件并创建版本
 */
export function uploadApkAndCreateVersion(formData) {
  return request({
    url: api.versionUpload,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 300000 // 5分钟超时
  })
}

/**
 * 获取版本列表
 */
export function fetchVersionList(params) {
  return request({
    url: api.versionList,
    method: 'get',
    params
  })
}

/**
 * 更新版本信息
 */
export function updateVersion(id, data) {
  return request({
    url: api.versionUpdate.replace('{id}', id),
    method: 'put',
    data
  })
}

/**
 * 激活/停用版本
 */
export function toggleVersionStatus(id, isActive) {
  return request({
    url: api.versionToggle.replace('{id}', id),
    method: 'put',
    params: { isActive }
  })
}

/**
 * 删除版本
 */
export function deleteVersion(id) {
  return request({
    url: api.versionDelete.replace('{id}', id),
    method: 'delete'
  })
}

/**
 * 获取APK文件信息
 */
export function getApkInfo(formData) {
  return request({
    url: api.apkInfo,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 验证APK文件
 */
export function validateApkFile(formData) {
  return request({
    url: api.validateApk,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 获取设备列表
 */
export function fetchDeviceList(params) {
  return request({
    url: api.deviceList,
    method: 'get',
    params
  })
}

/**
 * 获取设备统计信息
 */
export function fetchDeviceStatistics() {
  return request({
    url: api.deviceStatistics,
    method: 'get'
  })
}

/**
 * 获取更新日志
 */
export function fetchUpdateLogs(params) {
  return request({
    url: api.updateLogs,
    method: 'get',
    params
  })
}

/**
 * 获取更新统计信息
 */
export function fetchUpdateStatistics(params) {
  return request({
    url: api.updateStatistics,
    method: 'get',
    params
  })
}
