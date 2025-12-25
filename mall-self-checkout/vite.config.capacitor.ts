import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  // 设置相对路径，解决 Android 应用白屏问题
  base: './',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src/renderer', import.meta.url)),
      '@shared': fileURLToPath(new URL('./src/shared', import.meta.url))
    }
  },
  build: {
    outDir: 'dist/renderer',
    emptyOutDir: true,
    rollupOptions: {
      // 确保所有资源都被正确打包
      external: [],
      output: {
        // 确保生成的文件名是稳定的
        entryFileNames: 'assets/[name].js',
        chunkFileNames: 'assets/[name].js',
        assetFileNames: 'assets/[name].[ext]',

      }
    },
    // 移动端优化
    target: 'es2015',
    minify: 'terser',
    sourcemap: false
  },
  define: {
    __VUE_OPTIONS_API__: false,
    __VUE_PROD_DEVTOOLS__: false
  },
  // 移动端相关配置
  server: {
    host: '0.0.0.0',
    port: 3000
  }
}) 