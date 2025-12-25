import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    proxy: {
      '/api/auth': {
        target: 'http://localhost:9001',
        changeOrigin: true,
        secure: false,
      },
      '/api/videos': {
        target: 'http://localhost:9002',
        changeOrigin: true,
        secure: false,
      },
      '/api/detections': {
        target: 'http://localhost:9004',
        changeOrigin: true,
        secure: false,
      },
      '/api/tasks': {
        target: 'http://localhost:9004',
        changeOrigin: true,
        secure: false,
      },
      '/api/audit': {
        target: 'http://localhost:9006',
        changeOrigin: true,
        secure: false,
      },
      '/uploads': {
        target: 'http://localhost:9002',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
