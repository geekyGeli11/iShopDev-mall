import { defineConfig, externalizeDepsPlugin } from 'electron-vite'
import { resolve } from 'path'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  main: {
    plugins: [externalizeDepsPlugin()],
    build: {
      lib: {
        entry: 'src/main/index.ts'
      },
      outDir: 'dist/main'
    }
  },
  preload: {
    plugins: [externalizeDepsPlugin()],
    build: {
      lib: {
        entry: 'src/main/preload.ts'
      },
      outDir: 'dist/preload'
    }
  },
  renderer: {
    resolve: {
      alias: {
        '@': resolve('src/renderer'),
        '@shared': resolve('src/shared'),
        '@main': resolve('src/main')
      }
    },
    plugins: [vue()],
    build: {
      rollupOptions: {
        input: 'src/renderer/index.html'
      },
      outDir: 'dist/renderer'
    }
  }
}) 