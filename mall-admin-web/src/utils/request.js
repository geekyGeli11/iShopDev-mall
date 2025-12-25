import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '../store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API, // api的base_url
  timeout: 30000, // 增加超时时间，支持大数据传输
  maxContentLength: 50 * 1024 * 1024, // 50MB - 支持大的base64数据
  maxBodyLength: 50 * 1024 * 1024 // 50MB - 支持大的base64数据
})

// request拦截器
service.interceptors.request.use(config => {
  if (store.getters.token) {
    config.headers['Authorization'] = getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  return config
}, error => {
  // Do something with request error
  console.log(error) // for debug
  Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
  response => {
    // 如果是文件流（blob/arraybuffer），直接返回，不做业务 code 判断
    const responseType = response.config && response.config.responseType;
    if (responseType === 'blob' || responseType === 'arraybuffer') {
      return response; // 保留headers以便前端获取文件名
    }

    /**
    * code为非200是抛错 可结合自己业务进行修改
    */
    const res = response.data

    // 检查响应数据是否包含大的base64数据，如果是则记录日志但不截断
    if (res && res.data && typeof res.data === 'object') {
      Object.keys(res.data).forEach(key => {
        if (typeof res.data[key] === 'string' && res.data[key].startsWith('data:image/')) {
          console.log(`检测到base64图片数据: ${key}, 长度: ${res.data[key].length}`);
        }
      });
    }

    if (res.code !== 200) {
      Message({
        message: res.message,
        type: 'error',
        duration: 3 * 1000
      })

      // 401:未登录;
      if (res.code === 401) {
        MessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确定登出', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('FedLogOut').then(() => {
            location.reload()// 为了重新实例化vue-router对象 避免bug
          })
        })
      }
      return Promise.reject('error')
    } else {
      return response.data
    }
  },
  error => {
    console.log('err' + error)// for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 3 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
