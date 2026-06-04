import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const apiProxy = {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5174,
    strictPort: false,
    open: true,
    proxy: apiProxy
  },
  preview: {
    port: 5174,
    proxy: apiProxy
  }
})
