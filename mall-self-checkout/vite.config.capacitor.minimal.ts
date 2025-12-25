import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// 专门针对有问题设备的极简配置
export default defineConfig({
  plugins: [vue()],
  // 设置相对路径
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
      external: [],
      output: {
        // 使用最简单的文件名
        entryFileNames: 'app.js',
        chunkFileNames: 'chunk-[name].js',
        assetFileNames: 'assets/[name].[ext]',
        // 使用 IIFE 格式，兼容性最好
        format: 'iife',
        // 禁用代码分割
        manualChunks: undefined
      }
    },
    // 极端兼容性设置
    target: 'es5',
    minify: false,  // 禁用压缩避免兼容性问题
    sourcemap: false,
    cssTarget: 'chrome50',
    // 禁用现代特性
    cssCodeSplit: false
  },
  define: {
    __VUE_OPTIONS_API__: true,  // 启用 Options API
    __VUE_PROD_DEVTOOLS__: false,
    // 禁用现代特性
    'import.meta.hot': false
  },
  server: {
    host: '0.0.0.0',
    port: 3000
  }
})
