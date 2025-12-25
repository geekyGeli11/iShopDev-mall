import request from '@/utils/request'

// 获取客服配置
export function getCustomerServiceConfig() {
  return request({
    url: 'api/customerService/get',
    method: 'get'
  })
}

// 更新客服配置
export function updateCustomerServiceConfig(data) {
  return request({
    url: 'api/customerService/update',
    method: 'post',
    data: data
  })
}
