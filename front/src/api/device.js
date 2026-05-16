import request from './request'

/**
 * 获取设备列表
 */
export const getDeviceList = () => {
  return request({
    url: '/device/list',
    method: 'GET'
  })
}

/**
 * 根据ID获取设备
 */
export const getDeviceById = (id) => {
  return request({
    url: `/device/${id}`,
    method: 'GET'
  })
}

/**
 * 添加设备
 */
export const addDevice = (data) => {
  return request({
    url: '/device',
    method: 'POST',
    data
  })
}

/**
 * 更新设备
 */
export const updateDevice = (id, data) => {
  return request({
    url: `/device/${id}`,
    method: 'PUT',
    data
  })
}

/**
 * 删除设备
 */
export const deleteDevice = (id) => {
  return request({
    url: `/device/${id}`,
    method: 'DELETE'
  })
}

/**
 * 上传设备照片
 */
export const uploadDevicePhoto = (file, deviceId = null) => {
  const formData = new FormData()
  formData.append('file', file)
  if (deviceId) {
    formData.append('deviceId', deviceId)
  }
  return request({
    url: '/device/upload-photo',
    method: 'POST',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}