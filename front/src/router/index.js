import { createRouter, createWebHistory } from 'vue-router'

// 定义路由
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from) => {
  console.log('=== 路由守卫触发 ===')
  console.log('从:', from.path, '到:', to.path)
  console.log('目标路由需要认证:', to.matched.some(record => record.meta.requiresAuth))
  
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查是否存在token
    const token = localStorage.getItem('token')
    console.log('当前token:', token ? '存在' : '不存在')
    
    if (!token) {
      // 未登录，跳转到登录页
      console.log('未找到token，重定向到登录页')
      return {
        path: '/login',
        query: { redirect: to.fullPath }
      }
    }
    // 已登录，继续访问
    console.log('token存在，允许访问')
    return true
  }
  // 不需要认证的路由，直接访问
  console.log('路由不需要认证，直接访问')
  return true
})

export default router