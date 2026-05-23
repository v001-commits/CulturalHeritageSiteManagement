<template>
  <div class="file-upload">
    <!-- 上传区域 -->
    <div 
      class="upload-area" 
      :class="{ 'drag-over': isDragOver }"
      @drop.prevent="handleDrop"
      @dragover.prevent="handleDragOver"
      @dragenter.prevent="handleDragEnter"
      @dragleave.prevent="handleDragLeave"
      @click="triggerFileInput"
    >
      <div class="upload-content">
        <div class="upload-icon">📁</div>
        <div class="upload-text">
          <p class="main-text">{{ uploadText }}</p>
          <p class="sub-text">支持 {{ allowedTypes.join(', ') }} 格式，最大 {{ maxSize }}</p>
        </div>
        <button type="button" class="select-btn">选择文件</button>
      </div>
    </div>

    <!-- 文件输入框 -->
    <input
      ref="fileInput"
      type="file"
      :accept="acceptTypes"
      :multiple="multiple"
      @change="handleFileSelect"
      style="display: none"
    />

    <!-- 上传进度 -->
    <div v-if="uploadProgress > 0 && uploadProgress < 100" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <span class="progress-text">{{ uploadProgress }}%</span>
    </div>

    <!-- 已选择文件列表 -->
    <div v-if="selectedFiles.length > 0" class="selected-files">
      <div class="files-header">
        <span>已选择 {{ selectedFiles.length }} 个文件</span>
        <button @click="clearSelectedFiles" class="clear-btn">清空</button>
      </div>
      <div class="files-list">
        <div v-for="(file, index) in selectedFiles" :key="index" class="file-item">
          <div class="file-info">
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size">{{ formatFileSize(file.size) }}</span>
          </div>
          <button @click="removeFile(index)" class="remove-btn">×</button>
        </div>
      </div>
      <div class="upload-actions">
        <button @click="handleUpload" :disabled="uploading" class="upload-btn">
          {{ uploading ? '上传中...' : '开始上传' }}
        </button>
      </div>
    </div>

    <!-- 上传结果 -->
    <div v-if="uploadedFiles.length > 0" class="uploaded-files">
      <h4>上传成功文件</h4>
      <div class="files-grid">
        <div v-for="file in uploadedFiles" :key="file.id" class="uploaded-file-card">
          <div class="file-preview">
            <img 
              v-if="isImageFile(file.fileType)" 
              :src="previewUrls[file.id] || 'loading...'" 
              :alt="file.originalName" 
              @error="handleImageError(file)"
            />
            <div v-else class="file-icon">{{ getFileIcon(file.fileType) }}</div>
          </div>
          <div class="file-details">
            <div class="file-name">{{ file.originalName }}</div>
            <div class="file-meta">
              <span>{{ formatFileSize(file.fileSize) }}</span>
              <span>{{ formatDate(file.createTime) }}</span>
            </div>
            <div class="file-actions">
                <button @click="handlePreview(file)" class="preview-link">预览</button>
                <button @click="handleDownload(file)" class="download-link">下载</button>
              </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误信息 -->
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { uploadFile, uploadFiles, deleteFile, getSupportedTypes, getUploadConfig, getFilePreviewUrl, getFileDownloadUrl } from '../api/file'
import request from '../api/request'
import notification from '../utils/notification'

// Office 文档渲染库（需要安装：npm install mammoth xlsx）
import mammoth from 'mammoth'
import * as XLSX from 'xlsx'

// Props
const props = defineProps({
  module: {
    type: String,
    default: 'default'
  },
  multiple: {
    type: Boolean,
    default: false
  },
  maxSize: {
    type: String,
    default: '100MB'
  }
})

// Emits
const emit = defineEmits(['upload-success', 'upload-error'])

