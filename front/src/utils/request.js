import axios from 'axios'
import { ElMessage } from 'element-plus'
import auth from '@/services/auth'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 15000, // 增加超时时长到 15 秒
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = auth.getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果返回的不是 JSON 格式，直接返回
    if (typeof response.data !== 'object') {
      return response
    }
    // 检查业务层返回的 code: 401（认证失败）
    if (response.data && response.data.code === 401) {
      // 清除 token 并跳转到登录页
      auth.removeToken()
      ElMessage.error(response.data.message || '登录已过期，请重新登录')
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)
      return Promise.reject(new Error(response.data.message))
    }
    return response.data
  },
  error => {
    console.error('Response error:', error)

    // 处理 CORS 错误
    if (error.message.includes('CORS') || error.message.includes('cross-origin')) {
      console.error('CORS error:', error)
      ElMessage.error('跨域请求被阻止，请检查后端 CORS 配置')
      return Promise.reject(new Error('跨域请求被阻止，请检查后端 CORS 配置'))
    }

    // 处理 HTTP 401 错误（真正的认证失败，如 token 无效或缺失）
    if (error.response && error.response.status === 401) {
      // 清除 token 并跳转到登录页
      auth.removeToken()
      ElMessage.error('登录已失效，请重新登录')
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)
      return Promise.reject(new Error('登录已失效'))
    }

    // 统一错误处理
    const errorMessage = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(errorMessage)
    return Promise.reject(new Error(errorMessage))
  }
)

export default service