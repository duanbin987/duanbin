import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({ baseURL: '/api', timeout: 15000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function resolveErrorMessage(err) {
  const data = err.response?.data
  if (typeof data === 'string' && data) return data
  if (data?.message) return data.message
  if (data?.error) return data.error
  if (err.code === 'ECONNABORTED') return '请求超时，请检查后端是否正常运行'
  if (!err.response) {
    return '无法连接后端 API，请确认：1) 使用 npm run dev 启动前端；2) Spring Boot 已在 8080 端口运行'
  }
  return `请求失败（HTTP ${err.response.status}）`
}

request.interceptors.response.use(
  res => {
    const data = res.data
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(data)
    }
    return data
  },
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    ElMessage.error(resolveErrorMessage(err))
    return Promise.reject(err)
  }
)

export default request
