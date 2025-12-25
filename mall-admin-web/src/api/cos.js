import request from '@/utils/request'

/**
 * 获取腾讯云COS临时密钥
 * @returns {Promise} - 返回临时密钥信息
 */
export function getStsToken() {
  return request({
    url: '/tencent/cos/sts',
    method: 'get'
  })
}

/**
 * 获取上传策略
 * @returns {Promise} - 返回上传策略
 */
export function getPolicy() {
  return request({
    url: '/tencent/cos/policy',
    method: 'get'
  })
}

/**
 * 上传文件到COS(通过服务端中转)
 * @param {Object} data - 文件数据
 * @returns {Promise} - 返回上传结果
 */
export function upload(data) {
  return request({
    url: '/tencent/cos/upload',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 