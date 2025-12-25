import request from '@/utils/requestUtil'

// 获取客服信息
export function getCustomerServiceInfo() {
  return request({
    method: 'GET',
    url: '/customerService/info'
  })
}
