// 简化的通知实现，直接操作DOM
const showNotification = (options) => {
  const {
    message,
    type = 'success',
    duration = 3000,
    showClose = true
  } = options
  
  // 创建通知容器
  const notificationEl = document.createElement('div')
  notificationEl.className = `notification ${type}`
  
  // 设置样式
  Object.assign(notificationEl.style, {
    position: 'fixed',
    top: '20px',
    right: '20px',
    padding: '12px 20px',
    borderRadius: '6px',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    zIndex: '10000',
    minWidth: '300px',
    maxWidth: '500px',
    backgroundColor: '#fff',
    borderLeft: '4px solid',
    opacity: '0',
    transform: 'translateX(100%)',
    transition: 'all 0.3s ease'
  })
  
  // 设置不同类型的样式
  switch (type) {
    case 'success':
      notificationEl.style.borderLeftColor = '#67c23a'
      notificationEl.style.backgroundColor = '#f0f9eb'
      notificationEl.style.color = '#67c23a'
      break
    case 'error':
      notificationEl.style.borderLeftColor = '#f56c6c'
      notificationEl.style.backgroundColor = '#fef0f0'
      notificationEl.style.color = '#f56c6c'
      break
    case 'warning':
      notificationEl.style.borderLeftColor = '#e6a23c'
      notificationEl.style.backgroundColor = '#fdf6ec'
      notificationEl.style.color = '#e6a23c'
      break
    case 'info':
      notificationEl.style.borderLeftColor = '#909399'
      notificationEl.style.backgroundColor = '#f4f4f5'
      notificationEl.style.color = '#909399'
      break
  }
  
  // 创建图标
  const iconEl = document.createElement('span')
  iconEl.style.marginRight = '10px'
  iconEl.style.fontSize = '18px'
  iconEl.style.fontWeight = 'bold'
  
  switch (type) {
    case 'success':
      iconEl.textContent = '✓'
      break
    case 'error':
      iconEl.textContent = '✕'
      break
    case 'warning':
      iconEl.textContent = '⚠'
      break
    case 'info':
      iconEl.textContent = 'ℹ'
      break
  }
  
  // 创建文本
  const textEl = document.createElement('span')
  textEl.textContent = message
  textEl.style.flex = '1'
  textEl.style.fontSize = '14px'
  textEl.style.lineHeight = '1.4'
  
  // 创建关闭按钮
  const closeEl = document.createElement('button')
  closeEl.textContent = '×'
  closeEl.style.background = 'none'
  closeEl.style.border = 'none'
  closeEl.style.fontSize = '18px'
  closeEl.style.cursor = 'pointer'
  closeEl.style.color = 'inherit'
  closeEl.style.opacity = '0.7'
  closeEl.style.transition = 'opacity 0.3s'
  closeEl.style.padding = '0'
  closeEl.style.width = '20px'
  closeEl.style.height = '20px'
  closeEl.style.display = 'flex'
  closeEl.style.alignItems = 'center'
  closeEl.style.justifyContent = 'center'
  
  closeEl.addEventListener('mouseenter', () => {
    closeEl.style.opacity = '1'
  })
  
  closeEl.addEventListener('mouseleave', () => {
    closeEl.style.opacity = '0.7'
  })
  
  // 组装通知元素
  notificationEl.appendChild(iconEl)
  notificationEl.appendChild(textEl)
  if (showClose) {
    notificationEl.appendChild(closeEl)
  }
  
  // 添加到页面
  document.body.appendChild(notificationEl)
  
  // 显示动画
  setTimeout(() => {
    notificationEl.style.opacity = '1'
    notificationEl.style.transform = 'translateX(0)'
  }, 10)
  
  // 自动关闭
  let timer = null
  if (duration > 0) {
    timer = setTimeout(() => {
      closeNotification()
    }, duration)
  }
  
  // 关闭通知的函数
  function closeNotification() {
    notificationEl.style.opacity = '0'
    notificationEl.style.transform = 'translateX(100%)'
    
    setTimeout(() => {
      if (notificationEl.parentNode) {
        notificationEl.parentNode.removeChild(notificationEl)
      }
    }, 300)
    
    if (timer) {
      clearTimeout(timer)
    }
  }
  
  // 手动关闭
  closeEl.addEventListener('click', closeNotification)
  
  return {
    close: closeNotification
  }
}

// 通知方法
const notification = {
  success(message, duration) {
    return showNotification({ message, type: 'success', duration })
  },
  error(message, duration) {
    return showNotification({ message, type: 'error', duration })
  },
  warning(message, duration) {
    return showNotification({ message, type: 'warning', duration })
  },
  info(message, duration) {
    return showNotification({ message, type: 'info', duration })
  }
}

export default notification