<template>
  <transition name="notification-fade">
    <div v-if="visible" class="notification" :class="type">
      <div class="notification-content">
        <span class="notification-icon">{{ getIcon() }}</span>
        <span class="notification-text">{{ message }}</span>
      </div>
      <button class="notification-close" @click="close">&times;</button>
    </div>
  </transition>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'success',
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  duration: {
    type: Number,
    default: 3000
  },
  showClose: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['close'])

const visible = ref(true)

const timer = ref(null)

// 关闭通知
const close = () => {
  visible.value = false
  if (timer.value) {
    clearTimeout(timer.value)
    timer.value = null
  }
  // 延迟触发关闭事件，确保动画完成
  setTimeout(() => {
    emit('close')
  }, 300)
}

// 获取图标
const getIcon = () => {
  switch (props.type) {
    case 'success':
      return '✓'
    case 'error':
      return '✕'
    case 'warning':
      return '⚠'
    case 'info':
      return 'ℹ'
    default:
      return ''
  }
}

// 组件挂载后设置自动关闭
onMounted(() => {
  if (props.duration > 0) {
    timer.value = setTimeout(() => {
      close()
    }, props.duration)
  }
})
</script>

<style scoped>
.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 10000;
  min-width: 300px;
  max-width: 500px;
  background-color: #fff;
  border-left: 4px solid;
}

.notification.success {
  border-left-color: #67c23a;
  background-color: #f0f9eb;
  color: #67c23a;
}

.notification.error {
  border-left-color: #f56c6c;
  background-color: #fef0f0;
  color: #f56c6c;
}

.notification.warning {
  border-left-color: #e6a23c;
  background-color: #fdf6ec;
  color: #e6a23c;
}

.notification.info {
  border-left-color: #909399;
  background-color: #f4f4f5;
  color: #909399;
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-icon {
  font-size: 18px;
  font-weight: bold;
}

.notification-text {
  font-size: 14px;
  line-height: 1.4;
}

.notification-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: inherit;
  opacity: 0.7;
  transition: opacity 0.3s;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-close:hover {
  opacity: 1;
}

/* 动画效果 */
.notification-fade-enter-active,
.notification-fade-leave-active {
  transition: all 0.3s ease;
}

.notification-fade-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.notification-fade-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>