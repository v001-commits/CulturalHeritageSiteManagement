import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // API基础URL，使用相对路径，通过Vite代理转发
  timeout: 5000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    // 确保headers对象存在
    config.headers = config.headers || {}
    
    // 如果token存在，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 检查请求体是否为FormData
    const isFormData = config.data instanceof FormData
    
    // 对于FormData请求，删除Content-Type让浏览器自动设置boundary
    if (isFormData) {
      delete config.headers['Content-Type']
      console.log('检测到FormData请求，删除Content-Type让浏览器自动处理')
    }
    
    // 日志记录
    console.log('请求配置:', {
      url: config.url,
      method: config.method,
      isFormData: isFormData,
      hasToken: !!token,
      headers: config.headers
    })
    
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 特殊处理blob和arraybuffer类型响应，直接返回原始响应
    if (response.config.responseType === 'blob' || response.config.responseType === 'arraybuffer') {
      console.log('Blob/ArrayBuffer类型响应，直接返回原始数据，大小:', response.data?.size || response.data?.byteLength)
      return response.data
    }
    
    // 获取响应数据
    const res = response.data
    
    console.log('响应数据:', res)
    
    // 处理不同的返回格式
    if (Array.isArray(res)) {
      // 如果直接返回数组（如用户列表），封装成约定格式
      return {
        code: 200,
        msg: 'success',
        data: res
      }
    } else if (typeof res === 'object') {
      // 检查响应码
      if (res.code !== 200) {
        // 401表示未登录或token失效
        if (res.code === 401) {
          // 暂时禁用自动跳转到登录页，改为返回错误信息
          console.error('401错误，但未跳转到登录页')
          // 不要清除token，避免影响后续请求
          // localStorage.removeItem('token')
          // 不要自动跳转到登录页，让调用者处理
          return Promise.reject(new Error(res.msg || '请求失败'))
        }
        // 其他错误，返回错误信息
        return Promise.reject(new Error(res.msg || '请求失败'))
      } else {
        // 请求成功，返回数据
        return res
      }
    } else if (res === true) {
      // 接口返回true表示成功
      return {
        code: 200,
        msg: 'success',
        data: res
      }
    } else if (res === false) {
      // 接口返回false表示失败
      return Promise.reject(new Error('请求失败'))
    } else {
      // 其他类型数据，封装成成功格式
      return {
        code: 200,
        msg: 'success',
        data: res
      }
    }
  },
  error => {
    // 处理响应错误
    console.error('响应错误:', error)
    console.error('错误详情:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    })
    
    // 如果是网络错误或请求超时，返回友好的错误信息
    if (error.code === 'ECONNABORTED' || !error.response) {
      return Promise.reject(new Error('网络错误，请检查连接'))
    }
    
    // 如果是404错误，返回友好的错误信息
    if (error.response.status === 404) {
      return Promise.reject(new Error('请求的资源不存在'))
    }
    
    // 如果是401错误，暂时不自动跳转到登录页
    if (error.response.status === 401) {
      console.error('401错误，但未跳转到登录页')
      // 不要清除token，避免影响后续请求
      // localStorage.removeItem('token')
      // 不要自动跳转到登录页，让调用者处理
      return Promise.reject(new Error(error.response.data?.msg || '请求失败'))
    }
    
    // 其他错误，返回错误信息
    return Promise.reject(new Error(error.response.data?.msg || error.message || '请求失败'))
  }
)

export default service