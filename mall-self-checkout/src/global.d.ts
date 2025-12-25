declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// 全局常量
declare const __VUE_OPTIONS_API__: boolean
declare const __VUE_PROD_DEVTOOLS__: boolean

// Electron API
declare global {
  interface Window {
    electronAPI?: {
      invoke: (channel: string, ...args: any[]) => Promise<any>
      on: (channel: string, callback: (...args: any[]) => void) => void
      removeAllListeners: (channel: string) => void
      platform: string
      getVersion: () => Promise<string>
      minimize: () => Promise<void>
      maximize: () => Promise<void>
      close: () => Promise<void>
      startScanner: () => Promise<void>
      stopScanner: () => Promise<void>
      printReceipt: (data: any) => Promise<void>
      selectFile: (options?: any) => Promise<any>
      saveFile: (data: any, options?: any) => Promise<void>
    }
  }
}

export {} 