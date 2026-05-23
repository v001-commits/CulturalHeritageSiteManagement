import request from './request'

// 单文件上传
export function uploadFile(data) {
  return request({
    url: '/file/upload',
    method: 'post',
    data
  })
}

// 多文件上传
export function uploadFiles(data) {
  return request({
    url: '/file/uploads',
    method: 'post',
    data
  })
}

// 获取文件列表
export function getFileList(params) {
  return request({
    url: '/file/list',
    method: 'get',
    params
  })
}

// 根据ID获取文件信息
export function getFileInfo(id) {
  return request({
    url: `/file/${id}`,
    method: 'get'
  })
}

// 根据用户ID获取文件列表
export function getFileListByUserId(userId) {
  return request({
    url: `/file/user/${userId}`,
    method: 'get'
  })
}

// 删除文件
export function deleteFile(id) {
  return request({
    url: `/file/${id}`,
    method: 'delete'
  })
}

// 获取支持的文件类型
export function getSupportedTypes() {
  return request({
    url: '/file/types',
    method: 'get'
  })
}

// 获取上传配置
export function getUploadConfig() {
  return request({
    url: '/file/config',
    method: 'get'
  })
}

// 获取文件预览URL
export function getFilePreviewUrl(id) {
  return `/file/preview/${id}`
}

// 获取文件下载URL
export function getFileDownloadUrl(id) {
  return `/file/download/${id}`
}