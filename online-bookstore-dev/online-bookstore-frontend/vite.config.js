import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        // 开发时使用 Mock 服务器 (端口 8080)
        // 后端完成后改为: 'http://localhost:8080'
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