// 响应式数据
const fileInput = ref(null)
const isDragOver = ref(false)
const selectedFiles = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadedFiles = ref([])
const errorMessage = ref('')
const previewUrls = ref({}) // 用于存储图片预览的blob URLs
// 设置默认支持的文件类型，避免API调用失败时无法使用
const allowedTypes = ref(['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt', 'csv', 'zip', 'rar', 'jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'mp4', 'avi', 'mp3', 'wav'])
const maxFileSize = ref(100 * 1024 * 1024) // 100MB

// 计算属性
const uploadText = computed(() => {
  if (isDragOver.value) {
    return '松开鼠标上传文件'
  }
  return props.multiple ? '拖拽文件到这里或点击选择多个文件' : '拖拽文件到这里或点击选择文件'
})

const acceptTypes = computed(() => {
  return allowedTypes.value.map(type => `.${type}`).join(',')
})

// 生命周期
onMounted(async () => {
  await loadConfig()
})

// 方法
const loadConfig = async () => {
  try {
    const [typesRes, configRes] = await Promise.all([
      getSupportedTypes(),
      getUploadConfig()
    ])
    
    if (typesRes.data && typesRes.data.code === 200 && typesRes.data.data && typesRes.data.data.length > 0) {
      allowedTypes.value = typesRes.data.data
    }
    
    if (configRes.data && configRes.data.code === 200) {
      // 可以根据配置调整最大文件大小等
    }
  } catch (error) {
    console.error('加载配置失败:', error)
    // API调用失败时，使用默认的文件类型列表
    console.log('使用默认支持的文件类型:', allowedTypes.value)
  }
}

const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  addFiles(files)
}

const handleDrop = (event) => {
  isDragOver.value = false
  const files = Array.from(event.dataTransfer.files)
  addFiles(files)
}

const handleDragOver = () => {
  isDragOver.value = true
}

const handleDragEnter = () => {
  isDragOver.value = true
}

const handleDragLeave = () => {
  isDragOver.value = false
}

const addFiles = (files) => {
  errorMessage.value = ''
  
  for (let file of files) {
    // 检查文件大小
    if (file.size > maxFileSize.value) {
      errorMessage.value = `文件 ${file.name} 超过大小限制 ${props.maxSize}`
      continue
    }
    
    // 检查文件类型
    const fileExtension = file.name.split('.').pop().toLowerCase()
    if (!allowedTypes.value.includes(fileExtension)) {
      errorMessage.value = `不支持的文件类型: ${file.name}`
      continue
    }
    
    // 检查是否已存在
    if (!props.multiple && selectedFiles.value.length > 0) {
      selectedFiles.value = [file]
    } else {
      selectedFiles.value.push(file)
    }
  }
}

const removeFile = (index) => {
  selectedFiles.value.splice(index, 1)
}

const clearSelectedFiles = () => {
  selectedFiles.value = []
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const handleUpload = async () => {
  if (selectedFiles.value.length === 0) {
    notification.error('请选择要上传的文件')
    return
  }

  uploading.value = true
  uploadProgress.value = 0
  errorMessage.value = ''

  try {
    const formData = new FormData()
    
    if (props.multiple) {
      selectedFiles.value.forEach(file => {
        formData.append('files', file)
      })
    } else {
      formData.append('file', selectedFiles.value[0])
    }
    
    formData.append('module', props.module)

    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 200)

    const response = props.multiple 
      ? await uploadFiles(formData)
      : await uploadFile(formData)

    clearInterval(progressInterval)
    uploadProgress.value = 100

    if (response.code === 200) {
      const result = response.data
      
      if (props.multiple) {
        uploadedFiles.value = [...uploadedFiles.value, ...result]
        // 为每张图片生成预览URL
        result.forEach(file => {
          if (isImageFile(file.fileType)) {
            generatePreviewUrl(file)
          }
        })
      } else {
        uploadedFiles.value.push(result)
        // 为图片生成预览URL
        if (isImageFile(result.fileType)) {
          generatePreviewUrl(result)
        }
      }
      
      clearSelectedFiles()
      emit('upload-success', result)
      notification.success(`文件上传成功`)
    } else {
      throw new Error(response.msg || '上传失败')
    }
  } catch (error) {
    errorMessage.value = error.message || '上传失败'
    emit('upload-error', error)
    notification.error(errorMessage.value)
  } finally {
    uploading.value = false
    setTimeout(() => {
      uploadProgress.value = 0
    }, 1000)
  }
}

// 生成图片预览URL
const generatePreviewUrl = async (file) => {
  try {
    // 使用axios发送带token的请求获取文件流
    const response = await request({
      url: file.fileUrl,
      method: 'get',
      responseType: 'blob',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    // 创建blob URL
    const blobUrl = URL.createObjectURL(response)
    
    // 存储预览URL
    previewUrls.value[file.id] = blobUrl
  } catch (error) {
    console.error('生成预览URL失败:', error)
    // 生成预览URL失败时，使用默认图片或不显示
    previewUrls.value[file.id] = ''
  }
}

// 处理图片加载错误
const handleImageError = (file) => {
  console.error('图片加载失败:', file.originalName)
  // 可以设置一个默认图片或者清空src
  previewUrls.value[file.id] = ''
}

const deleteUploadedFile = async (id) => {
  if (confirm('确定要删除这个文件吗？')) {
    try {
      const response = await deleteFile(id)
      if (response.data.code === 200) {
        // 清理预览URL
        if (previewUrls.value[id]) {
          URL.revokeObjectURL(previewUrls.value[id])
          delete previewUrls.value[id]
        }
        // 从列表中移除文件
        uploadedFiles.value = uploadedFiles.value.filter(file => file.id !== id)
        notification.success('文件删除成功')
      } else {
        throw new Error(response.data.msg || '删除失败')
      }
    } catch (error) {
      notification.error(error.message || '删除失败')
    }
  }
}

// 处理文件预览
const handlePreview = async (file) => {
  try {
    const fileType = file.fileType?.toLowerCase()

    // Office 文档类型（使用前端渲染）
    const officePreviewTypes = ['doc', 'docx', 'xls', 'xlsx']

    if (officePreviewTypes.includes(fileType)) {
      // Office 文档使用前端渲染
      await handleOfficePreview(file)
      return
    }

    // PPT 暂不支持预览
    if (['ppt', 'pptx'].includes(fileType)) {
      notification.warning('PPT文档暂不支持在线预览，请下载后查看')
      return
    }

    // 浏览器原生支持预览的类型
    const browserPreviewTypes = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'pdf', 'txt', 'csv', 'mp4', 'mp3', 'wav', 'avi', 'mov']

    if (!browserPreviewTypes.includes(fileType)) {
      notification.warning('该文件类型无法在浏览器中直接预览，建议下载后查看')
      return
    }

    notification.info('正在加载预览文件...')

    const previewUrl = getFilePreviewUrl(file.id)

    // 使用axios发送带token的请求获取文件流 - 不单独设置headers，让拦截器自动处理
    const response = await request({
      url: previewUrl,
      method: 'get',
      responseType: 'blob'
    })

    // 获取文件的 MIME type
    const mimeType = getMimeType(file.fileType)

    // 创建带 MIME type 的 blob
    const blob = new Blob([response], { type: mimeType })
    const blobUrl = URL.createObjectURL(blob)

    // 对于文本类型（txt, csv），直接在窗口中显示内容
    if (['txt', 'csv'].includes(fileType)) {
      const previewWindow = window.open('', '_blank')
      if (previewWindow) {
        const reader = new FileReader()
        reader.onload = (e) => {
          previewWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <title>${file.originalName}</title>
              <style>
                body { font-family: monospace; padding: 20px; white-space: pre-wrap; word-wrap: break-word; }
              </style>
            </head>
            <body>${e.target.result}</body>
            </html>
          `)
          previewWindow.document.close()
        }
        reader.readAsText(blob)
        notification.success('文件预览成功')
      } else {
        notification.warning('预览窗口被拦截，请允许弹窗')
      }
      return
    }

    // 对于图片、PDF、视频、音频，使用新窗口打开
    const previewWindow = window.open('', '_blank')
    if (!previewWindow) {
      notification.warning('预览窗口被拦截，请允许弹窗')
      return
    }

    // 根据文件类型生成预览内容
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(fileType)) {
      previewWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <title>${file.originalName}</title>
          <style>
            body { margin: 0; display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f0f0f0; }
            img { max-width: 100%; max-height: 100vh; object-fit: contain; }
          </style>
        </head>
        <body>
          <img src="${blobUrl}" alt="${file.originalName}">
        </body>
        </html>
      `)
    } else if (fileType === 'pdf') {
      previewWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <title>${file.originalName}</title>
          <style>
            body { margin: 0; }
            iframe { width: 100vw; height: 100vh; border: none; }
          </style>
        </head>
        <body>
          <iframe src="${blobUrl}" type="application/pdf"></iframe>
        </body>
        </html>
      `)
    } else if (['mp4', 'avi', 'mov'].includes(fileType)) {
      previewWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <title>${file.originalName}</title>
          <style>
            body { margin: 0; display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #000; }
            video { max-width: 100%; max-height: 100vh; }
          </style>
        </head>
        <body>
          <video controls autoplay>
            <source src="${blobUrl}" type="${mimeType}">
            您的浏览器不支持视频播放
          </video>
        </body>
        </html>
      `)
    } else if (['mp3', 'wav'].includes(fileType)) {
      previewWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8">
          <title>${file.originalName}</title>
          <style>
            body { margin: 0; display: flex; justify-content: center; align-items: center; min-height: 100vh; background: #f5f5f5; }
            .player { text-align: center; }
            h2 { color: #333; margin-bottom: 20px; }
            audio { width: 300px; }
          </style>
        </head>
        <body>
          <div class="player">
            <h2>🎵 ${file.originalName}</h2>
            <audio controls autoplay>
              <source src="${blobUrl}" type="${mimeType}">
              您的浏览器不支持音频播放
            </audio>
          </div>
        </body>
        </html>
      `)
    }

    previewWindow.document.close()
    notification.success('文件预览成功')
  } catch (error) {
    console.error('文件预览失败:', error)
    notification.error('文件预览失败: ' + (error.message || '未知错误'))
  }
}

// 处理 Office 文档预览（本地渲染方案）
const handleOfficePreview = async (file) => {
  console.log('开始处理Office文档预览，文件:', file)

  try {
    const fileType = file.fileType?.toLowerCase()
    console.log('文件类型:', fileType)
    notification.info('正在加载Office文档预览...')

    // 获取文件内容 - 注意：不单独设置headers，让拦截器自动处理token
    const previewUrl = getFilePreviewUrl(file.id)
    console.log('预览URL:', previewUrl)

    const response = await request({
      url: previewUrl,
      method: 'get',
      responseType: 'arraybuffer'
    })

    console.log('Office文档数据获取成功，类型:', fileType, '数据大小:', response?.byteLength)

    if (!response || response.byteLength === 0) {
      throw new Error('获取文件内容失败，数据为空')
    }

    let htmlContent = ''

    if (fileType === 'doc' || fileType === 'docx') {
      console.log('开始渲染Word文档...')
      // 使用 mammoth 渲染 Word 文档
      htmlContent = await renderWordDocument(response)
      console.log('Word文档渲染完成，内容长度:', htmlContent.length)
    } else if (fileType === 'xls' || fileType === 'xlsx') {
      console.log('开始渲染Excel文档...')
      // 使用 SheetJS 渲染 Excel 文档
      htmlContent = renderExcelDocument(response)
      console.log('Excel文档渲染完成，内容长度:', htmlContent.length)
    } else if (fileType === 'ppt' || fileType === 'pptx') {
      // PPT 暂不支持，提示下载
      notification.warning('PPT文档暂不支持在线预览，请下载后查看')
      return
    }

    console.log('准备打开预览窗口...')

    // 在新窗口中显示渲染结果
    const previewWindow = window.open('', '_blank')
    if (!previewWindow) {
      notification.warning('预览窗口被拦截，请允许弹窗')
      return
    }

    console.log('预览窗口已打开，开始写入内容...')

    previewWindow.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${file.originalName} - 预览</title>
        <style>
          * { box-sizing: border-box; margin: 0; padding: 0; }
          body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: #f5f5f5;
            padding: 20px;
          }
          .header {
            background: #fff;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
          }
          .header h1 { font-size: 18px; color: #333; }
          .content {
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            min-height: 400px;
            overflow-x: auto;
          }
          table { border-collapse: collapse; width: 100%; }
          th, td { border: 1px solid #ddd; padding: 8px 12px; text-align: left; }
          th { background: #f0f0f0; font-weight: 600; }
          tr:nth-child(even) { background: #fafafa; }
          tr:hover { background: #f5f5f5; }
          img { max-width: 100%; height: auto; }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>📄 ${file.originalName}</h1>
        </div>
        <div class="content">
          ${htmlContent}
        </div>
      </body>
      </html>
    `)
    previewWindow.document.close()

    console.log('预览内容写入完成')
    notification.success('文档预览成功')
  } catch (error) {
    console.error('Office文档预览失败:', error)
    notification.error('Office文档预览失败: ' + (error.message || '未知错误'))
  }
}

// 渲染 Word 文档
const renderWordDocument = async (arrayBuffer) => {
  try {
    const result = await mammoth.convertToHtml({ arrayBuffer: arrayBuffer })
    return result.value || '<p style="color: #999;">文档内容为空</p>'
  } catch (error) {
    console.error('Word文档渲染失败:', error)
    throw new Error('Word文档解析失败')
  }
}

// 渲染 Excel 文档
const renderExcelDocument = (arrayBuffer) => {
  try {
    const workbook = XLSX.read(arrayBuffer, { type: 'array' })
    let html = ''

    // 遍历所有工作表
    workbook.SheetNames.forEach((sheetName, index) => {
      if (workbook.SheetNames.length > 1) {
        html += `<h3 style="margin: ${index > 0 ? '30px' : '0'} 0 15px 0; color: #333;">${sheetName}</h3>`
      }
      const sheet = workbook.Sheets[sheetName]
      const htmlTable = XLSX.utils.sheet_to_html(sheet, { editable: false })
      // 清理 XLSX 生成的多余样式
      html += htmlTable.replace(/<table[^>]*>/, '<table>')
    })

    return html || '<p style="color: #999;">文档内容为空</p>'
  } catch (error) {
    console.error('Excel文档渲染失败:', error)
    throw new Error('Excel文档解析失败')
  }
}

// 处理文件下载
const handleDownload = async (file) => {
  console.log('开始处理文件下载，文件:', file)

  try {
    notification.info('正在下载文件...')

    const downloadUrl = getFileDownloadUrl(file.id)
    console.log('下载URL:', downloadUrl)

    // 使用axios发送带token的请求获取文件流 - 不单独设置headers，让拦截器自动处理
    const response = await request({
      url: downloadUrl,
      method: 'get',
      responseType: 'blob'
    })

    console.log('文件数据获取成功，大小:', response?.size, '类型:', response?.type)

    if (!response || response.size === 0) {
      throw new Error('获取文件内容失败，数据为空')
    }

    // 创建下载链接
    const blob = new Blob([response], { type: getMimeType(file.fileType) })
    const blobUrl = URL.createObjectURL(blob)
    console.log('Blob URL创建成功:', blobUrl)

    const link = document.createElement('a')
    link.href = blobUrl
    link.download = file.originalName
    link.style.display = 'none'
    document.body.appendChild(link)
    console.log('下载链接已创建，准备触发点击...')

    link.click()
    console.log('点击已触发')

    // 延迟释放资源，确保下载完成
    setTimeout(() => {
      document.body.removeChild(link)
      URL.revokeObjectURL(blobUrl)
      console.log('资源已释放')
      notification.success('文件下载完成')
    }, 1000)
  } catch (error) {
    console.error('文件下载失败:', error)
    notification.error('文件下载失败: ' + (error.message || '未知错误'))
  }
}

// 获取文件的 MIME type
const getMimeType = (fileType) => {
  const mimeTypes = {
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'png': 'image/png',
    'gif': 'image/gif',
    'bmp': 'image/bmp',
    'webp': 'image/webp',
    'pdf': 'application/pdf',
    'txt': 'text/plain',
    'csv': 'text/csv',
    'doc': 'application/msword',
    'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'xls': 'application/vnd.ms-excel',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'ppt': 'application/vnd.ms-powerpoint',
    'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'mp4': 'video/mp4',
    'avi': 'video/x-msvideo',
    'mov': 'video/quicktime',
    'wmv': 'video/x-ms-wmv',
    'mp3': 'audio/mpeg',
    'wav': 'audio/wav',
    'zip': 'application/zip',
    'rar': 'application/x-rar-compressed'
  }
  return mimeTypes[fileType?.toLowerCase()] || 'application/octet-stream'
}

const isImageFile = (fileType) => {
  const imageTypes = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
  return imageTypes.includes(fileType.toLowerCase())
}

const getFileIcon = (fileType) => {
  const icons = {
    'pdf': '📄',
    'doc': '📝',
    'docx': '📝',
    'xls': '📊',
    'xlsx': '📊',
    'ppt': '📽️',
    'pptx': '📽️',
    'txt': '📝',
    'csv': '📋',
    'zip': '📦',
    'rar': '📦',
    'mp4': '🎬',
    'avi': '🎬',
    'mp3': '🎵',
    'wav': '🎵'
  }
  return icons[fileType.toLowerCase()] || '📁'
}

const formatFileSize = (size) => {
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
  if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(1) + ' MB'
  return (size / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}
</script>

<style scoped>
.file-upload {
  max-width: 800px;
  margin: 0 auto;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fafafa;
  margin-bottom: 20px;
}

.upload-area:hover,
.upload-area.drag-over {
  border-color: #667eea;
  background-color: #f0f5ff;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.upload-icon {
  font-size: 48px;
}

.upload-text .main-text {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
}

.upload-text .sub-text {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.select-btn {
  padding: 10px 20px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.select-btn:hover {
  background-color: #5568d3;
}

.upload-progress {
  margin: 20px 0;
  display: flex;
  align-items: center;
  gap: 15px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #667eea;
  transition: width 0.3s;
}

.progress-text {
  font-size: 14px;
  color: #666;
  min-width: 40px;
}

.selected-files {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.files-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.clear-btn {
  padding: 6px 12px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.clear-btn:hover {
  background-color: #d32f2f;
}

.files-list {
  margin-bottom: 20px;
}

.file-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.file-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.file-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.file-size {
  font-size: 12px;
  color: #666;
}

.remove-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  color: #f44336;
}

.upload-actions {
  text-align: center;
}

.upload-btn {
  padding: 12px 30px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.upload-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.upload-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.uploaded-files h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.files-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.uploaded-file-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.file-preview {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
}

.file-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.file-icon {
  font-size: 40px;
}

.file-details {
  padding: 15px;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
  word-break: break-all;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 12px;
}

.file-actions {
  display: flex;
  gap: 10px;
}

.preview-link, .delete-link, .download-link {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  text-decoration: none;
  cursor: pointer;
  border: none;
}

.preview-link {
  background-color: #667eea;
  color: white;
}

.preview-link:hover {
  background-color: #5568d3;
}

.download-link {
  background-color: #4caf50;
  color: white;
}

.download-link:hover {
  background-color: #45a049;
}

.delete-link {
  background-color: #f44336;
  color: white;
}

.delete-link:hover {
  background-color: #d32f2f;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 12px;
  border-radius: 4px;
  margin: 15px 0;
  border-left: 4px solid #f44336;
}
</style>