import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

// 响应拦截：统一解包 Result { code, message, data }
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res && res.code === 200) {
      return res.data
    }
    const msg = (res && res.message) || '请求失败'
    return Promise.reject(new Error(msg))
  },
  error => {
    const msg = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export default service
