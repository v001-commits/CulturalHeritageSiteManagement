import request from './request'

/**
 * 获取区域列表
 */
export const getAreaList = () => {
  return request({
    url: '/area/list',
    method: 'GET'
  })
}

/**
 * 获取当前用户有权限访问的区域列表
 */
export const getUserAuthorizedAreas = () => {
  return request({
    url: '/area/user/authorized',
    method: 'GET'
  })
}

/**
 * 根据ID获取区域
 */
export const getAreaById = (id) => {
  return request({
    url: `/area/${id}`,
    method: 'GET'
  })
}

/**
 * 添加区域
 */
export const addArea = (data) => {
  return request({
    url: '/area',
    method: 'POST',
    data
  })
}

/**
 * 更新区域
 */
export const updateArea = (id, data) => {
  return request({
    url: `/area/${id}`,
    method: 'PUT',
    data
  })
}

/**
 * 删除区域
 */
export const deleteArea = (id) => {
  return request({
    url: `/area/${id}`,
    method: 'DELETE'
  })
}

/**
 * 获取所有权限
 */
export const getAllPermissions = () => {
  return request({
    url: '/area/permission/all',
    method: 'GET'
  })
}

/**
 * 获取用户权限
 */
export const getUserPermissions = (userId) => {
  return request({
    url: `/area/permission/user/${userId}`,
    method: 'GET'
  })
}

/**
 * 获取区域权限
 */
export const getAreaPermissions = (areaId) => {
  return request({
    url: `/area/permission/area/${areaId}`,
    method: 'GET'
  })
}

/**
 * 添加权限
 */
export const addPermission = (data) => {
  return request({
    url: '/area/permission',
    method: 'POST',
    data
  })
}

/**
 * 删除权限
 */
export const deletePermission = (id) => {
  return request({
    url: `/area/permission/${id}`,
    method: 'DELETE'
  })
}

/**
 * 批量设置用户权限
 */
export const batchSetPermissions = (data) => {
  return request({
    url: '/area/permission/batch',
    method: 'POST',
    data
  })
}
