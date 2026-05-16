<template>
  <div class="login-container">
    <!-- 左侧：系统介绍 -->
    <div class="login-left">
      <div class="left-content">
        <div class="logo-section">
          <div class="logo-icon">🏛️</div>
          <h1 class="system-title">文化遗产地环境监测系统</h1>
          <p class="system-subtitle">Cultural Heritage Environmental Monitoring System</p>
        </div>
        
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon">🌍</span>
            <div class="feature-text">
              <h3>三维可视化监测</h3>
              <p>实时监控文化遗产地环境状况</p>
            </div>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📊</span>
            <div class="feature-text">
              <h3>智能数据分析</h3>
              <p>多维度环境数据统计与预警</p>
            </div>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔔</span>
            <div class="feature-text">
              <h3>风险预警推送</h3>
              <p>及时发现并处理环境异常</p>
            </div>
          </div>
        </div>
        
        <div class="copyright">
          <p>© 2026 文化遗产环境监测系统</p>
          <p>守护历史，传承未来</p>
        </div>
      </div>
    </div>

    <!-- 右侧：登录表单 -->
    <div class="login-right">
      <div class="login-card">
        <div class="card-header">
          <h2>欢迎登录</h2>
          <p>Welcome Back</p>
        </div>

        <div class="login-tabs">
          <button :class="['tab-btn', { active: loginType === 'password' }]" @click="loginType = 'password'">
            <span class="tab-icon">👤</span>
            <span>账号登录</span>
          </button>
          <button 
            :class="['tab-btn', { active: loginType === 'phone' }]" 
            @click="loginType = 'phone'" 
            v-if="showPhoneLoginTab"
            :title="!canUsePhoneLogin ? '仅监测人员可使用手机登录' : ''"
          >
            <span class="tab-icon">📱</span>
            <span>手机登录</span>
          </button>
        </div>

        <form v-if="loginType === 'password'" @submit.prevent="handleLogin" class="login-form">
          <div class="input-group">
            <span class="input-icon">👤</span>
            <input 
              type="text" 
              v-model="loginForm.username" 
              @blur="checkPhoneLoginAvailability"
              placeholder="请输入用户名或手机号（监测人员）" 
              required 
            />
          </div>
          <div class="input-group">
            <span class="input-icon">🔒</span>
            <input type="password" v-model="loginForm.password" placeholder="请输入密码" required />
          </div>
          <div v-if="loginHint" class="login-hint">
            {{ loginHint }}
          </div>
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </form>

        <form v-else @submit.prevent="handlePhoneLogin" class="login-form">
          <div class="input-group">
            <span class="input-icon">📱</span>
            <input type="tel" v-model="phoneForm.phone" placeholder="请输入手机号（仅限监测人员）" required />
          </div>
          
          <!-- 图形验证码 -->
          <div class="captcha-group">
            <div class="input-group captcha-input">
              <span class="input-icon">🔐</span>
              <input 
                type="text" 
                v-model="phoneForm.captcha" 
                placeholder="请输入图形验证码" 
                maxlength="4" 
                required 
              />
            </div>
            <div class="captcha-display" @click="refreshCaptcha" title="点击刷新验证码">
              <span class="captcha-text">{{ displayCaptcha }}</span>
              <span class="refresh-icon">🔄</span>
            </div>
          </div>
          
          <button type="submit" class="submit-btn" :disabled="loading">
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </form>

        <div v-if="errorMsg" class="error-tip">{{ errorMsg }}</div>
        
        <div class="login-tips">
          <p>💡 提示：</p>
          <p>• 监测人员可使用账号密码或手机号+图形验证码登录</p>
          <p>• 其他人员仅支持账号密码登录</p>
          <p>• 点击图形验证码可刷新</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import request from '../api/request'

const router = useRouter()

const loginType = ref('password')
const loginForm = ref({ username: '', password: '' })
const phoneForm = ref({ phone: '', captcha: '' })  // 只保留图形验证码
const loading = ref(false)
const errorMsg = ref('')
const canUsePhoneLogin = ref(false)
const loginHint = ref('')
const displayCaptcha = ref('????')  // 显示的图形验证码
const actualCaptcha = ref('')  // 实际的验证码值

// 是否显示手机登录标签
const showPhoneLoginTab = computed(() => {
  return canUsePhoneLogin.value || loginType.value === 'phone'
})

