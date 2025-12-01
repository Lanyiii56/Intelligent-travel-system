import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          // 将 node_modules 中的依赖按包名拆分
          if (id.includes('node_modules')) {
            if (id.includes('element-plus')) {
              return 'element-plus'
            }
            if (id.includes('@element-plus/icons-vue')) {
              return 'element-icons'
            }
            if (id.includes('vue') || id.includes('pinia') || id.includes('vue-router')) {
              return 'vue-vendor'
            }
            if (id.includes('echarts') || id.includes('zrender')) {
              return 'echarts'
            }
            if (id.includes('axios') || id.includes('dayjs')) {
              return 'utils'
            }
          }
        },
      },
    },
    // Element Plus 全量导入较大，提高阈值避免警告
    chunkSizeWarningLimit: 1100,
  },
})
