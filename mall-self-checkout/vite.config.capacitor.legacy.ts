import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import legacy from '@vitejs/plugin-legacy'

// 专门针对 Android 7.1.2 等极老设备的配置
export default defineConfig({
  // 使用自定义 HTML 模板
  root: '.',
  plugins: [
    vue(),
    // 极端兼容性配置，强制使用 legacy 模式
    legacy({
      targets: ['chrome >= 50', 'android >= 5'],
      additionalLegacyPolyfills: [
        'regenerator-runtime/runtime'
      ],
      renderLegacyChunks: true,
      // 完全禁用现代模块
      renderModernChunks: false,
      modernPolyfills: false,
      // 添加大量 polyfills 确保兼容性
      polyfills: [
        'es.symbol',
        'es.symbol.description',
        'es.symbol.iterator',
        'es.array.filter',
        'es.array.for-each',
        'es.array.iterator',
        'es.array.map',
        'es.array.reduce',
        'es.array.find',
        'es.array.find-index',
        'es.array.includes',
        'es.object.assign',
        'es.object.keys',
        'es.object.values',
        'es.object.entries',
        'es.promise',
        'es.promise.finally',
        'es.string.includes',
        'es.string.starts-with',
        'es.string.ends-with',
        'es.string.pad-start',
        'es.string.pad-end',
        'es.number.is-nan',
        'es.number.is-finite',
        'web.dom-collections.for-each',
        'web.dom-collections.iterator'
      ]
    })
  ],
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
        // 强制使用 SystemJS 格式
        format: 'system'
      }
    },
    // 极端兼容性设置
    target: 'es5',
    minify: false, // 禁用压缩以避免兼容性问题
    sourcemap: false,
    // 添加兼容性配置
    cssTarget: 'chrome50'
  },
  define: {
    __VUE_OPTIONS_API__: true, // 启用 Options API 以提高兼容性
    __VUE_PROD_DEVTOOLS__: false
  },
  // 移动端相关配置
  server: {
    host: '0.0.0.0',
    port: 3000
  }
})