// 刷新图形验证码
const refreshCaptcha = async () => {
  try {
    const response = await request.get('/captcha/generate')
    if (response.code === 200) {
      actualCaptcha.value = response.data
      displayCaptcha.value = response.data
    }
  } catch (error) {
    console.error('获取图形验证码失败:', error)
    displayCaptcha.value = '????'
  }
}

// 监听登录类型切换，自动刷新验证码
watch(loginType, (newType) => {
  if (newType === 'phone') {
    refreshCaptcha()
  }
})

// 检查用户是否可以使用手机登录
const checkPhoneLoginAvailability = async () => {
  if (!loginForm.value.username) {
    canUsePhoneLogin.value = false
    loginHint.value = ''
    return
  }
  
  try {
    const response = await request.get('/auth/check-phone-login', {
      params: { identifier: loginForm.value.username }
    })
    
    if (response.code === 200) {
      canUsePhoneLogin.value = response.data
      if (canUsePhoneLogin.value) {
        loginHint.value = '✓ 您是监测人员，可以使用手机验证码登录'
        // 主动刷新验证码，以便用户切换到手机登录时可以看到
        refreshCaptcha()
      } else {
        loginHint.value = ''
      }
    }
  } catch (error) {
    console.error('检查手机登录权限失败:', error)
    canUsePhoneLogin.value = false
    loginHint.value = ''
  }
}

const handleLogin = async () => {
  try {
    loading.value = true
    errorMsg.value = ''
    console.log('开始登录，请求数据:', loginForm.value)
    const response = await request.post('/auth/login', loginForm.value)
    
    console.log('登录响应:', response)
    
    if (response.code === 200 && response.data) {
      const { token, user } = response.data
      console.log('提取的token:', token)
      console.log('提取的user:', user)
      
      if (!token || !user) {
        console.error('token或user为空！')
        errorMsg.value = '登录响应数据格式错误'
        return
      }
      
      localStorage.setItem('token', token)
      localStorage.setItem('role', user.role)
      localStorage.setItem('userInfo', JSON.stringify(user))
      
      console.log('localStorage已保存，准备跳转到/home')
      
      await router.replace('/home')
      console.log('路由跳转完成')
    } else {
      console.error('登录失败，响应:', response)
      errorMsg.value = response.msg || '登录失败'
    }
  } catch (error) {
    console.error('登录错误:', error)
    errorMsg.value = error.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}

const handlePhoneLogin = async () => {
  // 验证图形验证码
  if (!phoneForm.value.captcha) {
    errorMsg.value = '请输入图形验证码'
    return
  }
  
  if (phoneForm.value.captcha.length !== 4) {
    errorMsg.value = '图形验证码必须是4位数字'
    return
  }
  
  try {
    loading.value = true
    errorMsg.value = ''
    const response = await request.post('/auth/phone-login', phoneForm.value)
    
    if (response.code === 200 && response.data) {
      const { token, user } = response.data
      localStorage.setItem('token', token)
      localStorage.setItem('role', user.role)
      localStorage.setItem('userInfo', JSON.stringify(user))
      await router.replace('/home')
    } else {
      errorMsg.value = response.msg || '登录失败'
      // 如果是验证码错误，刷新验证码
      if (response.msg && response.msg.includes('验证码')) {
        phoneForm.value.captcha = ''
        refreshCaptcha()
      }
    }
  } catch (error) {
    console.error('手机登录错误:', error)
    errorMsg.value = error.message || '登录失败，请检查手机号和验证码'
    // 刷新验证码
    phoneForm.value.captcha = ''
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 删除sendCode函数，不再需要
</script>

<style>
/* 确保html和body占满全屏 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style>

<style scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
}

/* ========== 左侧：系统介绍 ========== */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #1F4E5F 0%, #2A6577 50%, #C8A96B 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 800"><defs><pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse"><path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="1"/></pattern></defs><rect width="100%" height="100%" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.login-left::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(200, 169, 107, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  top: -100px;
  right: -100px;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-30px, 30px); }
}

.left-content {
  position: relative;
  z-index: 10;
  max-width: 500px;
  animation: slideInLeft 0.8s ease-out;
}

@keyframes slideInLeft {
  from { opacity: 0; transform: translateX(-50px); }
  to { opacity: 1; transform: translateX(0); }
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
}

