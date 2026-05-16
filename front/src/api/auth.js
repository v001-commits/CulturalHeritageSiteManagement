import request from './request'

// 登录接口
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'POST',
    data
  })
}