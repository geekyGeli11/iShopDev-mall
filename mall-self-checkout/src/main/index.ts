/**
 * Electron 主进程入口
 */

import { app, BrowserWindow } from 'electron'
import { join } from 'path'

// 创建主窗口
const createWindow = (): void => {
  const mainWindow = new BrowserWindow({
    width: 1920,  // 增加默认宽度
    height: 1080, // 增加默认高度
    minWidth: 1024,  // 设置最小宽度
    minHeight: 768,  // 设置最小高度
    show: false,
    autoHideMenuBar: true,
    frame: true,     // 保留窗口边框，可以设置为false实现无边框
    fullscreenable: true,  // 允许全屏
    maximizable: true,     // 允许最大化
    webPreferences: {
      preload: join(__dirname, '../preload/index.js'),
      sandbox: false,
      nodeIntegration: false,    // 安全考虑
      contextIsolation: true,    // 安全考虑
      webSecurity: true          // 安全考虑
    }
  })

  // 窗口准备好后显示并最大化
  mainWindow.on('ready-to-show', () => {
    mainWindow.show()
    mainWindow.maximize() // 启动时最大化窗口
  })

  // 禁用右键菜单
  mainWindow.webContents.on('context-menu', (e) => {
    e.preventDefault()
  })

  // 开发环境加载开发服务器，生产环境加载打包文件
  if (process.env.NODE_ENV === 'development') {
    mainWindow.loadURL('http://localhost:3000')
    mainWindow.webContents.openDevTools()
  } else {
    const htmlPath = join(__dirname, '../renderer/index.html')
    console.log('Loading HTML file from:', htmlPath)
    mainWindow.loadFile(htmlPath)
    // 生产环境不打开开发者工具
    // mainWindow.webContents.openDevTools()
  }
}

// 应用准备就绪
app.whenReady().then(() => {
  createWindow()

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

// 关闭所有窗口时退出应用
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit()
}) 