.logo-icon {
  font-size: 80px;
  margin-bottom: 20px;
  animation: bounce 2s infinite;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.system-title {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  letter-spacing: 2px;
}

.system-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
  margin-bottom: 60px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(10px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.feature-icon {
  font-size: 40px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.feature-text h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: white;
}

.feature-text p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
}

.copyright {
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;
  line-height: 1.8;
}

.copyright p {
  margin: 0;
}

/* ========== 右侧：登录表单 ========== */
.login-right {
  flex: 0 0 500px;
  background: #F4F7F9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: white;
  padding: 45px 40px;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(31, 78, 95, 0.15);
  animation: slideInRight 0.8s ease-out;
  border: 1px solid rgba(200, 169, 107, 0.2);
}

@keyframes slideInRight {
  from { opacity: 0; transform: translateX(50px); }
  to { opacity: 1; transform: translateX(0); }
}

.card-header {
  text-align: center;
  margin-bottom: 35px;
}

.card-header h2 {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #1F4E5F, #C8A96B);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0 0 8px 0;
}

.card-header p {
  font-size: 13px;
  color: #94A3B8;
  margin: 0;
  letter-spacing: 1px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-left {
    display: none;
  }
  
  .login-right {
    flex: 1;
  }
}

@media (max-width: 640px) {
  .login-right {
    padding: 20px;
  }
  
  .login-card {
    padding: 30px 25px;
  }
}

.login-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  border: 2px solid #E5E7EB;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  font-size: 15px;
  color: #475569;
  transition: all 0.3s;
}

.tab-btn:hover {
  border-color: #1F4E5F;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(31, 78, 95, 0.12);
}

.tab-btn.active {
  background: linear-gradient(135deg, #1F4E5F, #2A6577);
  border-color: transparent;
  color: white;
  box-shadow: 0 8px 20px rgba(31, 78, 95, 0.3);
}

.tab-icon {
  font-size: 18px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  background: #F4F7F9;
  border-radius: 12px;
  padding: 4px;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.input-group:focus-within {
  background: white;
  border-color: #C8A96B;
  box-shadow: 0 0 0 3px rgba(200, 169, 107, 0.15);
}

.input-icon {
  font-size: 20px;
  padding: 0 15px;
  color: #94A3B8;
}

.input-group input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 14px 10px;
  font-size: 15px;
  outline: none;
  color: #1E293B;
}

.input-group input::placeholder {
  color: #94A3B8;
}

/* 图形验证码样式 */
.captcha-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-display {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 120px;
  height: 50px;
  background: linear-gradient(135deg, #1F4E5F, #C8A96B);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
  position: relative;
  overflow: hidden;
}

.captcha-display::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    45deg,
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.1) 10px,
    transparent 10px,
    transparent 20px
  );
  pointer-events: none;
}

.captcha-display:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(31, 78, 95, 0.4);
}

.captcha-display:active {
  transform: scale(0.98);
}

.captcha-text {
  font-size: 24px;
  font-weight: bold;
  color: white;
  letter-spacing: 4px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  font-family: 'Courier New', monospace;
  position: relative;
  z-index: 1;
}

.refresh-icon {
  font-size: 18px;
  animation: rotate-hint 2s ease-in-out infinite;
  position: relative;
  z-index: 1;
}

@keyframes rotate-hint {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
}

.submit-btn {
  margin-top: 10px;
  padding: 16px;
  background: linear-gradient(135deg, #1F4E5F, #2A6577);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 8px 20px rgba(31, 78, 95, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(31, 78, 95, 0.4);
  background: linear-gradient(135deg, #2A6577, #1F4E5F);
}

.submit-btn:disabled {
  background: #94A3B8;
  cursor: not-allowed;
  box-shadow: none;
}

.error-tip {
  margin-top: 15px;
  padding: 12px;
  background: #FEE2E2;
  color: #D9534F;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
  animation: shake 0.5s;
  border-left: 3px solid #D9534F;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-10px); }
  75% { transform: translateX(10px); }
}

.login-hint {
  padding: 10px 12px;
  background: #D1FAE5;
  color: #3BA272;
  border-radius: 8px;
  font-size: 13px;
  text-align: center;
  border-left: 3px solid #3BA272;
}

.login-tips {
  margin-top: 25px;
  padding: 15px;
  background: #F4F7F9;
  border-radius: 10px;
  font-size: 13px;
  color: #475569;
  line-height: 1.8;
  border: 1px solid #E5E7EB;
}

.login-tips p {
  margin: 0;
}

.login-tips p:first-child {
  font-weight: 600;
  color: #1F4E5F;
  margin-bottom: 8px;
}
</style>