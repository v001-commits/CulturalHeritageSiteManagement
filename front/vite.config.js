import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 配置代理，解决跨域问题
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 需要rewrite，移除/api前缀，因为后端路径不包含/api
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 配置文件访问代理
      '/files': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 不需要rewrite，直接转发/files路径
        rewrite: (path) => path,
        // 自动转发请求头，包括Authorization
        headers: {
          // 注意：这里不能使用localStorage，因为vite.config.js运行在Node.js环境中
          // 浏览器请求已经携带了Authorization头，proxy会自动转发
        }
      }
    }
  }
})