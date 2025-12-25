/**
 * Electron Preload Script
 * 
 * @description 在渲染进程中暴露安全的API接口
 */

import { contextBridge, ipcRenderer } from 'electron'

// 暴露给渲染进程的API
const electronAPI = {
  // IPC通信
  invoke: (channel: string, ...args: any[]) => ipcRenderer.invoke(channel, ...args),
  
  // 监听事件
  on: (channel: string, callback: (...args: any[]) => void) => {
    ipcRenderer.on(channel, (event, ...args) => callback(...args))
  },
  
  // 移除监听器
  removeAllListeners: (channel: string) => {
    ipcRenderer.removeAllListeners(channel)
  },
  
  // 系统信息
  platform: process.platform,
  
  // 应用信息
  getVersion: () => ipcRenderer.invoke('app:get-version'),
  
  // 窗口控制
  minimize: () => ipcRenderer.invoke('window:minimize'),
  maximize: () => ipcRenderer.invoke('window:maximize'),
  close: () => ipcRenderer.invoke('window:close'),
  
  // 扫码相关
  startScanner: () => ipcRenderer.invoke('scanner:start'),
  stopScanner: () => ipcRenderer.invoke('scanner:stop'),
  
  // 打印相关
  printReceipt: (data: any) => ipcRenderer.invoke('printer:print', data),
  
  // 文件操作
  selectFile: (options?: any) => ipcRenderer.invoke('dialog:select-file', options),
  saveFile: (data: any, options?: any) => ipcRenderer.invoke('dialog:save-file', data, options)
}

// 将API暴露给渲染进程
contextBridge.exposeInMainWorld('electronAPI', electronAPI) 