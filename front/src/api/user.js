import request from './request'

// 查询所有用户
export const getUserList = () => {
  return request({
    url: '/user/list',
    method: 'GET'
  })
}

// 查询单个用户
export const getUserById = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'GET'
  })
}

// 新增用户
export const addUser = (data) => {
  return request({
    url: '/user',
    method: 'POST',
    data
  })
}

// 修改用户
export const updateUser = (data) => {
  return request({
    url: '/user',
    method: 'PUT',
    data
  })
}

// 删除用户
export const deleteUser = (id) => {
  return request({
    url: `/user/${id}`,
    method: 'DELETE'
  })
}